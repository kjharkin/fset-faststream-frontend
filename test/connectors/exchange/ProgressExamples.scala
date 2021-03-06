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

import models.{ Phase1TestProgress, Phase2TestProgress, Phase3TestProgress, Progress }

object ProgressExamples {
  val InitialProgress = Progress()

  val SubmittedProgress = Progress(
    personalDetails = true,
    schemePreferences = true,
    preview = true,
    startedQuestionnaire = true,
    diversityQuestionnaire = true,
    educationQuestionnaire = true,
    occupationQuestionnaire = true,
    submitted = true
  )

  val Phase1TestsInvitedNotStarted = Progress(
    phase1TestProgress = Phase1TestProgress(phase1TestsInvited = true)
  )

  val Phase1TestsStarted = SubmittedProgress.copy(
    phase1TestProgress = Phase1TestProgress(phase1TestsInvited = true, phase1TestsStarted = true)
  )

  val Phase1TestsPassed = Phase1TestsStarted.copy(
    phase1TestProgress = Phase1TestProgress(phase1TestsCompleted = true, phase1TestsPassed = true)
  )

  val Phase1TestsFailed = SubmittedProgress.copy(
    phase1TestProgress = Phase1TestProgress(phase1TestsInvited = true, phase1TestsStarted = true, phase1TestsFailed = true)
  )

  val Phase2TestsFailed = SubmittedProgress.copy(
    phase2TestProgress = Phase2TestProgress(phase2TestsInvited = true, phase2TestsStarted = true, phase2TestsFailed = true)
  )

  val Phase3TestsFailed = SubmittedProgress.copy(
    phase3TestProgress = Phase3TestProgress(phase3TestsInvited = true, phase3TestsStarted = true, phase3TestsFailed = true)
  )

  val FullProgress = Progress(
    personalDetails = true,
    schemePreferences = true,
    partnerGraduateProgrammes = true,
    assistanceDetails = true,
    preview = true,
    startedQuestionnaire = true,
    diversityQuestionnaire = true,
    educationQuestionnaire = true,
    occupationQuestionnaire = true,
    submitted = true,
    phase1TestProgress = Phase1TestProgress(
      phase1TestsInvited = true,
      phase1TestsFirstReminder = true,
      phase1TestsSecondReminder = true,
      phase1TestsStarted = true,
      phase1TestsCompleted = true,
      phase1TestsResultsReady = true,
      phase1TestsResultsReceived = true,
      phase1TestsPassed = true
    ),
     phase2TestProgress = Phase2TestProgress(
      phase2TestsInvited = true,
      phase2TestsFirstReminder = true,
      phase2TestsSecondReminder = true,
      phase2TestsStarted = true,
      phase2TestsCompleted = true,
      phase2TestsResultsReady = true,
      phase2TestsResultsReceived = true,
      phase2TestsPassed = true
    ),
      phase3TestProgress = Phase3TestProgress(
      phase3TestsInvited = true,
      phase3TestsFirstReminder = true,
      phase3TestsSecondReminder = true,
      phase3TestsStarted = true,
      phase3TestsCompleted = true,
      phase3TestsResultsReceived = true,
      phase3TestsPassed = true
    ),
    exported = true,
    assessmentScores = AssessmentScores(entered = true, accepted = true),
    assessmentCentre = AssessmentCentre(
      awaitingReevaluation = true,
      passed = true,
      passedNotified = true
    )
  )



  val Phase3TestsPassed = Progress(true, true, true, true, true, true, true, true, true, true, true, false,
    false,
    phase1TestProgress = Phase1TestProgress(true, true, true, true, true, true,
      true, true, true, true),
    phase2TestProgress = Phase2TestProgress(true, true, true, true, true, true,
      true, true, true, true),
    phase3TestProgress = Phase3TestProgress(phase3TestsInvited = true,
      phase3TestsStarted = true,
      phase3TestsCompleted = true,
      phase3TestsExpired = true,
      phase3TestsResultsReceived = true,
      phase3TestsPassed = true
    ),
    false,
    false,
    true,
    AssessmentScores(true, true),
    AssessmentCentre(true, true, true)
  )


  val PersonalDetailsProgress = InitialProgress.copy(personalDetails = true)
  val SchemePreferencesProgress = PersonalDetailsProgress.copy(schemePreferences = true)
  val PartnerGraduateProgrammesProgress = SchemePreferencesProgress.copy(partnerGraduateProgrammes = true)
  val AssistanceDetailsProgress = PartnerGraduateProgrammesProgress.copy(assistanceDetails = true)
  val QuestionnaireProgress = AssistanceDetailsProgress.copy(startedQuestionnaire = true, diversityQuestionnaire = true,
    educationQuestionnaire = true ,occupationQuestionnaire = true)
  val PreviewProgress = QuestionnaireProgress.copy(preview = true)
  val WithdrawnAfterSubmitProgress = SubmittedProgress.copy(withdrawn = true)
  val ExportedToParityProgress = Phase3TestsPassed.copy(exported = true)
  val UpdateExportedToParityProgress = ExportedToParityProgress.copy(updateExported = true)
}
