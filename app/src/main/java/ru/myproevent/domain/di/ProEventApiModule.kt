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
import ru.myproevent.domain.models.IProEventDataSource
import ru.myproevent.domain.models.repositories.local_proevent_user_token.ITokenLocalRepository
import javax.inject.Named
import javax.inject.Singleton


@Module
class ProEventApiModule {

    @Named("baseUrl")
    @Provides
    fun baseUrl(): String = "http://178.249.69.107:8762/api/v1/"

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
}

