package data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mjob.feednewsstore4.DispatcherProvider
import com.mjob.feednewsstore4.data.NewsRepositoryImpl
import com.mjob.feednewsstore4.data.local.Database
import com.mjob.feednewsstore4.data.local.model.LocalNews
import com.mjob.feednewsstore4.data.remote.NewsRemoteDataSource
import com.mjob.feednewsstore4.data.remote.model.FeedResponse
import com.mjob.feednewsstore4.data.remote.model.NewsResponse
import com.mjob.feednewsstore4.domain.NewsRepository
import com.mjob.feednewsstore4.domain.model.News
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.io.IOException
import java.util.*
import java.util.concurrent.CountDownLatch
import com.mjob.feednewsstore4.domain.model.Result
import kotlinx.coroutines.flow.collect
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response


@FlowPreview
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class NewsRepositoryImplTest {

    lateinit var db: Database

    val dispatcher = TestCoroutineDispatcher()

    @Mock
    lateinit var mockRemoteDataSource: NewsRemoteDataSource

    private lateinit var sut: NewsRepository

    private val testDispatcherProvider = object : DispatcherProvider {
        override fun default(): CoroutineDispatcher = dispatcher

        override fun io(): CoroutineDispatcher = dispatcher

        override fun main(): CoroutineDispatcher = dispatcher

        override fun unconfined(): CoroutineDispatcher = dispatcher
    }

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(
            context, Database::class.java
        ).build()

        MockitoAnnotations.initMocks(this)
    }


    @Test
    fun given_an_empty_database_when_getting_latest_news_then_source_of_truth_is_empty_and_data_from_fetcher_returns_data() {
        runBlocking {
            val apiResult = FeedResponse(
                news = listOf(
                    NewsResponse(
                        author = "Michael Jordan",
                        category = listOf(),
                        description = "Book about MJ",
                        id = "gh20ycg",
                        image = "",
                        publicationDate = Date(1222222),
                        title = "The last dance",
                        url = ""
                    )
                )
            )

            Mockito.`when`(mockRemoteDataSource.getLatestNews()).thenReturn(apiResult)

            val firstResultLatch = CountDownLatch(1)
            val secondResultLatch = CountDownLatch(1)
            val thirdResultLatch = CountDownLatch(1)

            var results: MutableList<Result<List<News>>>? = null

            sut = NewsRepositoryImpl(
                db.dao(),
                mockRemoteDataSource,
                testDispatcherProvider
            )

            var resultCounter = 0
            val job = async(testDispatcherProvider.io()) {
                sut.getLatestNews()
                    .collect {
                        if (results.isNullOrEmpty()) {
                            results = mutableListOf()
                        }
                        results?.add(it)
                        resultCounter += 1
                        when (resultCounter) {
                            1 -> firstResultLatch.countDown()
                            2 -> secondResultLatch.countDown()
                            3 -> thirdResultLatch.countDown()
                        }
                    }
            }

            firstResultLatch.await()
            assert(results?.size == 1)
            assert(results!![0].isSuccess())
            assert(results!![0].data!!.isEmpty())

            secondResultLatch.await()
            assert(results!![1].isLoading())

            thirdResultLatch.await()
            assert(results!![2].isSuccess())
            assert(results!![2].data!!.size == apiResult.news.size)

            job.cancelAndJoin()
        }
    }


    @Test
    fun given_a_non_empty_database_when_getting_latest_news_then_source_of_truth_is_returns_data_and_fetcher_also() {
        runBlocking {
            val apiResult = FeedResponse(
                news = listOf(
                    NewsResponse(
                        author = "Michael Jordan",
                        category = listOf(),
                        description = "Book about MJ",
                        id = "gh20ycg",
                        image = "",
                        publicationDate = Date(1222222),
                        title = "The last dance",
                        url = ""
                    ),
                    NewsResponse(
                        author = "JK Rowling",
                        category = listOf(),
                        description = "Fairy tail book",
                        id = "kjbjbjkb12",
                        image = "",
                        publicationDate = Date(1222222),
                        title = "Harry Potter",
                        url = ""
                    )
                )
            )

            Mockito.`when`(mockRemoteDataSource.getLatestNews()).thenReturn(apiResult)


            val newsInDB = listOf(
                LocalNews(
                    author = "",
                    category = "",
                    description = "",
                    id = "",
                    image = "",
                    publishedAt = "",
                    title = "",
                    url = ""
                )
            )

            db.dao().insert(
                newsInDB
            )

            sut = NewsRepositoryImpl(
                db.dao(),
                mockRemoteDataSource,
                testDispatcherProvider
            )

            val firstResultLatch = CountDownLatch(1)
            val secondResultLatch = CountDownLatch(1)
            val thirdResultLatch = CountDownLatch(1)

            var results: MutableList<Result<List<News>>>? = null

            var resultCounter = 0
            val job = async(testDispatcherProvider.io()) {
                sut.getLatestNews()
                    .collect {
                        if (results.isNullOrEmpty()) {
                            results = mutableListOf()
                        }
                        results?.add(it)
                        resultCounter += 1
                        when (resultCounter) {
                            1 -> firstResultLatch.countDown()
                            2 -> secondResultLatch.countDown()
                            3 -> thirdResultLatch.countDown()
                        }
                    }
            }

            firstResultLatch.await()
            assert(results!![0].isSuccess())
            assert(results!![0].data!!.size == newsInDB.size)

            secondResultLatch.await()
            assert(results!![1].isLoading())

            thirdResultLatch.await()
            assert(results!![2].isSuccess())
            assert(results!![2].data!!.size == apiResult.news.size)

            job.cancelAndJoin()
        }
    }

    @Test
    @Throws(Throwable::class)
    fun given_an_empty_source_of_truth_when_fetcher_throws_exception_then_result_is_an_exception() {
        runBlocking {
            val apiException = HttpException(
                Response.error<Any>(
                    500,
                    "Test Server Error".toResponseBody("application/json".toMediaTypeOrNull())
                )
            )

            Mockito.`when`(mockRemoteDataSource.getLatestNews()).thenThrow(apiException)

            sut = NewsRepositoryImpl(
                db.dao(),
                mockRemoteDataSource,
                testDispatcherProvider
            )

            val firstResultLatch = CountDownLatch(1)
            val secondResultLatch = CountDownLatch(1)
            val thirdResultLatch = CountDownLatch(1)

            var results: MutableList<Result<List<News>>>? = null

            var resultCounter = 0
            val job = async(testDispatcherProvider.io()) {
                sut.getLatestNews()
                    .collect {
                        if (results.isNullOrEmpty()) {
                            results = mutableListOf()
                        }
                        results?.add(it)
                        resultCounter += 1
                        when (resultCounter) {
                            1 -> firstResultLatch.countDown()
                            2 -> secondResultLatch.countDown()
                            3 -> thirdResultLatch.countDown()
                        }
                    }
            }


            firstResultLatch.await()
            assert(results!![0].isSuccess())

            secondResultLatch.await()
            assert(results!![1].isLoading())

            thirdResultLatch.await()
            assert(results!![2].isError())

            job.cancelAndJoin()
        }
    }


    @After
    @Throws(IOException::class)
    fun tearDown() {
        db.close()
    }
}