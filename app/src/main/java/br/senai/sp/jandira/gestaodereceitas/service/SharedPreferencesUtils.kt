package br.senai.sp.jandira.gestaodereceitas.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object SharedPreferencesUtils {
    private const val PREFS_NAME = "MyRecipeAppPrefs"
    private const val USER_ID_KEY = "userId"
    private const val KEY_USER_NAME = "userName"
    private const val KEY_USER_EMAIL = "userEmail"
    private const val KEY_PROFILE_IMAGE_URL = "profileImageUrl" // Nova chave para a URL da foto de perfil

    // Função auxiliar para obter SharedPreferences de forma consistente
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Funções de UserID (mantidas como estavam, pois são chamadas específicas)
    fun saveUserId(context: Context, userId: Int) {
        val editor = getSharedPreferences(context).edit()
        editor.putInt(USER_ID_KEY, userId)
        editor.apply()
        Log.i("SharedPreferencesUtils", "UserID salvo: $userId")
    }

    fun getUserId(context: Context): Int {
        val userId = getSharedPreferences(context).getInt(USER_ID_KEY, 0)
        Log.i("SharedPreferencesUtils", "UserID recuperado: $userId")
        return userId
    }

    fun clearUserId(context: Context) {
        getSharedPreferences(context).edit().remove(USER_ID_KEY).apply()
        Log.i("SharedPreferencesUtils", "UserID limpo.")
    }

    // Função para salvar todos os dados do usuário de uma vez
    fun saveUserData(context: Context, userId: Int, userName: String, userEmail: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putInt(USER_ID_KEY, userId)
        editor.putString(KEY_USER_NAME, userName)
        editor.putString(KEY_USER_EMAIL, userEmail)
        editor.apply()
        Log.i("SharedPreferencesUtils", "UserData salvo: ID=$userId, Nome=$userName, Email=$userEmail")
    }

    // Funções para obter nome e email do usuário
    fun getUserName(context: Context): String {
        return getSharedPreferences(context).getString(KEY_USER_NAME, "Usuário") ?: "Usuário"
    }

    fun getUserEmail(context: Context): String {
        return getSharedPreferences(context).getString(KEY_USER_EMAIL, "email@example.com") ?: "email@example.com"
    }


    // NOVO MÉTODO: Salvar a URL da foto de perfil
    fun saveProfileImageUrl(context: Context, imageUrl: String?) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_PROFILE_IMAGE_URL, imageUrl)
        editor.apply()
        Log.i("SharedPreferencesUtils", "URL da foto de perfil salva: $imageUrl")
    }

    // NOVO MÉTODO: Obter a URL da foto de perfil
    fun getProfileImageUrl(context: Context): String? {
        val imageUrl = getSharedPreferences(context).getString(KEY_PROFILE_IMAGE_URL, null)
        Log.i("SharedPreferencesUtils", "URL da foto de perfil recuperada: $imageUrl")
        return imageUrl
    }

    // NOVO MÉTODO: Limpar todos os dados do usuário, incluindo a foto de perfil
    fun clearAllUserData(context: Context) {
        getSharedPreferences(context).edit().clear().apply()
        Log.i("SharedPreferencesUtils", "Todos os dados do usuário (incluindo foto de perfil) foram limpos.")
    }
}