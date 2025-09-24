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
public final class SuperAdminAdminsViewModel_Factory implements Factory<SuperAdminAdminsViewModel> {
  private final Provider<SuperAdminRepository> superAdminRepositoryProvider;

  public SuperAdminAdminsViewModel_Factory(
      Provider<SuperAdminRepository> superAdminRepositoryProvider) {
    this.superAdminRepositoryProvider = superAdminRepositoryProvider;
  }

  @Override
  public SuperAdminAdminsViewModel get() {
    return newInstance(superAdminRepositoryProvider.get());
  }

  public static SuperAdminAdminsViewModel_Factory create(
      Provider<SuperAdminRepository> superAdminRepositoryProvider) {
    return new SuperAdminAdminsViewModel_Factory(superAdminRepositoryProvider);
  }

  public static SuperAdminAdminsViewModel newInstance(SuperAdminRepository superAdminRepository) {
    return new SuperAdminAdminsViewModel(superAdminRepository);
  }
}
