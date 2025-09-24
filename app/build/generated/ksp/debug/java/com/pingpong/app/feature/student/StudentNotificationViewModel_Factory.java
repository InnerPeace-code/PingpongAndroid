package com.pingpong.app.feature.student;

import com.pingpong.app.core.data.NotificationRepository;
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
public final class StudentNotificationViewModel_Factory implements Factory<StudentNotificationViewModel> {
  private final Provider<SessionRepository> sessionRepositoryProvider;

  private final Provider<NotificationRepository> notificationRepositoryProvider;

  public StudentNotificationViewModel_Factory(Provider<SessionRepository> sessionRepositoryProvider,
      Provider<NotificationRepository> notificationRepositoryProvider) {
    this.sessionRepositoryProvider = sessionRepositoryProvider;
    this.notificationRepositoryProvider = notificationRepositoryProvider;
  }

  @Override
  public StudentNotificationViewModel get() {
    return newInstance(sessionRepositoryProvider.get(), notificationRepositoryProvider.get());
  }

  public static StudentNotificationViewModel_Factory create(
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<NotificationRepository> notificationRepositoryProvider) {
    return new StudentNotificationViewModel_Factory(sessionRepositoryProvider, notificationRepositoryProvider);
  }

  public static StudentNotificationViewModel newInstance(SessionRepository sessionRepository,
      NotificationRepository notificationRepository) {
    return new StudentNotificationViewModel(sessionRepository, notificationRepository);
  }
}
