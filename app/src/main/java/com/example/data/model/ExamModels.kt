package com.example.data.model

enum class StreamType(val label: String, val description: String) {
    PCM("PCM", "Physics, Chemistry, Mathematics"),
    PCB("PCB", "Physics, Chemistry, Biology"),
    PCMB("PCMB", "Physics, Chemistry, Maths & Bio")
}

enum class ExamCategory(val displayName: String, val shortLabel: String) {
    ENGINEERING("Engineering (PCM)", "Engg"),
    MEDICAL("Medical & Allied (PCB)", "Medical"),
    DEFENSE("Defense & Armed Forces", "Defense"),
    RESEARCH("Research & Pure Sciences", "Research"),
    STATE_CET("State CETs & Regional", "State CET")
}

enum class ExamStageType(val title: String, val stepNumber: Int) {
    NOTIFICATION_RELEASED("Information Bulletin Out", 1),
    REGISTRATION_OPEN("Registration Window", 2),
    REGISTRATION_LATE_FEE("Late-Fee Extension Window", 3),
    CORRECTION_WINDOW("Correction Window", 4),
    CITY_SLIP_ADMIT_CARD("Admit Card & City Slip", 5),
    EXAM_DATES("Exam Dates", 6),
    ANSWER_KEY_CHALLENGE("Answer Key & Challenge", 7),
    FINAL_RESULT_COUNSELING("Result & Counseling", 8)
}

enum class ExamStatus(val label: String) {
    UPCOMING("Upcoming"),
    REGISTRATION_OPEN("Registration Open"),
    LAST_48_HOURS("Last 48 Hours"),
    ADMIT_CARD_LIVE("Admit Card Live"),
    CLOSED("Closed")
}

data class StageMilestone(
    val stageType: ExamStageType,
    val title: String,
    val dateDisplay: String,
    val endTimestampMs: Long? = null,
    val isTentative: Boolean = false,
    val isExtended: Boolean = false,
    val isCompleted: Boolean = false,
    val isCurrent: Boolean = false
)

data class FeeItem(
    val categoryLabel: String,
    val amount: String
)

data class EligibilityDetails(
    val min12thPercentage: String,
    val ageLimits: String,
    val compulsorySubjects: String,
    val attemptLimit: String = "No overall bar"
)

data class ExamPatternInfo(
    val durationMinutes: Int,
    val mode: String, // "CBT (Online)", "Pen & Paper (OMR)", "Hybrid"
    val markingScheme: String,
    val questionSplit: String,
    val totalMarks: Int
)

data class ExamItem(
    val id: String,
    val slug: String,
    val fullName: String,
    val shortCode: String,
    val category: ExamCategory,
    val streamEligibility: List<StreamType>,
    val conductingBody: String,
    val officialWebsite: String,
    val applicationUrl: String,
    val eligibilitySummary: String,
    val eligibilityDetails: EligibilityDetails,
    val fees: List<FeeItem>,
    val pattern: ExamPatternInfo,
    val stages: List<StageMilestone>,
    val stateDomicile: String? = null, // e.g. "Maharashtra", "Karnataka", "West Bengal"
    val syllabusDocName: String = "Official Syllabus (PDF)",
    val bulletinDocName: String = "Information Bulletin 2026 (PDF)",
    val currentStatus: ExamStatus,
    val nextMilestoneTitle: String,
    val nextMilestoneTimestampMs: Long,
    val isLateFeeApplicable: Boolean = false,
    val targetYear: Int = 2026
)
