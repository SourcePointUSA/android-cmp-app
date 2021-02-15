package com.sourcepoint.cmplibrary.data.local

import android.content.SharedPreferences
import com.fasterxml.jackson.jr.ob.impl.DeferredMap

internal interface DataStorage : DataStorageGdpr, DataStorageCcpa {

    override val preference: SharedPreferences
    companion object

}
