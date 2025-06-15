package br.senai.sp.jandira.gestaodereceitas.service

import android.content.Context
import android.util.Log

object SharedPreferencesUtils {
    private const val PREFS_NAME = "MyRecipeAppPrefs"
    private const val USER_ID_KEY = "userId"
    private const val KEY_USER_NAME = "userName"
    private const val KEY_USER_EMAIL = "userEmail"

    // Funções já existentes (corretas)
    fun saveUserId(context: Context, userId: Int) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putInt(USER_ID_KEY, userId)
        editor.apply()
        Log.i("br.senai.sp.jandira.gestaodereceitas.service.SharedPreferencesUtils", "UserID salvo: $userId")
    }

    fun getUserId(context: Context): Int {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userId = sharedPrefs.getInt(USER_ID_KEY, 0)
        Log.i("br.senai.sp.jandira.gestaodereceitas.service.SharedPreferencesUtils", "UserID recuperado: $userId")
        return userId
    }

    fun clearUserId(context: Context) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit().remove(USER_ID_KEY).apply()
        Log.i("br.senai.sp.jandira.gestaodereceitas.service.SharedPreferencesUtils", "UserID limpo.")
    }

    // Função saveUserData corrigida
    fun saveUserData(context: Context, userId: Int, userName: String, userEmail: String) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) // Corrigido aqui
        val editor = sharedPrefs.edit()
        editor.putInt(USER_ID_KEY, userId)
        editor.putString(KEY_USER_NAME, userName)
        editor.putString(KEY_USER_EMAIL, userEmail)
        editor.apply()
        Log.i("br.senai.sp.jandira.gestaodereceitas.service.SharedPreferencesUtils", "UserData salvo: ID=$userId, Nome=$userName, Email=$userEmail") // Log para verificar o salvamento
    }

    // Funções getUserName e getUserEmail corrigidas
    fun getUserName(context: Context): String {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) // Corrigido aqui
        return sharedPrefs.getString(KEY_USER_NAME, "Usuário") ?: "Usuário"
    }

    fun getUserEmail(context: Context): String {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) // Corrigido aqui
        return sharedPrefs.getString(KEY_USER_EMAIL, "email@example.com") ?: "email@example.com"
    }

    // Removendo a função getSharedPreferences privada, pois você não a está usando globalmente.
    // As chamadas diretas são mais explícitas nesse contexto.
}