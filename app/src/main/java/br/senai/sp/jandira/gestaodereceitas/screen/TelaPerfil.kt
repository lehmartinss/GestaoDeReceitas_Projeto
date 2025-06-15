package br.senai.sp.jandira.gestaodereceitas.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons // Importe para usar ícones padrão
import androidx.compose.material.icons.filled.ArrowBack // Importe o ícone de seta para trás
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.senai.sp.jandira.gestaodereceitas.R
import br.senai.sp.jandira.gestaodereceitas.model.Receita
import br.senai.sp.jandira.gestaodereceitas.model.RespostaHome
import br.senai.sp.jandira.gestaodereceitas.service.RetrofitFactory
import br.senai.sp.jandira.gestaodereceitas.service.SharedPreferencesUtils // Certifique-se de que este import está correto
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class) // Anotação necessária para ExposedDropdownMenuBox, etc.
@Composable
fun TelaPerfil(navController: NavController?) {

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var receitasPerfil by remember { mutableStateOf<List<Receita>>(emptyList()) }
    val userId = SharedPreferencesUtils.getUserId(context)

    // NOVAS VARIÁVEIS DE ESTADO PARA NOME E EMAIL DO USUÁRIO
    val userName = remember { mutableStateOf("") }
    val userEmail = remember { mutableStateOf("") }

    // Carregar nome e email do SharedPreferences quando a tela é composta
    LaunchedEffect(Unit) { // Usar Unit como chave para garantir que rode apenas uma vez na composição inicial
        userName.value = SharedPreferencesUtils.getUserName(context)
        userEmail.value = SharedPreferencesUtils.getUserEmail(context)
        Log.d("TelaPerfil", "Nome do usuário carregado: ${userName.value}")
        Log.d("TelaPerfil", "Email do usuário carregado: ${userEmail.value}")
    }

    LaunchedEffect(userId) {
        if (userId != 0) {
            Log.d("TelaPerfil", "Tentando carregar receitas para o userId: $userId")
            RetrofitFactory()
                .getCadastroService()
                .listarReceitasDoUsuario(userId)
                .enqueue(object : Callback<RespostaHome> {
                    override fun onResponse(call: Call<RespostaHome>, response: Response<RespostaHome>) {
                        if (response.isSuccessful) {
                            val corpo = response.body()
                            if (corpo != null) {
                                receitasPerfil = corpo.receitasPublicadas ?: emptyList()
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
                            receitasPerfil = emptyList()
                        }
                    }

                    override fun onFailure(call: Call<RespostaHome>, t: Throwable) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Falha na requisição de receitas do perfil: ${t.message}")
                        }
                        Log.e("API_PERFIL", "Falha de rede ao carregar receitas do perfil: ${t.message}")
                        receitasPerfil = emptyList()
                    }
                })
        } else {
            Log.w("TelaPerfil", "ID do usuário não encontrado em SharedPreferences. Não foi possível carregar receitas do perfil.")
            scope.launch {
                snackbarHostState.showSnackbar("Usuário não logado. Faça login para ver suas receitas.")
            }
            receitasPerfil = emptyList()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F0D6))
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // Topo com avatar e botão de voltar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Botão de voltar
                    IconButton(
                        onClick = { navController?.navigate("home") },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar para Home",
                            tint = Color.Black
                        )
                    }

                    // Avatar e informações do usuário (alinhados à direita do botão de voltar)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.avatar),
                            contentDescription = stringResource(R.string.avatar_description),
                            modifier = Modifier.size(75.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            // Exibindo o nome do usuário do SharedPreferences
                            Text(
                                text = userName.value,
                                color = Color.Black,
                                fontSize = 25.sp
                            )
                            // Exibindo o email do usuário do SharedPreferences
                            Text(
                                text = userEmail.value,
                                color = Color.Black,
                                fontSize = 20.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f)) // Empurra o conteúdo para a esquerda
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
                    text = stringResource(R.string.minhas_receitas),
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
        Text(text = "Ingredientes: ${receita.ingrediente}", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = "Modo de Preparo: ${receita.modo_preparo}", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = "Tempo de Preparo: ${receita.tempo_preparo}", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = "Dificuldade: ${receita.dificuldade}", fontSize = 14.sp)
    }
}

@Preview(showSystemUi = true)
@Composable
fun TelaPerfilPreview() {
    TelaPerfil(navController = null)
}