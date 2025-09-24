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
public final class SuperAdminCoachesViewModel_Factory implements Factory<SuperAdminCoachesViewModel> {
  private final Provider<SuperAdminRepository> superAdminRepositoryProvider;

  public SuperAdminCoachesViewModel_Factory(
      Provider<SuperAdminRepository> superAdminRepositoryProvider) {
    this.superAdminRepositoryProvider = superAdminRepositoryProvider;
  }

  @Override
  public SuperAdminCoachesViewModel get() {
    return newInstance(superAdminRepositoryProvider.get());
  }

  public static SuperAdminCoachesViewModel_Factory create(
      Provider<SuperAdminRepository> superAdminRepositoryProvider) {
    return new SuperAdminCoachesViewModel_Factory(superAdminRepositoryProvider);
  }

  public static SuperAdminCoachesViewModel newInstance(SuperAdminRepository superAdminRepository) {
    return new SuperAdminCoachesViewModel(superAdminRepository);
  }
}
