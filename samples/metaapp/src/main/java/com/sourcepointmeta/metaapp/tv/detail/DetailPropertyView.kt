package com.sourcepointmeta.metaapp.tv.detail

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.tv.edit.PropertyField.* //ktlint-disable
import kotlinx.android.synthetic.main.detail_view_property.view.* //ktlint-disable

class DetailPropertyView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}

fun DetailPropertyView.bind(holder: Property, clickListener: (view: View, Int) -> Unit) {
    property.run {
        text = holder.propertyName
        setOnClickListener { v -> clickListener(v, PROPERTY_NAME.ordinal) }
    }
    account_id.run {
        text = holder.accountId.toString()
        setOnClickListener { v -> clickListener(v, ACCOUNT_ID.ordinal) }
    }
    mess_language.run {
        text = holder.messageLanguage.toString()
        setOnClickListener { v -> clickListener(v, MESSAGE_LANGUAGE.ordinal) }
    }
    timeout.run {
        text = holder.timeout.toString()
        setOnClickListener { v -> clickListener(v, TIMEOUT.ordinal) }
    }
    pm_id_gdpr.run {
        text = holder.gdprPmId.toString()
        setOnClickListener { v -> clickListener(v, GDPR_PM_ID.ordinal) }
    }
}
