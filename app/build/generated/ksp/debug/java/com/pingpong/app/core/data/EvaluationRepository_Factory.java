package com.pingpong.app.core.data;

import com.pingpong.app.core.network.api.EvaluationApi;
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
public final class EvaluationRepository_Factory implements Factory<EvaluationRepository> {
  private final Provider<EvaluationApi> evaluationApiProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public EvaluationRepository_Factory(Provider<EvaluationApi> evaluationApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.evaluationApiProvider = evaluationApiProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public EvaluationRepository get() {
    return newInstance(evaluationApiProvider.get(), ioDispatcherProvider.get());
  }

  public static EvaluationRepository_Factory create(Provider<EvaluationApi> evaluationApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new EvaluationRepository_Factory(evaluationApiProvider, ioDispatcherProvider);
  }

  public static EvaluationRepository newInstance(EvaluationApi evaluationApi,
      CoroutineDispatcher ioDispatcher) {
    return new EvaluationRepository(evaluationApi, ioDispatcher);
  }
}
