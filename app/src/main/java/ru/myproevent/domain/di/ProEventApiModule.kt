package ru.myproevent.domain.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.myproevent.BuildConfig
import ru.myproevent.domain.models.IEmailHintDataSource
import ru.myproevent.domain.models.IProEventDataSource
import ru.myproevent.domain.models.repositories.local_proevent_user_token.ITokenLocalRepository
import javax.inject.Named
import javax.inject.Singleton

@Module
class ProEventApiModule {

    @Named("baseUrl")
    @Provides
    fun baseUrl(): String = BuildConfig.PROEVENT_API_URL

    @Provides
    fun provideProEventApi(
        @Named("baseUrl") baseUrl: String,
        gson: Gson,
        @Named("addTokenInterceptor") tokenInterceptor: Interceptor
    ): IProEventDataSource {
        val interceptor = HttpLoggingInterceptor()
            .apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
            .addNetworkInterceptor(tokenInterceptor)
            .addInterceptor(interceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(IProEventDataSource::class.java)
    }

    @Singleton
    @Named("addTokenInterceptor")
    @Provides
    fun addTokenInterceptor(tokenRepository: ITokenLocalRepository): Interceptor =
        object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                var request: Request = chain.request()

                tokenRepository.getTokenOrNull()?.let { token ->
                    request = request.newBuilder()
                        .addHeader("Authorization", "Bearer $token").build()
                }
                return chain.proceed(request)
            }
        }

    @Singleton
    @Provides
    fun provideGson() = GsonBuilder().create()

    @Provides
    fun provideEmailHintApi(
        gson: Gson,
    ): IEmailHintDataSource {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    var request: Request = chain.request()
                    request = request.newBuilder()
                        .addHeader("Authorization", "Token ${BuildConfig.EMAIL_HINT_API_TOKEN}")
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-Type", "application/json").build()
                    return chain.proceed(request)
                }
            })
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.EMAIL_HINT_API_URL)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(IEmailHintDataSource::class.java)
    }
}

