package com.androidcollider.easyfin.common.app;

import com.androidcollider.easyfin.common.MainActivity;
import com.androidcollider.easyfin.repository.RepositoryModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Ihor Bilous
 */

@Singleton
@Component(modules = {
        AppModule.class,
        RepositoryModule.class
})
public interface AppComponent {

    void inject(MainActivity mainActivity);
}
