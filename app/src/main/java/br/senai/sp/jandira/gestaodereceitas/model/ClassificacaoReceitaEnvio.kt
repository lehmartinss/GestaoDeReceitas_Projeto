// br.senai.sp.jandira.gestaodereceitas.model/ClassificacaoReceitaEnvio.kt

package br.senai.sp.jandira.gestaodereceitas.model

import com.google.gson.annotations.SerializedName

data class ClassificacaoReceitaEnvio(
    @SerializedName("id_classificacao") val idClassificacao: Int
)