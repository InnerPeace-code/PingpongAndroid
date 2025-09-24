package com.pingpong.app.feature.student;

import com.pingpong.app.core.data.CoachChangeRepository;
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
public final class StudentCoachChangeViewModel_Factory implements Factory<StudentCoachChangeViewModel> {
  private final Provider<SessionRepository> sessionRepositoryProvider;

  private final Provider<CoachChangeRepository> coachChangeRepositoryProvider;

  public StudentCoachChangeViewModel_Factory(Provider<SessionRepository> sessionRepositoryProvider,
      Provider<CoachChangeRepository> coachChangeRepositoryProvider) {
    this.sessionRepositoryProvider = sessionRepositoryProvider;
    this.coachChangeRepositoryProvider = coachChangeRepositoryProvider;
  }

  @Override
  public StudentCoachChangeViewModel get() {
    return newInstance(sessionRepositoryProvider.get(), coachChangeRepositoryProvider.get());
  }

  public static StudentCoachChangeViewModel_Factory create(
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<CoachChangeRepository> coachChangeRepositoryProvider) {
    return new StudentCoachChangeViewModel_Factory(sessionRepositoryProvider, coachChangeRepositoryProvider);
  }

  public static StudentCoachChangeViewModel newInstance(SessionRepository sessionRepository,
      CoachChangeRepository coachChangeRepository) {
    return new StudentCoachChangeViewModel(sessionRepository, coachChangeRepository);
  }
}
