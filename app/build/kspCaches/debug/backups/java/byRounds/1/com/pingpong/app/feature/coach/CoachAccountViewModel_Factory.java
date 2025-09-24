package com.pingpong.app.feature.coach;

import com.pingpong.app.core.data.CoachRepository;
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
public final class CoachAccountViewModel_Factory implements Factory<CoachAccountViewModel> {
  private final Provider<CoachRepository> coachRepositoryProvider;

  private final Provider<SessionRepository> sessionRepositoryProvider;

  public CoachAccountViewModel_Factory(Provider<CoachRepository> coachRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    this.coachRepositoryProvider = coachRepositoryProvider;
    this.sessionRepositoryProvider = sessionRepositoryProvider;
  }

  @Override
  public CoachAccountViewModel get() {
    return newInstance(coachRepositoryProvider.get(), sessionRepositoryProvider.get());
  }

  public static CoachAccountViewModel_Factory create(
      Provider<CoachRepository> coachRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    return new CoachAccountViewModel_Factory(coachRepositoryProvider, sessionRepositoryProvider);
  }

  public static CoachAccountViewModel newInstance(CoachRepository coachRepository,
      SessionRepository sessionRepository) {
    return new CoachAccountViewModel(coachRepository, sessionRepository);
  }
}
