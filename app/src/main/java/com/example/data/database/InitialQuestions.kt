package com.example.data.database

import com.example.data.model.Question
import com.example.data.model.Subject
import com.example.data.model.ExamType

object InitialQuestions {
    val list = listOf(
        Question(
            subject = Subject.QUANT.name,
            examType = ExamType.CGL.name,
            questionText = "If x + 1/x = 5, then find the value of x³ + 1/x³.",
            optionA = "110",
            optionB = "115",
            optionC = "120",
            optionD = "125",
            correctAnswer = "A",
            explanation = "Using the formula: If x + 1/x = k, then x³ + 1/x³ = k³ - 3k.\nHere, k = 5.\nTherefore, x³ + 1/x³ = 5³ - 3(5) = 125 - 15 = 110.",
            year = 2022,
            difficulty = "Easy"
        ),
        Question(
            subject = Subject.QUANT.name,
            examType = ExamType.CHSL.name,
            questionText = "A shopkeeper marks his goods 40% above the cost price and allows a discount of 25% on the marked price. His gain or loss percentage is:",
            optionA = "5% Gain",
            optionB = "5% Loss",
            optionC = "10% Gain",
            optionD = "10% Loss",
            correctAnswer = "A",
            explanation = "Let Cost Price (CP) = 100.\nMarked Price (MP) = 140 (40% above CP).\nDiscount = 25% of 140 = (25/100) * 140 = 35.\nSelling Price (SP) = MP - Discount = 140 - 35 = 105.\nSince SP > CP, Gain % = ((105 - 100) / 100) * 100 = 5% Gain.",
            year = 2021,
            difficulty = "Medium"
        ),
        Question(
            subject = Subject.QUANT.name,
            examType = ExamType.MTS.name,
            questionText = "The ratio of two numbers is 3:4 and their HCF is 4. Their LCM is:",
            optionA = "12",
            optionB = "16",
            optionC = "24",
            optionD = "48",
            correctAnswer = "D",
            explanation = "Let the two numbers be 3x and 4x.\nTheir HCF = x. Given HCF = 4, so x = 4.\nThe numbers are 3 * 4 = 12 and 4 * 4 = 16.\nLCM of 12 and 16 is 48.\nShortcut: LCM = Product of ratios * HCF = 3 * 4 * 4 = 48.",
            year = 2020,
            difficulty = "Easy"
        ),
        Question(
            subject = Subject.QUANT.name,
            examType = ExamType.CGL.name,
            questionText = "If tan θ = 4/3, then find the value of (sin θ + cos θ) / (sin θ - cos θ).",
            optionA = "7",
            optionB = "1",
            optionC = "-7",
            optionD = "1/7",
            correctAnswer = "A",
            explanation = "Given tan θ = 4/3.\nDividing the numerator and the denominator of the expression by cos θ:\n((sin θ / cos θ) + 1) / ((sin θ / cos θ) - 1)\n= (tan θ + 1) / (tan θ - 1)\n= (4/3 + 1) / (4/3 - 1)\n= (7/3) / (1/3) = 7.",
            year = 2023,
            difficulty = "Medium"
        ),
        Question(
            subject = Subject.REASONING.name,
            examType = ExamType.CGL.name,
            questionText = "Select the option that is related to the third term in the same way as the second term is related to the first term.\nBIOLOGY : YRLOLTB :: PHYSICS : ?",
            optionA = "KSBHIXH",
            optionB = "KSBHRXH",
            optionC = "KSHBHCB",
            optionD = "KBHVRSG",
            correctAnswer = "B",
            explanation = "The pattern is opposite letters in the English alphabet (A↔Z, B↔Y, C↔X, etc.).\nB↔Y, I↔R, O↔L, L↔O, O↔L, G↔T, Y↔B.\nApplying the same logic to PHYSICS:\nP↔K, H↔S, Y↔B, S↔H, I↔R, C↔X, S↔H.\nTherefore, PHYSICS becomes KSBHRXH.",
            year = 2022,
            difficulty = "Hard"
        ),
        Question(
            subject = Subject.REASONING.name,
            examType = ExamType.CHSL.name,
            questionText = "In a certain code language, 'PEN' is written as '35' and 'PAPER' is written as '56'. How will 'BOOK' be written in that code language?",
            optionA = "43",
            optionB = "41",
            optionC = "35",
            optionD = "38",
            correctAnswer = "A",
            explanation = "The code is the sum of the alphabetical positions of each letter.\nP = 16, E = 5, N = 14. Sum = 16 + 5 + 14 = 35.\nP = 16, A = 1, P = 16, E = 5, R = 18. Sum = 16 + 1 + 16 + 5 + 18 = 56.\nFor BOOK:\nB = 2, O = 15, O = 15, K = 11.\nSum = 2 + 15 + 15 + 11 = 43.",
            year = 2021,
            difficulty = "Easy"
        ),
        Question(
            subject = Subject.REASONING.name,
            examType = ExamType.MTS.name,
            questionText = "Find the missing term in the series: 2, 6, 12, 20, 30, ?",
            optionA = "40",
            optionB = "42",
            optionC = "44",
            optionD = "46",
            correctAnswer = "B",
            explanation = "The difference between consecutive terms is increasing by 2:\n6 - 2 = 4\n12 - 6 = 6\n20 - 12 = 8\n30 - 20 = 10\nNext difference should be 12.\nMissing term = 30 + 12 = 42.\nAlternative logic: n² + n. (1²+1=2, 2²+2=6, 3²+3=12, 4²+4=20, 5²+5=30, 6²+6=42).",
            year = 2021,
            difficulty = "Easy"
        ),
        Question(
            subject = Subject.ENGLISH.name,
            examType = ExamType.CGL.name,
            questionText = "Choose the word most opposite in meaning (Antonym) to the given word:\nIMPEDE",
            optionA = "Obstruct",
            optionB = "Facilitate",
            optionC = "Delay",
            optionD = "Hinder",
            correctAnswer = "B",
            explanation = "IMPEDE means to delay or prevent someone or something by obstructing them; to hinder.\nThe antonym is 'Facilitate', which means to make an action or process easy or easier.",
            year = 2022,
            difficulty = "Easy"
        ),
        Question(
            subject = Subject.ENGLISH.name,
            examType = ExamType.CHSL.name,
            questionText = "Select the most appropriate meaning of the given idiom:\n'Spill the beans'",
            optionA = "To drop something accidentally",
            optionB = "To work very hard",
            optionC = "To reveal a secret",
            optionD = "To waste resources",
            correctAnswer = "C",
            explanation = "The idiom 'Spill the beans' means to reveal secret information, especially unintentionally or prematurely.",
            year = 2022,
            difficulty = "Easy"
        ),
        Question(
            subject = Subject.ENGLISH.name,
            examType = ExamType.CGL.name,
            questionText = "Identify the segment in the sentence that contains a grammatical error:\n'Each of the girls have completed their homework.'",
            optionA = "Each of the",
            optionB = "girls have completed",
            optionC = "their homework",
            optionD = "No error",
            correctAnswer = "B",
            explanation = "The subject 'Each' is a singular distributive pronoun and always takes a singular verb.\nTherefore, 'girls have completed' should be corrected to 'girls has completed'.",
            year = 2023,
            difficulty = "Medium"
        ),
        Question(
            subject = Subject.ENGLISH.name,
            examType = ExamType.MTS.name,
            questionText = "Choose the correct synonym of the given word:\nENTHUSIASTIC",
            optionA = "Zealous",
            optionB = "Apathetic",
            optionC = "Indifferent",
            optionD = "Lethargic",
            correctAnswer = "A",
            explanation = "ENTHUSIASTIC means having or showing intense and eager enjoyment, interest, or approval.\n'Zealous' is the closest synonym, meaning having or showing passion and zeal.",
            year = 2022,
            difficulty = "Easy"
        ),
        Question(
            subject = Subject.GK.name,
            examType = ExamType.CGL.name,
            questionText = "Who is known as the 'Father of the Indian Constitution'?",
            optionA = "Mahatma Gandhi",
            optionB = "Dr. B.R. Ambedkar",
            optionC = "Jawaharlal Nehru",
            optionD = "Sardar Vallabhbhai Patel",
            correctAnswer = "B",
            explanation = "Dr. B.R. Ambedkar was the Chairman of the Drafting Committee of the Constituent Assembly and is regarded as the Chief Architect of the Indian Constitution, commonly known as the Father of the Indian Constitution.",
            year = 2020,
            difficulty = "Easy"
        ),
        Question(
            subject = Subject.GK.name,
            examType = ExamType.CHSL.name,
            questionText = "Which of the following Articles of the Indian Constitution deals with the 'Right to Equality'?",
            optionA = "Articles 14-18",
            optionB = "Articles 19-22",
            optionC = "Articles 23-24",
            optionD = "Articles 25-28",
            correctAnswer = "A",
            explanation = "Articles 14 to 18 of the Constitution of India deal with the Right to Equality, which is one of the fundamental rights. Article 14 guarantees equality before law, Article 15 prohibits discrimination, Article 16 guarantees equality of opportunity in public employment, Article 17 abolishes untouchability, and Article 18 abolishes titles.",
            year = 2022,
            difficulty = "Medium"
        ),
        Question(
            subject = Subject.GK.name,
            examType = ExamType.MTS.name,
            questionText = "The historical 'Battle of Plassey' was fought in the year:",
            optionA = "1757",
            optionB = "1764",
            optionC = "1782",
            optionD = "1857",
            correctAnswer = "A",
            explanation = "The Battle of Plassey was fought on June 23, 1757, in Palashi (Bengal) between the British East India Company (led by Robert Clive) and the Nawab of Bengal (Siraj-ud-Daulah), aided by French allies. It established Company rule in Bengal and subsequently India.",
            year = 2019,
            difficulty = "Easy"
        ),
        Question(
            subject = Subject.GK.name,
            examType = ExamType.CPO.name,
            questionText = "Which Indian river is also popularly known as the 'Dakshin Ganga'?",
            optionA = "Krishna River",
            optionB = "Godavari River",
            optionC = "Cauvery River",
            optionD = "Narmada River",
            correctAnswer = "B",
            explanation = "The Godavari River is the largest river basin in Peninsular India and is known as 'Dakshin Ganga' (Ganges of the South) because of its large size, length, and sacred nature similar to the Ganges River.",
            year = 2021,
            difficulty = "Medium"
        ),
        Question(
            subject = Subject.GK.name,
            examType = ExamType.CGL.name,
            questionText = "Which of the following vitamins is water-soluble?",
            optionA = "Vitamin A",
            optionB = "Vitamin D",
            optionC = "Vitamin C",
            optionD = "Vitamin K",
            correctAnswer = "C",
            explanation = "Vitamins are classified as fat-soluble or water-soluble.\nVitamins B-complex and C are water-soluble (dissolve in water and are not stored in the body).\nVitamins A, D, E, and K are fat-soluble (absorbed with fats and stored in liver and fatty tissues).",
            year = 2021,
            difficulty = "Easy"
        )
    )
}
