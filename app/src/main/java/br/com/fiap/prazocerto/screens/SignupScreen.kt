package br.com.fiap.prazocerto.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.fiap.prazocerto.R
import br.com.fiap.prazocerto.ui.theme.PrazoCertoTheme
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignupScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }

    // PASSO 01 - Obter uma instância do Firebase
    val autentica = FirebaseAuth.getInstance()

    // PASSO 02 - Obter o contexto para exibir um toast
    val context = LocalContext.current

    // PASSO 03 - Criar uma variável de estado para
    // controlar a exibição de um indicador de progresso
    var estaCarregando by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Prazo Certo",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = modifier
                    .padding(16.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.calendar),
                contentDescription = "Logo"
            )
        }
        Spacer(modifier = modifier.height(48.dp))
        Text(
            text = "Criar conta",
            fontSize = 28.sp
        )
        Spacer(modifier = modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text(text = "E-mail")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            )
        )
        Spacer(modifier = modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(text = "Password")
            },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = modifier.height(16.dp))
        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    estaCarregando = true

                    // Envia o e-mail e senha para o Firebase Authentication
                    autentica.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { tarefa ->
                            estaCarregando = false
                            if (tarefa.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "Conta criada com sucesso",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Ocorreu um erro!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        ) {
            if (estaCarregando){
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(text = "Criar conta")
            }
        }
        Spacer(modifier = modifier.height(8.dp))
        TextButton(
            onClick = {
                navController.navigate("login")
            }
        ) {
            Text("Já tem uma conta? Entrar")
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun SignupScreenPreview() {
    PrazoCertoTheme() {
        SignupScreen(modifier = Modifier, navController = rememberNavController())
    }
}