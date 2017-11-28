package com.egoriku.ladyhappy.di.app

import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import javax.inject.Singleton

@Module
class NavigationModule(private var cicerone: Cicerone<Router> = Cicerone.create()) {

    @Singleton
    @Provides
    fun provideRouter(): Router = cicerone.router

    @Singleton
    @Provides
    fun provideNavigationHolder(): NavigatorHolder = cicerone.navigatorHolder
}