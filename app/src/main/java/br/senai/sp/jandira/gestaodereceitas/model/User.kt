package br.senai.sp.jandira.gestaodereceitas.model

import com.google.gson.annotations.SerializedName

// A data class User (se já não estiver definida em seu projeto)
data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("nome_usuario") val nome_usuario: String,
    @SerializedName("email") val email: String,
    @SerializedName("senha") val senha: String, // Geralmente não se retorna a senha, mas está no seu exemplo.
    @SerializedName("palavra_chave") val palavra_chave: String,
    @SerializedName("foto_perfil") val foto_perfil: String?, // Pode ser nulo
    @SerializedName("data_criacao") val data_criacao: String,
    @SerializedName("data_atualizacao") val data_atualizacao: String
)