package com.pingpong.app.core.auth;

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
public final class TokenProvider_Factory implements Factory<TokenProvider> {
  private final Provider<TokenManager> tokenManagerProvider;

  public TokenProvider_Factory(Provider<TokenManager> tokenManagerProvider) {
    this.tokenManagerProvider = tokenManagerProvider;
  }

  @Override
  public TokenProvider get() {
    return newInstance(tokenManagerProvider.get());
  }

  public static TokenProvider_Factory create(Provider<TokenManager> tokenManagerProvider) {
    return new TokenProvider_Factory(tokenManagerProvider);
  }

  public static TokenProvider newInstance(TokenManager tokenManager) {
    return new TokenProvider(tokenManager);
  }
}
