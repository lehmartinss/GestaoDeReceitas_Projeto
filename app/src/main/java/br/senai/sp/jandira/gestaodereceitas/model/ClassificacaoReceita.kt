package br.senai.sp.jandira.gestaodereceitas.model

import com.google.gson.annotations.SerializedName

data class ClassificacaoReceita(
    @SerializedName("id") val id_classificacao: Int,
    @SerializedName("nome") val nome: String
)