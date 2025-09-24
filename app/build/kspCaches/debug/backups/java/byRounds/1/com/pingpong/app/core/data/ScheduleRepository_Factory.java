package com.pingpong.app.core.data;

import com.pingpong.app.core.network.api.ScheduleApi;
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
public final class ScheduleRepository_Factory implements Factory<ScheduleRepository> {
  private final Provider<ScheduleApi> scheduleApiProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public ScheduleRepository_Factory(Provider<ScheduleApi> scheduleApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.scheduleApiProvider = scheduleApiProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public ScheduleRepository get() {
    return newInstance(scheduleApiProvider.get(), ioDispatcherProvider.get());
  }

  public static ScheduleRepository_Factory create(Provider<ScheduleApi> scheduleApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new ScheduleRepository_Factory(scheduleApiProvider, ioDispatcherProvider);
  }

  public static ScheduleRepository newInstance(ScheduleApi scheduleApi,
      CoroutineDispatcher ioDispatcher) {
    return new ScheduleRepository(scheduleApi, ioDispatcher);
  }
}
