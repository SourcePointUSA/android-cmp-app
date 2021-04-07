package com.sourcepoint.cmplibrary.stub

import com.sourcepoint.cmplibrary.util.ExecutorManager

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
