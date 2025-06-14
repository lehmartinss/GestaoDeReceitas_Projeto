package br.senai.sp.jandira.gestaodereceitas.model

import com.google.gson.annotations.SerializedName

data class RespostaHome(
    @SerializedName("status") val status: Boolean, // Adicionado para mapear o campo 'status'
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("message") val message: String,
    @SerializedName("receitasPublicadas") val receitasPublicadas: List<Receita>? // Corrigido para 'receitasPublicadas' (camelCase)
)