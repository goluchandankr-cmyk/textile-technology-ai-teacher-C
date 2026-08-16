package com.example.data.model

data class TextileCategory(
    val id: String,
    val titleHindi: String,
    val titleEnglish: String,
    val iconName: String,
    val description: String,
    val sampleQuestions: List<String>
)

data class ExamQuestionItem(
    val id: String,
    val question: String,
    val marksCategory: String, // 1M, 2M, 5M, 10M, VIVA, MCQ
    val topic: String,
    val answer: String,
    val options: List<String>? = null, // for MCQs
    val correctOptionIndex: Int? = null
)

data class TextilePracticalItem(
    val experimentNo: Int,
    val title: String,
    val subject: String,
    val aim: String,
    val apparatus: String,
    val theory: String,
    val procedure: List<String>,
    val calculationFormula: String,
    val vivaQuestions: List<Pair<String, String>>
)

object TextileSyllabusData {

    val categories = listOf(
        TextileCategory(
            id = "weaving",
            titleHindi = "वीविंग टेक्नोलॉजी और लूम्स",
            titleEnglish = "Weaving Technology & Looms",
            iconName = "weaving",
            description = "Shuttle Loom, Shuttleless (Rapier, Air Jet, Water Jet, Projectile), Warping, Sizing & Drawing-in",
            sampleQuestions = listOf(
                "Shuttle loom aur Shuttleless loom mein antar batao.",
                "Rapier loom ka working principle explain karo.",
                "Air jet loom ke advantage aur disadvantage batao.",
                "Sizing process ka kya objective hai?",
                "Warping machine kya hai aur iske types kaun se hain?"
            )
        ),
        TextileCategory(
            id = "spinning",
            titleHindi = "स्पिनिंग टेक्नोलॉजी और यार्न मैन्युफैक्चरिंग",
            titleEnglish = "Spinning Tech & Yarn Mfg",
            iconName = "spinning",
            description = "Blow Room, Ginning & Baling, Opening, Cleaning, Winding, Carding, Drawframe & Ring Frame",
            sampleQuestions = listOf(
                "Kirschner Beater ka kya kaam hai?",
                "Blow Room ke main objectives kya hain?",
                "Feed Roller aur Pedal Roller mein difference batao.",
                "Axi-Flow machine ka working principle samjhao.",
                "Parallel package aur Cross wound package mein antar."
            )
        ),
        TextileCategory(
            id = "testing",
            titleHindi = "टेक्सटाइल टेस्टिंग और क्वालिटी कंट्रोल",
            titleEnglish = "Textile Testing & Quality",
            iconName = "testing",
            description = "Uster Evenness Tester, Fibre Fineness, Tensile Strength, Yarn Count, Twist & Moisture",
            sampleQuestions = listOf(
                "Uster Evenness Tester ka principle aur working explain karo.",
                "Yarn Count (Ne, Tex, Denier) ki calculation kaise hoti hai?",
                "Fibre length aur strength test karne ke tareeqe.",
                "Trash percentage determination in cotton."
            )
        ),
        TextileCategory(
            id = "fibres",
            titleHindi = "फाइबर (कॉटन और मैन-मेड)",
            titleEnglish = "Fibres (Cotton & Man-made)",
            iconName = "fibres",
            description = "Cotton fibre properties, Polyester, Viscose, Nylon, Fibre grading & blending",
            sampleQuestions = listOf(
                "Cotton fibre ke physical aur chemical properties batao.",
                "Man-made fibre mfg: Melt spinning vs Wet spinning.",
                "Viscose rayon aur Polyester fibre mein difference."
            )
        ),
        TextileCategory(
            id = "practicals",
            titleHindi = "टेक्सटाइल प्रैक्टिकल्स और वाइवा",
            titleEnglish = "Textile Practicals & Viva",
            iconName = "practical",
            description = "Diploma Practicals, Apparatus, Procedure, Calculations, Result & Viva Questions",
            sampleQuestions = listOf(
                "Uster Tester par yarn evenness check karne ka practical writeup.",
                "Warp yarn count (Ne) determine karne ka experiment.",
                "Fabric GSM calculation practical procedure."
            )
        ),
        TextileCategory(
            id = "exam_prep",
            titleHindi = "एग्जाम प्रिपरेशन (Marks 1, 2, 5, 10)",
            titleEnglish = "Exam Prep & MCQ Practice",
            iconName = "exam",
            description = "1 Mark, 2 Marks, 5 Marks, 10 Marks Question Answers & MCQ Test Sets",
            sampleQuestions = listOf(
                "5 Marks Answer: Rapier Loom Mechanism.",
                "10 Marks Answer: Complete Blow Room Line.",
                "MCQ Practice for Textile Diploma Exams."
            )
        )
    )

    val sampleMcqs = listOf(
        ExamQuestionItem(
            id = "mcq_1",
            question = "Blow room mein fine opening aur cleaning ke liye kaun sa beater sabse accha mana jata hai?",
            marksCategory = "MCQ",
            topic = "Blow Room",
            answer = "Kirschner Beater (Pinned Beater)",
            options = listOf("Blade Beater", "Porcupine Beater", "Kirschner Beater", "Step Cleaner"),
            correctOptionIndex = 2
        ),
        ExamQuestionItem(
            id = "mcq_2",
            question = "Shuttleless looms mein sabse highest speed aur production kis loom ki hoti hai?",
            marksCategory = "MCQ",
            topic = "Weaving Technology",
            answer = "Air Jet Loom",
            options = listOf("Rapier Loom", "Water Jet Loom", "Air Jet Loom", "Projectile Loom"),
            correctOptionIndex = 2
        ),
        ExamQuestionItem(
            id = "mcq_3",
            question = "Cotton Yarn Direct System count unit kya hoti hai?",
            marksCategory = "MCQ",
            topic = "Textile Testing",
            answer = "Tex / Denier",
            options = listOf("English Count (Ne)", "Tex", "Metric Count (Nm)", "Worsted Count"),
            correctOptionIndex = 1
        ),
        ExamQuestionItem(
            id = "mcq_4",
            question = "Uster Evenness Tester kis principle par kaam karta hai?",
            marksCategory = "MCQ",
            topic = "Textile Testing",
            answer = "Capacitance Principle",
            options = listOf("Optical Principle", "Capacitance Principle", "Pneumatic Principle", "Mechanical Principle"),
            correctOptionIndex = 1
        ),
        ExamQuestionItem(
            id = "mcq_5",
            question = "Sizing Process mein warp yarn par sizing material lagane ka primary objective kya hai?",
            marksCategory = "MCQ",
            topic = "Weaving Preparatory",
            answer = "Warp strength badhana aur hairiness kam karna",
            options = listOf("Yarn color badalna", "Warp strength badhana aur hairiness kam karna", "Yarn count change karna", "Fabric GSM kam karna"),
            correctOptionIndex = 1
        )
    )

    val samplePracticals = listOf(
        TextilePracticalItem(
            experimentNo = 1,
            title = "Determination of Yarn Evenness & Imperfections using Uster Evenness Tester",
            subject = "Textile Testing Practical",
            aim = "To determine the yarn evenness (U% or CV%) and count thin places, thick places, and neps in a given yarn package.",
            apparatus = "Uster Evenness Tester, Yarn Cop/Cone, Tension device, Recording Unit.",
            theory = "Yarn mass variation is measured using the capacitance measurement cell. As the yarn passes between capacitor plates at high speed, variation in dielectric constant converts into mass variation signals.",
            procedure = listOf(
                "Condition the yarn package standard testing atmosphere (65% RH, 20°C) for 24 hours.",
                "Mount the yarn cone on the unwinding creel of Uster Tester.",
                "Thread the yarn through guides, tensioner, and measuring slot.",
                "Set test speed (e.g. 400 m/min) and testing duration (e.g. 2.5 minutes / 1000m).",
                "Start the test and record U%, CV%, Thin (-50%), Thick (+50%), and Neps (+200%)."
            ),
            calculationFormula = "CV% = 1.25 × U%",
            vivaQuestions = listOf(
                "Uster Tester kis principle par work karta hai?" to "Capacitance Principle par.",
                "Thin place aur Thick place ki threshold sensitivity kitni hoti hai?" to "Thin place: -50%, Thick place: +50%, Neps: +200%.",
                "CV% aur U% mein kya relation hota hai?" to "CV% roughly equal to 1.25 × U%."
            )
        ),
        TextilePracticalItem(
            experimentNo = 2,
            title = "Determination of Warp & Weft Yarn Count (Ne) from Fabric Sample using Beesley Balance",
            subject = "Fabric Testing Practical",
            aim = "To extract warp and weft yarns from fabric sample and determine their English Cotton Count (Ne) using Beesley Balance.",
            apparatus = "Beesley Balance, Template/Ruler, Dissecting Needle, Scissor, Standard Weight.",
            theory = "Beesley balance is a direct reading balance where a known length of yarn (e.g. 1/10th of a lea = 12 yards or short template length) is balanced against a fixed standard weight.",
            procedure = listOf(
                "Cut 10 warp threads and 10 weft threads of exact template length.",
                "Level the Beesley balance pointer to zero mark.",
                "Place yarn threads one by one on the yarn hook until the pointer levels.",
                "Read the count directly off the graduated beam scale."
            ),
            calculationFormula = "Ne = (Length in Yards × 840) / Weight in lbs OR Direct scale reading.",
            vivaQuestions = listOf(
                "Ne count direct system hai ya indirect system?" to "Indirect System (Jitna bada number, utna patla धागा).",
                "1 Lea mein kitne yards hote hain?" to "1 Lea = 120 Yards."
            )
        )
    )
}
