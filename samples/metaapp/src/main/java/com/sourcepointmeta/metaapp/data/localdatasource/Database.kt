package com.sourcepointmeta.metaapp.data.localdatasource

import android.content.Context
import com.sourcepointmeta.metaapp.db.MetaAppDB
import com.squareup.sqldelight.android.AndroidSqliteDriver

fun createDb(appContext: Context): MetaAppDB = MetaAppDB(AndroidSqliteDriver(MetaAppDB.Schema, appContext, "newmetaapp.db"))
