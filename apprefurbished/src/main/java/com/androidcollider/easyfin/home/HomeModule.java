package com.androidcollider.easyfin.home;

import com.androidcollider.easyfin.common.repository.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class HomeModule {

    @Provides
    HomeMVP.Presenter provideHomeMVPPresenter(HomeMVP.Model model) {
        return new HomePresenter(model);
    }

    @Provides
    HomeMVP.Model provideHomeMVPModel(Repository repository) {
        return new HomeModel(repository);
    }
}