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

package security

import java.util.UUID

import com.mohiva.play.silhouette.api.util.Credentials
import config.CSRHttp
import connectors.exchange.UserResponse
import connectors.UserManagementClient._
import connectors.UserManagementClient.InvalidRoleException
import controllers.BaseSpec
import models.{CachedUser, UniqueIdentifier}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class CsrCredentialsProviderSpec extends BaseSpec with ScalaFutures{


  implicit override val patienceConfig =
    PatienceConfig(timeout = Span(2, Seconds), interval = Span(1, Seconds))

  "authenticate" should {
    "return user when credentials are valid" in new TestFixture {
      val csrCredentialsProvider = new TestCsrCredentialsProvider(
        signInResponse = Future.successful(userResponse))
      val result = csrCredentialsProvider.authenticate(credentials)
      result.futureValue mustBe Right(CachedUser(UserId, FirstName, LastName, Some(PreferredName), Email, true, ""))
    }

    "return InvalidRole if the role is invalid" in new TestFixture {
      val csrCredentialsProvider = new TestCsrCredentialsProvider(signInResponse = Future.failed(new InvalidRoleException))
      csrCredentialsProvider.authenticate(credentials).futureValue mustBe Left(InvalidRole)
    }

    "return AccountLocked when account status is locked" in new TestFixture {
      val csrCredentialsProvider = new TestCsrCredentialsProvider(
        signInResponse = Future.failed(new InvalidCredentialsException()),
        failedLoginResponse = Future.successful(userResponse.copy(lockStatus = "LOCKED")))
      csrCredentialsProvider.authenticate(credentials).futureValue mustBe Left(AccountLocked)
    }

    "return LastAttempt when account status is last attempt" in new TestFixture {
      val csrCredentialsProvider = new TestCsrCredentialsProvider(
        signInResponse = Future.failed(new InvalidCredentialsException()),
        failedLoginResponse = Future.successful(userResponse.copy(lockStatus = "LAST_ATTEMPT")))
      csrCredentialsProvider.authenticate(credentials).futureValue mustBe Left(LastAttempt)
    }

    "return InvalidCredentials when the credentials are invalid and we cannot record login failure" in new TestFixture {
      val csrCredentialsProvider = new TestCsrCredentialsProvider(
        signInResponse = Future.failed(new InvalidCredentialsException()),
        failedLoginResponse = Future.failed(new InvalidCredentialsException()))
      csrCredentialsProvider.authenticate(credentials).futureValue mustBe Left(InvalidCredentials)
    }

    "return InvalidCredentials when the credentials are invalid and we can record failed login" in new TestFixture {
      val csrCredentialsProvider = new TestCsrCredentialsProvider(
        signInResponse = Future.failed(new InvalidCredentialsException()),
        failedLoginResponse = Future.successful(userResponse))
      csrCredentialsProvider.authenticate(credentials).futureValue mustBe Left(InvalidCredentials)
    }

    "return InvalidCredentials when sign in fails and status is not last attempt or locked" in new TestFixture {
      val csrCredentialsProvider = new TestCsrCredentialsProvider(
        signInResponse = Future.failed(new InvalidCredentialsException()),
        failedLoginResponse = Future.successful(userResponse.copy(lockStatus = "xxxx")))
      csrCredentialsProvider.authenticate(credentials).futureValue mustBe Left(InvalidCredentials)
    }

    "return AccountLocked when there is an AccountLockedOutException" in new TestFixture {
      val csrCredentialsProvider = new TestCsrCredentialsProvider(
        signInResponse = Future.failed(new InvalidCredentialsException()),
        failedLoginResponse = Future.failed(new AccountLockedOutException))
      csrCredentialsProvider.authenticate(credentials).futureValue mustBe Left(AccountLocked)
    }
  }

  trait TestFixture {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val UserId = UniqueIdentifier(UUID.randomUUID())
    val FirstName = "firstName"
    val LastName = "lastName"
    val PreferredName = "preferredName"
    val Email = "email@mailinator.com"
    val Role = "role"
    val ServiceName = "faststream"
    val Id = "login"
    val Password = "password"

    val credentials = Credentials(Id, Password)
    val userResponse = UserResponse(FirstName, LastName, Some(PreferredName), true, UserId,
      Email, "", Role, ServiceName)

    class TestCsrCredentialsProvider(signInResponse: Future[UserResponse] = Future.successful(userResponse),
                                     failedLoginResponse: Future[UserResponse] = Future.successful(userResponse))
      extends CsrCredentialsProvider {

      val http = CSRHttp

      override def signIn(email: String, password: String)(implicit hc: HeaderCarrier): Future[UserResponse] = {
        signInResponse
      }

      override def failedLogin(email: String)(implicit hc: HeaderCarrier): Future[UserResponse] = {
        failedLoginResponse
      }
    }

  }
}
