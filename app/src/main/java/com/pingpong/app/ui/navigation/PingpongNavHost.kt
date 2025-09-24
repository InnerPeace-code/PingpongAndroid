package com.pingpong.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navOptions
import com.pingpong.app.feature.auth.login.LoginRoute
import com.pingpong.app.feature.auth.pending.RegisterPendingRoute
import com.pingpong.app.feature.auth.register.RegisterRoute
import com.pingpong.app.feature.dashboard.DashboardRoute
import com.pingpong.app.feature.profile.ProfileRoute
import com.pingpong.app.feature.admin.AdminManagementRoute
import com.pingpong.app.feature.superadmin.SuperAdminManagementRoute
import com.pingpong.app.feature.student.StudentHomeRoute
import com.pingpong.app.feature.coach.CoachHomeRoute
import com.pingpong.app.feature.schedule.ScheduleRoute
import com.pingpong.app.feature.evaluation.EvaluationRoute

@Composable
fun PingpongNavHost(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = RootDestination.AUTH.route
    ) {
        navigation(
            route = RootDestination.AUTH.route,
            startDestination = AuthDestination.LOGIN.route
        ) {
            composable(AuthDestination.LOGIN.route) {
                LoginRoute(
                    onLoginSuccess = { role ->
                        navController.navigate(
                            route = when (role) {
                                "super_admin" -> TopLevelDestination.SUPER_ADMIN_MANAGE.route
                                "campus_admin" -> TopLevelDestination.ADMIN_MANAGE.route
                                "coach" -> TopLevelDestination.COACH.route
                                "student" -> TopLevelDestination.STUDENT.route
                                else -> TopLevelDestination.DASHBOARD.route
                            },
                            navOptions = navOptions {
                                popUpTo(RootDestination.AUTH.route) {
                                    inclusive = true
                                }
                            }
                        )
                    },
                    onNavigateToRegister = {
                        navController.navigate(AuthDestination.REGISTER.route)
                    }
                )
            }
            composable(AuthDestination.REGISTER.route) {
                RegisterRoute(
                    onBack = { navController.popBackStack() },
                    onPending = {
                        navController.navigate(AuthDestination.REGISTER_PENDING.route)
                    }
                )
            }
            composable(AuthDestination.REGISTER_PENDING.route) {
                RegisterPendingRoute(onBackToLogin = {
                    navController.navigate(AuthDestination.LOGIN.route) {
                        popUpTo(RootDestination.AUTH.route) { inclusive = true }
                    }
                })
            }
        }

        navigation(
            route = RootDestination.MAIN.route,
            startDestination = TopLevelDestination.DASHBOARD.route
        ) {
            composable(TopLevelDestination.DASHBOARD.route) {
                DashboardRoute(onNavigateToProfile = {
                    navController.navigate(TopLevelDestination.PROFILE.route)
                })
            }
            composable(TopLevelDestination.PROFILE.route) {
                ProfileRoute(onBack = { navController.popBackStack() })
            }
            composable(TopLevelDestination.ADMIN_MANAGE.route) {
                AdminManagementRoute()
            }
            composable(TopLevelDestination.STUDENT.route) {
                StudentHomeRoute()
            }
            composable(TopLevelDestination.COACH.route) {
                CoachHomeRoute()
            }
            composable(TopLevelDestination.SCHEDULE.route) {
                ScheduleRoute()
            }
            composable(TopLevelDestination.EVALUATION.route) {
                EvaluationRoute()
            }
        }
    }
}

