package com.combyne.mytube.ui.shows

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.combyne.mytube.R
import com.combyne.mytube.databinding.FragmentAddShowBinding
import com.combyne.mytube.ui.ADD_SHOW_REQUEST_KEY
import com.combyne.mytube.ui.ADD_SHOW_RESULT_KEY
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.text.DateFormat
import java.util.*

@AndroidEntryPoint
class AddShowFragment : Fragment(R.layout.fragment_add_show) {

    private val viewModel: AddShowViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAddShowBinding.bind(view)

        // initialize views
        binding.apply {
            txtFieldShowName.editText?.setText(viewModel.showName)
            txtFieldReleaseDate.editText?.setText(
                DateFormat.getDateInstance().format(Date(viewModel.releaseDate))
            )
            txtFieldSeasons.editText?.setText(viewModel.seasons.toInt().toString())
            txtFieldShowName.editText?.addTextChangedListener {
                viewModel.showName = it.toString()
            }
            txtFieldSeasons.editText?.addTextChangedListener {
                viewModel.seasons =
                    Integer.parseInt(if (it.isNullOrBlank()) "0" else it.toString()).toDouble()
            }
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setCalendarConstraints(
                        CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now())
                            .build()
                    )
                    .setTitleText(getString(R.string.select_date))
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
            txtFieldReleaseDate.setEndIconOnClickListener {
                datePicker.show(childFragmentManager, AddShowFragment::class.simpleName)
            }
            datePicker.addOnPositiveButtonClickListener {
                binding.txtFieldReleaseDate.editText?.setText(
                    DateFormat.getDateInstance().format(Date(it))
                )
                viewModel.releaseDate = it
            }
            btnSaveShow.setOnClickListener {
                viewModel.onSaveShowClick(requireContext())
            }
        }

        // wait for the event to be received only after the fragment is visible
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addShowEvent.collect { event ->
                when (event) {

                    // even while navigating back we use an event as we don't
                    // want view related stuff inside the view model.
                    is AddShowViewModel.AddShowEvent.NavigateBackWithResult -> {
                        binding.txtFieldShowName.clearFocus()
                        binding.txtFieldSeasons.clearFocus()
                        setFragmentResult(
                            ADD_SHOW_REQUEST_KEY,
                            bundleOf(ADD_SHOW_RESULT_KEY to event.result)
                        )
                        findNavController().popBackStack()
                    }

                    is AddShowViewModel.AddShowEvent.ShowInvalidInputMessage -> {
                        binding.txtFieldShowName.error = event.msg
                    }

                    // control loading state from event sent from view model
                    is AddShowViewModel.AddShowEvent.ToggleLoaderEvent -> {
                        if (event.show) {
                            binding.btnSaveShow.isEnabled = false
                            binding.btnSaveShow.text = getString(R.string.saving)
                            binding.btnSaveShow.alpha = 0.5f
                        } else {
                            binding.btnSaveShow.isEnabled = true
                            binding.btnSaveShow.text = getString(R.string.save)
                            binding.btnSaveShow.alpha = 1f
                        }
                    }
                }
            }
        }

    }
}