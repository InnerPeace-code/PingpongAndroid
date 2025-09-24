package com.pingpong.app;

import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.internal.GeneratedEntryPoint;
import javax.annotation.processing.Generated;

@OriginatingElement(
    topLevelClass = PingpongActivity.class
)
@GeneratedEntryPoint
@InstallIn(ActivityComponent.class)
@Generated("dagger.hilt.android.processor.internal.androidentrypoint.InjectorEntryPointGenerator")
public interface PingpongActivity_GeneratedInjector {
  void injectPingpongActivity(PingpongActivity pingpongActivity);
}
