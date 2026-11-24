package com.sourcepoint.cmplibrary.integrations.didomi

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import org.junit.Assert.assertEquals
import org.junit.Test

class LifecycleExtTest {
    private class TestOwner : LifecycleOwner {
        val registry = LifecycleRegistry.createUnsafe(this)
        override val lifecycle: Lifecycle get() = registry
    }

    @Test
    fun `doOnDestroy runs the block once when the lifecycle is destroyed`() {
        val owner = TestOwner()
        var calls = 0
        owner.registry.currentState = Lifecycle.State.CREATED
        owner.lifecycle.doOnDestroy { calls++ }
        owner.registry.currentState = Lifecycle.State.RESUMED
        assertEquals(0, calls)

        owner.registry.currentState = Lifecycle.State.DESTROYED
        assertEquals(1, calls)
    }

    @Test
    fun `doOnDestroy runs the block right away when the lifecycle is already destroyed`() {
        val owner = TestOwner()
        owner.registry.currentState = Lifecycle.State.CREATED
        owner.registry.currentState = Lifecycle.State.DESTROYED
        var calls = 0
        owner.lifecycle.doOnDestroy { calls++ }
        assertEquals(1, calls)
    }

    @Test
    fun `doOnDestroy removes its observer after running`() {
        val owner = TestOwner()
        owner.registry.currentState = Lifecycle.State.CREATED
        owner.lifecycle.doOnDestroy { }
        assertEquals(1, owner.registry.observerCount)
        owner.registry.currentState = Lifecycle.State.DESTROYED
        assertEquals(0, owner.registry.observerCount)
    }
}
