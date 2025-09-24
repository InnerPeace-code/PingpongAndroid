package com.pingpong.app.core.network;

import com.pingpong.app.core.network.api.SuperAdminApi;
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
public final class ApiModule_ProvideSuperAdminApiFactory implements Factory<SuperAdminApi> {
  private final Provider<Retrofit> retrofitProvider;

  public ApiModule_ProvideSuperAdminApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public SuperAdminApi get() {
    return provideSuperAdminApi(retrofitProvider.get());
  }

  public static ApiModule_ProvideSuperAdminApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new ApiModule_ProvideSuperAdminApiFactory(retrofitProvider);
  }

  public static SuperAdminApi provideSuperAdminApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(ApiModule.INSTANCE.provideSuperAdminApi(retrofit));
  }
}
