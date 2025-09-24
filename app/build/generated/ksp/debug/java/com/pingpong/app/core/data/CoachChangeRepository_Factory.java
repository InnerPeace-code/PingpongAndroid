package com.pingpong.app.core.data;

import com.pingpong.app.core.network.api.CoachChangeApi;
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
public final class CoachChangeRepository_Factory implements Factory<CoachChangeRepository> {
  private final Provider<CoachChangeApi> coachChangeApiProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public CoachChangeRepository_Factory(Provider<CoachChangeApi> coachChangeApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.coachChangeApiProvider = coachChangeApiProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public CoachChangeRepository get() {
    return newInstance(coachChangeApiProvider.get(), ioDispatcherProvider.get());
  }

  public static CoachChangeRepository_Factory create(
      Provider<CoachChangeApi> coachChangeApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new CoachChangeRepository_Factory(coachChangeApiProvider, ioDispatcherProvider);
  }

  public static CoachChangeRepository newInstance(CoachChangeApi coachChangeApi,
      CoroutineDispatcher ioDispatcher) {
    return new CoachChangeRepository(coachChangeApi, ioDispatcher);
  }
}
