/*
 * Copyright 2017 HM Revenue & Customs
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

package connectors.exchange

import models.UniqueIdentifier
import org.joda.time.DateTime
import play.api.libs.json.Json

case class CubiksTest(usedForResults: Boolean,
  testUrl: String,
  token: UniqueIdentifier,
  cubiksUserId: Int,
  invitationDate: DateTime,
  startedDateTime: Option[DateTime] = None,
  completedDateTime: Option[DateTime] = None,
  resultsReadyToDownload: Boolean = false
) extends Test {
  def started = startedDateTime.isDefined
  def completed = completedDateTime.isDefined
}

object CubiksTest {
  implicit def phase1TestFormat = Json.format[CubiksTest]
}

case class Phase1TestGroup(expirationDate: DateTime,
  tests: List[CubiksTest]
) extends CubiksTestGroup

object Phase1TestGroup {
  implicit def phase1TestGroupFormat = Json.format[Phase1TestGroup]
}

case class Phase2TestGroup(expirationDate: DateTime,
  tests: List[CubiksTest]
) extends CubiksTestGroup

object Phase2TestGroup {
  implicit def phase1TestProfileFormat = Json.format[Phase2TestGroup]
}
