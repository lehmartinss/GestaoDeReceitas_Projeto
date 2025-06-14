package br.senai.sp.jandira.gestaodereceitas.model

// Em br.senai.sp.jandira.gestaodereceitas.model/RespostaCadastro.kt
import com.google.gson.annotations.SerializedName

data class RespostaCadastro(
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("message") val message: String,
    @SerializedName("usuario") val usuario: User? // A API retorna um objeto 'usuario' aninhado
)
