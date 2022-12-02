package com.sourcepointmeta.metaapp.ui.viewer

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sourcepointmeta.metaapp.R
import kotlinx.android.synthetic.main.activity_demo.*
import kotlinx.android.synthetic.main.add_property_fragment.*
import kotlinx.android.synthetic.main.jsonviewer_layout.*

abstract class JsonViewerBaseFragment : Fragment() {

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
        return inflater.inflate(R.layout.jsonviewer_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_json.run {
            setKeyColor(colorJsonKey)
            setValueTextColor(colorJsonValueText)
            setValueNumberColor(colorJsonValueNumber)
            setValueUrlColor(colorJsonValueUrl)
            setValueNullColor(colorJsonValueNull)
            setBracesColor(colorJsonValueBraces)
            setTextSize(16.toFloat())
        }
    }
}
