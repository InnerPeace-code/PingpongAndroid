package com.pingpong.app.core.data;

import com.pingpong.app.core.network.api.AppointmentApi;
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
public final class AppointmentRepository_Factory implements Factory<AppointmentRepository> {
  private final Provider<AppointmentApi> appointmentApiProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public AppointmentRepository_Factory(Provider<AppointmentApi> appointmentApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.appointmentApiProvider = appointmentApiProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public AppointmentRepository get() {
    return newInstance(appointmentApiProvider.get(), ioDispatcherProvider.get());
  }

  public static AppointmentRepository_Factory create(
      Provider<AppointmentApi> appointmentApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new AppointmentRepository_Factory(appointmentApiProvider, ioDispatcherProvider);
  }

  public static AppointmentRepository newInstance(AppointmentApi appointmentApi,
      CoroutineDispatcher ioDispatcher) {
    return new AppointmentRepository(appointmentApi, ioDispatcher);
  }
}
