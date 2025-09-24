package com.pingpong.app.feature.admin;

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
public final class AdminChangeManageViewModel_Factory implements Factory<AdminChangeManageViewModel> {
  private final Provider<CoachChangeRepository> coachChangeRepositoryProvider;

  private final Provider<SessionRepository> sessionRepositoryProvider;

  public AdminChangeManageViewModel_Factory(
      Provider<CoachChangeRepository> coachChangeRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    this.coachChangeRepositoryProvider = coachChangeRepositoryProvider;
    this.sessionRepositoryProvider = sessionRepositoryProvider;
  }

  @Override
  public AdminChangeManageViewModel get() {
    return newInstance(coachChangeRepositoryProvider.get(), sessionRepositoryProvider.get());
  }

  public static AdminChangeManageViewModel_Factory create(
      Provider<CoachChangeRepository> coachChangeRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    return new AdminChangeManageViewModel_Factory(coachChangeRepositoryProvider, sessionRepositoryProvider);
  }

  public static AdminChangeManageViewModel newInstance(CoachChangeRepository coachChangeRepository,
      SessionRepository sessionRepository) {
    return new AdminChangeManageViewModel(coachChangeRepository, sessionRepository);
  }
}
