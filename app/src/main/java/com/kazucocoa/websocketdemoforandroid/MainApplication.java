package com.kazucocoa.websocketdemoforandroid;

import android.app.Application;

import toothpick.Configuration;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.registries.FactoryRegistryLocator;
import toothpick.registries.MemberInjectorRegistryLocator;
import toothpick.smoothie.module.SmoothieApplicationModule;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Configuration.setConfiguration(Configuration.reflectionFree());
        MemberInjectorRegistryLocator.setRootRegistry(new com.kazucocoa.websocketdemoforandroid.MemberInjectorRegistry());
        FactoryRegistryLocator.setRootRegistry(new com.kazucocoa.websocketdemoforandroid.FactoryRegistry());

        Scope scope = Toothpick.openScope(this);
        scope.installModules(new SmoothieApplicationModule(this));
    }
}
