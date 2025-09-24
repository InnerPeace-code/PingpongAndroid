package com.pingpong.app.core.data;

import com.pingpong.app.core.network.api.CampusApi;
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
public final class CampusRepository_Factory implements Factory<CampusRepository> {
  private final Provider<CampusApi> campusApiProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public CampusRepository_Factory(Provider<CampusApi> campusApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.campusApiProvider = campusApiProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public CampusRepository get() {
    return newInstance(campusApiProvider.get(), ioDispatcherProvider.get());
  }

  public static CampusRepository_Factory create(Provider<CampusApi> campusApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new CampusRepository_Factory(campusApiProvider, ioDispatcherProvider);
  }

  public static CampusRepository newInstance(CampusApi campusApi,
      CoroutineDispatcher ioDispatcher) {
    return new CampusRepository(campusApi, ioDispatcher);
  }
}
