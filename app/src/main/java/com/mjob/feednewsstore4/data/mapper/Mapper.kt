package com.mjob.feednewsstore4.data.mapper

import com.mjob.feednewsstore4.data.local.model.LocalNews
import com.mjob.feednewsstore4.data.remote.model.FeedResponse
import com.mjob.feednewsstore4.data.remote.model.NewsResponse
import com.mjob.feednewsstore4.domain.model.News

fun FeedResponse.toNewsList(): List<News> {
    return this.news.map { it.toNews() }
}

fun NewsResponse.toNews(): News {
    with(this) {
        return News(
            author = author,
            category = category.first(),
            description = description,
            id = id,
            image = image,
            publishedAt = publicationDate.toString(),
            title = title,
            url = url
        )
    }
}

fun FeedResponse.toLocalNewsList(): List<LocalNews> {
    return this.news.map { it.toLocalNews() }
}

fun NewsResponse.toLocalNews(): LocalNews {
    with(this) {
        return LocalNews(
            author = author,
            category = (category.firstOrNull() ?: "General").capitalize(),
            description = description,
            id = id,
            image = image,
            publishedAt = publicationDate.toString(),
            title = title,
            url = url
        )
    }
}

fun List<LocalNews>.toLocalNewsList(): List<News> {
    return this.map { it.toNews() }
}

fun LocalNews.toNews(): News {
    with(this) {
        return News(
            author = author,
            category = category.capitalize(),
            description = description,
            id = id,
            image = image,
            publishedAt = publishedAt,
            title = title,
            url = url
        )
    }
}

