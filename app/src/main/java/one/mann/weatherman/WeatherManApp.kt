package one.mann.weatherman

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import one.mann.weatherman.di.component.DaggerWeatherManAppComponent
import one.mann.weatherman.di.component.WeatherManAppComponent
import one.mann.weatherman.di.module.WeatherManAppModule
import javax.inject.Inject

internal class WeatherManApp : Application() {

    companion object {
        lateinit var appComponent: WeatherManAppComponent
            private set
    }

    @Inject
    lateinit var workerFactory: WorkerFactory

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerWeatherManAppComponent.builder()
                .weatherManAppModule(WeatherManAppModule(this))
                .build()
                .apply { injectApplication(this@WeatherManApp) }

        WorkManager.initialize(this, Configuration.Builder().setWorkerFactory(workerFactory).build())
    }
}