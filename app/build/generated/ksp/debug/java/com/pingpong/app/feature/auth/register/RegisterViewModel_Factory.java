package com.pingpong.app.feature.auth.register;

import com.pingpong.app.core.data.AuthRepository;
import com.pingpong.app.core.data.CampusRepository;
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
public final class RegisterViewModel_Factory implements Factory<RegisterViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<CampusRepository> campusRepositoryProvider;

  public RegisterViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<CampusRepository> campusRepositoryProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.campusRepositoryProvider = campusRepositoryProvider;
  }

  @Override
  public RegisterViewModel get() {
    return newInstance(authRepositoryProvider.get(), campusRepositoryProvider.get());
  }

  public static RegisterViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<CampusRepository> campusRepositoryProvider) {
    return new RegisterViewModel_Factory(authRepositoryProvider, campusRepositoryProvider);
  }

  public static RegisterViewModel newInstance(AuthRepository authRepository,
      CampusRepository campusRepository) {
    return new RegisterViewModel(authRepository, campusRepository);
  }
}
