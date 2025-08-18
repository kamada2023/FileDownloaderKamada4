package jp.co.abs.filedownloaderkamada4

import android.os.Build
import android.util.Log
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
//import androidx.test.rule.GrantPermissionRule
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
        composeTestRule.onNodeWithText("許可する").assertExists()
        composeTestRule.onNodeWithText("しない").assertExists()
        composeTestRule.onNodeWithText("許可する").performClick()
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
        Log.d("ALLOW_CLICK?",arrowPermission.text)
        // 取得したオブジェクトが画面上に存在すれば押下
        if (arrowPermission.exists()){arrowPermission.click()}
        // Activityの表示まで待つ等あれば待機する
        device.wait(Until.hasObject(By.res("TextField").depth(0)),3000)
        // 動かせるまで待機
        composeTestRule.waitForIdle()
        // 画面終了を確認
        composeTestRule.onNodeWithText("許可する").assertDoesNotExist()
        composeTestRule.onNodeWithTag("TextField").assertExists()
        composeTestRule.onNodeWithTag("TextField").performTextInput("")

    }

    @Test
    fun downloadFailureTest1(){
        // UiDeviceのインスタンス化
        val device = UiDevice.getInstance(getInstrumentation())
        composeTestRule.setContent {
            FileDownloaderApp()
        }
        composeTestRule.onNodeWithText("許可する").assertExists()
        composeTestRule.onNodeWithText("しない").assertExists()
        composeTestRule.onNodeWithText("許可する").performClick()
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
        composeTestRule.onNodeWithText("許可する").assertDoesNotExist()
        // 失敗通知
        composeTestRule.onNodeWithTag("TextField").performTextInput("")
        composeTestRule.onNodeWithText("ダウンロード開始").performClick()
        // 現状ではどうやっても、一時的な表示のToastを取得するのが不可能
        //composeTestRule.onRoot(useUnmergedTree = true).printToLog("currentLabelExists")

    }

    @Test
    fun downloadFailureTest2(){
        // UiDeviceのインスタンス化
        val device = UiDevice.getInstance(getInstrumentation())
        composeTestRule.setContent {
            FileDownloaderApp()
        }
        composeTestRule.onNodeWithText("許可する").assertExists()
        composeTestRule.onNodeWithText("しない").assertExists()
        composeTestRule.onNodeWithText("許可する").performClick()
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
        composeTestRule.onNodeWithText("許可する").assertDoesNotExist()
        // 失敗通知
        composeTestRule.onNodeWithTag("TextField").performTextInput("https://xn--eckyfna8731bop8c.jp/wp-content/uploads/2014/12/wpid-607566c4.png")
        composeTestRule.onNodeWithText("ダウンロード開始").performClick()
        //Toastの処理を待つ
        composeTestRule.waitForIdle()
        composeTestRule.onRoot(useUnmergedTree = true).printToLog("currentLabelExists")
        //composeTestRule.onNodeWithTag(testTag = "notifyFailure", useUnmergedTree = true).assertExists()
    }

    @Test
    fun downloadSuccessTest() {
        // UiDeviceのインスタンス化
        val device = UiDevice.getInstance(getInstrumentation())
        composeTestRule.setContent {
            FileDownloaderApp()
        }
        composeTestRule.onNodeWithText("許可する").assertExists()
        composeTestRule.onNodeWithText("しない").assertExists()
        composeTestRule.onNodeWithText("許可する").performClick()
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
        composeTestRule.onNodeWithText("許可する").assertDoesNotExist()
        // 成功通知
        composeTestRule.onNodeWithTag("TextField",useUnmergedTree = true).performTextInput("https://thumb.photo-ac.com/e5/e5a0c264175fb95f735396d5b8ac3287_t.jpeg")
        composeTestRule.onNodeWithText("ダウンロード開始").performClick()
        //Toastの処理を待つ
        composeTestRule.waitForIdle()
        composeTestRule.onRoot(useUnmergedTree = true).printToLog("currentLabelExists")
        composeTestRule.onNodeWithTag(testTag = "notifySuccess", useUnmergedTree = true).assertExists()
    }
}