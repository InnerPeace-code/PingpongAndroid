package com.pingpong.app.core.network;

import com.pingpong.app.core.network.api.CoachChangeApi;
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
public final class ApiModule_ProvideCoachChangeApiFactory implements Factory<CoachChangeApi> {
  private final Provider<Retrofit> retrofitProvider;

  public ApiModule_ProvideCoachChangeApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public CoachChangeApi get() {
    return provideCoachChangeApi(retrofitProvider.get());
  }

  public static ApiModule_ProvideCoachChangeApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new ApiModule_ProvideCoachChangeApiFactory(retrofitProvider);
  }

  public static CoachChangeApi provideCoachChangeApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(ApiModule.INSTANCE.provideCoachChangeApi(retrofit));
  }
}
