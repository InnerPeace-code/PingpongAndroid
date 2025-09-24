package com.pingpong.app.feature.coach;

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
public final class CoachChangeViewModel_Factory implements Factory<CoachChangeViewModel> {
  private final Provider<CoachChangeRepository> coachChangeRepositoryProvider;

  private final Provider<SessionRepository> sessionRepositoryProvider;

  public CoachChangeViewModel_Factory(Provider<CoachChangeRepository> coachChangeRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    this.coachChangeRepositoryProvider = coachChangeRepositoryProvider;
    this.sessionRepositoryProvider = sessionRepositoryProvider;
  }

  @Override
  public CoachChangeViewModel get() {
    return newInstance(coachChangeRepositoryProvider.get(), sessionRepositoryProvider.get());
  }

  public static CoachChangeViewModel_Factory create(
      Provider<CoachChangeRepository> coachChangeRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    return new CoachChangeViewModel_Factory(coachChangeRepositoryProvider, sessionRepositoryProvider);
  }

  public static CoachChangeViewModel newInstance(CoachChangeRepository coachChangeRepository,
      SessionRepository sessionRepository) {
    return new CoachChangeViewModel(coachChangeRepository, sessionRepository);
  }
}
