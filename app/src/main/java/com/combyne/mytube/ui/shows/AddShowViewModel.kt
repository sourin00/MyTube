package com.combyne.mytube.ui.shows

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.combyne.mytube.R
import com.combyne.mytube.repository.ShowsRepository
import com.combyne.mytube.ui.ADD_SHOW_RESULT_ERROR
import com.combyne.mytube.ui.ADD_SHOW_RESULT_OK
import com.combyne.mytube.util.DateUtils
import com.combyne.mytube.util.StateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddShowViewModel @Inject constructor(
    // to retain user input data and repopulate on orientation change
    private val state: SavedStateHandle,
    private val showsRepository: ShowsRepository
) : ViewModel() {

    // method invoked on save button click from AddShowFragment
    fun onSaveShowClick(@ApplicationContext context: Context) {
        if (showName.isBlank()) {
            showInvalidInputMessage(context.getString(R.string.show_name_blank_err))
            return
        }
        createShow(showName, DateUtils.getDateTimeString(releaseDate), seasons)
    }

    // send empty show name error to fragment using channel
    private fun showInvalidInputMessage(s: String) = viewModelScope.launch {
        addShowEventChannel.send(AddShowEvent.ShowInvalidInputMessage(s))
    }

    // send user input data to Repository to perform API call and save the new TV Show
    private fun createShow(show: String, dateTimeString: String, seasons: Double) =
        viewModelScope.launch {
            showsRepository.createMovie(show, dateTimeString, seasons, object : StateListener {
                override fun onComplete(s: StateListener.State) = when (s) {
                    StateListener.State.SUCCESS -> onSuccessEvent()
                    StateListener.State.LOADING -> onLoadingEvent()
                    StateListener.State.ERROR -> onErrorEvent()
                }
            })
        }

    private fun onErrorEvent() {
        viewModelScope.launch {
            addShowEventChannel.send(AddShowEvent.ToggleLoaderEvent(false))
            addShowEventChannel.send(AddShowEvent.NavigateBackWithResult(ADD_SHOW_RESULT_ERROR))
        }
    }

    private fun onLoadingEvent() {
        viewModelScope.launch {
            addShowEventChannel.send(AddShowEvent.ToggleLoaderEvent(true))
        }
    }

    private fun onSuccessEvent() {
        viewModelScope.launch {
            addShowEventChannel.send(AddShowEvent.ToggleLoaderEvent(false))
            addShowEventChannel.send(AddShowEvent.NavigateBackWithResult(ADD_SHOW_RESULT_OK))
        }
    }

    // update data from savedInstanceState
    var showName = state.get<String>("showName") ?: ""
        set(value) {
            field = value
            state.set("showName", value)
        }
    // update data from savedInstanceState
    var releaseDate = state.get<Long>("releaseDate") ?: System.currentTimeMillis()
        set(value) {
            field = value
            state.set("releaseDate", value)
        }
    // update data from savedInstanceState
    var seasons = state.get<Double>("seasons") ?: 0.0
        set(value) {
            field = value
            state.set("seasons", value)
        }

    private val addShowEventChannel = Channel<AddShowEvent>()
    val addShowEvent = addShowEventChannel.receiveAsFlow()

    sealed class AddShowEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddShowEvent()
        data class NavigateBackWithResult(val result: Int) : AddShowEvent()
        data class ToggleLoaderEvent(val show: Boolean) : AddShowEvent()
    }
}