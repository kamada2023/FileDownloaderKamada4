package jp.co.abs.filedownloaderkamada4

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.min


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun HistoryScreen() {
    //端末のスクリーンサイズ取得
    val imageWidth = (LocalConfiguration.current.screenWidthDp / 3).dp
    val context = LocalContext.current
    Column {
        val imageBitmap =
            try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                bitmap?.asImageBitmap()
            } catch (e: Exception) {
                // エラー処理
                e.printStackTrace()
                null
            }
        Image(
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
            bitmap = imageBitmap!!,
            contentDescription = "Internal Storage Image"
        )
    }
}

//    val imageUris = rememberArrayList<String>(List<String>(null))
//    val contentResolver = context.contentResolver
//    val cursor = contentResolver.query(
//        imageUri,
//        arrayOf(MediaStore.Images.Media._ID),
//        null,
//        null,
//        null
//    )
//    val idColumn = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
//    while (cursor?.moveToNext() == true) { /* 順にカーソルを動かしながら、情報を取得していく。*/
//        val id = idColumn?.let { cursor.getLong(it) }
//        /* IDからURIを取得してリストに格納 */
//        val uri = id?.let {
//            ContentUris.withAppendedId(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, it
//            )
//        }
//        imageUris.add(uri)
//    }
//
//    cursor?.close()

//    val imageBitmap =
//        try {
////            val boundsStream = context.contentResolver.openInputStream(imageUri)
//            val options = BitmapFactory.Options()
////                options.inJustDecodeBounds = true
////                var bitmap = BitmapFactory.decodeStream(boundsStream, null, options)
////            boundsStream?.close()
//            val density = LocalDensity.current
//            with(density) {
//                val px = imageWidth.toPx()
//                if ( options.outHeight != 0 ) {
//                    // we've got bounds
//                    val widthSample = px
//                    val heightSample = px
//                    val sample = min(widthSample, heightSample)
//                    if (sample > 1) {
//                        options.inSampleSize = sample.toInt()
//                    }
//                }
//            }
//
//            options.inJustDecodeBounds = false
//            val decodeStream = context.contentResolver.openInputStream(imageUri)
//            val bitmap = BitmapFactory.decodeStream(decodeStream, null, options)
//            decodeStream?.close()
//            bitmap?.asImageBitmap()
//        } catch (e: Exception) {
//            // エラー処理
//            e.printStackTrace()
//            null
//        }

//    Box (modifier = Modifier.fillMaxSize()){
//
//        Image(
//            bitmap = imageBitmap!!,
//            contentDescription = "thumbnail"
//            // modifier = Modifier.size(imageWidth.dp),
//            // contentScale = ContentScale.Crop
//        )
//    }
//}