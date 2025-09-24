package com.pingpong.app.feature.superadmin;

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
public final class SuperAdminSchoolsViewModel_Factory implements Factory<SuperAdminSchoolsViewModel> {
  private final Provider<SuperAdminRepository> superAdminRepositoryProvider;

  public SuperAdminSchoolsViewModel_Factory(
      Provider<SuperAdminRepository> superAdminRepositoryProvider) {
    this.superAdminRepositoryProvider = superAdminRepositoryProvider;
  }

  @Override
  public SuperAdminSchoolsViewModel get() {
    return newInstance(superAdminRepositoryProvider.get());
  }

  public static SuperAdminSchoolsViewModel_Factory create(
      Provider<SuperAdminRepository> superAdminRepositoryProvider) {
    return new SuperAdminSchoolsViewModel_Factory(superAdminRepositoryProvider);
  }

  public static SuperAdminSchoolsViewModel newInstance(SuperAdminRepository superAdminRepository) {
    return new SuperAdminSchoolsViewModel(superAdminRepository);
  }
}
