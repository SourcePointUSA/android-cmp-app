package com.sourcepointmeta.metaapp.tv.properties

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import com.sourcepointmeta.metaapp.tv.detail.createAction
import com.sourcepointmeta.metaapp.tv.hideKeyboard
import com.sourcepointmeta.metaapp.tv.updatePropertyListAndGoBack
import com.sourcepointmeta.metaapp.ui.BaseState
import kotlinx.coroutines.* // ktlint-disable
import org.koin.androidx.viewmodel.ext.android.viewModel

class DeleteAllDialog : GuidedStepSupportFragment() {

    companion object {
        const val DELETE_ALL_ACTION = -1L
        const val CANCEL_ACTION = 1L
    }

    private val viewModel by viewModel<DeleteAllViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.liveData.observe(viewLifecycleOwner) {
            when (it) {
                is BaseState.StateDone -> requireActivity().updatePropertyListAndGoBack()
                else -> requireActivity().onBackPressed()
            }
        }
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(
            "Delete all properties",
            "Delete all saved properties in the Metaapp",
            "",
            null
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        super.onCreateActions(actions, savedInstanceState)
        actions.apply {
            add(
                createAction(
                    CANCEL_ACTION,
                    "Cancel",
                    "Return to the property list",
                    false,
                    InputType.TYPE_CLASS_TEXT
                )
            )
            add(
                createAction(
                    DELETE_ALL_ACTION,
                    "Delete all",
                    "Delete all",
                    false,
                    InputType.TYPE_CLASS_TEXT
                )
            )
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        hideKeyboard()
        MainScope().launch {
            when (action.id) {
                DELETE_ALL_ACTION -> viewModel.deleteAll()
                else -> requireActivity().onBackPressed()
            }
        }
    }
}
