package br.senai.sp.jandira.gestaodereceitas.model

import com.google.gson.annotations.SerializedName

data class RespostaReceita(
    @SerializedName("status") val status: String, // Ou Boolean, dependendo da sua API
    @SerializedName("message") val message: String,
    @SerializedName("receita") val receita: Receita? // Se a API retorna a receita criada/atualizada
    // Adicione outros campos se a API retornar
)