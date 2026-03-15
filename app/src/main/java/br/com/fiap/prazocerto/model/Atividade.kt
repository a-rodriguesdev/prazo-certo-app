package br.com.fiap.prazocerto.model

data class Atividade(
    var id: String = "",
    val titulo: String = "",
    val disciplina: String = "",
    val dataEntrega: String = "",
    val foiEntregue: Boolean = false
){
    fun toJson(): Map<String, Any> =
        mapOf(
            "id" to id,
            "titulo" to titulo,
            "disciplina" to disciplina,
            "dataEntrega" to dataEntrega,
            "foiEntregue" to foiEntregue
        )
}
