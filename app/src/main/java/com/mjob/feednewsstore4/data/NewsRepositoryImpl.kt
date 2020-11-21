package com.mjob.feednewsstore4.data

import com.dropbox.android.external.store4.*
import com.mjob.feednewsstore4.data.local.dao.NewsLocalDataSource
import com.mjob.feednewsstore4.data.local.model.LocalNews
import com.mjob.feednewsstore4.data.mapper.toLocalNewsList
import com.mjob.feednewsstore4.data.mapper.toNewsList
import com.mjob.feednewsstore4.data.remote.NewsRemoteDataSource
import com.mjob.feednewsstore4.domain.NewsRepository
import com.mjob.feednewsstore4.domain.model.News
import com.mjob.feednewsstore4.domain.model.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow


@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@FlowPreview
class NewsRepositoryImpl(
    private val localDataSource: NewsLocalDataSource,
    private val remoteDataSource: NewsRemoteDataSource
) : NewsRepository {

    private val storeForLatestNews: Store<String, List<LocalNews>> = StoreBuilder.from(
        fetcher = Fetcher.of { _: String ->
            remoteDataSource.getLatestNews().toLocalNewsList()
        },
        sourceOfTruth = SourceOfTruth.Companion.of(
            reader = { key -> localDataSource.read() },
            writer = { key: String, input: List<LocalNews> -> localDataSource.update(input) }
        )
    ).build()

    private val storeForNewsByKeyword: Store<String, List<News>> = StoreBuilder.from(
        fetcher = Fetcher.of { key: String ->
            remoteDataSource.getNewsByKeyword(key).toNewsList()
        }
    ).build()

    override suspend fun getLatestNews(): Flow<Result<List<News>>> {
        return flow {
            storeForLatestNews.stream(StoreRequest.cached(key = "latest_news", refresh = true))
                .collect { response: StoreResponse<List<LocalNews>> ->
                    when (response) {
                        is StoreResponse.Loading -> {
                            print("[Store 4] Loading from ${response.origin} \n")
                            emit(Result.loading<List<News>>())
                        }
                        is StoreResponse.Error ->{
                            print("[Store 4] Error from  ${response.origin}  \n")
                            emit(Result.error<List<News>>())
                        }
                        is StoreResponse.Data -> {
                            val data = response.value.toLocalNewsList()
                            print("[Store 4] Data from ${response.origin}  with ${response.value.size} elements \n")
                            emit(Result.success(data))
                        }
                        is StoreResponse.NoNewData -> emit(Result.success(emptyList<News>()))
                    }
                }
        }
    }

    override suspend fun getNewsByKeyword(keyword: String): Flow<Result<List<News>>> {
        return flow {
            emit(Result.loading())
            try {
                val data = storeForNewsByKeyword.fresh(keyword)
                emit(Result.success(data))
            } catch (error: Throwable) {
                emit(Result.error<List<News>>())
            }
        }
    }
}