package br.senai.sp.jandira.gestaodereceitas.model

import com.google.gson.annotations.SerializedName

data class Receita(
    @SerializedName("id_receita") val id_receita: Int?, // Mudei de 'id' para 'id_receita' conforme o esperado
    @SerializedName("titulo") val titulo: String,
    @SerializedName("tempo_preparo") val tempo_preparo: String,
    @SerializedName("fotoUrl") val fotoUrl: String, // Mudei de 'foto_receita' para 'fotoUrl' para refletir a URL do Firebase
    @SerializedName("ingrediente") val ingrediente: String,
    @SerializedName("modo_preparo") val modo_preparo: String,
    @SerializedName("dificuldade") val dificuldade: String,
    @SerializedName("id_usuario") val id_usuario: Int?,
    @SerializedName("data_publicacao") val data_publicacao: String? = null, // Deixei como nullable com padrão null
    @SerializedName("classificacao_ids") val classificacao_ids: List<Int>? = null, // Usado para enviar os IDs
    @SerializedName("classificacao_nome") val classificacao_nome: String? = null, // Usado para receber o nome
    @SerializedName("usuario") val usuario: User? = null, // Objeto aninhado para dados do usuário
    @SerializedName("classificacoes_detalhe") val classificacoes_detalhe: List<ClassificacaoReceita>? = null // Lista de objetos ClassificacaoReceita para detalhes
)
