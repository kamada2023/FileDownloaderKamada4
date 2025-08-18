package jp.co.abs.filedownloaderkamada4

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
//import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test
import java.net.URL

class ToastTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var notifyFailure = "画像取得に失敗しました"
    private var notifySuccess = "ダウンロードが完了しました"
    private var imageUri = if (Build.VERSION.SDK_INT >= 29) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else { MediaStore.Images.Media.EXTERNAL_CONTENT_URI }

    @Composable
    fun FileDownloaderScreenTest() {
        val openAlertDialog = remember { mutableStateOf(true) }
        // 親コンポーネントにフォーカスを移動させるのに使う
        val focusRequester = remember { FocusRequester() }
        val interactionSource = remember { MutableInteractionSource() }
        // URL
        var url by remember { mutableStateOf("") }
        // 画像の処理判定
        val showDownloadImage = remember { mutableStateOf(false) }
        val downloadClick = remember { mutableStateOf(false) }
        // Toastが表示されたか？判定
        val toastIsDisplayed = remember { mutableIntStateOf(ToastStatus.Initialization.ordinal) }
        // アプリの終了判定
        val exitTheApplication = remember { mutableStateOf(false) }
        val context = LocalContext.current
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission_group.STORAGE
        }
        var imageBitmap by remember { mutableStateOf(
            try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                bitmap?.asImageBitmap()
            } catch (e: Exception) {
                // エラー処理
                null
            }
        ) }

        when {
            openAlertDialog.value ->
                if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED){
                    //パーミッションが許可されている
                }else {
                    //パーミッションが不許可である
                    PermissionDialog(openAlertDialog,exitTheApplication)
                }
        }

        if(exitTheApplication.value){
            //明示的にアクティビティ終了
            (context as? Activity)?.finish()
        }

        Column(
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    enabled = true,
                    indication = null,
                    onClick = { focusRequester.requestFocus() } // 押したら外す
                )
                .focusRequester(focusRequester) // フォーカス操作するやつをセット
                .focusTarget(), // フォーカス当たるように
        ) {
            Button(
                onClick = {
                    //ギャラリーへ遷移
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_VIEW
                        type = "image/*"
                    }
                    context.startActivity(Intent.createChooser(shareIntent,null))
                },
                modifier = Modifier.weight(0.8f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(text = "GALLERYから選択")
            }
            Text(text = "URLを入力してください", modifier = Modifier.padding(10.dp))
            Row{
                TextField(
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1f)
                        .testTag("TextField"),
                    value = url,
                    onValueChange = { url = it },
                    placeholder = { Text(text = "http://") },
                    singleLine = false
                )
                Button(
                    modifier = Modifier.weight(0.7f),
                    onClick = {
                        downloadImageWithMock(
                            urlEntered = url,
                            showDownloadImage = showDownloadImage,
                        )
                        downloadClick.value = true
                        toastIsDisplayed.intValue = ToastStatus.NotDisplayed.ordinal
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(text = "ダウンロード開始")
                }
            }
            Box(modifier = Modifier
                .weight(10f)
                .fillMaxWidth()
            ){
                // 画像表示判定
                if(imageBitmap != null){
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        bitmap = imageBitmap!!,
                        contentDescription = "Internal Storage Image"
                    )
                }
                //Toastのテスト用のUI
                Box (modifier = Modifier.testTag("${toastIsDisplayed.intValue}"))

                // toast,downloadの終了判定
                if (toastIsDisplayed.intValue == ToastStatus.NotDisplayed.ordinal || downloadClick.value){
                    // ダウンロードボタンクリック時処理
                    if (showDownloadImage.value) {
                        // ダウンロード完了時の処理
                        imageBitmap =
                            try {
                                val inputStream = context.contentResolver.openInputStream(imageUri)
                                val bitmap = BitmapFactory.decodeStream(inputStream)
                                bitmap?.asImageBitmap()
                            } catch (e: Exception) {
                                // エラー処理
                                e.printStackTrace()
                                null
                            }
                        if (imageBitmap != null){
                            if (toastIsDisplayed.intValue == ToastStatus.NotDisplayed.ordinal){
                                Box(modifier = Modifier.fillMaxSize().testTag("notifySuccess")){
                                    Toast.makeText(context, notifySuccess, Toast.LENGTH_SHORT).show()
                                }
                                toastIsDisplayed.intValue = ToastStatus.SuccessNotification.ordinal
                            }
                            downloadClick.value = false
                        }else {
                            if (toastIsDisplayed.intValue == ToastStatus.NotDisplayed.ordinal){
                                Box(modifier = Modifier.fillMaxSize().testTag("notifyFailure")){
                                    Toast.makeText(context, notifyFailure, Toast.LENGTH_SHORT).show()
                                }
                                toastIsDisplayed.intValue = ToastStatus.FailureNotification.ordinal
                            }
                            downloadClick.value = false
                        }
                    }else{
                        // プログレスバー表示
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .width(64.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                }
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onClick = {
                    imageBitmap = null
                    url = ""
                },
                shape = MaterialTheme.shapes.small
            ) {
                Text(text = "Clear")
            }
        }
    }

    private fun downloadImageWithMock(urlEntered :String, showDownloadImage: MutableState<Boolean>){
        // 任意のURLと任意の値入力
        if (urlEntered == "https://thumb.photo-ac.com/e5/e5a0c264175fb95f735396d5b8ac3287_t.jpeg"){
            imageUri = ("content://media/external_primary/images/media/82").toUri()
        }
        showDownloadImage.value = true
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun notifySuccessTest(){
        composeTestRule.setContent {
            val navController = rememberNavController()
            var selectedDestination by remember { mutableIntStateOf(Nav.FileDownloaderScreen.ordinal) }
            Scaffold(
                modifier = Modifier.fillMaxSize().semantics { testTagsAsResourceId = true },
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Done, contentDescription = "ダウンロード") },
                            label = { Text(text = "ダウンロード") },
                            selected = selectedDestination == Nav.FileDownloaderScreen.ordinal,
                            onClick = {
                                navController.navigate(Nav.FileDownloaderScreen.name)
                                selectedDestination = Nav.FileDownloaderScreen.ordinal
                            }
                        )
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    Icons.AutoMirrored.Filled.List,
                                    contentDescription = "履歴"
                                )
                            },
                            label = { Text(text = "履歴") },
                            selected = selectedDestination == Nav.HistoryScreen.ordinal,
                            onClick = {
                                navController.navigate(Nav.HistoryScreen.name)
                                selectedDestination = Nav.HistoryScreen.ordinal
                            }
                        )
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Nav.FileDownloaderScreen.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    composable(route = Nav.FileDownloaderScreen.name) {
                        FileDownloaderScreenTest() }
                    composable(route = Nav.HistoryScreen.name) { HistoryScreen() }
                }
            }
        }
        // ダイアログ処理
        val device = UiDevice.getInstance(getInstrumentation())
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
        // mock ダウンロード成功
        composeTestRule.onNodeWithTag("TextField",useUnmergedTree = true).performTextInput("https://thumb.photo-ac.com/e5/e5a0c264175fb95f735396d5b8ac3287_t.jpeg")
        composeTestRule.onNodeWithText("ダウンロード開始",useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithContentDescription("Internal Storage Image").assertExists()
        // 判定
        composeTestRule.onNodeWithTag("${ToastStatus.SuccessNotification.ordinal}",useUnmergedTree = true).assertExists()
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun notifyFailedTest(){
        // Toastが表示されたか？判定
        composeTestRule.setContent {
            val navController = rememberNavController()
            var selectedDestination by remember { mutableIntStateOf(Nav.FileDownloaderScreen.ordinal) }
            Scaffold(
                modifier = Modifier.fillMaxSize().semantics { testTagsAsResourceId = true },
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Done, contentDescription = "ダウンロード") },
                            label = { Text(text = "ダウンロード") },
                            selected = selectedDestination == Nav.FileDownloaderScreen.ordinal,
                            onClick = {
                                navController.navigate(Nav.FileDownloaderScreen.name)
                                selectedDestination = Nav.FileDownloaderScreen.ordinal
                            }
                        )
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    Icons.AutoMirrored.Filled.List,
                                    contentDescription = "履歴"
                                )
                            },
                            label = { Text(text = "履歴") },
                            selected = selectedDestination == Nav.HistoryScreen.ordinal,
                            onClick = {
                                navController.navigate(Nav.HistoryScreen.name)
                                selectedDestination = Nav.HistoryScreen.ordinal
                            }
                        )
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Nav.FileDownloaderScreen.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    composable(route = Nav.FileDownloaderScreen.name) { FileDownloaderScreenTest() }
                    composable(route = Nav.HistoryScreen.name) { HistoryScreen() }
                }
            }
        }
        // ダイアログ処理
        val device = UiDevice.getInstance(getInstrumentation())
        if (composeTestRule.onNodeWithText("許可する").isDisplayed()){
            composeTestRule.onNodeWithText("しない").isDisplayed()
            composeTestRule.onNodeWithText("許可する").performClick()
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
        composeTestRule.onNodeWithText("許可する").assertDoesNotExist()
        // mock 失敗通知
        composeTestRule.onNodeWithTag("TextField").performTextInput("")
        composeTestRule.onNodeWithText("ダウンロード開始").performClick()
        // 判定
        composeTestRule.onNodeWithTag("${ToastStatus.FailureNotification.ordinal}",useUnmergedTree = true).assertExists()
    }
}