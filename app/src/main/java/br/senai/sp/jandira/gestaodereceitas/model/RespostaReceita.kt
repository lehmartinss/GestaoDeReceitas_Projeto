package br.senai.sp.jandira.gestaodereceitas.model

import com.google.gson.annotations.SerializedName

data class RespostaReceita(
    @SerializedName("status") val status: Boolean,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("mensagem") val mensagem: String,
    @SerializedName("receita") val receita: Receita? // Pode retornar a receita criada com o ID gerado
)