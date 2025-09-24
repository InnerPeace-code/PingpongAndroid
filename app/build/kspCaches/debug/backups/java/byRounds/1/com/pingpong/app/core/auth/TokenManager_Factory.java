package com.pingpong.app.core.auth;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class TokenManager_Factory implements Factory<TokenManager> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  public TokenManager_Factory(Provider<DataStore<Preferences>> dataStoreProvider) {
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public TokenManager get() {
    return newInstance(dataStoreProvider.get());
  }

  public static TokenManager_Factory create(Provider<DataStore<Preferences>> dataStoreProvider) {
    return new TokenManager_Factory(dataStoreProvider);
  }

  public static TokenManager newInstance(DataStore<Preferences> dataStore) {
    return new TokenManager(dataStore);
  }
}
