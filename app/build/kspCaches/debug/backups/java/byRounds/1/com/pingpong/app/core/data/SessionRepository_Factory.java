package com.pingpong.app.core.data;

import com.pingpong.app.core.auth.TokenProvider;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineDispatcher;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.pingpong.app.core.common.IoDispatcher")
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
public final class SessionRepository_Factory implements Factory<SessionRepository> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<TokenProvider> tokenProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public SessionRepository_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<TokenProvider> tokenProvider, Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.tokenProvider = tokenProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public SessionRepository get() {
    return newInstance(authRepositoryProvider.get(), tokenProvider.get(), ioDispatcherProvider.get());
  }

  public static SessionRepository_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<TokenProvider> tokenProvider, Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new SessionRepository_Factory(authRepositoryProvider, tokenProvider, ioDispatcherProvider);
  }

  public static SessionRepository newInstance(AuthRepository authRepository,
      TokenProvider tokenProvider, CoroutineDispatcher ioDispatcher) {
    return new SessionRepository(authRepository, tokenProvider, ioDispatcher);
  }
}
