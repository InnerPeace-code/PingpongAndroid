package com.pingpong.app.core.network;

import com.pingpong.app.core.network.api.AdminApi;
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
public final class ApiModule_ProvideAdminApiFactory implements Factory<AdminApi> {
  private final Provider<Retrofit> retrofitProvider;

  public ApiModule_ProvideAdminApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public AdminApi get() {
    return provideAdminApi(retrofitProvider.get());
  }

  public static ApiModule_ProvideAdminApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new ApiModule_ProvideAdminApiFactory(retrofitProvider);
  }

  public static AdminApi provideAdminApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(ApiModule.INSTANCE.provideAdminApi(retrofit));
  }
}
