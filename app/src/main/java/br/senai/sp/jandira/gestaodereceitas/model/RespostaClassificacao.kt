package br.senai.sp.jandira.gestaodereceitas.model

import com.google.gson.annotations.SerializedName

data class RespostaClassificacao(
    val status: Boolean,
    val status_code: Int,
    val items: Int,
    @SerializedName("usuario") // <--- ESTE É O CAMPO QUE CONTÉM A LISTA NO SEU JSON DO POSTMAN
    val classificacoes: List<ClassificacaoReceita> // O nome da propriedade no seu app para a lista
)