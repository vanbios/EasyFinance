package com.androidcollider.easyfin.repository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class RepositoryModule {

    @Provides
    @Singleton
    @Memory
    public Repository provideMemoryRepository() {
        return new MemoryRepository();
    }

    @Provides
    @Singleton
    @Database
    public Repository provideDatabaseRepository() {
        return new DatabaseRepository();
    }

    @Provides
    @Singleton
    public Repository provideDataRepository(@Memory Repository memoryRepository,
                                            @Database Repository databaseRepository) {
        return new DataRepository(memoryRepository, databaseRepository);
    }
}