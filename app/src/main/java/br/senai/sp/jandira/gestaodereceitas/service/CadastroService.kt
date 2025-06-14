package br.senai.sp.jandira.gestaodereceitas.service

import br.senai.sp.jandira.gestaodereceitas.model.*
import retrofit2.Call
import retrofit2.http.*

interface CadastroService {

    // --- Métodos de Usuário (Cadastro, Login, Recuperação de Senha) ---

    // Método para cadastrar um novo usuário
    @Headers("Content-Type: application/json")
    @POST("usuario") // Endpoint para criar um novo usuário
    fun insert(@Body cadastro: Cadastro): Call<RespostaCadastro>

    // Método para realizar o login
    @Headers("Content-Type: application/json")
    @POST("login") // Endpoint para autenticar o login
    fun inserir(@Body login: Login): Call<LoginApiResponse>

    // Método para recuperar senha (atualizar dados do usuário, pode ser um PUT)
    @Headers("Content-Type: application/json")
    @PUT("usuario") // Endpoint para atualizar dados de um usuário
    fun update(@Body recuperarSenha: RecuperarSenha): Call<RecuperarSenha>


    // --- Métodos de Receita ---

    // Método para publicar uma nova receita
    @Headers("Content-Type: application/json")
    @POST("receita") // Endpoint para criar uma nova receita
    fun publicar(@Body receita: Receita): Call<RespostaReceita>

    // Método para listar TODAS as receitas (usado como fallback ou para explorar todas as receitas)
    // Assumindo um endpoint como GET /v1/controle-receita/receita/all
    // Verifique o path real no seu backend se não for 'receita/all'
    @Headers("Content-Type: application/json")
    @GET("receita/all") // Endpoint para listar todas as receitas
    fun listarTodasReceitas(): Call<RespostaHome>


    // Método para listar receitas de um USUÁRIO específico pelo ID (as "Minhas Receitas")
    // ✅ Este é o que está no seu backend Express: /usuario/:id/receitas
    // Note que não tem o prefixo "receita/" aqui, pois ele vem da rota /usuario
    @Headers("Content-Type: application/json")
    @GET("/usuario/{id}/receitas") // Endpoint para listar receitas de um usuário específico
    fun listarReceitasDoUsuario(@Path("id") id: Int): Call<RespostaHome>


    // Método para buscar receitas por um termo (ex: no título, ingredientes)
    // Assumindo um endpoint como: GET /v1/controle-receita/receita/buscar?termo=query
    @Headers("Content-Type: application/json")
    @GET("receita/buscar") // Endpoint para buscar receitas por termo
    fun buscarReceitas(@Query("termo") termo: String): Call<RespostaHome>


    // Método para listar receitas por ID de Classificação
    // Assumindo um endpoint como: GET /v1/controle-receita/receita/classificacao/{id}
    @Headers("Content-Type: application/json")
    @GET("receita/classificacao/{id}") // Endpoint para filtrar receitas por classificação
    fun listarReceitaByClassificacao(@Path("id") idClassificacao: Int): Call<RespostaHome>


    // --- Métodos de Classificação ---

    @Headers("Content-Type: application/json")
    @GET("classificacao/") // Endpoint para listar todas as classificações
    fun listarTodasClassificacoes(): Call<RespostaClassificacao>

    @Headers("Content-Type: application/json")
    @GET("receitas/{id}") // <<-- O endpoint da sua API para buscar uma receita por ID
    fun buscarReceitaPorId(@Path("id") idReceita: Int): Call<RespostaReceita>

}