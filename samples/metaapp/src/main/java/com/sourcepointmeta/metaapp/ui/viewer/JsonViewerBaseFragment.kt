package com.sourcepointmeta.metaapp.ui.viewer

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.databinding.JsonviewerLayoutBinding

abstract class JsonViewerBaseFragment : Fragment() {

    private lateinit var binding: JsonviewerLayoutBinding

    val colorJsonKey: Int by lazy {
        TypedValue().apply { requireContext().theme.resolveAttribute(R.attr.colorJsonKey, this, true) }
            .data
    }

    val colorJsonValueText: Int by lazy {
        TypedValue().apply { requireContext().theme.resolveAttribute(R.attr.colorJsonValueText, this, true) }
            .data
    }

    val colorJsonValueNumber: Int by lazy {
        TypedValue().apply { requireContext().theme.resolveAttribute(R.attr.colorJsonValueNumber, this, true) }
            .data
    }

    val colorJsonValueUrl: Int by lazy {
        TypedValue().apply { requireContext().theme.resolveAttribute(R.attr.colorJsonValueUrl, this, true) }
            .data
    }

    val colorJsonValueNull: Int by lazy {
        TypedValue().apply { requireContext().theme.resolveAttribute(R.attr.colorJsonValueNull, this, true) }
            .data
    }

    val colorJsonValueBraces: Int by lazy {
        TypedValue().apply { requireContext().theme.resolveAttribute(R.attr.colorJsonValueBraces, this, true) }
            .data
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = JsonviewerLayoutBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.rvJson.run {
//            setKeyColor(colorJsonKey)
//            setValueTextColor(colorJsonValueText)
//            setValueNumberColor(colorJsonValueNumber)
//            setValueUrlColor(colorJsonValueUrl)
//            setValueNullColor(colorJsonValueNull)
//            setBracesColor(colorJsonValueBraces)
//            setTextSize(16.toFloat())
//        }
    }
}
