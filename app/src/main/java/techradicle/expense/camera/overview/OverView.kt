package techradicle.expense.camera.overview

import androidx.compose.foundation.layout.Column
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import techradicle.expense.camera.ImageUploadScreen

@Composable
fun DashboardScreen(navController: NavHostController) {
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(text = "Dashboard")
            })
    }) {
        Column() {
            Text(text = "Dashboard Body")
            OutlinedButton(onClick = {
                navController.navigate(ImageUploadScreen.route)
            }) {
                Text(text = "Upload Receipt")
            }
        }
    }
}