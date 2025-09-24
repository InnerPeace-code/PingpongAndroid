package com.pingpong.app.core.data;

import com.pingpong.app.core.network.api.NotificationApi;
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
public final class NotificationRepository_Factory implements Factory<NotificationRepository> {
  private final Provider<NotificationApi> notificationApiProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public NotificationRepository_Factory(Provider<NotificationApi> notificationApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.notificationApiProvider = notificationApiProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public NotificationRepository get() {
    return newInstance(notificationApiProvider.get(), ioDispatcherProvider.get());
  }

  public static NotificationRepository_Factory create(
      Provider<NotificationApi> notificationApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new NotificationRepository_Factory(notificationApiProvider, ioDispatcherProvider);
  }

  public static NotificationRepository newInstance(NotificationApi notificationApi,
      CoroutineDispatcher ioDispatcher) {
    return new NotificationRepository(notificationApi, ioDispatcher);
  }
}
