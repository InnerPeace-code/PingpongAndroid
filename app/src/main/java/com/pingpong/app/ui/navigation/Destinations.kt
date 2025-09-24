package com.pingpong.app.ui.navigation

enum class RootDestination(val route: String) {
    AUTH("auth"),
    MAIN("main")
}

enum class AuthDestination(val route: String) {
    LOGIN("auth/login"),
    REGISTER("auth/register"),
    REGISTER_PENDING("auth/register/pending")
}

enum class TopLevelDestination(val route: String, val title: String) {
    DASHBOARD("main/dashboard", "Dashboard"),
    PROFILE("main/profile", "������Ϣ"),
    ADMIN_MANAGE("main/admin/manage", "����"),
    STUDENT("main/student", "ѧԱ"),
    COACH("main/coach", "����"),
    SCHEDULE("main/schedule", "�α�"),
    EVALUATION("main/evaluation", "ѵ������")
}
