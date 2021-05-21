package com.sourcepoint.cmplibrary.model.ext

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.util.file2String
import org.junit.Test

class ConsentRespExtKtTest {

    @Test
    fun `GIVEN an action sequence RETURN a (ConsentAction)`() {
        "action/gdpr_first_layer_accept_all.json".file2String().toConsentAction().actionType.assertEquals(ActionType.ACCEPT_ALL)
        "action/gdpr_first_layer_show_option.json".file2String().toConsentAction().actionType.assertEquals(ActionType.SHOW_OPTIONS)
        "action/gdpr_pm_accept_all.json".file2String().toConsentAction().actionType.assertEquals(ActionType.ACCEPT_ALL)
        "action/gdpr_pm_reject_all.json".file2String().toConsentAction().actionType.assertEquals(ActionType.REJECT_ALL)
        "action/gdpr_pm_save_and_exit.json".file2String().toConsentAction().actionType.assertEquals(ActionType.SAVE_AND_EXIT)
    }
}
