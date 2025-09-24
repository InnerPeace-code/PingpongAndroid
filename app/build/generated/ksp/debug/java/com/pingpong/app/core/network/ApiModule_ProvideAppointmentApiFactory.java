package com.pingpong.app.core.network;

import com.pingpong.app.core.network.api.AppointmentApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ApiModule_ProvideAppointmentApiFactory implements Factory<AppointmentApi> {
  private final Provider<Retrofit> retrofitProvider;

  public ApiModule_ProvideAppointmentApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public AppointmentApi get() {
    return provideAppointmentApi(retrofitProvider.get());
  }

  public static ApiModule_ProvideAppointmentApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new ApiModule_ProvideAppointmentApiFactory(retrofitProvider);
  }

  public static AppointmentApi provideAppointmentApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(ApiModule.INSTANCE.provideAppointmentApi(retrofit));
  }
}
