package com.sourcepointmeta.metaapp.tv.properties

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import com.sourcepointmeta.metaapp.R
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
            getString(R.string.title_deletell_dialog),
            getString(R.string.descr_deleteall_dialog),
            "",
            null
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        super.onCreateActions(actions, savedInstanceState)
        actions.apply {
            add(
                createAction(
                    DELETE_ALL_ACTION,
                    getString(R.string.title_deleteall_action),
                    getString(R.string.descr_deleteall_action),
                    false,
                    InputType.TYPE_CLASS_TEXT
                )
            )
            add(
                createAction(
                    CANCEL_ACTION,
                    getString(R.string.title_cancel_action),
                    getString(R.string.descr_cancel_action),
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
