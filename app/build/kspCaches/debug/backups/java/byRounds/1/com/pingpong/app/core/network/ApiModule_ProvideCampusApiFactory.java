package com.pingpong.app.core.network;

import com.pingpong.app.core.network.api.CampusApi;
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
public final class ApiModule_ProvideCampusApiFactory implements Factory<CampusApi> {
  private final Provider<Retrofit> retrofitProvider;

  public ApiModule_ProvideCampusApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public CampusApi get() {
    return provideCampusApi(retrofitProvider.get());
  }

  public static ApiModule_ProvideCampusApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new ApiModule_ProvideCampusApiFactory(retrofitProvider);
  }

  public static CampusApi provideCampusApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(ApiModule.INSTANCE.provideCampusApi(retrofit));
  }
}
