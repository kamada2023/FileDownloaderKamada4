package jp.co.abs.filedownloaderkamada4

import android.os.Build
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test

class HistoryScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun historyScreenTest(){
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
        //履歴画面を表示
        composeTestRule.onNodeWithContentDescription("履歴",useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithTag("HistoryScreen").assertIsDisplayed()
        //表示確認
        composeTestRule.onNodeWithContentDescription("image_1").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("image_1").performClick()
        // 動かせるまで待機
        composeTestRule.waitForIdle()
        //クリック処理(戻る)
        device.pressBack()
        // Activityの表示まで待つ等あれば待機する
        device.wait(Until.hasObject(By.res("HistoryScreen").depth(0)), 3000)
        //スワイプ処理
        composeTestRule.onNodeWithTag("HistoryScreen").performTouchInput { swipeUp() }
        Thread.sleep(500)
    }
}