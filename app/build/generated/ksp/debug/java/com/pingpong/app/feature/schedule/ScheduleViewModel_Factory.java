package com.pingpong.app.feature.schedule;

import com.pingpong.app.core.auth.TokenProvider;
import com.pingpong.app.core.data.ScheduleRepository;
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
public final class ScheduleViewModel_Factory implements Factory<ScheduleViewModel> {
  private final Provider<ScheduleRepository> scheduleRepositoryProvider;

  private final Provider<SessionRepository> sessionRepositoryProvider;

  private final Provider<TokenProvider> tokenProvider;

  public ScheduleViewModel_Factory(Provider<ScheduleRepository> scheduleRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<TokenProvider> tokenProvider) {
    this.scheduleRepositoryProvider = scheduleRepositoryProvider;
    this.sessionRepositoryProvider = sessionRepositoryProvider;
    this.tokenProvider = tokenProvider;
  }

  @Override
  public ScheduleViewModel get() {
    return newInstance(scheduleRepositoryProvider.get(), sessionRepositoryProvider.get(), tokenProvider.get());
  }

  public static ScheduleViewModel_Factory create(
      Provider<ScheduleRepository> scheduleRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<TokenProvider> tokenProvider) {
    return new ScheduleViewModel_Factory(scheduleRepositoryProvider, sessionRepositoryProvider, tokenProvider);
  }

  public static ScheduleViewModel newInstance(ScheduleRepository scheduleRepository,
      SessionRepository sessionRepository, TokenProvider tokenProvider) {
    return new ScheduleViewModel(scheduleRepository, sessionRepository, tokenProvider);
  }
}
