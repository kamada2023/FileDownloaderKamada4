package jp.co.abs.filedownloaderkamada4

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import org.junit.Rule
import org.junit.Test

class DialogLogicTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<FileDownloaderActivity>()

    @SuppressLint("UnrememberedMutableState")
    @Test
    fun testFinishCalled(){
        composeTestRule.activity.setContent {
            val exitTheApplication = mutableStateOf(true)
            val context = LocalContext.current
            if(exitTheApplication.value){
                //明示的にアクティビティ終了
                (context as? Activity)?.finish()
            }
        }
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED
        }
    }
}