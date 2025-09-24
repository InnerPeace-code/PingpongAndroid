package com.pingpong.app.feature.admin;

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
public final class AdminCoachManageViewModel_Factory implements Factory<AdminCoachManageViewModel> {
  private final Provider<AdminRepository> adminRepositoryProvider;

  public AdminCoachManageViewModel_Factory(Provider<AdminRepository> adminRepositoryProvider) {
    this.adminRepositoryProvider = adminRepositoryProvider;
  }

  @Override
  public AdminCoachManageViewModel get() {
    return newInstance(adminRepositoryProvider.get());
  }

  public static AdminCoachManageViewModel_Factory create(
      Provider<AdminRepository> adminRepositoryProvider) {
    return new AdminCoachManageViewModel_Factory(adminRepositoryProvider);
  }

  public static AdminCoachManageViewModel newInstance(AdminRepository adminRepository) {
    return new AdminCoachManageViewModel(adminRepository);
  }
}
