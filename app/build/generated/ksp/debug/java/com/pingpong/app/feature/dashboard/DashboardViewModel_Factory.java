package com.pingpong.app.feature.dashboard;

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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<AdminRepository> adminRepositoryProvider;

  public DashboardViewModel_Factory(Provider<AdminRepository> adminRepositoryProvider) {
    this.adminRepositoryProvider = adminRepositoryProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(adminRepositoryProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<AdminRepository> adminRepositoryProvider) {
    return new DashboardViewModel_Factory(adminRepositoryProvider);
  }

  public static DashboardViewModel newInstance(AdminRepository adminRepository) {
    return new DashboardViewModel(adminRepository);
  }
}
