package com.pingpong.app.core.network

import com.pingpong.app.core.network.api.AdminApi
import com.pingpong.app.core.network.api.AppointmentApi
import com.pingpong.app.core.network.api.AuthApi
import com.pingpong.app.core.network.api.CampusApi
import com.pingpong.app.core.network.api.CoachApi
import com.pingpong.app.core.network.api.CoachChangeApi
import com.pingpong.app.core.network.api.EvaluationApi
import com.pingpong.app.core.network.api.NotificationApi
import com.pingpong.app.core.network.api.PaymentApi
import com.pingpong.app.core.network.api.ScheduleApi
import com.pingpong.app.core.network.api.StudentApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideAdminApi(retrofit: Retrofit): AdminApi = retrofit.create(AdminApi::class.java)

    @Provides
    @Singleton
    fun provideCampusApi(retrofit: Retrofit): CampusApi = retrofit.create(CampusApi::class.java)

    @Provides
    @Singleton
    fun provideCoachApi(retrofit: Retrofit): CoachApi = retrofit.create(CoachApi::class.java)

    @Provides
    @Singleton
    fun provideStudentApi(retrofit: Retrofit): StudentApi = retrofit.create(StudentApi::class.java)

    @Provides
    @Singleton
    fun provideScheduleApi(retrofit: Retrofit): ScheduleApi = retrofit.create(ScheduleApi::class.java)

    @Provides
    @Singleton
    fun provideAppointmentApi(retrofit: Retrofit): AppointmentApi = retrofit.create(AppointmentApi::class.java)

    @Provides
    @Singleton
    fun provideEvaluationApi(retrofit: Retrofit): EvaluationApi = retrofit.create(EvaluationApi::class.java)

    @Provides
    @Singleton
    fun provideCoachChangeApi(retrofit: Retrofit): CoachChangeApi = retrofit.create(CoachChangeApi::class.java)

    @Provides
    @Singleton
    fun provideNotificationApi(retrofit: Retrofit): NotificationApi = retrofit.create(NotificationApi::class.java)

    @Provides
    @Singleton
    fun providePaymentApi(retrofit: Retrofit): PaymentApi = retrofit.create(PaymentApi::class.java)
}
