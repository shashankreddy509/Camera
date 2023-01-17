package techradicle.expense.camera

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import techradicle.expense.camera.imageupload.TakePictureWithUriReturnContract
import techradicle.expense.camera.imageupload.UploadImageScreen
import techradicle.expense.camera.login.LoginScreen
import techradicle.expense.camera.overview.DashboardScreen
import techradicle.expense.camera.ui.theme.CameraTheme
import java.io.File

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var functions: FirebaseFunctions

    private val takeImageResult =
        registerForActivityResult(TakePictureWithUriReturnContract()) { (isSuccess, imageUri) ->
            if (isSuccess) {
//            previewImage.setImageURI(imageUri)
            }
        }


    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                takeImageResult.launch(uri)
            }
        }
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(
            applicationContext,
            "${BuildConfig.APPLICATION_ID}.provider",
            tmpFile
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContent {
            CameraTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = if (auth.currentUser != null) OverViewScreen.route else LoginScreen.route,
                    ) {
                        composable(route = LoginScreen.route) {
                            LoginScreen(this@MainActivity, navController)
                        }
                        composable(route = OverViewScreen.route) {
                            DashboardScreen(navController)
                        }
                        composable(route = ImageUploadScreen.route) {
                            UploadImageScreen(upload = {
                                takeImage()
                            })
                        }
                    }
                }
            }
        }
    }
}