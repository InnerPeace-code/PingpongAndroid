package com.pingpong.app.core.data;

import com.pingpong.app.core.auth.TokenProvider;
import com.pingpong.app.core.network.api.SuperAdminApi;
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
public final class SuperAdminRepository_Factory implements Factory<SuperAdminRepository> {
  private final Provider<SuperAdminApi> superAdminApiProvider;

  private final Provider<TokenProvider> tokenProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public SuperAdminRepository_Factory(Provider<SuperAdminApi> superAdminApiProvider,
      Provider<TokenProvider> tokenProvider, Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.superAdminApiProvider = superAdminApiProvider;
    this.tokenProvider = tokenProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public SuperAdminRepository get() {
    return newInstance(superAdminApiProvider.get(), tokenProvider.get(), ioDispatcherProvider.get());
  }

  public static SuperAdminRepository_Factory create(Provider<SuperAdminApi> superAdminApiProvider,
      Provider<TokenProvider> tokenProvider, Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new SuperAdminRepository_Factory(superAdminApiProvider, tokenProvider, ioDispatcherProvider);
  }

  public static SuperAdminRepository newInstance(SuperAdminApi superAdminApi,
      TokenProvider tokenProvider, CoroutineDispatcher ioDispatcher) {
    return new SuperAdminRepository(superAdminApi, tokenProvider, ioDispatcher);
  }
}
