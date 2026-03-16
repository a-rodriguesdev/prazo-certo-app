package br.com.fiap.prazocerto.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }

    // Obter uma instância do Firebase
    val autentica = FirebaseAuth.getInstance()

    // Obter o contexto para exibir um toast
    val context = LocalContext.current

    // Criar uma variável de estado para
    // controlar a exibição de um indicador de progresso
    var estaCarregando by remember {
        mutableStateOf(false)
    }

    // Verificar se o usuário já está autenticado
    if (autentica.currentUser != null) {
        navController.navigate("home")
    }

    // Lógica para autenticação via Google Account
    // Configurar as opções do Google Signin
    val auth = FirebaseAuth.getInstance()
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    // Criar o lançador que irá abrir a janelinha do Google
    // e esperar o resultado
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            // Obter a conta do Google, se o usuário logou com sucesso
            val conta = task.getResult(ApiException::class.java)

            // Obter o token da conta
            val idToken = conta.idToken

            if (idToken != null) {
                // Trocar o token com o Firebase
                val credential = GoogleAuthProvider.getCredential(idToken, null)

                auth.signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            val usuario = auth.currentUser
                            navController.navigate("home")
                            Toast.makeText(
                                context, "Logado como ${usuario?.email}",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Falha no login ${authTask.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
            }

        } catch (e: ApiException) {
            Toast.makeText(context, "Falha no login ${e.message}", Toast.LENGTH_SHORT)
        }
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
                painter = painterResource(id = R.drawable.schedule),
                contentDescription = "Logo"
            )
        }
        Spacer(modifier = modifier.height(48.dp))
        Text(
            text = "Autenticação",
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
                    autentica.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { tarefa ->
                            estaCarregando = false
                            if (tarefa.isSuccessful) {
                                navController.navigate("home")
                            } else {
                                Toast.makeText(
                                    context,
                                    "Autenticação falhou",
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
                Text(text = "Entrar")
            }
        }

        // Botão de login com Google
        Spacer(modifier = modifier.height(8.dp))
        Button(
            onClick = {
                // Iniciar o fluxo de login do Google
                launcher.launch(googleSignInClient.signInIntent)
            }
        ) {
            Text(text = "Entrar com Google")
        }

        Spacer(modifier = modifier.height(8.dp))
        TextButton(
            onClick = {
                navController.navigate("signup")
            }
        ) {
            Text("Não tem uma conta? Criar conta")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    PrazoCertoTheme() {
        LoginScreen(modifier = Modifier, rememberNavController())
    }
}