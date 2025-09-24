package com.pingpong.app.feature.student;

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
public final class StudentCoachBrowserViewModel_Factory implements Factory<StudentCoachBrowserViewModel> {
  private final Provider<StudentCoachRepository> studentCoachRepositoryProvider;

  private final Provider<SessionRepository> sessionRepositoryProvider;

  public StudentCoachBrowserViewModel_Factory(
      Provider<StudentCoachRepository> studentCoachRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    this.studentCoachRepositoryProvider = studentCoachRepositoryProvider;
    this.sessionRepositoryProvider = sessionRepositoryProvider;
  }

  @Override
  public StudentCoachBrowserViewModel get() {
    return newInstance(studentCoachRepositoryProvider.get(), sessionRepositoryProvider.get());
  }

  public static StudentCoachBrowserViewModel_Factory create(
      Provider<StudentCoachRepository> studentCoachRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    return new StudentCoachBrowserViewModel_Factory(studentCoachRepositoryProvider, sessionRepositoryProvider);
  }

  public static StudentCoachBrowserViewModel newInstance(
      StudentCoachRepository studentCoachRepository, SessionRepository sessionRepository) {
    return new StudentCoachBrowserViewModel(studentCoachRepository, sessionRepository);
  }
}
