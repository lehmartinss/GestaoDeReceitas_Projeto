package br.senai.sp.jandira.gestaodereceitas

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType // Importar NavType
import androidx.navigation.navArgument // Importar navArgument
import br.senai.sp.jandira.gestaodereceitas.screens.TelaCadastro
import br.senai.sp.jandira.gestaodereceitas.screens.TelaHome
import br.senai.sp.jandira.gestaodereceitas.screens.TelaLogin
import br.senai.sp.jandira.gestaodereceitas.screens.TelaPerfil
import br.senai.sp.jandira.gestaodereceitas.screens.TelaReceita
import br.senai.sp.jandira.gestaodereceitas.screens.TelaRecuperacaoSenha
import br.senai.sp.jandira.gestaodereceitas.screens.TelaDetalhesReceita // <-- NOVO IMPORT: Sua tela de detalhes
import br.senai.sp.jandira.gestaodereceitas.ui.theme.GestaoDeReceitasProjetoTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GestaoDeReceitasProjetoTheme {
                var navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") {
                        TelaLogin(navController)
                    }
                    composable("home") {
                        TelaHome(navController)
                    }
                    composable("cadastro") {
                        TelaCadastro(navController)
                    }
                    composable("RecuperarSenha") {
                        TelaRecuperacaoSenha(navController)
                    }
                    composable("receita") {
                        TelaReceita(navController)
                    }
                    composable("perfil") {
                        TelaPerfil(navController)
                    }
                    // **** ADIÇÃO IMPORTANTE AQUI: ROTA PARA DETALHES DA RECEITA ****
                    composable(
                        // Define a rota com um argumento dinâmico {idReceita}
                        route = "detalhes_receita/{idReceita}",
                        // Declara que o argumento "idReceita" é do tipo inteiro
                        arguments = listOf(navArgument("idReceita") { type = NavType.IntType })
                    ) { backStackEntry ->
                        // Extrai o valor do ID da receita da rota
                        val idReceita = backStackEntry.arguments?.getInt("idReceita")
                        // Chama a TelaDetalhesReceita passando o ID extraído
                        TelaDetalhesReceita(navController = navController, idReceita = idReceita)
                    }
                    // *****************************************************************
                }
            }
        }
    }
}
