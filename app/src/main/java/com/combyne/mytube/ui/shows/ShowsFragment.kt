package com.combyne.mytube.ui.shows

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.combyne.mytube.R
import com.combyne.mytube.databinding.FragmentShowsBinding
import com.combyne.mytube.ui.ADD_SHOW_REQUEST_KEY
import com.combyne.mytube.ui.ADD_SHOW_RESULT_KEY
import com.combyne.mytube.util.ExtendedFabOnScrollListener
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ShowsFragment : Fragment(R.layout.fragment_shows) {
    private val viewModel: ShowsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentShowsBinding.bind(view)
        val showsAdapter = ShowsAdapter()
        binding.apply {
            extFabAddShow.setOnClickListener {
                val action = ShowsFragmentDirections.actionShowsFragmentToAddShowFragment()
                findNavController().navigate(action)
            }
            recyclerViewShows.apply {
                adapter = showsAdapter
                layoutManager = LinearLayoutManager(requireContext())
                // performance optimization
                setHasFixedSize(true)
                // shrink/extend animation for extended FAB on recycler view scroll
                addOnScrollListener(ExtendedFabOnScrollListener(extFabAddShow))
            }
        }
        viewModel.shows.observe(viewLifecycleOwner) {
            // Submit the entire list whenever there is change in the sqlite table.
            // DiffUtil will take care of updating only updated items.
            showsAdapter.submitList(it)
        }
        setFragmentResultListener(ADD_SHOW_REQUEST_KEY) { _, bundle ->
            val result = bundle.getInt(ADD_SHOW_RESULT_KEY)
            viewModel.onAddShowResult(result)
        }

        // wait for the event to be received only after the fragment is visible
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.showsEvent.collect { event ->
                when (event) {
                    // show success/error msg on add show
                    is ShowsViewModel.ShowsEvent.ShowAddedMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                    // control loading state from event sent from view model
                    is ShowsViewModel.ShowsEvent.ToggleLoaderEvent -> {
                        binding.progressBar.visibility = if (event.show) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }
}