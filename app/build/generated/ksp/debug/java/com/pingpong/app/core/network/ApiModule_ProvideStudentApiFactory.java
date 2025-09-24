package com.pingpong.app.core.network;

import com.pingpong.app.core.network.api.StudentApi;
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
public final class ApiModule_ProvideStudentApiFactory implements Factory<StudentApi> {
  private final Provider<Retrofit> retrofitProvider;

  public ApiModule_ProvideStudentApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public StudentApi get() {
    return provideStudentApi(retrofitProvider.get());
  }

  public static ApiModule_ProvideStudentApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new ApiModule_ProvideStudentApiFactory(retrofitProvider);
  }

  public static StudentApi provideStudentApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(ApiModule.INSTANCE.provideStudentApi(retrofit));
  }
}
