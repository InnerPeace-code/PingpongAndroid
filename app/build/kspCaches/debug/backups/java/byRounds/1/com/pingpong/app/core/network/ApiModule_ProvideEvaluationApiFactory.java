package com.pingpong.app.core.network;

import com.pingpong.app.core.network.api.EvaluationApi;
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
public final class ApiModule_ProvideEvaluationApiFactory implements Factory<EvaluationApi> {
  private final Provider<Retrofit> retrofitProvider;

  public ApiModule_ProvideEvaluationApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public EvaluationApi get() {
    return provideEvaluationApi(retrofitProvider.get());
  }

  public static ApiModule_ProvideEvaluationApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new ApiModule_ProvideEvaluationApiFactory(retrofitProvider);
  }

  public static EvaluationApi provideEvaluationApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(ApiModule.INSTANCE.provideEvaluationApi(retrofit));
  }
}
