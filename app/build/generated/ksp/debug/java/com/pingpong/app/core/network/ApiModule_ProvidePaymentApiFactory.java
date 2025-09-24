package com.pingpong.app.core.network;

import com.pingpong.app.core.network.api.PaymentApi;
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
public final class ApiModule_ProvidePaymentApiFactory implements Factory<PaymentApi> {
  private final Provider<Retrofit> retrofitProvider;

  public ApiModule_ProvidePaymentApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public PaymentApi get() {
    return providePaymentApi(retrofitProvider.get());
  }

  public static ApiModule_ProvidePaymentApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new ApiModule_ProvidePaymentApiFactory(retrofitProvider);
  }

  public static PaymentApi providePaymentApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(ApiModule.INSTANCE.providePaymentApi(retrofit));
  }
}
