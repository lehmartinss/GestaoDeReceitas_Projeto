package br.senai.sp.jandira.gestaodereceitas.service

import android.content.Context
import android.util.Log // Adicione este import

object SharedPreferencesUtils {
    private const val PREFS_NAME = "MyRecipeAppPrefs"
    private const val USER_ID_KEY = "userId"

    fun saveUserId(context: Context, userId: Int) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putInt(USER_ID_KEY, userId)
        editor.apply()
        Log.i("br.senai.sp.jandira.gestaodereceitas.service.SharedPreferencesUtils", "UserID salvo: $userId") // Log para verificar o salvamento
    }

    fun getUserId(context: Context): Int {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userId = sharedPrefs.getInt(USER_ID_KEY, 0) // 0 é o valor padrão se não encontrar
        Log.i("br.senai.sp.jandira.gestaodereceitas.service.SharedPreferencesUtils", "UserID recuperado: $userId") // Log para verificar a leitura
        return userId
    }

    fun clearUserId(context: Context) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit().remove(USER_ID_KEY).apply()
        Log.i("br.senai.sp.jandira.gestaodereceitas.service.SharedPreferencesUtils", "UserID limpo.")
    }
}