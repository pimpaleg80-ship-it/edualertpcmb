package com.example.data.repository

import com.example.data.model.*

object SampleExamsData {

    private const val ONE_DAY_MS = 86_400_000L
    private const val ONE_HOUR_MS = 3_600_000L

    fun getInitialExams(currentTimeMs: Long = System.currentTimeMillis()): List<ExamItem> {
        return listOf(
            // =========================================================================
            // 1. ENGINEERING (PCM)
            // =========================================================================
            ExamItem(
                id = "jee-main-2026",
                slug = "jee-main-2026",
                fullName = "Joint Entrance Examination (Main) 2026",
                shortCode = "JEE Main",
                category = ExamCategory.ENGINEERING,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCMB),
                conductingBody = "National Testing Agency (NTA)",
                officialWebsite = "https://jeemain.nta.nic.in",
                applicationUrl = "https://jeemain.nta.nic.in/apply-online",
                eligibilitySummary = "12th Pass/Appearing with Physics, Chemistry & Math. Minimum 75% in 12th for NITs/IIITs (65% for SC/ST).",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "75% aggregate in 12th (65% for SC/ST) or top 20 percentile",
                    ageLimits = "No age limit for appearing in JEE Main",
                    compulsorySubjects = "Physics and Mathematics + Chemistry/Biotechnology/Technical Voc.",
                    attemptLimit = "Max 3 consecutive years from year of passing Class 12"
                ),
                fees = listOf(
                    FeeItem("General / OBC Male", "₹1,000"),
                    FeeItem("General / OBC Female", "₹800"),
                    FeeItem("SC / ST / PwD / Transgender", "₹500")
                ),
                pattern = ExamPatternInfo(
                    durationMinutes = 180,
                    mode = "CBT (Computer Based Test)",
                    markingScheme = "+4 for correct, -1 for incorrect",
                    questionSplit = "90 Questions (30 each: Phys, Chem, Math). Answer 75.",
                    totalMarks = 300
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.NOTIFICATION_RELEASED, "Information Bulletin Out", "Nov 15, 2025", isCompleted = true),
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "Registration Window", "Nov 20 - Dec 28, 2025", isCompleted = true),
                    StageMilestone(ExamStageType.CORRECTION_WINDOW, "Correction Window", "Jan 4 - Jan 6, 2026", isCompleted = true),
                    StageMilestone(ExamStageType.CITY_SLIP_ADMIT_CARD, "Admit Card Released", "Live Now", isCompleted = true, isCurrent = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "Session 1 Exam Dates", "Jan 22 - Jan 31, 2026", endTimestampMs = currentTimeMs + 3 * ONE_DAY_MS + 6 * ONE_HOUR_MS),
                    StageMilestone(ExamStageType.ANSWER_KEY_CHALLENGE, "Provisional Answer Key", "Feb 8 - Feb 10, 2026", isTentative = true),
                    StageMilestone(ExamStageType.FINAL_RESULT_COUNSELING, "Results & JoSAA Counseling", "Feb 20, 2026", isTentative = true)
                ),
                currentStatus = ExamStatus.ADMIT_CARD_LIVE,
                nextMilestoneTitle = "Session 1 Examination Begins",
                nextMilestoneTimestampMs = currentTimeMs + 3 * ONE_DAY_MS + 6 * ONE_HOUR_MS
            ),

            ExamItem(
                id = "jee-adv-2026",
                slug = "jee-advanced-2026",
                fullName = "Joint Entrance Examination (Advanced) 2026",
                shortCode = "JEE Adv",
                category = ExamCategory.ENGINEERING,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCMB),
                conductingBody = "IIT Joint Admission Board (JAB)",
                officialWebsite = "https://jeeadv.ac.in",
                applicationUrl = "https://jeeadv.ac.in/portal",
                eligibilitySummary = "Top 2,50,000 rankers in JEE Main. Passed 12th in 2025 or 2026.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "75% in 12th Board (65% for SC/ST/PwD)",
                    ageLimits = "Born on or after October 1, 2001 (5 years relaxation for SC/ST)",
                    compulsorySubjects = "Physics, Chemistry, and Mathematics",
                    attemptLimit = "Maximum 2 times in consecutive years"
                ),
                fees = listOf(
                    FeeItem("Male (General/OBC)", "₹3,200"),
                    FeeItem("Female candidates (All categories)", "₹1,600"),
                    FeeItem("SC / ST / PwD", "₹1,600")
                ),
                pattern = ExamPatternInfo(
                    durationMinutes = 360,
                    mode = "CBT (Two Mandatory Papers of 3 hrs each)",
                    markingScheme = "Partial marking, +4, +3, +2, -1 or -2",
                    questionSplit = "Paper 1 (Physics, Chemistry, Maths) + Paper 2",
                    totalMarks = 360
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.NOTIFICATION_RELEASED, "Information Brochure", "March 2026", isTentative = true),
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "Registration Window", "Apr 25 - May 8, 2026", isTentative = true),
                    StageMilestone(ExamStageType.CITY_SLIP_ADMIT_CARD, "Admit Card Download", "May 18, 2026", isTentative = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "Exam Day (Paper 1 & 2)", "May 24, 2026", isTentative = false)
                ),
                currentStatus = ExamStatus.UPCOMING,
                nextMilestoneTitle = "Registration Opens Post-JEE Main",
                nextMilestoneTimestampMs = currentTimeMs + 45 * ONE_DAY_MS
            ),

            ExamItem(
                id = "bitsat-2026",
                slug = "bitsat-2026",
                fullName = "BITS Admission Test (BITSAT) 2026",
                shortCode = "BITSAT",
                category = ExamCategory.ENGINEERING,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCMB),
                conductingBody = "BITS Pilani",
                officialWebsite = "https://bitsadmission.com",
                applicationUrl = "https://bitsadmission.com/bitsat2026",
                eligibilitySummary = "Minimum 75% aggregate in PCM in 12th and at least 60% in each individual subject.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "75% aggregate in Physics, Chemistry, Math + 60% in each",
                    ageLimits = "Passed 12th in 2025 or appearing in 2026",
                    compulsorySubjects = "Physics, Chemistry, Mathematics & English proficiency",
                    attemptLimit = "Appearing batch or immediate previous year only"
                ),
                fees = listOf(
                    FeeItem("Male (Both Sessions)", "₹5,400"),
                    FeeItem("Female (Both Sessions)", "₹4,400"),
                    FeeItem("Single Session Only", "₹3,400")
                ),
                pattern = ExamPatternInfo(
                    durationMinutes = 180,
                    mode = "CBT (130 Questions + 12 Bonus Questions)",
                    markingScheme = "+3 for correct, -1 for incorrect",
                    questionSplit = "Phys 30, Chem 30, Eng 10, LR 20, Math 40",
                    totalMarks = 390
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.NOTIFICATION_RELEASED, "Information Bulletin", "Jan 10, 2026", isCompleted = true),
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "Session 1 Applications", "Jan 15 - Mar 18, 2026", isCurrent = true),
                    StageMilestone(ExamStageType.CORRECTION_WINDOW, "Slot Booking Window", "Apr 28 - May 3, 2026", isTentative = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "Session 1 Exam Dates", "May 20 - May 25, 2026", isTentative = true)
                ),
                currentStatus = ExamStatus.REGISTRATION_OPEN,
                nextMilestoneTitle = "Session 1 Registration Deadline",
                nextMilestoneTimestampMs = currentTimeMs + 14 * ONE_DAY_MS + 8 * ONE_HOUR_MS
            ),

            ExamItem(
                id = "viteee-2026",
                slug = "viteee-2026",
                fullName = "VIT Engineering Entrance Examination 2026",
                shortCode = "VITEEE",
                category = ExamCategory.ENGINEERING,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCMB),
                conductingBody = "Vellore Institute of Technology",
                officialWebsite = "https://vit.ac.in",
                applicationUrl = "https://viteee.vit.ac.in",
                eligibilitySummary = "60% aggregate in PCM/PCB in Class 12 (50% for SC/ST and North-East states).",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "60% aggregate in PCM/PCB (50% for reserved categories)",
                    ageLimits = "Born on or after July 1, 2004",
                    compulsorySubjects = "Physics, Chemistry, Maths/Biology + English & Aptitude"
                ),
                fees = listOf(
                    FeeItem("All Categories Application Fee", "₹1,350")
                ),
                pattern = ExamPatternInfo(
                    durationMinutes = 150,
                    mode = "CBT (No Negative Marking)",
                    markingScheme = "+1 for correct, 0 for incorrect",
                    questionSplit = "Math/Bio 40, Phys 35, Chem 35, Eng 5, Aptitude 10",
                    totalMarks = 125
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "Online Registration", "Closing in 36 Hours", endTimestampMs = currentTimeMs + 36 * ONE_HOUR_MS, isCurrent = true),
                    StageMilestone(ExamStageType.CITY_SLIP_ADMIT_CARD, "OTBS Slot Booking", "Apr 10, 2026", isTentative = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "Examination Dates", "Apr 21 - Apr 27, 2026", isTentative = true)
                ),
                currentStatus = ExamStatus.LAST_48_HOURS,
                nextMilestoneTitle = "Registration Portal Shuts at 11:59 PM",
                nextMilestoneTimestampMs = currentTimeMs + 36 * ONE_HOUR_MS
            ),

            ExamItem(
                id = "srmjeee-2026",
                slug = "srmjeee-2026",
                fullName = "SRM Joint Engineering Entrance Examination 2026",
                shortCode = "SRMJEEE",
                category = ExamCategory.ENGINEERING,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCMB),
                conductingBody = "SRM Institute of Science and Technology",
                officialWebsite = "https://srmist.edu.in",
                applicationUrl = "https://applications.srmist.edu.in",
                eligibilitySummary = "Minimum 50% aggregate in PCM/PCB in Class 12.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "50% aggregate in PCM in 12th Board",
                    ageLimits = "16 to 21 years of age",
                    compulsorySubjects = "Physics, Chemistry, Mathematics/Biology"
                ),
                fees = listOf(FeeItem("Phase 1 & 2 Application", "₹1,200 per phase")),
                pattern = ExamPatternInfo(
                    durationMinutes = 150,
                    mode = "Remote Proctored Online Mode",
                    markingScheme = "+1 for correct, No negative marking",
                    questionSplit = "Math 40, Phys 35, Chem 35, Eng 5, Aptitude 10",
                    totalMarks = 125
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "Phase 1 Application", "Live Now", isCurrent = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "Phase 1 Exam", "Apr 17 - Apr 22, 2026", isTentative = true)
                ),
                currentStatus = ExamStatus.REGISTRATION_OPEN,
                nextMilestoneTitle = "Phase 1 Deadline",
                nextMilestoneTimestampMs = currentTimeMs + 21 * ONE_DAY_MS
            ),

            ExamItem(
                id = "met-2026",
                slug = "met-2026",
                fullName = "Manipal Entrance Test (MET) 2026",
                shortCode = "MET",
                category = ExamCategory.ENGINEERING,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCMB),
                conductingBody = "Manipal Academy of Higher Education (MAHE)",
                officialWebsite = "https://manipal.edu",
                applicationUrl = "https://manipal.edu/admissions/met-apply",
                eligibilitySummary = "Pass 10+2 with minimum 50% marks in PCM.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "50% aggregate in Physics, Mathematics & optional subject",
                    ageLimits = "No age barrier",
                    compulsorySubjects = "Physics, Math, Chemistry/Comp Sci"
                ),
                fees = listOf(FeeItem("Application + Entrance Exam", "₹2,000")),
                pattern = ExamPatternInfo(
                    durationMinutes = 120,
                    mode = "CBT (60 Questions: MCQ + Numerical)",
                    markingScheme = "+4 for correct, -1 for MCQ incorrect",
                    questionSplit = "Math 20, Phys 15, Chem 15, Eng 10",
                    totalMarks = 240
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "Phase 1 Applications", "Closing soon", isCurrent = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "MET Attempt 1", "Apr 18 - Apr 20, 2026", isTentative = true)
                ),
                currentStatus = ExamStatus.REGISTRATION_OPEN,
                nextMilestoneTitle = "Phase 1 Registration Closes",
                nextMilestoneTimestampMs = currentTimeMs + 8 * ONE_DAY_MS
            ),

            ExamItem(
                id = "aeee-2026",
                slug = "aeee-2026",
                fullName = "Amrita Engineering Entrance Examination 2026",
                shortCode = "AEEE",
                category = ExamCategory.ENGINEERING,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCMB),
                conductingBody = "Amrita Vishwa Vidyapeetham",
                officialWebsite = "https://amrita.edu",
                applicationUrl = "https://aoap.amrita.edu/aeee",
                eligibilitySummary = "Minimum 60% aggregate in PCM with at least 55% in each subject.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "60% aggregate in PCM, 55% each",
                    ageLimits = "Born on or after July 1, 2004",
                    compulsorySubjects = "Physics, Chemistry and Mathematics"
                ),
                fees = listOf(FeeItem("AEEE Candidate Fee", "₹1,200")),
                pattern = ExamPatternInfo(
                    durationMinutes = 150,
                    mode = "CBT (100 Questions)",
                    markingScheme = "+3 for correct, -1 for incorrect",
                    questionSplit = "Math 40, Phys 30, Chem 25, Eng 5",
                    totalMarks = 300
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "Phase 2 Registrations", "Open", isCurrent = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "Phase 2 Exam Window", "May 10 - May 14, 2026", isTentative = true)
                ),
                currentStatus = ExamStatus.REGISTRATION_OPEN,
                nextMilestoneTitle = "Phase 2 Application Closes",
                nextMilestoneTimestampMs = currentTimeMs + 18 * ONE_DAY_MS
            ),

            // =========================================================================
            // 2. MEDICAL & ALLIED SCIENCES (PCB)
            // =========================================================================
            ExamItem(
                id = "neet-ug-2026",
                slug = "neet-ug-2026",
                fullName = "National Eligibility cum Entrance Test (UG) 2026",
                shortCode = "NEET-UG",
                category = ExamCategory.MEDICAL,
                streamEligibility = listOf(StreamType.PCB, StreamType.PCMB),
                conductingBody = "National Testing Agency (NTA)",
                officialWebsite = "https://neet.nta.online",
                applicationUrl = "https://neet.nta.online/registration",
                eligibilitySummary = "12th with PCB & English. Min 50% for Gen (40% SC/ST/OBC). Minimum 17 years old.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "50% aggregate in PCB/Biotech (40% for SC/ST/OBC)",
                    ageLimits = "Must complete 17 years of age on or before Dec 31 of admission year. No upper age limit.",
                    compulsorySubjects = "Physics, Chemistry, Biology/Biotechnology & English",
                    attemptLimit = "No limit on number of attempts"
                ),
                fees = listOf(
                    FeeItem("General Category", "₹1,700"),
                    FeeItem("General-EWS / OBC-NCL", "₹1,600"),
                    FeeItem("SC / ST / PwBD / Third Gender", "₹1,000")
                ),
                pattern = ExamPatternInfo(
                    durationMinutes = 200,
                    mode = "Pen & Paper (OMR Sheet Mode)",
                    markingScheme = "+4 for correct, -1 for incorrect",
                    questionSplit = "200 Questions (Phys 45, Chem 45, Botany 45, Zoology 45). Attempt 180.",
                    totalMarks = 720
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.NOTIFICATION_RELEASED, "Information Bulletin Out", "Feb 8, 2026", isCompleted = true),
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "Registration Window", "Feb 10 - Mar 16, 2026", isCurrent = true),
                    StageMilestone(ExamStageType.CORRECTION_WINDOW, "Correction Facility", "Mar 20 - Mar 22, 2026", isTentative = true),
                    StageMilestone(ExamStageType.CITY_SLIP_ADMIT_CARD, "City Intimation & Admit Card", "Apr 26, 2026", isTentative = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "NEET UG Exam Day (Sunday)", "May 3, 2026", isTentative = false),
                    StageMilestone(ExamStageType.FINAL_RESULT_COUNSELING, "Results & MCC All India Counseling", "June 14, 2026", isTentative = true)
                ),
                currentStatus = ExamStatus.REGISTRATION_OPEN,
                nextMilestoneTitle = "Registration Deadline (9:00 PM)",
                nextMilestoneTimestampMs = currentTimeMs + 11 * ONE_DAY_MS + 4 * ONE_HOUR_MS
            ),

            ExamItem(
                id = "icar-aieea-2026",
                slug = "icar-aieea-ug-2026",
                fullName = "ICAR All India Entrance Examination (AIEEA-UG) via CUET",
                shortCode = "ICAR AIEEA",
                category = ExamCategory.MEDICAL,
                streamEligibility = listOf(StreamType.PCB, StreamType.PCMB, StreamType.PCM),
                conductingBody = "ICAR / National Testing Agency",
                officialWebsite = "https://icar.org.in",
                applicationUrl = "https://cuetug.nta.nic.in",
                eligibilitySummary = "Passed 10+2 with PCB/PCM/Agriculture with 50% marks (40% SC/ST). For B.Sc Agriculture & Allied.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "50% aggregate in 10+2 (40% for SC/ST/PwD)",
                    ageLimits = "At least 16 years as of August 31",
                    compulsorySubjects = "Physics, Chemistry, and Biology/Agriculture or Mathematics"
                ),
                fees = listOf(
                    FeeItem("General Category (3 subjects)", "₹1,000"),
                    FeeItem("OBC-NCL / EWS", "₹900"),
                    FeeItem("SC / ST / PwD", "₹500")
                ),
                pattern = ExamPatternInfo(
                    durationMinutes = 180,
                    mode = "CBT (Domain Subjects via CUET-UG)",
                    markingScheme = "+5 for correct, -1 for incorrect",
                    questionSplit = "50 questions per domain (Attempt 40)",
                    totalMarks = 600
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "CUET-ICAR Application", "Open Now", isCurrent = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "Entrance Exam Window", "May 15 - May 28, 2026", isTentative = true)
                ),
                currentStatus = ExamStatus.REGISTRATION_OPEN,
                nextMilestoneTitle = "Application Deadline",
                nextMilestoneTimestampMs = currentTimeMs + 16 * ONE_DAY_MS
            ),

            ExamItem(
                id = "aiims-paramedical-2026",
                slug = "aiims-paramedical-nursing-2026",
                fullName = "AIIMS B.Sc. (Hons) Nursing & Paramedical Entrance 2026",
                shortCode = "AIIMS Nursing",
                category = ExamCategory.MEDICAL,
                streamEligibility = listOf(StreamType.PCB, StreamType.PCMB),
                conductingBody = "All India Institute of Medical Sciences (AIIMS New Delhi)",
                officialWebsite = "https://aiimsexams.ac.in",
                applicationUrl = "https://aiimsexams.ac.in/nursing-portal",
                eligibilitySummary = "12th with PCB and English. Min 55% marks for Gen/OBC (50% SC/ST). Nursing for female candidates.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "55% for Gen/OBC/EWS, 50% for SC/ST in English, Physics, Chemistry, Biology",
                    ageLimits = "Minimum 17 years as of Dec 31, 2026",
                    compulsorySubjects = "English, Physics, Chemistry and Biology"
                ),
                fees = listOf(
                    FeeItem("General / OBC Candidates", "₹2,000"),
                    FeeItem("SC / ST / EWS", "₹1,600"),
                    FeeItem("PwD Candidates", "Exempted")
                ),
                pattern = ExamPatternInfo(
                    durationMinutes = 120,
                    mode = "CBT (100 MCQs)",
                    markingScheme = "+1 for correct, -1/3 for incorrect",
                    questionSplit = "Phys 30, Chem 30, Bio 30, Gen Knowledge 10",
                    totalMarks = 100
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "Basic Registration (PAAR)", "Closing in 42 Hours", endTimestampMs = currentTimeMs + 42 * ONE_HOUR_MS, isCurrent = true),
                    StageMilestone(ExamStageType.CORRECTION_WINDOW, "Code Generation for Final Reg", "Apr 5, 2026", isTentative = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "AIIMS Nursing Exam Day", "June 8, 2026", isTentative = true)
                ),
                currentStatus = ExamStatus.LAST_48_HOURS,
                nextMilestoneTitle = "Basic PAAR Registration Closes",
                nextMilestoneTimestampMs = currentTimeMs + 42 * ONE_HOUR_MS
            ),

            // =========================================================================
            // 3. DEFENSE & ARMED FORCES (PCM/PCMB)
            // =========================================================================
            ExamItem(
                id = "nda-na-2026",
                slug = "nda-na-1-2026",
                fullName = "National Defence Academy & Naval Academy Examination (I) 2026",
                shortCode = "NDA & NA (I)",
                category = ExamCategory.DEFENSE,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCMB),
                conductingBody = "Union Public Service Commission (UPSC)",
                officialWebsite = "https://upsc.gov.in",
                applicationUrl = "https://upsconline.nic.in",
                eligibilitySummary = "12th Pass/Appearing with Physics & Math (for Air Force/Navy). Unmarried male/female candidates.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "Pass in 12th Board with Physics and Mathematics",
                    ageLimits = "Born not earlier than July 2, 2007 and not later than July 1, 2010 (approx 16.5 - 19.5 yrs)",
                    compulsorySubjects = "Physics and Math for IAF & Navy; Any stream for Army wing",
                    attemptLimit = "Strict age-bracket criteria"
                ),
                fees = listOf(
                    FeeItem("General / OBC Male", "₹100"),
                    FeeItem("SC / ST / Female / Wards of JCO/NCO/OR", "Exempted (₹0)")
                ),
                pattern = ExamPatternInfo(
                    durationMinutes = 300,
                    mode = "Pen & Paper (OMR Offline)",
                    markingScheme = "Math: +2.5 / -0.83; GAT: +4 / -1.33",
                    questionSplit = "Paper 1 Math (120 Qs, 300 Marks) + Paper 2 GAT (150 Qs, 600 Marks)",
                    totalMarks = 900
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.NOTIFICATION_RELEASED, "UPSC NDA Notification", "Dec 20, 2025", isCompleted = true),
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "OTR Application Window", "Closed", isCompleted = true),
                    StageMilestone(ExamStageType.CITY_SLIP_ADMIT_CARD, "Admit Card Live on UPSC Portal", "Live Now", isCompleted = true, isCurrent = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "NDA (I) Written Examination", "Apr 12, 2026", endTimestampMs = currentTimeMs + 38 * ONE_DAY_MS),
                    StageMilestone(ExamStageType.FINAL_RESULT_COUNSELING, "SSB Interview Calls", "July 2026", isTentative = true)
                ),
                currentStatus = ExamStatus.ADMIT_CARD_LIVE,
                nextMilestoneTitle = "Written Examination Day",
                nextMilestoneTimestampMs = currentTimeMs + 38 * ONE_DAY_MS
            ),

            ExamItem(
                id = "army-tes-2026",
                slug = "indian-army-tes-54",
                fullName = "Indian Army 10+2 Technical Entry Scheme (TES-54)",
                shortCode = "Army TES",
                category = ExamCategory.DEFENSE,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCMB),
                conductingBody = "Directorate General of Recruiting, Indian Army",
                officialWebsite = "https://joinindianarmy.nic.in",
                applicationUrl = "https://joinindianarmy.nic.in/login.htm",
                eligibilitySummary = "10+2 with PCM minimum 60% aggregate AND appeared in JEE Main 2026. Permanent Commission in Army.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "60% aggregate in Physics, Chemistry, and Mathematics",
                    ageLimits = "16½ to 19½ years as on the first day of the month in which the course begins",
                    compulsorySubjects = "Physics, Chemistry, and Math + JEE Main CRL Rank mandatory"
                ),
                fees = listOf(
                    FeeItem("All Categories", "₹0 (Free Application)")
                ),
                pattern = ExamPatternInfo(
                    durationMinutes = 0,
                    mode = "Direct SSB Shortlisting based on JEE Main CRL Rank",
                    markingScheme = "Stage 1 (OIR + PPDT) & Stage 2 (Psych, GTO, Interview)",
                    questionSplit = "5-Day SSB Selection Board Process",
                    totalMarks = 900
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.NOTIFICATION_RELEASED, "TES Course Notification", "May 2026", isTentative = true),
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "Online Application on JoinArmy", "June 2026", isTentative = true),
                    StageMilestone(ExamStageType.FINAL_RESULT_COUNSELING, "SSB Dates & Center Allotment", "Aug 2026", isTentative = true)
                ),
                currentStatus = ExamStatus.UPCOMING,
                nextMilestoneTitle = "Application Opens Post-JEE CRL",
                nextMilestoneTimestampMs = currentTimeMs + 65 * ONE_DAY_MS
            ),

            ExamItem(
                id = "afcat-airforce-2026",
                slug = "iaf-direct-airmen-and-schemes",
                fullName = "Indian Air Force 10+2 Technical Entry & AFCAT Entry Scheme",
                shortCode = "IAF Entry",
                category = ExamCategory.DEFENSE,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCMB),
                conductingBody = "Indian Air Force (CDAC)",
                officialWebsite = "https://afcat.cdac.in",
                applicationUrl = "https://afcat.cdac.in/afcat-apply",
                eligibilitySummary = "50% in 10+2 with 50% in English + Physics & Maths for Technical Branch / Pilot Entry.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "50% aggregate in 12th + 50% in English & Math/Phys",
                    ageLimits = "17 to 21 years",
                    compulsorySubjects = "Physics and Mathematics"
                ),
                fees = listOf(FeeItem("Application Fee", "₹550")),
                pattern = ExamPatternInfo(
                    durationMinutes = 120,
                    mode = "CBT (100 Questions) + AFSB",
                    markingScheme = "+3 for correct, -1 for incorrect",
                    questionSplit = "Verbal Ability, Numerical Ability, Reasoning, Military Aptitude",
                    totalMarks = 300
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "Registration Window", "Opens Soon", isTentative = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "Exam Dates", "Aug 2026", isTentative = true)
                ),
                currentStatus = ExamStatus.UPCOMING,
                nextMilestoneTitle = "Notification Release",
                nextMilestoneTimestampMs = currentTimeMs + 32 * ONE_DAY_MS
            ),

            // =========================================================================
            // 4. RESEARCH & PURE SCIENCES (PCMB)
            // =========================================================================
            ExamItem(
                id = "iiser-iat-2026",
                slug = "iiser-iat-2026",
                fullName = "IISER Aptitude Test (IAT) 2026 (BS-MS Dual Degree)",
                shortCode = "IISER IAT",
                category = ExamCategory.RESEARCH,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCB, StreamType.PCMB),
                conductingBody = "Joint Admissions Committee, IISERs",
                officialWebsite = "https://iiseradmission.ac.in",
                applicationUrl = "https://iiseradmission.ac.in/apply",
                eligibilitySummary = "Passed 10+2 with at least three subjects among Biology, Chemistry, Mathematics and Physics. Min 60% (55% SC/ST).",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "60% marks in 12th Board for General/OBC (55% for SC/ST/PwD)",
                    ageLimits = "Passed 12th in 2024, 2025, or appearing in 2026",
                    compulsorySubjects = "At least three: Physics, Chemistry, Math, Biology"
                ),
                fees = listOf(
                    FeeItem("General / EWS / OBC-NCL", "₹2,000"),
                    FeeItem("SC / ST / PwD / Kashmiri Migrants", "₹1,000")
                ),
                pattern = ExamPatternInfo(
                    durationMinutes = 180,
                    mode = "CBT (60 Questions: 15 each in PCMB)",
                    markingScheme = "+4 for correct, -1 for incorrect",
                    questionSplit = "Physics 15, Chemistry 15, Mathematics 15, Biology 15",
                    totalMarks = 240
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.NOTIFICATION_RELEASED, "Official Bulletin Out", "Feb 14, 2026", isCompleted = true),
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "Registration Window", "Mar 1 - Apr 15, 2026", isCurrent = true),
                    StageMilestone(ExamStageType.CITY_SLIP_ADMIT_CARD, "Admit Card Download", "May 25, 2026", isTentative = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "IAT 2026 Examination Date", "June 7, 2026", isTentative = false)
                ),
                currentStatus = ExamStatus.REGISTRATION_OPEN,
                nextMilestoneTitle = "Application Deadline",
                nextMilestoneTimestampMs = currentTimeMs + 22 * ONE_DAY_MS
            ),

            ExamItem(
                id = "nest-2026",
                slug = "nest-2026",
                fullName = "National Entrance Screening Test (NEST) 2026 (NISER & UM-DAE CEBS)",
                shortCode = "NEST",
                category = ExamCategory.RESEARCH,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCB, StreamType.PCMB),
                conductingBody = "NISER Bhubaneswar & UM-DAE CEBS Mumbai",
                officialWebsite = "https://nestexam.in",
                applicationUrl = "https://nestexam.in/registration-2026",
                eligibilitySummary = "Minimum 60% aggregate in 12th (55% for SC/ST/PwD). Direct path to Department of Atomic Energy institutions.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "60% aggregate in 10+2 (55% for SC/ST/PwD)",
                    ageLimits = "Born on or after August 1, 2006 (5 years relaxation for SC/ST)",
                    compulsorySubjects = "Passed 12th in science stream with any combination of PCMB"
                ),
                fees = listOf(
                    FeeItem("General / OBC Male", "₹1,400"),
                    FeeItem("Female / SC / ST / PwD", "₹700")
                ),
                pattern = ExamPatternInfo(
                    durationMinutes = 210,
                    mode = "CBT (4 Sections: Bio, Chem, Math, Phys. Best 3 counted)",
                    markingScheme = "+2.5 / -1 for Single correct; Partial marking for Multi-correct",
                    questionSplit = "17 Questions per section (12 MCQ + 5 MSQ). Total 68 questions.",
                    totalMarks = 180
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "Online Application", "Feb 20 - May 10, 2026", isCurrent = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "Exam Day (2 Sessions)", "June 21, 2026", isTentative = false)
                ),
                currentStatus = ExamStatus.REGISTRATION_OPEN,
                nextMilestoneTitle = "Registration Portal Closing",
                nextMilestoneTimestampMs = currentTimeMs + 40 * ONE_DAY_MS
            ),

            ExamItem(
                id = "isi-admission-2026",
                slug = "isi-admission-test-2026",
                fullName = "Indian Statistical Institute Admission Test (B.Stat / B.Math)",
                shortCode = "ISI Test",
                category = ExamCategory.RESEARCH,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCMB),
                conductingBody = "Indian Statistical Institute (Kolkata/Bengaluru)",
                officialWebsite = "https://isical.ac.in",
                applicationUrl = "https://isical.ac.in/~admission",
                eligibilitySummary = "Successful completion of 10+2 with Mathematics and English. Full stipend & tuition waiver for admitted students.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "Pass in 12th Board with Mathematics & English",
                    ageLimits = "No age limit",
                    compulsorySubjects = "Mathematics & English"
                ),
                fees = listOf(
                    FeeItem("General Male Candidates", "₹1,500"),
                    FeeItem("Female / OBC / EWS", "₹1,000"),
                    FeeItem("SC / ST / PwD", "₹750")
                ),
                pattern = ExamPatternInfo(
                    durationMinutes = 240,
                    mode = "Offline (Forenoon: MCQ; Afternoon: Subjective Descriptive Math)",
                    markingScheme = "Forenoon: +4 / -1; Afternoon: Proofs & problem solving",
                    questionSplit = "UGA (30 MCQs) + UGB (8 Subjective Math Questions)",
                    totalMarks = 200
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "Application Window", "Mar 5 - Apr 10, 2026", isCurrent = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "ISI Written Test Day", "May 10, 2026", isTentative = false)
                ),
                currentStatus = ExamStatus.REGISTRATION_OPEN,
                nextMilestoneTitle = "Application Deadline",
                nextMilestoneTimestampMs = currentTimeMs + 19 * ONE_DAY_MS
            ),

            ExamItem(
                id = "cmi-entrance-2026",
                slug = "cmi-entrance-exam-2026",
                fullName = "Chennai Mathematical Institute Entrance Exam (B.Sc Hons Maths & CS)",
                shortCode = "CMI Entrance",
                category = ExamCategory.RESEARCH,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCMB),
                conductingBody = "Chennai Mathematical Institute (CMI)",
                officialWebsite = "https://cmi.ac.in",
                applicationUrl = "https://cmi.ac.in/admissions",
                eligibilitySummary = "12th Pass/Appearing with Mathematics. Olympiad qualifiers (INMO) directly exempted from written test.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "12th Pass with Mathematics",
                    ageLimits = "No age restriction",
                    compulsorySubjects = "Mathematics"
                ),
                fees = listOf(FeeItem("All Applicants", "₹1,000")),
                pattern = ExamPatternInfo(
                    durationMinutes = 180,
                    mode = "Pen & Paper (Part A: Objective, Part B: Proof-based Math)",
                    markingScheme = "Part A: 40 marks; Part B: 60 marks (rigorous mathematical proofs)",
                    questionSplit = "Part A (10 questions) + Part B (6 subjective questions)",
                    totalMarks = 100
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "Online Registration", "Mar 1 - Apr 18, 2026", isCurrent = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "CMI Entrance Day", "May 17, 2026", isTentative = false)
                ),
                currentStatus = ExamStatus.REGISTRATION_OPEN,
                nextMilestoneTitle = "Registration Deadline",
                nextMilestoneTimestampMs = currentTimeMs + 25 * ONE_DAY_MS
            ),

            // =========================================================================
            // 5. STATE CETS & REGIONAL ENTRANCES
            // =========================================================================
            ExamItem(
                id = "mht-cet-2026",
                slug = "mht-cet-2026",
                fullName = "Maharashtra Common Entrance Test (MHT CET) 2026",
                shortCode = "MHT CET",
                category = ExamCategory.STATE_CET,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCB, StreamType.PCMB),
                conductingBody = "State Common Entrance Test Cell, Maharashtra",
                officialWebsite = "https://cetcell.mahacet.org",
                applicationUrl = "https://cetcell.mahacet.org/portal",
                eligibilitySummary = "12th with PCM or PCB with 45% marks (40% for MH Backward classes/PwD). Domicile required for state quota.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "45% for Open category (40% for MH reserved categories)",
                    ageLimits = "No upper age limit",
                    compulsorySubjects = "Physics, Chemistry, and Mathematics (PCM group) or Biology (PCB group)"
                ),
                fees = listOf(
                    FeeItem("General / Outside Maharashtra (OMS)", "₹1,000"),
                    FeeItem("Reserved Categories (MH Domicile)", "₹800"),
                    FeeItem("Late Fee Window Surcharge", "+₹500")
                ),
                pattern = ExamPatternInfo(
                    durationMinutes = 180,
                    mode = "CBT (No Negative Marking)",
                    markingScheme = "Math: +2 per correct; Phys/Chem/Bio: +1 per correct",
                    questionSplit = "Paper 1 (Math: 50 Qs, 100 Marks), Paper 2 (Phys 50 + Chem 50, 100 Marks)",
                    totalMarks = 200
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.NOTIFICATION_RELEASED, "Information Brochure", "Jan 12, 2026", isCompleted = true),
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "Registration Window (Regular)", "Jan 16 - Mar 8, 2026", isCompleted = true),
                    StageMilestone(ExamStageType.REGISTRATION_LATE_FEE, "Late-Fee Extension Window", "Closing in 28 Hours", endTimestampMs = currentTimeMs + 28 * ONE_HOUR_MS, isExtended = true, isCurrent = true),
                    StageMilestone(ExamStageType.CITY_SLIP_ADMIT_CARD, "Admit Card Download", "Apr 10, 2026", isTentative = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "PCM Group Exams", "Apr 16 - Apr 30, 2026", isTentative = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "PCB Group Exams", "Apr 22 - Apr 30, 2026", isTentative = true)
                ),
                stateDomicile = "Maharashtra",
                currentStatus = ExamStatus.LAST_48_HOURS,
                nextMilestoneTitle = "Late-Fee Window Shuts (11:59 PM)",
                nextMilestoneTimestampMs = currentTimeMs + 28 * ONE_HOUR_MS,
                isLateFeeApplicable = true
            ),

            ExamItem(
                id = "kcet-comedk-2026",
                slug = "kcet-comedk-2026",
                fullName = "Karnataka CET & COMEDK UGET 2026",
                shortCode = "KCET / COMEDK",
                category = ExamCategory.STATE_CET,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCMB),
                conductingBody = "Karnataka Examinations Authority (KEA) & COMEDK",
                officialWebsite = "https://cetonline.karnataka.gov.in/kea",
                applicationUrl = "https://cetonline.karnataka.gov.in/kea/ugcet2026",
                eligibilitySummary = "45% in 10+2 with PCM (40% for Karnataka SC/ST/OBC). KCET for Karnataka Domicile; COMEDK open All-India.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "45% in PCM (40% for Karnataka reserved category)",
                    ageLimits = "No age limit",
                    compulsorySubjects = "Physics, Chemistry and Mathematics"
                ),
                fees = listOf(
                    FeeItem("KCET Karnataka Candidates", "₹500"),
                    FeeItem("KCET Outside Karnataka (OMS)", "₹750"),
                    FeeItem("COMEDK UGET (Both Engg & Arch)", "₹1,800")
                ),
                pattern = ExamPatternInfo(
                    durationMinutes = 180,
                    mode = "KCET: Offline OMR; COMEDK: CBT (Online)",
                    markingScheme = "+1 for correct, No negative marking",
                    questionSplit = "Biology (60 Qs), Mathematics (60 Qs), Physics (60 Qs), Chemistry (60 Qs)",
                    totalMarks = 180
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "Online Application (UGCET)", "Closing Soon", isCurrent = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "KCET Exam Dates", "Apr 18 - Apr 19, 2026", isTentative = false),
                    StageMilestone(ExamStageType.EXAM_DATES, "COMEDK Exam Day", "May 12, 2026", isTentative = false)
                ),
                stateDomicile = "Karnataka",
                currentStatus = ExamStatus.REGISTRATION_OPEN,
                nextMilestoneTitle = "Application Window Deadline",
                nextMilestoneTimestampMs = currentTimeMs + 7 * ONE_DAY_MS
            ),

            ExamItem(
                id = "wbjee-2026",
                slug = "wbjee-2026",
                fullName = "West Bengal Joint Entrance Examination 2026",
                shortCode = "WBJEE",
                category = ExamCategory.STATE_CET,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCMB),
                conductingBody = "West Bengal Joint Entrance Examinations Board (WBJEEB)",
                officialWebsite = "https://wbjeeb.nic.in",
                applicationUrl = "https://wbjeeb.nic.in/wbjee",
                eligibilitySummary = "45% in PCM (40% for SC/ST/OBC-A/B/PwD). Minimum 17 years old. Jadavpur University & WB engineering colleges.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "45% aggregate in PCM in Class 12 (40% for reserved category)",
                    ageLimits = "Minimum 17 years as on Dec 31, 2026. No upper limit except Marine Engg (25 yrs).",
                    compulsorySubjects = "Physics, Mathematics, and Chemistry"
                ),
                fees = listOf(
                    FeeItem("General Male Candidates", "₹500"),
                    FeeItem("General Female / Reserved Categories", "₹400")
                ),
                pattern = ExamPatternInfo(
                    durationMinutes = 240,
                    mode = "Offline Pen & Paper (OMR Sheet)",
                    markingScheme = "Cat 1: +1 / -0.25; Cat 2: +2 / -0.5; Cat 3: +2 / 0 (Multi-correct)",
                    questionSplit = "Paper 1 Math (75 Qs, 100 Marks) + Paper 2 Phys/Chem (80 Qs, 100 Marks)",
                    totalMarks = 200
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "Registration Closed", "Closed", isCompleted = true),
                    StageMilestone(ExamStageType.CITY_SLIP_ADMIT_CARD, "Admit Card Download Starts", "Apr 18, 2026", isTentative = true, isCurrent = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "WBJEE Exam Day (Sunday)", "Apr 26, 2026", isTentative = false)
                ),
                stateDomicile = "West Bengal",
                currentStatus = ExamStatus.UPCOMING,
                nextMilestoneTitle = "Admit Card Release on WBJEEB Portal",
                nextMilestoneTimestampMs = currentTimeMs + 15 * ONE_DAY_MS
            ),

            ExamItem(
                id = "tg-eapcet-2026",
                slug = "tg-eapcet-2026",
                fullName = "Telangana Engineering, Agriculture & Pharmacy CET (TG EAPCET) 2026",
                shortCode = "TG EAPCET",
                category = ExamCategory.STATE_CET,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCB, StreamType.PCMB),
                conductingBody = "JNTU Hyderabad on behalf of TGCHE",
                officialWebsite = "https://eapcet.tsche.ac.in",
                applicationUrl = "https://eapcet.tsche.ac.in/apply",
                eligibilitySummary = "45% marks in intermediate (40% for reserved categories). TS Domicile quota + Non-local quota.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "45% in group subjects in Intermediate / 10+2",
                    ageLimits = "16 years of age completed as on admission date",
                    compulsorySubjects = "Mathematics/Biology, Physics, and Chemistry"
                ),
                fees = listOf(
                    FeeItem("Engineering (E) Stream General", "₹900"),
                    FeeItem("Engineering (E) SC / ST / PwD", "₹500"),
                    FeeItem("Both E & AM Streams", "₹1,800")
                ),
                pattern = ExamPatternInfo(
                    durationMinutes = 180,
                    mode = "CBT (160 Questions, No Negative Marking)",
                    markingScheme = "+1 for correct answer, 0 for incorrect",
                    questionSplit = "Math 80 (or Bio 80), Physics 40, Chemistry 40",
                    totalMarks = 160
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "Online Application Window", "Mar 1 - Apr 6, 2026", isCurrent = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "Exam Window", "May 9 - May 13, 2026", isTentative = true)
                ),
                stateDomicile = "Telangana",
                currentStatus = ExamStatus.REGISTRATION_OPEN,
                nextMilestoneTitle = "Application Without Late Fee Deadline",
                nextMilestoneTimestampMs = currentTimeMs + 17 * ONE_DAY_MS
            ),

            ExamItem(
                id = "ap-eapcet-2026",
                slug = "ap-eapcet-2026",
                fullName = "Andhra Pradesh Engineering, Agriculture and Pharmacy CET 2026",
                shortCode = "AP EAPCET",
                category = ExamCategory.STATE_CET,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCB, StreamType.PCMB),
                conductingBody = "JNTU Kakinada on behalf of APSCHE",
                officialWebsite = "https://cets.apsche.ap.gov.in",
                applicationUrl = "https://cets.apsche.ap.gov.in/eapcet",
                eligibilitySummary = "45% in intermediate with PCM/PCB (40% reserved). State quota admissions in AP institutions.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "45% in 10+2 / Intermediate (40% for reserved categories)",
                    ageLimits = "16 years of age completed",
                    compulsorySubjects = "Physics, Chemistry, and Mathematics/Biology"
                ),
                fees = listOf(
                    FeeItem("Open Category (OC)", "₹600"),
                    FeeItem("BC Category", "₹550"),
                    FeeItem("SC / ST Candidates", "₹500")
                ),
                pattern = ExamPatternInfo(
                    durationMinutes = 180,
                    mode = "CBT (160 Questions)",
                    markingScheme = "+1 for correct, No negative marking",
                    questionSplit = "Math 80, Physics 40, Chemistry 40",
                    totalMarks = 160
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "Application Portal Live", "Open Now", isCurrent = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "Engineering Stream Exam", "May 16 - May 22, 2026", isTentative = true)
                ),
                stateDomicile = "Andhra Pradesh",
                currentStatus = ExamStatus.REGISTRATION_OPEN,
                nextMilestoneTitle = "Regular Registration Deadline",
                nextMilestoneTimestampMs = currentTimeMs + 20 * ONE_DAY_MS
            ),

            ExamItem(
                id = "keam-2026",
                slug = "keam-2026",
                fullName = "Kerala Engineering Architecture Medical Entrance Exam 2026",
                shortCode = "KEAM",
                category = ExamCategory.STATE_CET,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCMB),
                conductingBody = "Commissioner for Entrance Examinations (CEE Kerala)",
                officialWebsite = "https://cee.kerala.gov.in",
                applicationUrl = "https://cee.kerala.gov.in/keamonline2026",
                eligibilitySummary = "45% marks in PCM in Plus Two. Native Kerala candidates eligible for state communal reservations.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "45% aggregate in Physics, Chemistry, and Mathematics",
                    ageLimits = "At least 17 years completed as of Dec 31, 2026",
                    compulsorySubjects = "Physics, Mathematics, and Chemistry"
                ),
                fees = listOf(
                    FeeItem("General Category", "₹875"),
                    FeeItem("SC Candidates", "₹375"),
                    FeeItem("ST Candidates", "Exempted (₹0)")
                ),
                pattern = ExamPatternInfo(
                    durationMinutes = 180,
                    mode = "CBT (Computer Based Test from 2025 onwards)",
                    markingScheme = "+4 for correct, -1 for incorrect",
                    questionSplit = "Math 75 Qs, Phys 45 Qs, Chem 30 Qs. Total 150 Qs.",
                    totalMarks = 600
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "Online Submission", "Closing in 40 Hours", endTimestampMs = currentTimeMs + 40 * ONE_HOUR_MS, isCurrent = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "KEAM CBT Exam Session", "June 5 - June 9, 2026", isTentative = true)
                ),
                stateDomicile = "Kerala",
                currentStatus = ExamStatus.LAST_48_HOURS,
                nextMilestoneTitle = "Certificate & Application Upload Closes",
                nextMilestoneTimestampMs = currentTimeMs + 40 * ONE_HOUR_MS
            ),

            ExamItem(
                id = "gujcet-2026",
                slug = "gujcet-2026",
                fullName = "Gujarat Common Entrance Test (GUJCET) 2026",
                shortCode = "GUJCET",
                category = ExamCategory.STATE_CET,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCB, StreamType.PCMB),
                conductingBody = "Gujarat Secondary and Higher Secondary Education Board (GSEB)",
                officialWebsite = "https://gseb.org",
                applicationUrl = "https://gujcet.gseb.org",
                eligibilitySummary = "Passed Class 12 with PCM/PCB with 45% (40% for SC/ST/SEBC/EWS). 50% board weightage + 50% GUJCET.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "45% in PCM/PCB (40% for reserved categories)",
                    ageLimits = "No age barrier",
                    compulsorySubjects = "Physics, Chemistry and Maths or Biology"
                ),
                fees = listOf(FeeItem("Application Fee (All Categories)", "₹350")),
                pattern = ExamPatternInfo(
                    durationMinutes = 180,
                    mode = "Pen & Paper (OMR Offline)",
                    markingScheme = "+1 for correct, -0.25 for incorrect",
                    questionSplit = "Physics (40 Qs), Chemistry (40 Qs), Math/Bio (40 Qs)",
                    totalMarks = 120
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.CITY_SLIP_ADMIT_CARD, "Hall Ticket Available", "Live Now", isCompleted = true, isCurrent = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "GUJCET Exam Date", "Mar 23, 2026", endTimestampMs = currentTimeMs + 5 * ONE_DAY_MS)
                ),
                stateDomicile = "Gujarat",
                currentStatus = ExamStatus.ADMIT_CARD_LIVE,
                nextMilestoneTitle = "Exam Date (Hall Ticket Released)",
                nextMilestoneTimestampMs = currentTimeMs + 5 * ONE_DAY_MS
            ),

            ExamItem(
                id = "ojee-2026",
                slug = "ojee-2026",
                fullName = "Odisha Joint Entrance Examination 2026",
                shortCode = "OJEE",
                category = ExamCategory.STATE_CET,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCB, StreamType.PCMB),
                conductingBody = "OJEE Committee, Government of Odisha",
                officialWebsite = "https://ojee.nic.in",
                applicationUrl = "https://ojee.nic.in/ojee-apply",
                eligibilitySummary = "45% marks in 10+2 with PCM/PCB. State quota for B.Pharm, Integrated MBA and Allied Engineering.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "45% in 12th Board (40% for SC/ST)",
                    ageLimits = "Minimum 17 years",
                    compulsorySubjects = "Physics and Chemistry + Math/Biology"
                ),
                fees = listOf(FeeItem("Single Course Form", "₹1,000")),
                pattern = ExamPatternInfo(
                    durationMinutes = 120,
                    mode = "CBT (Computer Based Test)",
                    markingScheme = "+4 for correct, -1 for incorrect",
                    questionSplit = "120 MCQs across Physics, Chemistry, Math/Bio",
                    totalMarks = 480
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "Registration Window", "Open", isCurrent = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "OJEE Examination Dates", "May 6 - May 10, 2026", isTentative = true)
                ),
                stateDomicile = "Odisha",
                currentStatus = ExamStatus.REGISTRATION_OPEN,
                nextMilestoneTitle = "Online Application Closes",
                nextMilestoneTimestampMs = currentTimeMs + 24 * ONE_DAY_MS
            ),

            ExamItem(
                id = "bcece-2026",
                slug = "bcece-2026",
                fullName = "Bihar Combined Entrance Competitive Examination 2026",
                shortCode = "BCECE",
                category = ExamCategory.STATE_CET,
                streamEligibility = listOf(StreamType.PCM, StreamType.PCB, StreamType.PCMB),
                conductingBody = "BCECE Board (BCECEB), Patna",
                officialWebsite = "https://bceceboard.bihar.gov.in",
                applicationUrl = "https://bceceboard.bihar.gov.in/portal",
                eligibilitySummary = "45% in 12th with PCM/PCB (40% for SC/ST/OBC). For Agriculture, Pharmacy, Paramedical and Horticulture in Bihar.",
                eligibilityDetails = EligibilityDetails(
                    min12thPercentage = "45% aggregate in 12th (40% for SC/ST/EBC/BC)",
                    ageLimits = "Minimum 17 years",
                    compulsorySubjects = "Physics, Chemistry, and Mathematics/Biology/Agriculture"
                ),
                fees = listOf(
                    FeeItem("PCM or PCB Group (Gen/BC/EBC)", "₹1,000"),
                    FeeItem("PCM or PCB Group (SC/ST/DQ)", "₹500"),
                    FeeItem("PCMB Group (All 4 subjects)", "₹1,100")
                ),
                pattern = ExamPatternInfo(
                    durationMinutes = 270,
                    mode = "Offline Pen & Paper (OMR)",
                    markingScheme = "+4 for correct, -1 for incorrect",
                    questionSplit = "100 Qs per subject (90 mins per paper: Phys, Chem, Math/Bio)",
                    totalMarks = 1200
                ),
                stages = listOf(
                    StageMilestone(ExamStageType.REGISTRATION_OPEN, "BCECE Application Window", "Closing in 46 Hours", endTimestampMs = currentTimeMs + 46 * ONE_HOUR_MS, isCurrent = true),
                    StageMilestone(ExamStageType.EXAM_DATES, "Exam Dates", "June 27 - June 28, 2026", isTentative = true)
                ),
                stateDomicile = "Bihar",
                currentStatus = ExamStatus.LAST_48_HOURS,
                nextMilestoneTitle = "Registration Portal Shuts at 11:59 PM",
                nextMilestoneTimestampMs = currentTimeMs + 46 * ONE_HOUR_MS
            )
        )
    }
}
