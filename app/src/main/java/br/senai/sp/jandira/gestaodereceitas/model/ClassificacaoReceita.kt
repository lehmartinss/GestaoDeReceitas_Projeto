package br.senai.sp.jandira.gestaodereceitas.model

import com.google.gson.annotations.SerializedName

data class ClassificacaoReceita(
    val id: Int = 0,
    val nome: String = "",
    @SerializedName("id_classificacao") val idClassificacao: Int = 0 // ou Int? = null
)