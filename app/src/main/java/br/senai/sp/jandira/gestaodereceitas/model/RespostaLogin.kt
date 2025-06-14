package br.senai.sp.jandira.gestaodereceitas.model

import com.google.gson.annotations.SerializedName

data class RespostaLogin( // Ou LoginApiResponse
    @SerializedName("status") val status: Boolean,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("usuario") val usuario: List<User> // A API retorna uma lista, mesmo que com um único usuário
)