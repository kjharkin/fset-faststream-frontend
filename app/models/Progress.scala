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

package models

import connectors.exchange.{ AssessmentCentre, AssessmentScores, ProgressResponse }
import play.api.libs.json.{ Format, Json }

import scala.language.implicitConversions

case class Phase1TestProgress(
  phase1TestsInvited: Boolean = false,
  phase1TestsFirstReminder: Boolean = false,
  phase1TestsSecondReminder: Boolean = false,
  phase1TestsStarted: Boolean = false,
  phase1TestsCompleted: Boolean = false,
  phase1TestsExpired: Boolean = false,
  phase1TestsResultsReady: Boolean = false,
  phase1TestsResultsReceived: Boolean = false,
  phase1TestsPassed: Boolean = false,
  phase1TestsFailed: Boolean = false
 )

case class Phase2TestProgress(
  phase2TestsInvited: Boolean = false,
  phase2TestsFirstReminder: Boolean = false,
  phase2TestsSecondReminder: Boolean = false,
  phase2TestsStarted: Boolean = false,
  phase2TestsCompleted: Boolean = false,
  phase2TestsExpired: Boolean = false,
  phase2TestsResultsReady: Boolean = false,
  phase2TestsResultsReceived: Boolean = false,
  phase2TestsPassed: Boolean = false,
  phase2TestsFailed: Boolean = false
)

case class Phase3TestProgress(
  phase3TestsInvited: Boolean = false,
  phase3TestsFirstReminder: Boolean = false,
  phase3TestsSecondReminder: Boolean = false,
  phase3TestsStarted: Boolean = false,
  phase3TestsCompleted: Boolean = false,
  phase3TestsExpired: Boolean = false,
  phase3TestsResultsReceived: Boolean = false,
  phase3TestsPassed: Boolean = false,
  phase3TestsFailed: Boolean = false
)

case class Progress(
  personalDetails: Boolean = false,
  schemePreferences: Boolean = false,
  partnerGraduateProgrammes: Boolean = false,
  assistanceDetails: Boolean = false,
  preview: Boolean = false,
  startedQuestionnaire: Boolean = false,
  diversityQuestionnaire: Boolean = false,
  educationQuestionnaire: Boolean = false,
  occupationQuestionnaire: Boolean = false,
  submitted: Boolean = false,
  withdrawn: Boolean = false,
  sdipFSFailed: Boolean = false,
  sdipFSSuccessful: Boolean = false,
  phase1TestProgress: Phase1TestProgress = Phase1TestProgress(),
  phase2TestProgress: Phase2TestProgress = Phase2TestProgress(),
  phase3TestProgress: Phase3TestProgress = Phase3TestProgress(),
  exported: Boolean = false,
  updateExported: Boolean = false,
  failedToAttend: Boolean = false,
  assessmentScores: AssessmentScores = AssessmentScores(),
  assessmentCentre: AssessmentCentre = AssessmentCentre()
)

object Progress {
  implicit val assessmentScoresFormat = Json.format[AssessmentScores]
  implicit val assessmentCentreFormat = Json.format[AssessmentCentre]
  implicit val phase1TestProgressFormat = Json.format[Phase1TestProgress]
  implicit val phase2TestProgressFormat = Json.format[Phase2TestProgress]
  implicit val phase3TestProgressFormat = Json.format[Phase3TestProgress]
  implicit val progressFormat: Format[Progress] = Json.format[Progress]

  // scalastyle:off method.length
  implicit def fromProgressRespToAppProgress(progressResponse: ProgressResponse): Progress =
    Progress(
      personalDetails = progressResponse.personalDetails,
      schemePreferences = progressResponse.schemePreferences,
      partnerGraduateProgrammes = progressResponse.partnerGraduateProgrammes,
      assistanceDetails = progressResponse.assistanceDetails,
      preview = progressResponse.preview,
      startedQuestionnaire = progressResponse.questionnaire.contains("start_questionnaire"),
      diversityQuestionnaire = progressResponse.questionnaire.contains("diversity_questionnaire"),
      educationQuestionnaire = progressResponse.questionnaire.contains("education_questionnaire"),
      occupationQuestionnaire = progressResponse.questionnaire.contains("occupation_questionnaire"),
      submitted = progressResponse.submitted,
      withdrawn = progressResponse.withdrawn,
      sdipFSFailed = progressResponse.sdipFSFailed,
      sdipFSSuccessful = progressResponse.sdipFSSuccessful,
      phase1TestProgress = Phase1TestProgress(
        phase1TestsInvited = progressResponse.phase1ProgressResponse.phase1TestsInvited,
        phase1TestsStarted  = progressResponse.phase1ProgressResponse.phase1TestsStarted,
        phase1TestsFirstReminder = progressResponse.phase1ProgressResponse.phase1TestsFirstReminder,
        phase1TestsSecondReminder = progressResponse.phase1ProgressResponse.phase1TestsSecondReminder,
        phase1TestsCompleted = progressResponse.phase1ProgressResponse.phase1TestsCompleted,
        phase1TestsExpired= progressResponse.phase1ProgressResponse.phase1TestsExpired,
        phase1TestsResultsReady = progressResponse.phase1ProgressResponse.phase1TestsResultsReady,
        phase1TestsResultsReceived = progressResponse.phase1ProgressResponse.phase1TestsResultsReceived,
        phase1TestsPassed = progressResponse.phase1ProgressResponse.phase1TestsPassed,
        phase1TestsFailed = progressResponse.phase1ProgressResponse.phase1TestsFailed
      ),
      phase2TestProgress = Phase2TestProgress(
        phase2TestsInvited = progressResponse.phase2ProgressResponse.phase2TestsInvited,
        phase2TestsStarted  = progressResponse.phase2ProgressResponse.phase2TestsStarted,
        phase2TestsFirstReminder = progressResponse.phase2ProgressResponse.phase2TestsFirstReminder,
        phase2TestsSecondReminder = progressResponse.phase2ProgressResponse.phase2TestsSecondReminder,
        phase2TestsCompleted = progressResponse.phase2ProgressResponse.phase2TestsCompleted,
        phase2TestsExpired= progressResponse.phase2ProgressResponse.phase2TestsExpired,
        phase2TestsResultsReady = progressResponse.phase2ProgressResponse.phase2TestsResultsReady,
        phase2TestsResultsReceived = progressResponse.phase2ProgressResponse.phase2TestsResultsReceived,
        phase2TestsPassed = progressResponse.phase2ProgressResponse.phase2TestsPassed,
        phase2TestsFailed = progressResponse.phase2ProgressResponse.phase2TestsFailed
      ),
      phase3TestProgress = Phase3TestProgress(phase3TestsInvited = progressResponse.phase3ProgressResponse.phase3TestsInvited,
        phase3TestsFirstReminder = progressResponse.phase3ProgressResponse.phase3TestsFirstReminder,
        phase3TestsSecondReminder = progressResponse.phase3ProgressResponse.phase3TestsSecondReminder,
        phase3TestsStarted  = progressResponse.phase3ProgressResponse.phase3TestsStarted,
        phase3TestsCompleted = progressResponse.phase3ProgressResponse.phase3TestsCompleted,
        phase3TestsExpired = progressResponse.phase3ProgressResponse.phase3TestsExpired,
        phase3TestsResultsReceived = progressResponse.phase3ProgressResponse.phase3TestsResultsReceived,
        phase3TestsPassed = progressResponse.phase3ProgressResponse.phase3TestsPassed,
        phase3TestsFailed = progressResponse.phase3ProgressResponse.phase3TestsFailed
      ),
      exported = progressResponse.exported,
      updateExported = progressResponse.updateExported,
      failedToAttend = progressResponse.failedToAttend,
      assessmentScores = progressResponse.assessmentScores,
      assessmentCentre = progressResponse.assessmentCentre
    )
  // scalastyle:on method.length
}
