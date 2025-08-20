package jp.co.abs.filedownloaderkamada4

import android.os.Build
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NavTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup(){
        // UiDeviceのインスタンス化
        val device = UiDevice.getInstance(getInstrumentation())
        composeTestRule.setContent {
            FileDownloaderApp()
        }

        // 許可するボタンのindexを取得
        fun getAllowButtonIndex() =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                1
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                0
            } else {
                1
            }
        // 認可ダイアログから許可するボタンのindexを指定してオブジェクトを取得
        val arrowPermission = device.findObject(
            UiSelector()
                .clickable(true)
                .index(getAllowButtonIndex())
        )
        // 取得したオブジェクトが画面上に存在すれば押下
        if (arrowPermission.exists()) {
            arrowPermission.click()
        }
        // 動かせるまで待機
        composeTestRule.waitForIdle()
    }

    @Test
    fun firstScreen(){
        //初期表示
       composeTestRule.onNodeWithTag("FileDownloaderScreen").assertIsDisplayed()
    }

    @Test
    fun gotoHistoryScreen(){
        //履歴画面を表示
        composeTestRule.onNodeWithContentDescription("履歴",useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithTag("HistoryScreen").assertIsDisplayed()
        Thread.sleep(500)
    }


    @Test
    fun gotoFileDownloaderScreen(){
        //履歴画面を表示
        composeTestRule.onNodeWithContentDescription("履歴",useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithTag("HistoryScreen").assertIsDisplayed()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("ダウンロード",useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithTag("FileDownloaderScreen").assertIsDisplayed()
        Thread.sleep(500)
    }
}