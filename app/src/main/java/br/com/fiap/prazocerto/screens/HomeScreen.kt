package br.com.fiap.prazocerto.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.fiap.prazocerto.R
import br.com.fiap.prazocerto.model.Atividade
import br.com.fiap.prazocerto.ui.theme.PrazoCertoTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    // Obter uma instância do Firebase
    val usuarioLogado = FirebaseAuth.getInstance()

    // Necessário para efetuar o deslogar da conta do Google
    val context = LocalContext.current
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    // Controla a exibição da caixa de diálogo AtividadeDialog
    var mostrarDialog by remember {
        mutableStateOf(false)
    }

    // Controla a exibição da caixa de diálogo de confirmação de exclusão
    var mostrarDialogExcluir by remember {
        mutableStateOf(false)
    }

    // Armazena o id da atividade que será excluída
    var idAtividadeExcluir by remember {
        mutableStateOf("")
    }

    // Controla a exibição da caixa de diálogo de edição
    var mostrarDialogEditar by remember {
        mutableStateOf(false)
    }
    // Armazena instância da atividade selecionada para edição
    var atividadeEditar by remember {
        mutableStateOf(Atividade())
    }

    // Criamos a variável que irá receber a lista de atividades do RealTime
    var atividades = remember {
        mutableStateListOf<Atividade>()
    }

    // Criamos uma instância do Firebase Realtime Database
    val database = Firebase
        .database("https://prazo-certo-76a63-default-rtdb.firebaseio.com/")

    // Buscamos os dados do Firebase
    // e preenchemos a lista de atividades
    database.getReference("atividades")
        .get().addOnSuccessListener { dataSnapshot ->
            atividades.clear()
            for (child in dataSnapshot.children) {
                val atividade = child.getValue(Atividade::class.java)
                atividades.add(atividade!!)
            }
        }.addOnFailureListener {
            println("Erro ao buscar dados: ${it.message}")
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(){
                        Text(
                            text = "Prazo Certo",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                            Text(
                                text = usuarioLogado.currentUser!!.displayName!!,
                            color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 14.sp
                            )
                    }
                        IconButton(
                            onClick = {
                                val autentica = FirebaseAuth.getInstance()
                                autentica.signOut()
                                // Deslogar do Google
                                googleSignInClient.signOut()
                                    .addOnCompleteListener {
                                        navController.navigate("login")
                                    }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Efetuar Logout",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults
                    .topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    mostrarDialog = true
                },
                shape = RoundedCornerShape(50.dp),
                modifier = modifier
                    .width(100.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Text(
                text = "Atividades",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            // Exibindo a lista de atividades
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 48.dp)
            ) {
                items(atividades) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(1f)
                        ) {
                            Text(
                                text = it.titulo,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = it.disciplina,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = it.dataEntrega,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = {mostrarDialogEditar = true
                                atividadeEditar = it}
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = {
                                mostrarDialogExcluir = true
                                idAtividadeExcluir = it.id
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Apagar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }

    // Exibir AlertDialog para confirmar a exclusão da atividade
    if (mostrarDialogExcluir){
        AlertDialog(
            onDismissRequest = { mostrarDialogExcluir = false },
            title = {
                Text(
                    text = "Excluir atividade"
                )
            },
            text = {
                Text(
                    text = "Confirma a exclusão da atividade selecionada?"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogExcluir = false
                    database.getReference("atividades")
                        .child(idAtividadeExcluir)
                        .removeValue()
                }) {
                    Text(text = "Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogExcluir = false
                }) {
                    Text(text = "Cancelar")
                }
            }
        )
    }
    // Exibe o AlertDialog AtividadeDialog
    if (mostrarDialog) {
        AtividadeDialog(
            onDismiss = { mostrarDialog = false },
            onConfirm = { mostrarDialog = false },
            operacao = "Adicionar"
        )
    }
    // Exibe o AlertDialog AtividadeDialog para Edição
    if (mostrarDialogEditar) {
        AtividadeDialog(
            onDismiss = { mostrarDialogEditar = false },
            onConfirm = { mostrarDialogEditar = false },
            operacao = "Editar",
            atividade = atividadeEditar
        )
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    PrazoCertoTheme() {
        HomeScreen(navController = rememberNavController())
    }
}