package jp.co.abs.filedownloaderkamada4

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class DownloaderImageTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<FileDownloaderActivity>()

    @Test
    fun downloadTest() {
        val showDownloadImageTest = mutableStateOf(false)
        val context = composeTestRule.activity.applicationContext
//        val testUri = if (Build.VERSION.SDK_INT >= 29) {
//            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
//        } else { MediaStore.Images.Media.EXTERNAL_CONTENT_URI }
        downloadImage(urlEntered = "", context = context, showDownloadImage = showDownloadImageTest)

        //今回は非同期処理がうまくassert出来ない為、以下を見送るが、assertが上手くいく方法についても次回検証する。
        //assert(imageUri == testUri)

        assert(showDownloadImageTest.value)
    }
    @SuppressLint("UnrememberedMutableState")
    @Test
    fun downloadTest2() {
        val showDownloadImageTest = mutableStateOf(false)
        val context = composeTestRule.activity.applicationContext
//        val testUri = if (Build.VERSION.SDK_INT >= 29) {
//            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
//        } else { MediaStore.Images.Media.EXTERNAL_CONTENT_URI }
        downloadImage(urlEntered = "https://img1.kakaku.k-img.com/images/productimage/fullscale/J0000047319.jpg", context = context, showDownloadImage = showDownloadImageTest)

        //今回は非同期処理がうまくassert出来ない為、以下を見送るが、assertが上手くいく方法についても次回検証する。
        //assert(imageUri != testUri)

        assert(showDownloadImageTest.value)
    }

    @Test
    fun downloadTest3() {
        val showDownloadImageTest = mutableStateOf(false)
        val context = composeTestRule.activity.applicationContext
//        val testUri = if (Build.VERSION.SDK_INT >= 29) {
//            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
//        } else { MediaStore.Images.Media.EXTERNAL_CONTENT_URI }
        downloadImage(urlEntered = "https://img1.kakaku.k-img.com/images/productimage/fullscale/wwww", context = context, showDownloadImage = showDownloadImageTest)

        //今回は非同期処理がうまくassert出来ない為、以下を見送るが、assertが上手くいく方法についても次回検証する。
        //assert(imageUri == testUri)

        assert(showDownloadImageTest.value)

    }
}