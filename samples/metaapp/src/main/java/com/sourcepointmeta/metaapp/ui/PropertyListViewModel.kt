package com.sourcepointmeta.metaapp.ui

import androidx.lifecycle.ViewModel
import com.sourcepointmeta.metaapp.data.localdatasource.Property

class PropertyListViewModel : ViewModel() {
    fun fetchPropertyList() { }

    fun updateProperty(it: Property) { }

    fun deleteProperty(propertyName: String) { }
}
