package br.senai.sp.jandira.gestaodereceitas.model

import com.google.gson.annotations.SerializedName

data class RespostaHome(
    @SerializedName("status") val status: Boolean,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("message") val message: String,
    @SerializedName("items") val items: Int, // Adicionado para mapear o campo 'items' do JSON
    @SerializedName("receitas") val receitasPublicadas: List<Receita>? // CORRIGIDO: Mapeia a chave "receitas" do JSON para a variável 'receitasPublicadas'
)
