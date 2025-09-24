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
public final class StudentMyCoachesViewModel_Factory implements Factory<StudentMyCoachesViewModel> {
  private final Provider<StudentCoachRepository> studentCoachRepositoryProvider;

  private final Provider<SessionRepository> sessionRepositoryProvider;

  public StudentMyCoachesViewModel_Factory(
      Provider<StudentCoachRepository> studentCoachRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    this.studentCoachRepositoryProvider = studentCoachRepositoryProvider;
    this.sessionRepositoryProvider = sessionRepositoryProvider;
  }

  @Override
  public StudentMyCoachesViewModel get() {
    return newInstance(studentCoachRepositoryProvider.get(), sessionRepositoryProvider.get());
  }

  public static StudentMyCoachesViewModel_Factory create(
      Provider<StudentCoachRepository> studentCoachRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    return new StudentMyCoachesViewModel_Factory(studentCoachRepositoryProvider, sessionRepositoryProvider);
  }

  public static StudentMyCoachesViewModel newInstance(StudentCoachRepository studentCoachRepository,
      SessionRepository sessionRepository) {
    return new StudentMyCoachesViewModel(studentCoachRepository, sessionRepository);
  }
}
