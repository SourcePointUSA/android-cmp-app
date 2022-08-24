package com.sourcepointmeta.metaapp.tv.edit

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import com.sourcepointmeta.metaapp.tv.detail.DetailPropertyActivity
import com.sourcepointmeta.metaapp.tv.detail.createAction
import com.sourcepointmeta.metaapp.tv.hideKeyboard
import com.sourcepointmeta.metaapp.ui.BaseState
import kotlinx.coroutines.* // ktlint-disable
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditProperty : GuidedStepSupportFragment() {

    var listener4Update: ((String) -> Unit)? = null

    companion object {

        fun instance(
            propertyName: String,
            propertyField: Int
        ) = EditProperty().apply {
            arguments = Bundle().apply {
                putString(PROPERTY_NAME, propertyName)
                putInt(PROPERTY_FIELD, propertyField)
            }
        }

        const val PROPERTY_NAME = "property_name"
        const val PROPERTY_FIELD = "property_field"
        const val CANCEL_ACTION = -1L
    }

    private val viewModel by viewModel<AddUpdatePropertyViewModelTv>()

    private val propertyTvDTO by lazy {
        val name = arguments?.getString(PROPERTY_NAME) ?: throw RuntimeException("Property name not set!!!")
        viewModel.fetchPropertySync(name)
    }

    private val field by lazy {
        PropertyField.values().find { it.ordinal == propertyField } ?: throw RuntimeException("Field doesn't exist!!!!")
    }

    private val propertyField: Int by lazy {
        arguments?.getInt(PROPERTY_FIELD) ?: -1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.liveData.observe(viewLifecycleOwner) {
            when (it) {
                is BaseState.StateTvPropertySaved -> {
                    requireActivity().onBackPressed()
                    listener4Update?.invoke(it.propName)
                    val i = Intent(requireActivity(), DetailPropertyActivity::class.java)
                    i.putExtra(DetailPropertyActivity.PROPERTY_NAME_KEY, it.propName)
                    startActivity(i)
                    Handler().postDelayed({ activity?.finish() }, 1000)
                }
                else -> {}
            }
        }
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(
            field.dialogTitle,
            field.dialogDescription,
            "",
            null
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        super.onCreateActions(actions, savedInstanceState)
        actions.apply {
            add(
                createAction(
                    field.ordinal.toLong(),
                    propertyTvDTO.getFieldById(field),
                    field.dialogDescription,
                    true,
                    field.inputType
                )
            )
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        hideKeyboard()
        MainScope().launch {
            when (action.id) {
                CANCEL_ACTION -> requireActivity().onBackPressed()
                else -> saveField(action)
            }
        }
    }

    private fun saveField(action: GuidedAction) {
        val field = PropertyField.values().find { it.ordinal == action.id.toInt() } ?: return
        val newField = action.title?.toString()
        viewModel.createOrUpdateProperty(propertyTvDTO, field, newField)
    }
}
