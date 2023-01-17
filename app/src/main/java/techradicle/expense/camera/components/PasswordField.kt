package techradicle.expense.camera.components

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
interface PasswordInputFieldState {
    var password: String
    val isValid: Boolean
}

@Composable
fun PasswordInputField(
    modifier: Modifier = Modifier,
    state: PasswordInputFieldState = rememberPasswordInputFieldState(),
    label: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = state.password,
            onValueChange = { value -> state.password = value },
            modifier = Modifier.fillMaxWidth(),
            label = label,
            isError = !state.isValid
        )
        if (!state.isValid) {
            Text(
                text = "Invalid Password",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.error
            )
        }
    }
}

class PasswordInputFieldImpl(password: String = "") : PasswordInputFieldState {
    private var _password by mutableStateOf(password)
    override var password: String
        get() = _password
        set(value) {
            _password = value
        }
    override val isValid by derivedStateOf { isValidPassword(_password) }

    private fun isValidPassword(password: String): Boolean =
        password.isEmpty() || password.length > 6

    companion object {
        val saver = Saver<PasswordInputFieldImpl, List<Any>>(
            save = { listOf(it._password) },
            restore = {
                PasswordInputFieldImpl(password = it[0] as String)
            }
        )
    }
}

@Composable
fun rememberPasswordInputFieldState(): PasswordInputFieldState =
    rememberSaveable(saver = PasswordInputFieldImpl.saver) {
        PasswordInputFieldImpl("")
    }