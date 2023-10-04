package com.sourcepoint.cmplibrary.util

import android.app.Activity
import com.sourcepoint.cmplibrary.assertNull
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import java.lang.ref.WeakReference

class ViewsManagerImplTest {

    @MockK
    private lateinit var activity: Activity

    @MockK
    private lateinit var connectionManager: ConnectionManager

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    @Test
    fun `EXECUTED the dispose() CHECK the weakReference is empty`() {
        val weakReference = WeakReference(activity)
        val sut = ViewsManager.create(weakReference, connectionManager, 3000)
        sut.dispose()
        weakReference.get().assertNull()
    }
}
