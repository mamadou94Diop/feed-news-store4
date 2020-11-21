package com.mjob.feednewsstore4.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.ImageLoader
import com.mjob.feednewsstore4.databinding.FragmentNewsWithKeywordBinding
import com.mjob.feednewsstore4.ui.adapter.NewsAdapter
import com.mjob.feednewsstore4.ui.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NewsWithKeywordFragment : Fragment() {

    private lateinit var binding: FragmentNewsWithKeywordBinding

    private val viewModel: NewsViewModel by activityViewModels()

    private val args: NewsWithKeywordFragmentArgs by navArgs()

    @Inject
    lateinit var imageLoader: ImageLoader


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsWithKeywordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.keywordTextfield.editText?.setText(args.keyword)
        initListeners()
        loadWithNewsKeyword(args.keyword)
    }

    private fun loadWithNewsKeyword(keyword: String) {
        lifecycleScope.launch {
            viewModel.getNewsWithKeyword(keyword)
        }
    }

    private fun initListeners() {
        viewModel.newsLiveData.observe(viewLifecycleOwner, { result ->
            when {
                result.isSuccess() -> {
                    if (result.data.isNullOrEmpty()) {
                        Toast.makeText(context, "No news available", Toast.LENGTH_LONG).show()
                    } else {
                        binding.progressBar.visibility = View.INVISIBLE
                        binding.newsRecycler.adapter =
                            NewsAdapter(result.data, imageLoader)
                        binding.newsRecycler.visibility = View.VISIBLE
                    }
                }
                result.isLoading() -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                else -> {
                    Toast.makeText(context, "No news available", Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.INVISIBLE

                }
            }

        })

        binding.searchBtn.setOnClickListener {
            val keyword = binding.keywordTextfield.editText?.text.toString()
            if (keyword.isNotEmpty()) {
                loadWithNewsKeyword(keyword)
            }
        }
    }
}