package jp.co.abs.filedownloaderkamada4

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    Log.d("imageWidth","$imageWidth")
    Column {
        val options = BitmapFactory.Options()
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
        val imageBitmap =
            try {
                options.inJustDecodeBounds = false
                options.inMutable = true
                val decodeStream = context.contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(decodeStream, null, options)
                decodeStream?.close()
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
//    }
//}