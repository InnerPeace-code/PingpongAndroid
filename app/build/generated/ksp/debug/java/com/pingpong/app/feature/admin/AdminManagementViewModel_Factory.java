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
public final class AdminManagementViewModel_Factory implements Factory<AdminManagementViewModel> {
  private final Provider<AdminRepository> adminRepositoryProvider;

  public AdminManagementViewModel_Factory(Provider<AdminRepository> adminRepositoryProvider) {
    this.adminRepositoryProvider = adminRepositoryProvider;
  }

  @Override
  public AdminManagementViewModel get() {
    return newInstance(adminRepositoryProvider.get());
  }

  public static AdminManagementViewModel_Factory create(
      Provider<AdminRepository> adminRepositoryProvider) {
    return new AdminManagementViewModel_Factory(adminRepositoryProvider);
  }

  public static AdminManagementViewModel newInstance(AdminRepository adminRepository) {
    return new AdminManagementViewModel(adminRepository);
  }
}
