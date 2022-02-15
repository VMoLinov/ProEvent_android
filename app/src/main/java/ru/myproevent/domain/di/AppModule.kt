package ru.myproevent.domain.di

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.myproevent.ProEventApp

@Module
class AppModule(val app: ProEventApp) {

    @Provides
    fun provideApp(): ProEventApp {
        return app
    }

    @Provides
    fun provideApplicationResources(): Resources {
        return app.resources
    }

    @Provides
    fun provideUiScheduler(): Scheduler = AndroidSchedulers.mainThread()
}