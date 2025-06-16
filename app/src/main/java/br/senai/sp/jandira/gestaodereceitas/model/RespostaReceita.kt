package br.senai.sp.jandira.gestaodereceitas.model

import com.google.gson.annotations.SerializedName

data class RespostaReceita(
    @SerializedName("status") val status: Boolean,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("message") val message: String?, // Alterado para "message" e tornado nullable
    @SerializedName("items") val items: List<Receita>? // Adicionado 'items' como uma lista de Receita e tornado nullable
)