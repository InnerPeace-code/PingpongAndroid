package com.pingpong.app.feature.student;

import com.pingpong.app.core.data.AppointmentRepository;
import com.pingpong.app.core.data.ScheduleRepository;
import com.pingpong.app.core.data.SessionRepository;
import com.pingpong.app.core.data.StudentCoachRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class StudentBookingViewModel_Factory implements Factory<StudentBookingViewModel> {
  private final Provider<SessionRepository> sessionRepositoryProvider;

  private final Provider<StudentCoachRepository> studentCoachRepositoryProvider;

  private final Provider<AppointmentRepository> appointmentRepositoryProvider;

  private final Provider<ScheduleRepository> scheduleRepositoryProvider;

  public StudentBookingViewModel_Factory(Provider<SessionRepository> sessionRepositoryProvider,
      Provider<StudentCoachRepository> studentCoachRepositoryProvider,
      Provider<AppointmentRepository> appointmentRepositoryProvider,
      Provider<ScheduleRepository> scheduleRepositoryProvider) {
    this.sessionRepositoryProvider = sessionRepositoryProvider;
    this.studentCoachRepositoryProvider = studentCoachRepositoryProvider;
    this.appointmentRepositoryProvider = appointmentRepositoryProvider;
    this.scheduleRepositoryProvider = scheduleRepositoryProvider;
  }

  @Override
  public StudentBookingViewModel get() {
    return newInstance(sessionRepositoryProvider.get(), studentCoachRepositoryProvider.get(), appointmentRepositoryProvider.get(), scheduleRepositoryProvider.get());
  }

  public static StudentBookingViewModel_Factory create(
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<StudentCoachRepository> studentCoachRepositoryProvider,
      Provider<AppointmentRepository> appointmentRepositoryProvider,
      Provider<ScheduleRepository> scheduleRepositoryProvider) {
    return new StudentBookingViewModel_Factory(sessionRepositoryProvider, studentCoachRepositoryProvider, appointmentRepositoryProvider, scheduleRepositoryProvider);
  }

  public static StudentBookingViewModel newInstance(SessionRepository sessionRepository,
      StudentCoachRepository studentCoachRepository, AppointmentRepository appointmentRepository,
      ScheduleRepository scheduleRepository) {
    return new StudentBookingViewModel(sessionRepository, studentCoachRepository, appointmentRepository, scheduleRepository);
  }
}
