package br.senai.sp.jandira.gestaodereceitas.model

import com.google.gson.annotations.SerializedName

data class Cadastro(
    @SerializedName("nome_usuario") val nome_usuario: String,
    @SerializedName("email") val email: String,
    @SerializedName("senha") val senha: String,
    @SerializedName("palavra_chave") val palavra_chave: String
)