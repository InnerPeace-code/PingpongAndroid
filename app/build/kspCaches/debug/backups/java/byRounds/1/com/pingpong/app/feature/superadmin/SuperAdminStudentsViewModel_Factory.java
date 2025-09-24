package com.pingpong.app.feature.superadmin;

import com.pingpong.app.core.auth.TokenProvider;
import com.pingpong.app.core.data.SuperAdminRepository;
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
public final class SuperAdminStudentsViewModel_Factory implements Factory<SuperAdminStudentsViewModel> {
  private final Provider<SuperAdminRepository> superAdminRepositoryProvider;

  private final Provider<TokenProvider> tokenProvider;

  public SuperAdminStudentsViewModel_Factory(
      Provider<SuperAdminRepository> superAdminRepositoryProvider,
      Provider<TokenProvider> tokenProvider) {
    this.superAdminRepositoryProvider = superAdminRepositoryProvider;
    this.tokenProvider = tokenProvider;
  }

  @Override
  public SuperAdminStudentsViewModel get() {
    return newInstance(superAdminRepositoryProvider.get(), tokenProvider.get());
  }

  public static SuperAdminStudentsViewModel_Factory create(
      Provider<SuperAdminRepository> superAdminRepositoryProvider,
      Provider<TokenProvider> tokenProvider) {
    return new SuperAdminStudentsViewModel_Factory(superAdminRepositoryProvider, tokenProvider);
  }

  public static SuperAdminStudentsViewModel newInstance(SuperAdminRepository superAdminRepository,
      TokenProvider tokenProvider) {
    return new SuperAdminStudentsViewModel(superAdminRepository, tokenProvider);
  }
}
