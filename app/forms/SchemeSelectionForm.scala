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

package forms

import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.i18n.Messages

//scalastyle:off
object SchemeSelectionForm {
  val AllSchemes = Seq(
    "CentralDepartments" -> "Central Departments",
    "Commercial" -> "Commercial",
    "DigitalAndTechnology" -> "Digital and Technology",
    "DiplomaticService" -> "Diplomatic Service",
    "European" -> "European"
  )

  def form = {
    Form(
      mapping(
        "schemes" -> of(schemeFormatter("schemes"))
      )(Data.apply)(Data.unapply))
  }

  case class Data(selectedSchemes: Seq[(String, String)])

  val EmptyData = Data(AllSchemes)

  def schemeFormatter(formKey: String) = new Formatter[Seq[(String, String)]] {
    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Seq[(String, String)]] = {
      Right(List())
    }

    def unbind(key: String, value: Seq[(String, String)]): Map[String, String] = {
      Map()
    }
  }

}
