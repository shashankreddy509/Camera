package techradicle.expense.camera.imageupload

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.gson.*
import techradicle.expense.camera.BuildConfig
import techradicle.expense.camera.R
import java.io.ByteArrayOutputStream
import java.io.File

@Composable
fun UploadImageScreen(
    upload: () -> Unit = {}
) {

    var hasImage by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageData by remember { mutableStateOf("") }
    val context = LocalContext.current
    var functions: FirebaseFunctions = Firebase.functions

    val TAG = "Upload Image"

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            hasImage = success
        }
    )

    fun annotateImage(requestJson: String): Task<JsonElement> {
        return functions
            .getHttpsCallable("annotateImage")
            .call(requestJson)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result?.data
                JsonParser.parseString(Gson().toJson(result))
            }
    }

    fun scaleBitmapDown(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        var resizedWidth = maxDimension
        var resizedHeight = maxDimension
        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension
            resizedWidth =
                (resizedHeight * originalWidth.toFloat() / originalHeight.toFloat()).toInt()
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension
            resizedHeight =
                (resizedWidth * originalHeight.toFloat() / originalWidth.toFloat()).toInt()
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension
            resizedWidth = maxDimension
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false)
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(text = "Upload Receipt")
            })
    }) {

        Column {
            if (hasImage && imageUri != null) {
                var bitmap: Bitmap =
                    MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                bitmap = scaleBitmapDown(bitmap, 640)

                // Convert bitmap to base64 encoded string
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
                val base64encoded = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

                // Create json request to cloud vision
                val request = JsonObject()
                // Add image to request
                val image = JsonObject()
                image.add("content", JsonPrimitive(base64encoded))
                request.add("image", image)
                //Add features to the request
                val feature = JsonObject()
                feature.add("type", JsonPrimitive("TEXT_DETECTION"))
                // Alternatively, for DOCUMENT_TEXT_DETECTION:
                // feature.add("type", JsonPrimitive("DOCUMENT_TEXT_DETECTION"))
                val features = JsonArray()
                features.add(feature)
                request.add("features", features)

                val imageContext = JsonObject()
                val languageHints = JsonArray()
                languageHints.add("en")
                imageContext.add("languageHints", languageHints)
                request.add("imageContext", imageContext)
                annotateImage(request.toString())
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.e(TAG, "Failure")
                            // Task failed with an exception
                            // ...
                        } else {
                            // Task completed successfully
                            // ...
                            Log.e(TAG, "Success")
                            val annotation =
                                task.result!!.asJsonArray[0].asJsonObject["fullTextAnnotation"].asJsonObject
                            imageData = annotation["text"].asString
                            var pageText = ""
                            val paras = mutableListOf<String>()
                            for (page in annotation["pages"].asJsonArray) {
                                for (block in page.asJsonObject["blocks"].asJsonArray) {
                                    var blockText = ""
                                    for (para in block.asJsonObject["paragraphs"].asJsonArray) {
                                        var paraText = ""
                                        for (word in para.asJsonObject["words"].asJsonArray) {
                                            var wordText = ""
                                            for (symbol in word.asJsonObject["symbols"].asJsonArray) {
                                                wordText += symbol.asJsonObject["text"].asString
                                            }
                                            paraText = String.format("%s%s ", paraText, wordText)
                                        }
                                        paras.add(paraText)
                                        blockText += paraText
                                    }
                                    pageText += blockText
                                }
                            }
                            Log.e(TAG, paras.joinToString())
                        }
                    }
                Text(text = imageData)
                AsyncImage(
                    model = imageUri,
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(200.dp),
                    contentDescription = "Selected image",
                )
            }
            OutlinedButton(onClick = {
                val uri = getTmpFileUri(context)
                imageUri = uri
                cameraLauncher.launch(imageUri)
            }) {
                Text(text = "Upload")
            }
        }
    }
}

fun getTmpFileUri(context: Context): Uri {
    val tmpFile = File.createTempFile("tmp_image_file", ".png", context.cacheDir).apply {
        createNewFile()
        deleteOnExit()
    }

    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.provider",
        tmpFile
    )
}

class ComposeFileProvider : FileProvider(R.xml.provider_paths) {
    companion object {
        fun getImageUri(context: Context): Uri {
            // 1
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()
            // 2
            val file = File.createTempFile(
                "selected_image_",
                ".jpg",
                directory
            )
            // 3
            val authority = context.packageName + ".fileprovider"
            // 4
            return getUriForFile(
                context,
                authority,
                file,
            )
        }
    }
}