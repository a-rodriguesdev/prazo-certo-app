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
                    val novaAtividade = Atividade(
                        titulo = titulo,
                        disciplina = disciplina,
                        dataEntrega = dataEntrega,
                        foiEntregue = foiEntregue
                    )
                    onConfirm()
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

