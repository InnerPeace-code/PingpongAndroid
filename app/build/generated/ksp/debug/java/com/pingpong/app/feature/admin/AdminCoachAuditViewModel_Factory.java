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
public final class AdminCoachAuditViewModel_Factory implements Factory<AdminCoachAuditViewModel> {
  private final Provider<AdminRepository> adminRepositoryProvider;

  public AdminCoachAuditViewModel_Factory(Provider<AdminRepository> adminRepositoryProvider) {
    this.adminRepositoryProvider = adminRepositoryProvider;
  }

  @Override
  public AdminCoachAuditViewModel get() {
    return newInstance(adminRepositoryProvider.get());
  }

  public static AdminCoachAuditViewModel_Factory create(
      Provider<AdminRepository> adminRepositoryProvider) {
    return new AdminCoachAuditViewModel_Factory(adminRepositoryProvider);
  }

  public static AdminCoachAuditViewModel newInstance(AdminRepository adminRepository) {
    return new AdminCoachAuditViewModel(adminRepository);
  }
}
