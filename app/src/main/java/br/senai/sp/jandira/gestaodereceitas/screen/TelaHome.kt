package br.senai.sp.jandira.gestaodereceitas.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import br.senai.sp.jandira.gestaodereceitas.model.ClassificacaoReceita
import br.senai.sp.jandira.gestaodereceitas.model.Receita
import br.senai.sp.jandira.gestaodereceitas.model.RespostaClassificacao // Importe seu modelo de resposta
import br.senai.sp.jandira.gestaodereceitas.model.RespostaHome // Importe seu modelo de resposta
import br.senai.sp.jandira.gestaodereceitas.service.RetrofitFactory
import br.senai.sp.jandira.gestaodereceitas.service.SharedPreferencesUtils
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaHome(navController: NavController?) {
    val coroutineScope = rememberCoroutineScope()
    var receitasState by remember { mutableStateOf<List<Receita>>(emptyList()) }
    var classificacoesDisponiveis by remember { mutableStateOf<List<ClassificacaoReceita>>(emptyList()) }
    val listState = rememberLazyListState()
    var searchTerm by remember { mutableStateOf("") }

    val context = LocalContext.current
    val userId = SharedPreferencesUtils.getUserId(context)

    val fetchRecipes: (String?, Int?, Int?) -> Unit = { query, classificationId, currentUserId ->
        val service = RetrofitFactory().getCadastroService()
        val call: Call<RespostaHome>

        if (query != null && query.isNotBlank()) {
            call = service.buscarReceitas(query)
            Log.d("TelaHome", "Buscando receitas por termo: '$query'")
        }
        else if (classificationId != null && classificationId != 0) {
            call = service.listarReceitaByClassificacao(classificationId)
            Log.d("TelaHome", "Filtrando receitas por classificação ID: $classificationId")
        }
        else if (currentUserId != null && currentUserId != 0) {
            call = service.listarReceitasDoUsuario(currentUserId)
            Log.d("TelaHome", "Carregando receitas para o usuário ID: $currentUserId")
        }
        // 4. Se nenhuma das condições anteriores for atendida (nenhum termo, filtro ou ID de usuário),
        //    lista todas as receitas como fallback.
        else {
            call = service.listarTodasReceitas()
            Log.d("TelaHome", "Nenhum critério de busca/filtro/usuário, listando todas as receitas.")
        }

        call.enqueue(object : Callback<RespostaHome> {
            override fun onResponse(call: Call<RespostaHome>, response: Response<RespostaHome>) {
                if (response.isSuccessful) {
                    // Acessa a lista de receitas através do campo 'receitasPublicadas'
                    receitasState = response.body()?.receitasPublicadas ?: emptyList()
                    Log.d("TelaHome", "Receitas carregadas/filtradas: ${receitasState.size}")
                } else {
                    Log.e("TelaHome", "Erro ao carregar/filtrar receitas: ${response.code()} - ${response.errorBody()?.string()}")
                    // TODO: Considere mostrar um Toast ou Snackbar de erro aqui.
                }
            }

            override fun onFailure(call: Call<RespostaHome>, t: Throwable) {
                Log.e("TelaHome", "Erro de rede ao carregar/filtrar receitas: ${t.message}")
                // TODO: Considere mostrar um Toast ou Snackbar de erro de conexão aqui.
            }
        })
    }

    // Efeito colateral para buscar dados iniciais (classificações e receitas do usuário)
    LaunchedEffect(Unit) {
        // 1. Buscar todas as classificações disponíveis
        RetrofitFactory().getCadastroService().listarTodasClassificacoes().enqueue(object : Callback<RespostaClassificacao> {
            override fun onResponse(call: Call<RespostaClassificacao>, response: Response<RespostaClassificacao>) {
                if (response.isSuccessful) {
                    // Acessa a lista de classificações através do campo 'classificacoes'
                    classificacoesDisponiveis = response.body()?.classificacoes ?: emptyList()
                    Log.d("TelaHome", "Classificações carregadas: ${classificacoesDisponiveis.size}")
                } else {
                    Log.e("TelaHome", "Erro ao carregar classificações: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<RespostaClassificacao>, t: Throwable) {
                Log.e("TelaHome", "Erro de rede ao carregar classificações: ${t.message}")
            }
        })

        // 2. Buscar as receitas do usuário logado inicialmente
        fetchRecipes(null, null, userId) // userId é recuperado no escopo do Composable
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F0D6))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = stringResource(R.string.logo_description),
                    modifier = Modifier
                        .size(60.dp)
                        .weight(0.2f)
                        .padding(end = 8.dp)
                )
                // Campo de pesquisa
                OutlinedTextField(
                    value = searchTerm,
                    onValueChange = {
                        searchTerm = it
                    },
                    modifier = Modifier.weight(0.8f),
                    label = { Text(text = stringResource(R.string.pesquisar)) },
                    trailingIcon = {
                        IconButton(onClick = {
                            fetchRecipes(searchTerm, null, null)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.icone_pesquisar),
                                tint = Color.Gray
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFF325862),
                        focusedBorderColor = Color(0xFF325862),
                        focusedLabelColor = Color(0xFF325862),
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = Color(0xFF325862),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                IconButton(onClick = {
                    val firstVisible = listState.firstVisibleItemIndex
                    val targetIndex = maxOf(0, firstVisible - 3)
                    coroutineScope.launch {
                        listState.animateScrollToItem(targetIndex)
                    }
                }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = stringResource(R.string.voltar_categorias))
                }
                // Lista de categorias (classificações)
                LazyRow(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(classificacoesDisponiveis) { classificacao ->
                        Button(
                            onClick = {
                                // CORREÇÃO AQUI: Usar 'classificacao.id_classificacao'
                                Log.d("TelaHome", "Filtro clicado: ${classificacao.nome} (ID: ${classificacao.id_classificacao})")
                                fetchRecipes(null, classificacao.id_classificacao, null)
                                searchTerm = "" // Limpa o campo de pesquisa ao filtrar por categoria
                            },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF982829)),
                            modifier = Modifier
                                .height(32.dp)
                                .width(IntrinsicSize.Min)
                        ) {
                            Text(
                                text = classificacao.nome,
                                color = Color.White,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
                IconButton(onClick = {
                    val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                    val targetIndex = minOf(classificacoesDisponiveis.size - 1, lastVisible + 3)
                    coroutineScope.launch {
                        listState.animateScrollToItem(targetIndex)
                    }
                }) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = stringResource(R.string.avancar_categorias))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { navController?.navigate("perfil") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF325862))
                ) {
                    Text(text = stringResource(R.string.ver_perfil))
                }
                Button(
                    onClick = { navController?.navigate("receita") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF982829))
                ) {
                    Text(text = stringResource(R.string.publicar_receita_botao))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = stringResource(R.string.minhas_receitas), fontSize = 20.sp, color = Color(0xFF325862))

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(receitasState) { receita ->
                    ReceitaCard(receita = receita, classificacoesDisponiveis = classificacoesDisponiveis)
                }
            }
        }
    }
}

@Composable
fun ReceitaCard(receita: Receita, classificacoesDisponiveis: List<ClassificacaoReceita>) {
    val classificacaoExibida = receita.classificacao_nome ?:
    (receita.classificacao_ids?.firstOrNull()?.let { id -> // `classificacao_ids` é nulável
        // CORREÇÃO AQUI: Usar 'it.id_classificacao'
        classificacoesDisponiveis.find { it.id_classificacao == id }?.nome
    } ?: "N/A")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(text = receita.titulo, fontSize = 18.sp, color = Color(0xFF982829))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = stringResource(R.string.classificacao_dois_pontos, classificacaoExibida), fontSize = 14.sp)
            Text(text = stringResource(R.string.ingredientes_dois_pontos, receita.ingrediente), fontSize = 14.sp)
            Text(text = stringResource(R.string.modo_preparo_dois_pontos, receita.modo_preparo), fontSize = 14.sp)
            Text(text = stringResource(R.string.tempo_preparo_dois_pontos, receita.tempo_preparo), fontSize = 14.sp)
            Text(text = stringResource(R.string.dificuldade_dois_pontos, receita.dificuldade), fontSize = 14.sp)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TelaHomePreview() {
    TelaHome(navController = null)
}