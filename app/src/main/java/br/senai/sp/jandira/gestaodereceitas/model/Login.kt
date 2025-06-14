package br.senai.sp.jandira.gestaodereceitas.model

import com.google.gson.annotations.SerializedName

data class Login(
    @SerializedName("email") val email: String,
    @SerializedName("senha") val senha: String
)