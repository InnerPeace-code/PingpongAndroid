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
    PROFILE("main/profile", "个人信息"),
    ADMIN_MANAGE("main/admin/manage", "管理"),
    STUDENT("main/student", "学员"),
    COACH("main/coach", "教练"),
    SCHEDULE("main/schedule", "课表"),
    EVALUATION("main/evaluation", "训练评价")
}
