package com.mjob.feednewsstore4.di

import android.content.Context
import androidx.room.Room
import coil.ImageLoader
import coil.util.DebugLogger
import com.google.gson.GsonBuilder
import com.mjob.feednewsstore4.BuildConfig
import com.mjob.feednewsstore4.DispatcherProvider
import com.mjob.feednewsstore4.DispatcherProviderImpl
import com.mjob.feednewsstore4.R
import com.mjob.feednewsstore4.data.NewsRepositoryImpl
import com.mjob.feednewsstore4.data.local.Database
import com.mjob.feednewsstore4.data.remote.NewsRemoteDataSource
import com.mjob.feednewsstore4.domain.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@InstallIn(ApplicationComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    var request: Request = chain.request()
                    val url: HttpUrl =
                        request.url.newBuilder().addQueryParameter("apiKey", BuildConfig.API_KEY)
                            .build()
                    request = request.newBuilder().url(url).build()
                    return chain.proceed(request)
                }
            })
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Singleton
    @Provides
    fun provideRemoteDataSource(retrofit: Retrofit): NewsRemoteDataSource {
        return retrofit.create(NewsRemoteDataSource::class.java)
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): Database {
        val databaseName = "news_db"
        return Room.databaseBuilder(context.applicationContext, Database::class.java, databaseName)
            .build()
    }

    @Provides
    fun provideDispatcherProvider(): DispatcherProvider = DispatcherProviderImpl()

    @FlowPreview
    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    @Provides
    fun provideRepository(
        database: Database,
        remoteDataSource: NewsRemoteDataSource,
        dispatcherProvider: DispatcherProvider
    ): NewsRepository {
        return NewsRepositoryImpl(database.dao(), remoteDataSource, dispatcherProvider)
    }

    @Singleton
    @Provides
    fun providesImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .availableMemoryPercentage(1.0)
            .crossfade(true)
            .crossfade(2000)
            .error(R.drawable.no_image)
            .logger(
                DebugLogger()
            )
            .build()
    }
}