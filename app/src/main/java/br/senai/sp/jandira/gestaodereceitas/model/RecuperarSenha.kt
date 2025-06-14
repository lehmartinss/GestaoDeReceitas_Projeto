package br.senai.sp.jandira.gestaodereceitas.model

// Em br.senai.sp.jandira.gestaodereceitas.model/RecuperarSenha.kt
import com.google.gson.annotations.SerializedName

data class RecuperarSenha(
    @SerializedName("email") val email: String,
    @SerializedName("palavra_chave") val palavra_chave: String,
    @SerializedName("nova_senha") val nova_senha: String
    // Ajuste os campos conforme sua API espera para a atualização de senha
)
