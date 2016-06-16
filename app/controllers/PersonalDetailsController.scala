/*
 * Copyright 2016 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import _root_.forms.FastPassForm._
import _root_.forms.GeneralDetailsForm
import config.CSRCache
import connectors.ApplicationClient.PersonalDetailsNotFound
import connectors.exchange.CivilServiceExperienceDetails._
import connectors.exchange.{ CivilServiceExperienceDetails, SelectedSchemes }
import connectors.{ ApplicationClient, SchemeClient, UserManagementClient }
import helpers.NotificationType._
import mappings.{ Address, DayMonthYear }
import models.ApplicationData.ApplicationStatus._
import models.{ ApplicationRoute, CachedDataWithApp }
import org.joda.time.LocalDate
import play.api.data.Form
import play.api.mvc.{ Request, Result }
import security.Roles.{ EditPersonalDetailsAndContinueRole, EditPersonalDetailsRole }
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

object PersonalDetailsController extends PersonalDetailsController(ApplicationClient, SchemeClient, CSRCache, UserManagementClient)

class PersonalDetailsController(applicationClient: ApplicationClient,
                                schemeClient: SchemeClient,
                                cacheClient: CSRCache,
                                userManagementClient: UserManagementClient)
  extends BaseController(applicationClient, cacheClient) with GeneralDetailsToExchangeConverter {

  private sealed trait OnSuccess
  private case object ContinueToNextStepInJourney extends OnSuccess
  private case object RedirectToTheDashboard extends OnSuccess

  def presentAndContinue = CSRSecureAppAction(EditPersonalDetailsRole) { implicit request =>
    implicit user =>
      personalDetails(afterSubmission = ContinueToNextStepInJourney)
  }

  def present = CSRSecureAppAction(EditPersonalDetailsRole) { implicit request =>
    implicit user =>
      personalDetails(afterSubmission = RedirectToTheDashboard)
  }

  private def personalDetails(afterSubmission: OnSuccess)
                             (implicit user: CachedDataWithApp, hc: HeaderCarrier, request: Request[_]): Future[Result] = {
    implicit val now: LocalDate = LocalDate.now
    val continueToTheNextStep = continuetoTheNextStep(afterSubmission)

    applicationClient.getPersonalDetails(user.user.userID, user.application.applicationId).map { gd =>
      val form = GeneralDetailsForm.form.fill(GeneralDetailsForm.Data(
        gd.firstName,
        gd.lastName,
        gd.preferredName,
        gd.dateOfBirth,
        Some(gd.outsideUk),
        gd.address,
        gd.postCode,
        gd.country,
        gd.phone,
        gd.civilServiceExperienceDetails
      ))
      Ok(views.html.application.generalDetails(form, continueToTheNextStep))

    }.recover {
      case e: PersonalDetailsNotFound =>
        val formFromUser = GeneralDetailsForm.form.fill(GeneralDetailsForm.Data(
          user.user.firstName,
          user.user.lastName,
          user.user.firstName,
          DayMonthYear.emptyDate,
          outsideUk = None,
          address = Address.EmptyAddress,
          postCode = None,
          country = None,
          phone = None,
          civilServiceExperienceDetails = EmptyCivilServiceExperienceDetails
        ))
        Ok(views.html.application.generalDetails(formFromUser, continueToTheNextStep))
    }
  }

  def submitGeneralDetailsAndContinue() = CSRSecureAppAction(EditPersonalDetailsAndContinueRole) { implicit request =>
    implicit user =>
      val redirect = if(user.application.applicationRoute == ApplicationRoute.Edip) {
        Redirect(routes.AssistanceDetailsController.present())
      } else {
        Redirect(routes.SchemePreferencesController.present())
      }
      submit(GeneralDetailsForm.form(LocalDate.now), ContinueToNextStepInJourney, redirect)
  }

  def submitGeneralDetails() = CSRSecureAppAction(EditPersonalDetailsRole) { implicit request =>
    implicit user =>
      submit(GeneralDetailsForm.form(LocalDate.now, ignoreFastPassValidations = true), RedirectToTheDashboard,
        Redirect(routes.HomeController.present()).flashing(success("personalDetails.updated")), user.application.civilServiceExperienceDetails)
  }

  private def continuetoTheNextStep(onSuccess: OnSuccess) = onSuccess match {
    case ContinueToNextStepInJourney => true
    case RedirectToTheDashboard => false
  }

  private def submit(generalDetailsForm: Form[GeneralDetailsForm.Data], onSuccess: OnSuccess, redirectOnSuccess: Result,
                     overrideCivilServiceExperienceDetails: Option[CivilServiceExperienceDetails] = None)
                    (implicit cachedData: CachedDataWithApp, hc: HeaderCarrier, request: Request[_]) = {

    val handleFormWithErrors = (errorForm:Form[GeneralDetailsForm.Data]) => {
      Future.successful(Ok(views.html.application.generalDetails(
        generalDetailsForm.bind(errorForm.data.cleanupFastPassFields), continuetoTheNextStep(onSuccess)))
      )
    }

    val handleValidForm = (form: GeneralDetailsForm.Data) => {
      val civilServiceExperienceDetails: Option[CivilServiceExperienceDetails] =
        overrideCivilServiceExperienceDetails.orElse(form.civilServiceExperienceDetails)
      for {
        _ <- applicationClient.updateGeneralDetails(cachedData.application.applicationId, cachedData.user.userID,
          toExchange(form, cachedData.user.email, Some(continuetoTheNextStep(onSuccess)), overrideCivilServiceExperienceDetails))
        _ <- createDefaultSchemes
        _ <- userManagementClient.updateDetails(cachedData.user.userID, form.firstName, form.lastName, Some(form.preferredName))
        redirect <- updateProgress(data => {
          val applicationCopy = data.application.map(_.copy(civilServiceExperienceDetails = civilServiceExperienceDetails))
          data.copy(user = cachedData.user.copy(firstName = form.firstName, lastName = form.lastName,
            preferredName = Some(form.preferredName)), application =
            if (continuetoTheNextStep(onSuccess)) applicationCopy.map(_.copy(applicationStatus = IN_PROGRESS)) else applicationCopy)
        })(_ => redirectOnSuccess)
      } yield {
        redirect
      }
    }
    generalDetailsForm.bindFromRequest.fold(handleFormWithErrors, handleValidForm)
  }

  private def createDefaultSchemes(implicit cacheData: CachedDataWithApp, hc: HeaderCarrier, request: Request[_]): Future[Unit] =
    cacheData.application.applicationRoute match {
    case appRoute if appRoute == ApplicationRoute.Edip =>
      for {
        _ <- schemeClient.updateSchemePreferences(SelectedSchemes(List(appRoute), orderAgreed = true,
          eligible = true))(cacheData.application.applicationId)
        _ <- env.userService.refreshCachedUser(cacheData.user.userID)
      } yield ()
    case _ => Future.successful(())
  }

}

trait GeneralDetailsToExchangeConverter {

  def toExchange(generalDetails: GeneralDetailsForm.Data, email: String, updateApplicationStatus: Option[Boolean],
                 civilServiceExperienceDetails: Option[CivilServiceExperienceDetails] = None) = {
    val gd = generalDetails.insideUk match {
      case true => generalDetails.copy(country = None)
      case false => generalDetails.copy(postCode = None)
    }
    gd.toExchange(email, updateApplicationStatus, civilServiceExperienceDetails)
  }
}