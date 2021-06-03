package com.sourcepointmeta.metaapp

import com.example.uitestutil.isDisplayedAllOfByResId

class TestUseCaseMeta {
    companion object {
        fun checkMessageDisplayed() {
            isDisplayedAllOfByResId(resId = R.id.message)
        }
    }
}
