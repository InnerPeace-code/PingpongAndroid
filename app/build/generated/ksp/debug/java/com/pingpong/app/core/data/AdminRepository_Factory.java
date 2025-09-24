package com.pingpong.app.core.data;

import com.pingpong.app.core.network.api.AdminApi;
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
public final class AdminRepository_Factory implements Factory<AdminRepository> {
  private final Provider<AdminApi> adminApiProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public AdminRepository_Factory(Provider<AdminApi> adminApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.adminApiProvider = adminApiProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public AdminRepository get() {
    return newInstance(adminApiProvider.get(), ioDispatcherProvider.get());
  }

  public static AdminRepository_Factory create(Provider<AdminApi> adminApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new AdminRepository_Factory(adminApiProvider, ioDispatcherProvider);
  }

  public static AdminRepository newInstance(AdminApi adminApi, CoroutineDispatcher ioDispatcher) {
    return new AdminRepository(adminApi, ioDispatcher);
  }
}
