package br.senai.sp.jandira.gestaodereceitas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.senai.sp.jandira.gestaodereceitas.screens.TelaCadastro
import br.senai.sp.jandira.gestaodereceitas.screens.TelaHome
import br.senai.sp.jandira.gestaodereceitas.screens.TelaLogin
import br.senai.sp.jandira.gestaodereceitas.screens.TelaPerfil
import br.senai.sp.jandira.gestaodereceitas.screens.TelaReceita
import br.senai.sp.jandira.gestaodereceitas.screens.TelaRecuperacaoSenha
import br.senai.sp.jandira.gestaodereceitas.ui.theme.GestaoDeReceitasProjetoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Usa o tema principal do seu projeto
            GestaoDeReceitasProjetoTheme {
                // Instancia o NavController para gerenciar a navegação entre as telas
                var navController = rememberNavController()
                // Define o NavHost, que é o container para as rotas de navegação
                NavHost(
                    navController = navController,
                    startDestination = "login" // Define a tela de login como a tela inicial
                ) {
                    // Define a rota para a tela de login
                    composable("login") {
                        TelaLogin(navController)
                    }
                    // Define a rota para a tela Home
                    composable("home") {
                        TelaHome(navController)
                    }
                    // Define a rota para a tela de Cadastro
                    composable("cadastro") {
                        TelaCadastro(navController)
                    }
                    // Define a rota para a tela de Recuperação de Senha
                    composable("RecuperarSenha") { // Verifique se o nome da rota é exatamente este, incluindo maiúsculas
                        TelaRecuperacaoSenha(navController)
                    }
                    // Define a rota para a tela de Receita (Publicar Receita)
                    composable("receita") {
                        TelaReceita(navController)
                    }
                    // Define a rota para a tela de Perfil
                    composable("perfil") {
                        TelaPerfil(navController)
                    }
                }
            }
        }
    }
}