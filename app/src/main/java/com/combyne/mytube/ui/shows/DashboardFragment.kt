package com.combyne.mytube.ui.shows

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.combyne.mytube.R
import com.combyne.mytube.databinding.FragmentDashboardBinding
import com.combyne.mytube.ui.ADD_SHOW_REQUEST_KEY
import com.combyne.mytube.ui.ADD_SHOW_RESULT_KEY
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private val viewModel: DashboardViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDashboardBinding.bind(view)

        binding.apply {
            btnListShows.setOnClickListener {
                val action = DashboardFragmentDirections.actionDashboardFragmentToShowsFragment()
                findNavController().navigate(action)
            }
            btnAddShow.setOnClickListener {
                val action = DashboardFragmentDirections.actionDashboardFragmentToAddShowFragment()
                findNavController().navigate(action)
            }
        }

        setFragmentResultListener(ADD_SHOW_REQUEST_KEY) { _, bundle ->
            val result = bundle.getInt(ADD_SHOW_RESULT_KEY)
            viewModel.onAddShowResult(result)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.dashBoardEvent.collect { event ->
                when (event) {
                    is DashboardViewModel.DashBoardEvent.ShowAddedMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}