package jp.co.abs.filedownloaderkamada4

import android.os.Build
import android.provider.MediaStore
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
        val testUri = if (Build.VERSION.SDK_INT >= 29) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else { MediaStore.Images.Media.EXTERNAL_CONTENT_URI }
        downloadImage(urlEntered = "", context = context, showDownloadImage = showDownloadImageTest)

        assert(showDownloadImageTest.value)
        assert(imageUri == testUri)
    }
    @Test
    fun downloadTest2() {
        val showDownloadImageTest = mutableStateOf(false)
        val context = composeTestRule.activity.applicationContext
        val testUri = if (Build.VERSION.SDK_INT >= 29) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else { MediaStore.Images.Media.EXTERNAL_CONTENT_URI }
        downloadImage(urlEntered = "", context = context, showDownloadImage = showDownloadImageTest)

        assert(showDownloadImageTest.value)
        assert(imageUri == testUri)
    }
}