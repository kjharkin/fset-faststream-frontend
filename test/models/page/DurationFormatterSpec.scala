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

package models.page

import org.joda.time.DateTime
import org.scalatestplus.play.PlaySpec

class DurationFormatterSpec extends PlaySpec {

  "Duration formatter" should {
    "return years, months, days and hours" in new TestFixture {
      val futureDate = now.plusYears(50).plusMonths(7).plusDays(12).plusHours(1).plusMinutes(34)
      val result = durationFormatter.durationFromNow(futureDate)
      result mustBe "50 years, 7 months, 12 days and 1 hour"
    }

    "return months, days and hours" in new TestFixture {
      val futureDate = now.plusMonths(2).plusDays(3).plusHours(5).plusMinutes(59)
      val result = durationFormatter.durationFromNow(futureDate)
      result mustBe "2 months, 3 days and 5 hours"
    }

    "return days and hours" in new TestFixture {
      val futureDate = now.plusDays(10).plusHours(12).plusMinutes(30)
      val result = durationFormatter.durationFromNow(futureDate)
      result mustBe "10 days and 12 hours"
    }

    "return only hours" in new TestFixture {
      val futureDate = now.plusHours(2).plusMinutes(30)
      val result = durationFormatter.durationFromNow(futureDate)
      result mustBe "0 days and 2 hours"
    }

    "return 0 days and hours if there is less than 1 day" in new TestFixture {
      val futureDate = now.plusMinutes(4)
      val result = durationFormatter.durationFromNow(futureDate)
      result mustBe "0 days and 0 hours"
    }
  }

  trait TestFixture {
    self =>
    def now = DateTime.now

    val durationFormatter = new DurationFormatter {
      private[page] override def now = self.now
    }
  }
}
