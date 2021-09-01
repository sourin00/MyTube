package com.combyne.mytube.ui.shows

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.combyne.mytube.data.Show
import com.combyne.mytube.repository.ShowsRepository
import com.combyne.mytube.ui.ADD_SHOW_RESULT_ERROR
import com.combyne.mytube.ui.ADD_SHOW_RESULT_OK
import com.combyne.mytube.util.StateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowsViewModel @Inject constructor(
    showsRepository: ShowsRepository
) : ViewModel() {
    fun onAddShowResult(result: Int) {
        when (result) {
            ADD_SHOW_RESULT_OK -> showSaveSuccessMessage("Show added successfully")
            ADD_SHOW_RESULT_ERROR -> showSaveErrorMessage("Failed to add show")
        }
    }

    private fun showSaveErrorMessage(text: String) = viewModelScope.launch {
        showsEventChannel.send(ShowsEvent.ShowAddedMessage(text))
    }

    private fun showSaveSuccessMessage(text: String) = viewModelScope.launch {
        showsEventChannel.send(ShowsEvent.ShowAddedMessage(text))
    }

    val shows: LiveData<List<Show>> = showsRepository.queryShows(object : StateListener {
        override fun onComplete(s: StateListener.State) = when (s) {
            StateListener.State.SUCCESS -> onSuccessEvent()
            StateListener.State.LOADING -> onLoadingEvent()
            StateListener.State.ERROR -> onErrorEvent()
        }
    }).asLiveData()

    private fun onErrorEvent() {
        viewModelScope.launch {
            showsEventChannel.send(ShowsEvent.ToggleLoaderEvent(false))
        }
    }

    private fun onLoadingEvent() {
        viewModelScope.launch {
            showsEventChannel.send(ShowsEvent.ToggleLoaderEvent(true))
        }
    }

    private fun onSuccessEvent() {
        viewModelScope.launch {
            showsEventChannel.send(ShowsEvent.ToggleLoaderEvent(false))
        }
    }

    private val showsEventChannel = Channel<ShowsEvent>()
    val showsEvent = showsEventChannel.receiveAsFlow()

    sealed class ShowsEvent {
        data class ShowAddedMessage(val msg: String) : ShowsEvent()
        data class ToggleLoaderEvent(val show: Boolean) : ShowsEvent()
    }
}