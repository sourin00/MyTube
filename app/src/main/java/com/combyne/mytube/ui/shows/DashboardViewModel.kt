package com.combyne.mytube.ui.shows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.combyne.mytube.ui.ADD_SHOW_RESULT_ERROR
import com.combyne.mytube.ui.ADD_SHOW_RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {
    fun onAddShowResult(result: Int) {
        when (result) {
            ADD_SHOW_RESULT_OK -> showSaveSuccessMessage("Show added successfully")
            ADD_SHOW_RESULT_ERROR -> showSaveErrorMessage("Failed to add show")
        }
    }

    private fun showSaveErrorMessage(text: String) = viewModelScope.launch {
        dashboardEventChannel.send(DashBoardEvent.ShowAddedMessage(text))
    }

    private fun showSaveSuccessMessage(text: String) = viewModelScope.launch {
        dashboardEventChannel.send(DashBoardEvent.ShowAddedMessage(text))
    }

    private val dashboardEventChannel = Channel<DashBoardEvent>()
    val dashBoardEvent = dashboardEventChannel.receiveAsFlow()

    sealed class DashBoardEvent {
        data class ShowAddedMessage(val msg: String) : DashBoardEvent()
    }
}