package br.com.fiap.prazocerto.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.unit.dp
import br.com.fiap.prazocerto.model.Atividade
import com.google.firebase.Firebase
import com.google.firebase.database.database

@Composable
fun AtividadeDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    atividade: Atividade = Atividade(),
    operacao: String
) {

    // Definindo o estado dos campos
    var titulo by remember { mutableStateOf(atividade.titulo) }
    var disciplina by remember { mutableStateOf(atividade.disciplina) }
    var dataEntrega: String by remember { mutableStateOf(atividade.dataEntrega.toString()) }
    var foiEntregue by remember { mutableStateOf(atividade.foiEntregue) }

    // Criando o AlertDialog
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = operacao)
        },
        text = {
            Column(){
                OutlinedTextField(
                    value = titulo,
                    onValueChange = {
                        titulo = it
                    },
                    label = {
                        Text(text = "Título da atividade")
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = disciplina,
                    onValueChange = {
                        disciplina = it
                    },
                    label = {
                        Text(text = "Disciplina")
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = dataEntrega.toString(),
                    onValueChange = {
                        dataEntrega = it
                    },
                    label = {
                        Text(text = "Data da entrega")
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = foiEntregue,
                        onCheckedChange = {
                            foiEntregue = it
                        }
                    )
                    Text(text = "Entregue")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Criamos uma instância do Firebase Realtime Database
                    val database = Firebase
                        .database("https://prazo-certo-76a63-default-rtdb.firebaseio.com/")

                    if (operacao == "Adicionar") {
                        val novaAtividade = Atividade(
                            titulo = titulo,
                            disciplina = disciplina,
                            dataEntrega = dataEntrega,
                            foiEntregue = foiEntregue
                        )
                        // Obter a nova chave gerada pelo Firebase
                        val novaReferencia = database.getReference("atividades").push()

                        // Atualizar o ID da atividade com a nova chave
                        novaAtividade.id = novaReferencia.key.toString()

                        // Salvamos a nova atividade no Firebase
                        novaReferencia.setValue(novaAtividade.toJson())

                        onConfirm()
                    } else {
                        val novaAtividade  = atividade.copy(
                            titulo = titulo,
                            disciplina = disciplina,
                            dataEntrega = dataEntrega,
                            foiEntregue = foiEntregue
                        )
                        database.getReference("atividades")
                            .child(atividade.id)
                            .setValue(novaAtividade.toJson())
                        onConfirm()
                    }
                }
            ) {
                Text(text = "Confirmar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(text = "Cancelar")
            }
        }
    )
}

