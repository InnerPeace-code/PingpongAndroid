package com.pingpong.app.core.network;

import com.pingpong.app.core.network.api.NotificationApi;
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
public final class ApiModule_ProvideNotificationApiFactory implements Factory<NotificationApi> {
  private final Provider<Retrofit> retrofitProvider;

  public ApiModule_ProvideNotificationApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public NotificationApi get() {
    return provideNotificationApi(retrofitProvider.get());
  }

  public static ApiModule_ProvideNotificationApiFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new ApiModule_ProvideNotificationApiFactory(retrofitProvider);
  }

  public static NotificationApi provideNotificationApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(ApiModule.INSTANCE.provideNotificationApi(retrofit));
  }
}
