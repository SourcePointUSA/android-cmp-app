package com.sourcepointmeta.metaapp.tv.ui.edit

import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import com.sourcepointmeta.metaapp.tv.ui.hideKeyboard
import kotlinx.coroutines.*

class EditPropertyName(
    val title : String,
    private val description : String,
    val propertyName : String
    ) : GuidedStepSupportFragment() {

    enum class LOCAL_ACTION(val id : Long){
        SAVE(1),
        CANCEL(-1)
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(
            title,
            description,
            "",
            null
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        super.onCreateActions(actions, savedInstanceState)
        actions.add(
            GuidedAction.Builder(activity)
                .id(LOCAL_ACTION.SAVE.id)
                .title(propertyName)
                .description(description)
                .editable(true)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .build()
        )
        actions.add(
            GuidedAction.Builder(activity)
                .id(LOCAL_ACTION.CANCEL.id)
                .title("Cancel")
                .description("Exit without saving the changes")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .build()
        )
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        hideKeyboard()
        MainScope().launch {
            when(action.id){
                LOCAL_ACTION.SAVE.id-> {
                    Toast.makeText(requireActivity().baseContext, "Saving", Toast.LENGTH_SHORT).show();
                    withContext(Dispatchers.Default){
                        delay(2000)
                    }
                }
                LOCAL_ACTION.CANCEL.id-> {}
            }
            requireActivity().onBackPressed()
        }

    }

}