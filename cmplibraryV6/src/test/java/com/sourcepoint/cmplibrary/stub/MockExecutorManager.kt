package com.sourcepoint.cmplibrary.stub

import com.sourcepoint.cmplibrary.core.ExecutorManager

class MockExecutorManager : ExecutorManager {
    override fun executeOnMain(block: () -> Unit) {
        block()
    }

    override fun executeOnWorkerThread(block: () -> Unit) {
        block()
    }

    override fun executeOnSingleThread(block: () -> Unit) {
        block()
    }

    override fun dispose() { }
}
