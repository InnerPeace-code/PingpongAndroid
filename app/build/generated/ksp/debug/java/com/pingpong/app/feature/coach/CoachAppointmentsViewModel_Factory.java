package com.pingpong.app.feature.coach;

import com.pingpong.app.core.data.AppointmentRepository;
import com.pingpong.app.core.data.SessionRepository;
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
public final class CoachAppointmentsViewModel_Factory implements Factory<CoachAppointmentsViewModel> {
  private final Provider<AppointmentRepository> appointmentRepositoryProvider;

  private final Provider<SessionRepository> sessionRepositoryProvider;

  public CoachAppointmentsViewModel_Factory(
      Provider<AppointmentRepository> appointmentRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    this.appointmentRepositoryProvider = appointmentRepositoryProvider;
    this.sessionRepositoryProvider = sessionRepositoryProvider;
  }

  @Override
  public CoachAppointmentsViewModel get() {
    return newInstance(appointmentRepositoryProvider.get(), sessionRepositoryProvider.get());
  }

  public static CoachAppointmentsViewModel_Factory create(
      Provider<AppointmentRepository> appointmentRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    return new CoachAppointmentsViewModel_Factory(appointmentRepositoryProvider, sessionRepositoryProvider);
  }

  public static CoachAppointmentsViewModel newInstance(AppointmentRepository appointmentRepository,
      SessionRepository sessionRepository) {
    return new CoachAppointmentsViewModel(appointmentRepository, sessionRepository);
  }
}
