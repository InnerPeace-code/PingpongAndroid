package com.pingpong.app.feature.student;

import com.pingpong.app.core.data.PaymentRepository;
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
public final class StudentRechargeViewModel_Factory implements Factory<StudentRechargeViewModel> {
  private final Provider<SessionRepository> sessionRepositoryProvider;

  private final Provider<PaymentRepository> paymentRepositoryProvider;

  public StudentRechargeViewModel_Factory(Provider<SessionRepository> sessionRepositoryProvider,
      Provider<PaymentRepository> paymentRepositoryProvider) {
    this.sessionRepositoryProvider = sessionRepositoryProvider;
    this.paymentRepositoryProvider = paymentRepositoryProvider;
  }

  @Override
  public StudentRechargeViewModel get() {
    return newInstance(sessionRepositoryProvider.get(), paymentRepositoryProvider.get());
  }

  public static StudentRechargeViewModel_Factory create(
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<PaymentRepository> paymentRepositoryProvider) {
    return new StudentRechargeViewModel_Factory(sessionRepositoryProvider, paymentRepositoryProvider);
  }

  public static StudentRechargeViewModel newInstance(SessionRepository sessionRepository,
      PaymentRepository paymentRepository) {
    return new StudentRechargeViewModel(sessionRepository, paymentRepository);
  }
}
