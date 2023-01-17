package techradicle.expense.camera.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun TxtField(
    modifier: Modifier = Modifier,
    label: String = "",
    isPassword: Boolean = false
) {
    val inputvalue = remember { mutableStateOf(TextFieldValue()) }
    TextField(
        value = inputvalue.value,
        onValueChange = {},
        placeholder = { Text(text = label) },
        modifier = Modifier
            .padding(all = 16.dp)
            .fillMaxWidth(),
        leadingIcon = {
            if (isPassword) Icon(
                Icons.Filled.Lock,
                contentDescription = ""
            ) else Icon(Icons.Filled.Email, contentDescription = "")
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Email
        )
    )
}