package com.sourcepointmeta.metaapp.tv.ui.edit

import android.os.Bundle
import android.widget.Toast
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import com.sourcepointmeta.metaapp.tv.ui.createAction
import com.sourcepointmeta.metaapp.tv.ui.detail.prop1
import com.sourcepointmeta.metaapp.tv.ui.edit.EditPropertyName.LOCAL_ACTION.CANCEL
import com.sourcepointmeta.metaapp.tv.ui.edit.EditPropertyName.LOCAL_ACTION.SAVE
import com.sourcepointmeta.metaapp.tv.ui.hideKeyboard
import com.sourcepointmeta.metaapp.tv.ui.toPropertyTvDTO
import kotlinx.coroutines.* // ktlint-disable

class EditPropertyName : GuidedStepSupportFragment() {

    companion object {
        fun instance(
            propertyName: String,
            dialogTitle: String,
            dialogDescription: String
        ) = EditPropertyName().apply {
            arguments = Bundle().apply {
                putString("property_name", propertyName)
                putString("dialogTitle", dialogTitle)
                putString("dialogDescription", dialogDescription)
            }
        }
    }

    enum class LOCAL_ACTION(val id: Long) {
        SAVE(1),
        CANCEL(-1)
    }

    private val propertyTvDTO by lazy {
        val name = arguments?.getString("property_name") ?: "" // throw RuntimeException("Property name not set!!!")
//        dataSource.fetchPropertyByNameSync(name) USE THIS TO FETCH the prop
        prop1.toPropertyTvDTO()
    }

    private val dialogTitle: String by lazy {
        arguments?.getString("dialogTitle") ?: ""
    }

    private val dialogDescription: String by lazy {
        arguments?.getString("dialogDescription") ?: ""
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(
            dialogTitle,
            dialogDescription,
            "",
            null
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        super.onCreateActions(actions, savedInstanceState)
        actions.apply {
            add(createAction(SAVE.id, propertyTvDTO.propertyName, dialogDescription, true))
            add(createAction(CANCEL.id, "Cancel", "Exit without saving the changes"))
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        hideKeyboard()
        MainScope().launch {
            when (action.id) {
                SAVE.id -> {
                    Toast.makeText(requireActivity().baseContext, "Saving", Toast.LENGTH_SHORT).show()
                    withContext(Dispatchers.Default) {
                        delay(2000)
                    }
                }
                CANCEL.id -> {}
            }
            requireActivity().onBackPressed()
        }
    }
}
