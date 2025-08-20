package jp.co.abs.filedownloaderkamada4

import android.os.Build
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test

class OpenGalleryTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun openGalleryTest(){
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
        //GALLERYをクリック
        composeTestRule.onNodeWithText("GALLERYから選択",useUnmergedTree = true).performClick()
        val galleryActivity = device.findObject(UiSelector().text("Kamada_Picture"))
        galleryActivity.isCheckable
        //写真クリック
        device.click(250,600)
        // Activityの表示まで待つ等あれば待機する
        device.wait(Until.hasObject(By.res("TextField").depth(0)), 3000)
    }
}