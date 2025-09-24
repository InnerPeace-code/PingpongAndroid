package com.pingpong.app.feature.evaluation;

import com.pingpong.app.core.data.EvaluationRepository;
import com.pingpong.app.core.data.SessionRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class EvaluationViewModel_Factory implements Factory<EvaluationViewModel> {
  private final Provider<EvaluationRepository> evaluationRepositoryProvider;

  private final Provider<SessionRepository> sessionRepositoryProvider;

  public EvaluationViewModel_Factory(Provider<EvaluationRepository> evaluationRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    this.evaluationRepositoryProvider = evaluationRepositoryProvider;
    this.sessionRepositoryProvider = sessionRepositoryProvider;
  }

  @Override
  public EvaluationViewModel get() {
    return newInstance(evaluationRepositoryProvider.get(), sessionRepositoryProvider.get());
  }

  public static EvaluationViewModel_Factory create(
      Provider<EvaluationRepository> evaluationRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    return new EvaluationViewModel_Factory(evaluationRepositoryProvider, sessionRepositoryProvider);
  }

  public static EvaluationViewModel newInstance(EvaluationRepository evaluationRepository,
      SessionRepository sessionRepository) {
    return new EvaluationViewModel(evaluationRepository, sessionRepository);
  }
}
