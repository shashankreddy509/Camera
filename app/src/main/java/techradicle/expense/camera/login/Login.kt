package techradicle.expense.camera.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import techradicle.expense.camera.MainActivity
import techradicle.expense.camera.OverViewScreen
import techradicle.expense.camera.components.*
import techradicle.expense.camera.utils.Utils.AddSpacer
import techradicle.expense.camera.R

@Composable
fun LoginScreen(
    mainActivity: MainActivity,
    navController: NavHostController
) {
    val emailState = rememberEmailInputFieldState()
    val passwordState = rememberPasswordInputFieldState()

    Column(modifier = Modifier.background(Color.White)) {
        AddSpacer(modifier = Modifier.height(70.dp))
        AppNameLogo()
        AddSpacer(modifier = Modifier.height(50.dp))
        EmailInputField(
            modifier = Modifier.padding(all = 16.dp),
            state = emailState,
            label = { Text(text = "Email address") }
        )
        PasswordInputField(
            modifier = Modifier.padding(all = 16.dp),
            state = passwordState,
            label = { Text(text = "Enter Password") }
        )
        LoginButton(emailState, passwordState, mainActivity, navController)
    }
}

@Composable
fun AppNameLogo() {
    Column {
        Image(
            painter = painterResource(id = R.drawable.ic_expenses),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)
                .align(alignment = Alignment.CenterHorizontally)
                .fillMaxWidth()
        )
        AddSpacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Expense Tracker",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 33.sp
        )
    }
}

@Composable
fun LoginButton(
    emailState: EmailInputFieldState,
    passwordState: PasswordInputFieldState,
    mainActivity: MainActivity,
    navController: NavHostController
) {
    val auth: FirebaseAuth = Firebase.auth
    val TAG = "MainActivity"
    OutlinedButton(
        onClick = {
            auth.signInWithEmailAndPassword(
                emailState.email,
                passwordState.password
            ).addOnCompleteListener(mainActivity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    navController.navigate(OverViewScreen.route)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(
                        TAG,
                        "signInWithEmail:failure",
                        task.exception
                    )
                    Toast.makeText(
                        mainActivity.baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        },
        enabled = (emailState.isValid && passwordState.isValid &&
                emailState.email.isNotEmpty() && passwordState.password.isNotEmpty()),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Login",
            fontSize = 24.sp
        )
    }
}