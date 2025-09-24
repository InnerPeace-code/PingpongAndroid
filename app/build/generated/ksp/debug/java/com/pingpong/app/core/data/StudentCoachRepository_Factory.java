package com.pingpong.app.core.data;

import com.pingpong.app.core.network.api.StudentApi;
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
public final class StudentCoachRepository_Factory implements Factory<StudentCoachRepository> {
  private final Provider<StudentApi> studentApiProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public StudentCoachRepository_Factory(Provider<StudentApi> studentApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.studentApiProvider = studentApiProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public StudentCoachRepository get() {
    return newInstance(studentApiProvider.get(), ioDispatcherProvider.get());
  }

  public static StudentCoachRepository_Factory create(Provider<StudentApi> studentApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new StudentCoachRepository_Factory(studentApiProvider, ioDispatcherProvider);
  }

  public static StudentCoachRepository newInstance(StudentApi studentApi,
      CoroutineDispatcher ioDispatcher) {
    return new StudentCoachRepository(studentApi, ioDispatcher);
  }
}
