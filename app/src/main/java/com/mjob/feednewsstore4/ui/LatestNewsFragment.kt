package com.mjob.feednewsstore4.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import coil.ImageLoader
import com.mjob.feednewsstore4.databinding.FragmentLatestNewsBinding
import com.mjob.feednewsstore4.domain.model.Result
import com.mjob.feednewsstore4.ui.adapter.NewsAdapter
import com.mjob.feednewsstore4.ui.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LatestNewsFragment : Fragment() {

    private lateinit var binding: FragmentLatestNewsBinding

    private val viewModel: NewsViewModel by activityViewModels()

    @Inject
    lateinit var imageLoader: ImageLoader

    var newsAdapter: NewsAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLatestNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        getLatestNews()
    }

    private fun getLatestNews() {
        lifecycleScope.launch {
            viewModel.getLatestNews()
        }
    }

    private fun initListeners() {
        viewModel.newsLiveData.observe(viewLifecycleOwner, { result ->
            if (result.isSuccess()) {
                if (result.data.isNullOrEmpty()) {
                    Toast.makeText(context, "No news available", Toast.LENGTH_LONG).show()
                } else {
                    newsAdapter = NewsAdapter(result.data, imageLoader)
                    binding.newsRecycler.adapter = newsAdapter
                    binding.progressBar.visibility = INVISIBLE
                    binding.newsRecycler.visibility = VISIBLE
                    stopRefreshingLayout()
                }
            } else if (result.isLoading()) {
                binding.progressBar.visibility = VISIBLE
            } else {
                Toast.makeText(context, "Error occured while fetching news", Toast.LENGTH_LONG)
                    .show()
                binding.progressBar.visibility = View.INVISIBLE
                stopRefreshingLayout()
            }
        })

        binding.searchBtn.setOnClickListener {
            val keyword = binding.keywordTextfield.editText?.text.toString()
            if (keyword.isNotEmpty()) {
                val directions: NavDirections =
                    LatestNewsFragmentDirections.actionLatestNewsFragmentToNewsWithKeywordFragment(
                        keyword
                    )
                NavHostFragment.findNavController(this).navigate(directions)
            }
        }

        binding.swipeToRefreshLayout.setOnRefreshListener {
            print("[Store 4] Swipe to refresh layout  \n")
            getLatestNews()
        }
    }

    private fun stopRefreshingLayout() {
        if (binding.swipeToRefreshLayout.isRefreshing) {
            binding.swipeToRefreshLayout.isRefreshing = false;
        }
    }
}