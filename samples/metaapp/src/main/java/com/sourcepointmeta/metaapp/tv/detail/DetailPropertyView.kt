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
    property.text = holder.propertyName
    property.setOnClickListener { v -> clickListener(v, PROPERTY_NAME.ordinal) }
    account_id.text = holder.accountId.toString()
    account_id.setOnClickListener { v -> clickListener(v, ACCOUNT_ID.ordinal) }
    mess_language.text = holder.messageLanguage.toString()
    mess_language.setOnClickListener { v -> clickListener(v, MESSAGE_LANGUAGE.ordinal) }
    timeout.text = holder.timeout.toString()
    timeout.setOnClickListener { v -> clickListener(v, TIMEOUT.ordinal) }
}
