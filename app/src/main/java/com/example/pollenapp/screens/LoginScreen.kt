package com.example.pollenapp.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pollenapp.elements.LoginField
import com.example.pollenapp.elements.PasswordField
import com.example.pollenapp.Credentials
import com.example.pollenapp.LoginResult
import com.example.pollenapp.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = LoginViewModel(),
    onLoginSuccess: () -> Unit
) {
    Surface {
        val loginState by viewModel.loginState.collectAsState()
        val context = LocalContext.current
        var credentials by remember { mutableStateOf(Credentials()) }

        LaunchedEffect(loginState) {
            when (loginState) {
                is LoginResult.Success -> {
                    onLoginSuccess()
                    viewModel.resetState()
                }
                is LoginResult.Error -> {
                    Toast.makeText(context, (loginState as LoginResult.Error).message, Toast.LENGTH_SHORT).show()
                    viewModel.resetState()
                }
                null -> {}
            }
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
        ) {
            LoginField(
                value = credentials.login,
                onChange = { data -> credentials = credentials.copy(login = data) },
                modifier = Modifier.fillMaxWidth()
            )
            
            PasswordField(
                value = credentials.password,
                onChange = { data -> credentials = credentials.copy(password = data) },
                onDone = {
                    viewModel.login(credentials)
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.login(credentials)
                    },
                    enabled = credentials.isNotEmpty(),
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Login")
                }

                Button(
                    onClick = {
                        viewModel.register(credentials)
                    },
                    enabled = credentials.isNotEmpty(),
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Register")
                }
            }
        }
    }
}
