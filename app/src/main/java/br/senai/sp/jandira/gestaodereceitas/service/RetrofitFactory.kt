// NO SEU ARQUIVO RetrofitFactory.kt

package br.senai.sp.jandira.gestaodereceitas.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitFactory {

    // ALTERE A BASE_URL PARA ONDE COMEÇA A SUA API REALMENTE
    private val BASE_URL = "http://192.168.15.9:8080/v1/controle-receita/" // <<< Mude AQUI!!!

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()

    fun getCadastroService(): CadastroService {
        return retrofit.create(CadastroService::class.java)
    }

}