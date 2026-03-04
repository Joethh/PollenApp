package com.example.pollenapp.elements

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun PasswordField(
    value : String,
    onChange : (String)->Unit,
    modifier: Modifier = Modifier,
    label : String = "Password",
    placeholder : String = "Enter your Password",
    onDone: () -> Unit = {}
) {
    TextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Key,
                modifier = Modifier.size(20.dp),
                contentDescription = Icons.Default.Key.name,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
    )
}
