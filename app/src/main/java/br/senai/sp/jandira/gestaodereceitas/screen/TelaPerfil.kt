package br.senai.sp.jandira.gestaodereceitas.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext // Importar LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.senai.sp.jandira.gestaodereceitas.R
import br.senai.sp.jandira.gestaodereceitas.model.Receita
import br.senai.sp.jandira.gestaodereceitas.model.RespostaHome // RespostaHome pois listarReceitasDoUsuario retorna isso
import br.senai.sp.jandira.gestaodereceitas.service.RetrofitFactory
import br.senai.sp.jandira.gestaodereceitas.service.SharedPreferencesUtils
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun TelaPerfil(navController: NavController?) {

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current // Obtenha o contexto aqui

    var receitasPerfil by remember { mutableStateOf<List<Receita>>(emptyList()) }
    val userId = SharedPreferencesUtils.getUserId(context)

    LaunchedEffect(userId) { // O efeito será re-executado se o userId mudar (improvável para esta tela)
        if (userId != 0) { // Verifica se há um userId válido
            Log.d("TelaPerfil", "Tentando carregar receitas para o userId: $userId")
            RetrofitFactory()
                .getCadastroService()
                .listarReceitasDoUsuario(userId)
                .enqueue(object : Callback<RespostaHome> { // Agora espera RespostaHome
                    override fun onResponse(call: Call<RespostaHome>, response: Response<RespostaHome>) {
                        if (response.isSuccessful) {
                            val corpo = response.body()
                            if (corpo != null) {
                                receitasPerfil = corpo.receitasPublicadas ?: emptyList() // Acessa 'receitasPublicadas'
                                Log.i("API_PERFIL", "Receitas carregadas: ${receitasPerfil.size} para o usuário $userId")
                            } else {
                                Log.w("API_PERFIL", "Corpo da resposta de receitas do perfil é nulo.")
                                receitasPerfil = emptyList()
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Erro ao carregar receitas do perfil: ${response.code()}")
                            }
                            Log.e("API_PERFIL", "Erro HTTP ao carregar receitas do perfil: ${response.code()} - ${response.errorBody()?.string()}")
                            receitasPerfil = emptyList() // Limpa a lista em caso de erro
                        }
                    }

                    override fun onFailure(call: Call<RespostaHome>, t: Throwable) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Falha na requisição de receitas do perfil: ${t.message}")
                        }
                        Log.e("API_PERFIL", "Falha de rede ao carregar receitas do perfil: ${t.message}")
                        receitasPerfil = emptyList() // Limpa a lista em caso de falha
                    }
                })
        } else {
            Log.w("TelaPerfil", "ID do usuário não encontrado em SharedPreferences. Não foi possível carregar receitas do perfil.")
            scope.launch {
                snackbarHostState.showSnackbar("Usuário não logado. Faça login para ver suas receitas.")
            }
            receitasPerfil = emptyList() // Garante que a lista esteja vazia se não houver userId
            // Opcional: Redirecionar para a tela de login
            // navController?.navigate("login") {
            //     popUpTo("perfil") { inclusive = true }
            // }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues -> // Usar paddingValues para respeitar o Snackbar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F0D6))
                .padding(paddingValues) // Aplicar o padding aqui
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // Topo com avatar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.avatar),
                        contentDescription = stringResource(R.string.avatar_description),
                        modifier = Modifier.size(75.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        // Você precisará buscar o nome de usuário do SharedPreferences ou de uma API de perfil
                        // Por enquanto, manterei os placeholders.
                        Text(
                            text = stringResource(R.string.nomeUser), // Assumindo que você tem essas strings
                            color = Color.Black,
                            fontSize = 25.sp
                        )
                        Text(
                            text = stringResource(R.string.nomeDeUser), // Assumindo que você tem essas strings
                            color = Color.Black,
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Botão de publicação
                Button(
                    onClick = { navController?.navigate("receita") },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF982829)),
                    modifier = Modifier
                        .height(50.dp)
                        .width(250.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(text = stringResource(R.string.botaoPublicar), color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Título "Minhas Receitas"
                Text(
                    text = stringResource(R.string.minhas_receitas), // Reutilizando a string da TelaHome ou crie uma nova
                    fontSize = 20.sp,
                    color = Color(0xFF325862),
                    modifier = Modifier.padding(bottom = 8.dp)
                )


                // Lista de receitas do usuário
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(receitasPerfil) { receita ->
                        ReceitaItem(receita)
                    }
                }
            }
        }
    }
}

@Composable
fun ReceitaItem(receita: Receita) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(text = receita.titulo, fontSize = 18.sp, color = Color(0xFF982829))
        Spacer(modifier = Modifier.height(4.dp))
        // Ajuste para exibir os campos corretamente, talvez usando stringResource se eles forem fixos
        Text(text = "Ingredientes: ${receita.ingrediente}", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = "Modo de Preparo: ${receita.modo_preparo}", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = "Tempo de Preparo: ${receita.tempo_preparo}", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = "Dificuldade: ${receita.dificuldade}", fontSize = 14.sp)
        // Se a classificação for importante aqui, você pode adicioná-la também.
        // val classificacaoExibida = receita.classificacao_nome ?: "N/A"
        // Text(text = "Classificação: $classificacaoExibida", fontSize = 14.sp)
    }
}

@Preview(showSystemUi = true)
@Composable
fun TelaPerfilPreview() {
    // Para um Preview mais realista, você precisaria mockar o SharedPreferences ou
    // passar um userId simulado, o que é complexo em Previews.
    // O ideal é testar essa tela em um emulador ou dispositivo real.
    TelaPerfil(navController = null)
}