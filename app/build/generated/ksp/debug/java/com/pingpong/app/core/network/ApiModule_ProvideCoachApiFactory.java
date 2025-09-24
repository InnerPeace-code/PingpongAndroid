package com.pingpong.app.core.network;

import com.pingpong.app.core.network.api.CoachApi;
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
public final class ApiModule_ProvideCoachApiFactory implements Factory<CoachApi> {
  private final Provider<Retrofit> retrofitProvider;

  public ApiModule_ProvideCoachApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public CoachApi get() {
    return provideCoachApi(retrofitProvider.get());
  }

  public static ApiModule_ProvideCoachApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new ApiModule_ProvideCoachApiFactory(retrofitProvider);
  }

  public static CoachApi provideCoachApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(ApiModule.INSTANCE.provideCoachApi(retrofit));
  }
}
