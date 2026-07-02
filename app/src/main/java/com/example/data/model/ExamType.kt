package com.example.data.model

enum class ExamType(val displayName: String, val description: String) {
    CGL("SSC CGL", "Combined Graduate Level Exam"),
    CHSL("SSC CHSL", "Combined Higher Secondary Level"),
    MTS("SSC MTS", "Multi Tasking Staff"),
    CPO("SSC CPO", "Central Police Organisation"),
    NTPC("RRB NTPC", "Railway NTPC & Others")
}
