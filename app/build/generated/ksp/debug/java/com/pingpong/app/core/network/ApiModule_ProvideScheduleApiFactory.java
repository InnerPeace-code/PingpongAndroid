package com.pingpong.app.core.network;

import com.pingpong.app.core.network.api.ScheduleApi;
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
public final class ApiModule_ProvideScheduleApiFactory implements Factory<ScheduleApi> {
  private final Provider<Retrofit> retrofitProvider;

  public ApiModule_ProvideScheduleApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public ScheduleApi get() {
    return provideScheduleApi(retrofitProvider.get());
  }

  public static ApiModule_ProvideScheduleApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new ApiModule_ProvideScheduleApiFactory(retrofitProvider);
  }

  public static ScheduleApi provideScheduleApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(ApiModule.INSTANCE.provideScheduleApi(retrofit));
  }
}
