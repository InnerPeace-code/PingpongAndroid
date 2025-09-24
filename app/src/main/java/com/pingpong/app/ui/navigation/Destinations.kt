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
    PROFILE("main/profile", "Profile"),
    ADMIN_MANAGE("main/admin/manage", "Admin"),
    STUDENT("main/student", "Students"),
    COACH("main/coach", "Coaches"),
    SUPER_ADMIN_MANAGE("main/super-admin/manage", "Super Admin"),
    SCHEDULE("main/schedule", "Schedule"),
    EVALUATION("main/evaluation", "Evaluations")
}
