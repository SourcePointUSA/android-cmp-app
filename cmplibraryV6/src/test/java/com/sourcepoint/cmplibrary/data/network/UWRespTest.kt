package com.sourcepoint.cmplibrary.data.network

import com.fasterxml.jackson.jr.ob.JSON
import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.cmplibrary.data.network.TestUtilGson.Companion.jsonFile2String
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.junit.Test


class UWRespTest{

    @Test
    fun `parse response`(){
        val jsonContent = "unified_wrapper_resp.json".jsonFile2String()

        val ob = JSON.std.anyFrom(jsonContent)
        val map = JSON.std.mapFrom(jsonContent)
        val json = JSON.std.asString((map.get("gdpr") as DeferredMap).get("message"))
        println()


    }

}