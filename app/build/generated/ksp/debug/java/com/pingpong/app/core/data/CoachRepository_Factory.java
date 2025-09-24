package com.pingpong.app.core.data;

import com.pingpong.app.core.network.api.CoachApi;
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
public final class CoachRepository_Factory implements Factory<CoachRepository> {
  private final Provider<CoachApi> coachApiProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public CoachRepository_Factory(Provider<CoachApi> coachApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.coachApiProvider = coachApiProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public CoachRepository get() {
    return newInstance(coachApiProvider.get(), ioDispatcherProvider.get());
  }

  public static CoachRepository_Factory create(Provider<CoachApi> coachApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new CoachRepository_Factory(coachApiProvider, ioDispatcherProvider);
  }

  public static CoachRepository newInstance(CoachApi coachApi, CoroutineDispatcher ioDispatcher) {
    return new CoachRepository(coachApi, ioDispatcher);
  }
}
