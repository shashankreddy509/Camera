package techradicle.expense.camera.components

import android.util.Patterns
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier

@Stable
interface EmailInputFieldState {
    var email: String
    val isValid: Boolean
}

@Composable
fun EmailInputField(
    modifier: Modifier = Modifier,
    state: EmailInputFieldState = rememberEmailInputFieldState(),
    label: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = state.email,
            onValueChange = { value -> state.email = value },
            modifier = Modifier.fillMaxWidth(),
            label = label,
            isError = !state.isValid
        )
        if (!state.isValid) {
            Text(
                text = "Invalid Email",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.error
            )
        }
    }
}

class EmailInputFieldStateImpl(
    email: String = ""
) : EmailInputFieldState {
    private var _email by mutableStateOf(email)
    override var email: String
        get() = _email
        set(value) {
            _email = value
        }
    override val isValid by derivedStateOf { isValidEmail(_email) }

    companion object {
        val saver = Saver<EmailInputFieldStateImpl, List<Any>>(
            save = { listOf(it._email) },
            restore = {
                EmailInputFieldStateImpl(email = it[0] as String)
            }
        )
    }
}

@Composable
fun rememberEmailInputFieldState(): EmailInputFieldState =
    rememberSaveable(saver = EmailInputFieldStateImpl.saver) {
        EmailInputFieldStateImpl("")
    }

private fun isValidEmail(email: String): Boolean =
    email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches()