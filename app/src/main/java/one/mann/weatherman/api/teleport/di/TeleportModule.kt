package one.mann.weatherman.api.teleport.di

import dagger.Module
import dagger.Provides
import one.mann.weatherman.api.teleport.TeleportService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

@Module
internal class TeleportModule {

    companion object {
        private const val BASE_URL = "https://api.teleport.org/api/locations/"
    }

    @Provides
//    @Singleton
    @Named("TeleportInstance")
    fun provideTeleportRestAdapter(gsonConverterFactory: GsonConverterFactory): Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .build()

    @Provides
//    @Singleton
    fun provideTeleportService(@Named("TeleportInstance") retrofit: Retrofit): TeleportService =
            retrofit.create(TeleportService::class.java)
}