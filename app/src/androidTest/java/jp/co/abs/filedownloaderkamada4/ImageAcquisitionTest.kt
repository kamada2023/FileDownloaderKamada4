package jp.co.abs.filedownloaderkamada4

import android.os.Build
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageAcquisitionTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun appTest(){
        // UiDeviceのインスタンス化
        val device = UiDevice.getInstance(getInstrumentation())
        composeTestRule.setContent {
            FileDownloaderApp()
        }

        // 許可するボタンのindexを取得
        fun getAllowButtonIndex() =
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                1
            }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                0
            }else {
                1
            }
        // 認可ダイアログから許可するボタンのindexを指定してオブジェクトを取得
        val arrowPermission = device.findObject(
            UiSelector()
            .clickable(true)
            .index(getAllowButtonIndex())
        )
        // 取得したオブジェクトが画面上に存在すれば押下
        if (arrowPermission.exists()){arrowPermission.click()}
        // Activityの表示まで待つ等あれば待機する
        device.wait(Until.hasObject(By.res("TextField").depth(0)),3000)
        // 動かせるまで待機
        composeTestRule.waitForIdle()
        // 画面終了を確認
        composeTestRule.onNodeWithTag("TextField").assertExists()

    }

    @Test
    fun keyboardTest(){
        // UiDeviceのインスタンス化
        val device = UiDevice.getInstance(getInstrumentation())
        composeTestRule.setContent {
            FileDownloaderApp()
        }

        // 許可するボタンのindexを取得
        fun getAllowButtonIndex() =
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                1
            }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                0
            }else {
                1
            }
        // 認可ダイアログから許可するボタンのindexを指定してオブジェクトを取得
        val arrowPermission = device.findObject(
            UiSelector()
                .clickable(true)
                .index(getAllowButtonIndex())
        )
        // 取得したオブジェクトが画面上に存在すれば押下
        if (arrowPermission.exists()){arrowPermission.click()}
        // Activityの表示まで待つ等あれば待機する
        device.wait(Until.hasObject(By.res("TextField").depth(0)),3000)
        // 動かせるまで待機
        composeTestRule.waitForIdle()
        // 画面終了を確認
        composeTestRule.onNodeWithTag("TextField").assertExists()
        //キーボード表示
        composeTestRule.onNodeWithTag("TextField").performClick()
        //キーボード非表示
        composeTestRule.onNodeWithTag("FileDownloaderScreen").performClick()
        composeTestRule.waitForIdle()
    }

}