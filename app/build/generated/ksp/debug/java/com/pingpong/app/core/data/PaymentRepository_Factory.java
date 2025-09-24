package com.pingpong.app.core.data;

import com.pingpong.app.core.network.api.PaymentApi;
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
public final class PaymentRepository_Factory implements Factory<PaymentRepository> {
  private final Provider<PaymentApi> paymentApiProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public PaymentRepository_Factory(Provider<PaymentApi> paymentApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.paymentApiProvider = paymentApiProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public PaymentRepository get() {
    return newInstance(paymentApiProvider.get(), ioDispatcherProvider.get());
  }

  public static PaymentRepository_Factory create(Provider<PaymentApi> paymentApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new PaymentRepository_Factory(paymentApiProvider, ioDispatcherProvider);
  }

  public static PaymentRepository newInstance(PaymentApi paymentApi,
      CoroutineDispatcher ioDispatcher) {
    return new PaymentRepository(paymentApi, ioDispatcher);
  }
}
