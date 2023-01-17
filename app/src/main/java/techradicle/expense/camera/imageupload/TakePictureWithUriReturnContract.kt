package techradicle.expense.camera.imageupload

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract

class TakePictureWithUriReturnContract : ActivityResultContract<Uri, Pair<Boolean, Uri>>() {

    private lateinit var imageUri: Uri

    override fun createIntent(context: Context, input: Uri): Intent {
        imageUri = input
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, input)
    }

    override fun getSynchronousResult(
        context: Context,
        input: Uri
    ): SynchronousResult<Pair<Boolean, Uri>>? = null

    override fun parseResult(resultCode: Int, intent: Intent?): Pair<Boolean, Uri> {
        return (resultCode == Activity.RESULT_OK) to imageUri
    }

}