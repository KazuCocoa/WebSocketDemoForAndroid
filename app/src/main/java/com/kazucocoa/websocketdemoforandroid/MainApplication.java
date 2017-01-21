package com.kazucocoa.websocketdemoforandroid;

import android.app.Application;

import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.configuration.Configuration;
import toothpick.registries.FactoryRegistryLocator;
import toothpick.registries.MemberInjectorRegistryLocator;
import toothpick.smoothie.module.SmoothieApplicationModule;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Toothpick.setConfiguration(Configuration.forProduction().disableReflection());
        MemberInjectorRegistryLocator.setRootRegistry(new com.kazucocoa.websocketdemoforandroid.MemberInjectorRegistry());
        FactoryRegistryLocator.setRootRegistry(new com.kazucocoa.websocketdemoforandroid.FactoryRegistry());

        Scope scope = Toothpick.openScope(this);
        scope.installModules(new SmoothieApplicationModule(this));
    }
}
