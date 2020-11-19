package com.mjob.feednewsstore4.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.ImageLoader
import com.mjob.feednewsstore4.databinding.FragmentHomeBinding
import com.mjob.feednewsstore4.ui.home.adapter.NewsAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val homeViewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadNews()
    }

    private fun loadNews() {
        homeViewModel.newsLiveData.observe(viewLifecycleOwner, { result ->
            when {
                result.isSuccess() -> {
                    if (result.data.isNullOrEmpty()) {
                        Toast.makeText(context, "No news available", Toast.LENGTH_LONG).show()
                    } else {
                        binding.progressBar.visibility = INVISIBLE
                        binding.newsRecycler.adapter = NewsAdapter(result.data, imageLoader)
                        binding.newsRecycler.visibility = VISIBLE
                    }
                }
                result.isLoading() -> {
                    binding.progressBar.visibility = VISIBLE
                }
                else -> {

                }
            }

        })
    }
}