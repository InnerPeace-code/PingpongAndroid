package com.pingpong.app.core.data;

import com.pingpong.app.core.auth.TokenManager;
import com.pingpong.app.core.network.api.AuthApi;
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
public final class AuthRepository_Factory implements Factory<AuthRepository> {
  private final Provider<AuthApi> authApiProvider;

  private final Provider<TokenManager> tokenManagerProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public AuthRepository_Factory(Provider<AuthApi> authApiProvider,
      Provider<TokenManager> tokenManagerProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.authApiProvider = authApiProvider;
    this.tokenManagerProvider = tokenManagerProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public AuthRepository get() {
    return newInstance(authApiProvider.get(), tokenManagerProvider.get(), ioDispatcherProvider.get());
  }

  public static AuthRepository_Factory create(Provider<AuthApi> authApiProvider,
      Provider<TokenManager> tokenManagerProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new AuthRepository_Factory(authApiProvider, tokenManagerProvider, ioDispatcherProvider);
  }

  public static AuthRepository newInstance(AuthApi authApi, TokenManager tokenManager,
      CoroutineDispatcher ioDispatcher) {
    return new AuthRepository(authApi, tokenManager, ioDispatcher);
  }
}
