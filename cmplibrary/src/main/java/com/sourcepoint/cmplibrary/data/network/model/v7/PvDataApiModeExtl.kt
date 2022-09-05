package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.converter.failParam
import com.sourcepoint.cmplibrary.model.getFieldValue
import com.sourcepoint.cmplibrary.model.getMap
import org.json.JSONObject
import java.util.TreeMap

internal fun Map<String, Any?>.toPvDataResp(): PvDataResp {

    val gdpr = getMap("gdpr")?.toGdprPv()

    return PvDataResp(
        thisContent = JSONObject(this),
        gdprPv = gdpr
    )
}

internal fun Map<String, Any?>.toGdprPv(): GdprPv {
    val uuid = getFieldValue<String>("uuid") ?: failParam("PvDataResp - uuid")

    val cookies = getFieldValue<Iterable<Any?>>("cookies")
        ?.filterIsInstance(TreeMap::class.java)
        ?.map {
            JSONObject().apply {
                it.keys.forEach { key -> (key as? String)?.let { k -> put(key, it[k]) } }
            }
        }
        ?: failParam("cookies")

    return GdprPv(
        thisContent = JSONObject(this),
        cookies = cookies,
        uuid = uuid
    )
}
