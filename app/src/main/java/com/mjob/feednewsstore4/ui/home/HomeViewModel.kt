package com.mjob.feednewsstore4.ui.home

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.mjob.feednewsstore4.domain.NewsRepository
import com.mjob.feednewsstore4.domain.model.News
import com.mjob.feednewsstore4.domain.model.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
    private val repository: NewsRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _newsLiveData = MutableLiveData<Result<List<News>>>()
    var newsLiveData: LiveData<Result<List<News>>>  = _newsLiveData

    init {
        viewModelScope.launch {
            getLatestNews()
        }
    }

    suspend fun getLatestNews() {
        repository.getLatestNews().collect {
            _newsLiveData.value = it
            newsLiveData = _newsLiveData
        }
    }
}
