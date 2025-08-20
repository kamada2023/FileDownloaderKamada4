package jp.co.abs.filedownloaderkamada4

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import java.io.IOException
import kotlin.math.ceil


@SuppressLint("ConfigurationScreenWidthHeight", "CoroutineCreationDuringComposition")
@Composable
fun HistoryScreen() {
    //端末のスクリーンサイズ取得
    val imageWidth = (LocalConfiguration.current.screenWidthDp / 3).dp
    val imageHeight = LocalConfiguration.current.screenHeightDp
    val context = LocalContext.current

    val uri: Uri = if (Build.VERSION.SDK_INT >= 29) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    val selection = MediaStore.Images.ImageColumns.RELATIVE_PATH + " = ?"
    val path = Environment.DIRECTORY_PICTURES + "/Kamada_Picture/"
    val selectionArgs = arrayOf(path)

    val imageUris = remember { mutableListOf<Uri>() }
    val contentResolver = context.contentResolver
    try {
        val cursor = uri.let {
            contentResolver.query(
                it,
                null,
                selection,
                selectionArgs,
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

    LazyColumn(modifier = Modifier.height(imageHeight.dp).testTag("HistoryScreen")) {
        //val options = BitmapFactory.Options()
        //val boundsStream = context.contentResolver.openInputStream(imageUri)
        //options.inJustDecodeBounds = true
        //BitmapFactory.decodeStream(boundsStream, null, options)
        //boundsStream?.close()
        //if ( options.outHeight != 0 ) {
        //    // we've got bounds
        //    val widthSample = options.outWidth / (LocalConfiguration.current.screenWidthDp / 3)
        //    val heightSample = options.outHeight / (LocalConfiguration.current.screenWidthDp / 3)
        //    Log.d("width-height", "width:$widthSample, height:$heightSample")
        //    val sample = min(widthSample, heightSample)
        //    if (sample > 1) {
        //        options.inSampleSize = sample
        //    }
        //}
        val imageLists = ceil(imageUris.size.toDouble() / 3).toInt()
        items(imageLists){ imageList ->
            Row {
                if (imageUris.size >= imageList*3){
                    val imageBitmap =
                        try {
                            val inputStream = context.contentResolver.openInputStream(imageUris[imageList*3])
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
                            modifier = Modifier.size(imageWidth)
                                .clickable {
                                    val shareIntent = Intent().apply {
                                        action = Intent.ACTION_VIEW
                                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        setDataAndType(imageUris[imageList * 3],"image/*")
                                    }
                                    context.startActivity(shareIntent)
                                },
                            contentScale = ContentScale.Crop,
                            bitmap = imageBitmap,
                            contentDescription = "image_${imageList*3}",
                        )
                    }else{
                        Box(modifier = Modifier.size(imageWidth)){ }
                    }
                }
                if (imageUris.size >= imageList*3 + 1) {
                    val imageBitmap =
                        try {
                            val inputStream =
                                context.contentResolver.openInputStream(imageUris[imageList * 3 + 1])
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            inputStream?.close()
                            bitmap?.asImageBitmap()
                        } catch (e: Exception) {
                            // エラー処理
                            e.printStackTrace()
                            null
                        }
                    if (imageBitmap != null) {
                        Image(
                            modifier = Modifier.size(imageWidth)
                                .clickable {
                                    val shareIntent = Intent().apply {
                                        action = Intent.ACTION_VIEW
                                        setDataAndType(imageUris[imageList * 3 + 1],"image/*")
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent,null))
                                },
                            contentScale = ContentScale.Crop,
                            bitmap = imageBitmap,
                            contentDescription = "image_${imageList * 3 + 1}",
                        )
                    } else {
                        Box(modifier = Modifier.size(imageWidth)) { }
                    }
                }
                if (imageUris.size >= imageList*3 + 2) {
                    val imageBitmap =
                        try {
                            val inputStream =
                                context.contentResolver.openInputStream(imageUris[imageList * 3 + 2])
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            inputStream?.close()
                            bitmap?.asImageBitmap()
                        } catch (e: Exception) {
                            // エラー処理
                            e.printStackTrace()
                            null
                        }
                    if (imageBitmap != null) {
                        Image(
                            modifier = Modifier.size(imageWidth)
                                .clickable {
                                    val shareIntent = Intent().apply {
                                        action = Intent.ACTION_VIEW
                                        setDataAndType(imageUris[imageList * 3 + 2],"image/*")
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent,null))
                                },
                            contentScale = ContentScale.Crop,
                            bitmap = imageBitmap,
                            contentDescription = "image_${imageList * 3 + 2}",
                        )
                    } else {
                        Box(modifier = Modifier.size(imageWidth)) { }
                    }
                }
            }
        }
    }
}