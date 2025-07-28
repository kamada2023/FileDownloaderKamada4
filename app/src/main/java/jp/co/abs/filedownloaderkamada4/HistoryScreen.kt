package jp.co.abs.filedownloaderkamada4

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException



@SuppressLint("ConfigurationScreenWidthHeight", "CoroutineCreationDuringComposition")
@Composable
fun HistoryScreen() {
    //端末のスクリーンサイズ取得
    val imageWidth = (LocalConfiguration.current.screenWidthDp / 3).dp
    val context = LocalContext.current

    val uri: Uri = if (Build.VERSION.SDK_INT >= 29) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    val path = Environment.DIRECTORY_PICTURES + "/Kamada_Picture/"

    val imageUris = remember { mutableListOf<Uri>() }
    val contentResolver = context.contentResolver
    try {
        val cursor = uri.let {
            contentResolver.query(
                it,
                null,
                null,
                null,
                null
            )
        }
        val idColumn = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (cursor?.moveToNext() == true) { /* 順にカーソルを動かしながら、情報を取得していく。*/
            val id = idColumn?.let { cursor.getLong(it) }
            /* IDからURIを取得してリストに格納 */
            val myUri = id?.let {
                ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, it
                )
            }
            if (myUri != null) {
                imageUris.add(myUri)
            }
        }
        cursor?.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    for (url in imageUris){
        println(url)
    }

    Column {
        //val options = BitmapFactory.Options()
//        val boundsStream = context.contentResolver.openInputStream(imageUri)
//        options.inJustDecodeBounds = true
//        BitmapFactory.decodeStream(boundsStream, null, options)
//        boundsStream?.close()
//        if ( options.outHeight != 0 ) {
//            // we've got bounds
//            val widthSample = options.outWidth / (LocalConfiguration.current.screenWidthDp / 3)
//            val heightSample = options.outHeight / (LocalConfiguration.current.screenWidthDp / 3)
//            Log.d("width-height", "width:$widthSample, height:$heightSample")
//            val sample = min(widthSample, heightSample)
//            if (sample > 1) {
//                options.inSampleSize = sample
//            }
//        }

        for (urls in imageUris){
            val imageBitmap =
                try {
                    // options.inJustDecodeBounds = true
                    // options.inMutable = true
                    //val decodeStream = context.contentResolver.openInputStream(imageUri)
                    //val bitmap = BitmapFactory.decodeStream(decodeStream, null, options)
                    val inputStream = context.contentResolver.openInputStream(urls)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    bitmap?.asImageBitmap()
                } catch (e: Exception) {
                    // エラー処理
                    e.printStackTrace()
                    null
                }
            if(imageBitmap != null){
                Image(
                    modifier = Modifier.size(imageWidth),
                    contentScale = ContentScale.Crop,
                    bitmap = imageBitmap,
                    contentDescription = "Internal Storage Image"
                )
            }else{
                Box(modifier = Modifier.size(imageWidth)){
                    Text(
                        text = "NoImage",
                        modifier = Modifier.fillMaxSize().background(color = Color.White)
                    )
                }
            }
        }
    }
}