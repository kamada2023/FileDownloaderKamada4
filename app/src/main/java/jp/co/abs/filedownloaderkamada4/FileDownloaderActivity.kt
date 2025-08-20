package jp.co.abs.filedownloaderkamada4

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date


private var fileName = ""
private var notifySuccess = "ダウンロードが完了しました"
private var notifyFailure = "画像取得に失敗しました"
const val REQUEST_READ_MEDIA_IMAGES = 1
const val REQUEST_READ_EXTERNAL_GROUP = 2
var imageUri: Uri = if (Build.VERSION.SDK_INT >= 29) {
    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
} else { MediaStore.Images.Media.EXTERNAL_CONTENT_URI }

class FileDownloaderActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FileDownloaderApp()
        }
    }
}

enum class Nav {
    FileDownloaderScreen,
    HistoryScreen
}

enum class ToastStatus {
    Initialization,
    NotDisplayed,
    SuccessNotification,
    FailureNotification
}

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FileDownloaderApp(){
    val navController = rememberNavController()
    var selectedDestination by remember { mutableIntStateOf(Nav.FileDownloaderScreen.ordinal) }
    Scaffold (
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
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "履歴") },
                    label = { Text(text = "履歴") },
                    selected = selectedDestination == Nav.HistoryScreen.ordinal,
                    onClick = {
                        navController.navigate(Nav.HistoryScreen.name)
                        selectedDestination = Nav.HistoryScreen.ordinal
                    }
                )
            }
        }
    ){ innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Nav.FileDownloaderScreen.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(route = Nav.FileDownloaderScreen.name) { FileDownloaderScreen() }
            composable(route = Nav.HistoryScreen.name) { HistoryScreen() }
        }
    }
}

@SuppressLint("ContextCastToActivity")
@Composable
fun PermissionDialog(){
    val context = LocalContext.current

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // 外部ストレージの書き込み権限がアプリに対して既に付与されているかを確認
        if (checkSelfPermission(context,Manifest.permission.READ_MEDIA_IMAGES)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // 書き込み権限が付与されていない場合はリクエストを行う
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                REQUEST_READ_MEDIA_IMAGES
            )
        }
    }else{
        // 権限が付与されていない場合はリクエストを行う
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_READ_EXTERNAL_GROUP
        )
    }
}

@SuppressLint("SimpleDateFormat", "CoroutineCreationDuringComposition")
fun downloadImage(
    urlEntered: String,
    context: Context,
    showDownloadImage: MutableState<Boolean>
) {
    val stringUrl: String = urlEntered
    if (stringUrl.isEmpty()){
        showDownloadImage.value = true
        return
    }

    //launchを呼び出す前にプログレスバーを表示
    showDownloadImage.value = false
    CoroutineScope(Dispatchers.Default).launch(Dispatchers.IO) {
        try {
            val url = URL(stringUrl)
            val urlCon = url.openConnection() as HttpURLConnection
            // タイムアウト設定
            urlCon.readTimeout = 10000
            urlCon.connectTimeout = 20000
            // リクエストメソッド
            urlCon.requestMethod = "GET"
            // リダイレクトを自動で許可しない設定
            urlCon.instanceFollowRedirects = false
            //画像をダウンロード
            val ism = urlCon.inputStream
            val bmp = BitmapFactory.decodeStream(ism)

            val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
            val current = sdf.format(Date())
            // 保存先のファイル作成
            fileName = "$current.jpeg"

            val uri: Uri = if (Build.VERSION.SDK_INT >= 29) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/*")
                if (Build.VERSION.SDK_INT >= 29) {
                    put(MediaStore.Images.Media.IS_PENDING, true)
                }
                //※1　専用直下フォルダを作成したい場合
                put(
                    MediaStore.Images.ImageColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/Kamada_Picture/"
                )
            }

            val contentResolver = context.contentResolver
            val contentUri = contentResolver.insert(uri, contentValues)

            //※2 ファイルを書き込む
            contentResolver.openFileDescriptor(contentUri!!, "w", null).use {
                FileOutputStream(it!!.fileDescriptor).use { output ->
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, output)
                }
                imageUri = contentUri
            }

            contentValues.clear()
            if (Build.VERSION.SDK_INT >= 29) {
                contentResolver.update(contentUri, contentValues.apply {
                    put(MediaStore.Images.Media.IS_PENDING, false)
                }, null, null)
            } else {
                contentResolver.update(contentUri, contentValues, null, null)
            }

            // 処理が終わったら、メインスレッドに切り替える。
            withContext(Dispatchers.Main) {
                // プログレスバーを非表示
                showDownloadImage.value = true
            }
        } catch (e: IOException) {
            e.printStackTrace()
            // プログレスバーを非表示
            showDownloadImage.value = true
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            // プログレスバーを非表示
            showDownloadImage.value = true
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "Range")
@Composable
fun FileDownloaderScreen() {
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
                PermissionDialog()
            }
    }

    if(exitTheApplication.value){
        //明示的にアクティビティ終了
        (context as? Activity)?.finish()
    }

    Column(
        modifier = Modifier
            .testTag("FileDownloaderScreen")
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
                    downloadImage(
                        urlEntered = url,
                        context = context,
                        showDownloadImage = showDownloadImage
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
            .fillMaxWidth()){
            // 画像表示判定
            if(imageBitmap != null){
                Image(
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    bitmap = imageBitmap!!,
                    contentDescription = "Internal Storage Image"
                )
            }

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
                            .testTag("ProgressBar")
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
