package com.pingpong.app.feature.admin;

import com.pingpong.app.core.auth.TokenProvider;
import com.pingpong.app.core.data.AdminRepository;
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
public final class AdminStudentManageViewModel_Factory implements Factory<AdminStudentManageViewModel> {
  private final Provider<AdminRepository> adminRepositoryProvider;

  private final Provider<TokenProvider> tokenProvider;

  public AdminStudentManageViewModel_Factory(Provider<AdminRepository> adminRepositoryProvider,
      Provider<TokenProvider> tokenProvider) {
    this.adminRepositoryProvider = adminRepositoryProvider;
    this.tokenProvider = tokenProvider;
  }

  @Override
  public AdminStudentManageViewModel get() {
    return newInstance(adminRepositoryProvider.get(), tokenProvider.get());
  }

  public static AdminStudentManageViewModel_Factory create(
      Provider<AdminRepository> adminRepositoryProvider, Provider<TokenProvider> tokenProvider) {
    return new AdminStudentManageViewModel_Factory(adminRepositoryProvider, tokenProvider);
  }

  public static AdminStudentManageViewModel newInstance(AdminRepository adminRepository,
      TokenProvider tokenProvider) {
    return new AdminStudentManageViewModel(adminRepository, tokenProvider);
  }
}
