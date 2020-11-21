package com.mjob.feednewsstore4.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import com.mjob.feednewsstore4.databinding.ItemNewsBinding
import com.mjob.feednewsstore4.domain.model.News
import com.mjob.feednewsstore4.load

class NewsAdapter(private var newsList: List<News>, private val imageLoader: ImageLoader) : RecyclerView.Adapter<NewsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemNewsBinding =
            ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return NewsViewHolder(itemNewsBinding, imageLoader)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.bind(news)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    fun setData(data: List<News>) {
        newsList = data
    }
}

class NewsViewHolder(private val itemNewsBinding: ItemNewsBinding, private val imageLoader: ImageLoader) :
    RecyclerView.ViewHolder(itemNewsBinding.root) {
    fun bind(news: News) {
        itemNewsBinding.root.setPadding(0,0,0,0)

        with(news) {
            itemNewsBinding.author.text = author
            itemNewsBinding.category.text = category
            itemNewsBinding.title.text = title
            itemNewsBinding.description.text = description
            itemNewsBinding.publicationDate.text = publishedAt
            itemNewsBinding.image.load(image, imageLoader)
        }
    }
}

