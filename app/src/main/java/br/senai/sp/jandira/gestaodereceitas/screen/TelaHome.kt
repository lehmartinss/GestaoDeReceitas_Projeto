package br.senai.sp.jandira.gestaodereceitas.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable // Importar o modificador clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues // Importar PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells // Importar GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid // Importar LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items // Importar items para LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape // Importar CircleShape para botões redondos
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card // Importar Card
import androidx.compose.material3.CardDefaults // Importar CardDefaults
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
import androidx.compose.ui.draw.clip // Importar clip para o arredondamento da imagem
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale // Importar ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight // Importar FontWeight
import androidx.compose.ui.text.style.TextOverflow // Importar TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.senai.sp.jandira.gestaodereceitas.R
import br.senai.sp.jandira.gestaodereceitas.model.ClassificacaoReceita
import br.senai.sp.jandira.gestaodereceitas.model.Receita
import br.senai.sp.jandira.gestaodereceitas.model.RespostaClassificacao
import br.senai.sp.jandira.gestaodereceitas.model.RespostaHome
import br.senai.sp.jandira.gestaodereceitas.service.RetrofitFactory
import coil.compose.AsyncImage // Importar AsyncImage para carregar imagens da URL
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

    val fetchRecipes: (String?, Int?) -> Unit = { query, classificationId ->
        val service = RetrofitFactory().getCadastroService()
        val call: Call<RespostaHome>

        if (query != null && query.isNotBlank()) {
            call = service.buscarReceitas(query)
            Log.d("TelaHome", "Buscando receitas por termo: '$query'")
        } else if (classificationId != null && classificationId != 0) {
            call = service.listarReceitaByClassificacao(classificationId)
            Log.d("TelaHome", "Filtrando receitas por classificação ID: $classificationId")
        } else {
            call = service.listarTodasReceitas()
            Log.d("TelaHome", "Carregando TODAS as receitas para a Tela Home.")
        }

        call.enqueue(object : Callback<RespostaHome> {
            override fun onResponse(call: Call<RespostaHome>, response: Response<RespostaHome>) {
                if (response.isSuccessful) {
                    receitasState = response.body()?.receitasPublicadas ?: emptyList()
                    Log.d("TelaHome", "Receitas carregadas/filtradas: ${receitasState.size}")
                } else {
                    Log.e("TelaHome", "Erro ao carregar/filtrar receitas: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<RespostaHome>, t: Throwable) {
                Log.e("TelaHome", "Erro de rede ao carregar/filtrar receitas: ${t.message}")
            }
        })
    }

    LaunchedEffect(Unit) {
        RetrofitFactory().getCadastroService().listarTodasClassificacoes().enqueue(object : Callback<RespostaClassificacao> {
            override fun onResponse(call: Call<RespostaClassificacao>, response: Response<RespostaClassificacao>) {
                if (response.isSuccessful) {
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

        fetchRecipes(null, null)
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F0D6)) // Cor de fundo do Figma
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp) // Ajuste o padding horizontal
                .fillMaxHeight()
        ) {
            // ** Top Bar - Ajustado para o Figma **
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp), // Padding vertical para a top bar
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Logo Receitabook
                Image(
                    painter = painterResource(id = R.drawable.logo), // Use um logo que se pareça com o do Figma
                    contentDescription = stringResource(R.string.logo_description),
                    modifier = Modifier
                        .size(45.dp) // Ajuste o tamanho do logo
                        .clickable {
                            fetchRecipes(null, null)
                            searchTerm = ""
                            Log.d("TelaHome", "Logo clicada: Restaurando todas as receitas.")
                        }
                )

                // Campo de Busca
                OutlinedTextField(
                    value = searchTerm,
                    onValueChange = {
                        searchTerm = it
                    },
                    modifier = Modifier
                        .weight(1f) // Ocupa o espaço restante
                        .padding(horizontal = 8.dp) // Espaçamento entre logo e busca, e busca e avatar
                        .height(48.dp), // Altura ajustada para parecer com o Figma
                    placeholder = { Text(text = stringResource(R.string.app_name), fontSize = 14.sp, color = Color.Gray) }, // Placeholder como no Figma
                    trailingIcon = {
                        IconButton(onClick = {
                            fetchRecipes(searchTerm, null)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.icone_pesquisar),
                                tint = Color.Gray // Cor do ícone de busca
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent, // Sem borda visível
                        focusedBorderColor = Color.Transparent, // Sem borda visível
                        focusedLabelColor = Color.Transparent, // Ajuste se precisar de label
                        unfocusedLabelColor = Color.Transparent, // Ajuste se precisar de label
                        cursorColor = Color.Black, // Cor do cursor
                        focusedContainerColor = Color.White, // Fundo branco como no Figma
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp), // Borda arredondada (metade da altura para ser oval)
                    singleLine = true // Garante que fique em uma única linha
                )

                // Ícone de Perfil (avatar da pessoa)
                Image(
                    painter = painterResource(id = R.drawable.avatar), // Substitua por seu recurso de avatar
                    contentDescription = stringResource(R.string.nomeUser),
                    modifier = Modifier
                        .size(48.dp) // Tamanho do avatar
                        .clip(CircleShape) // Avatar redondo
                        .clickable { navController?.navigate("perfil") } // Navega para o perfil
                )
            }

            // ** Seção de Categorias - Ajustada para o Figma **
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp) // Padding vertical
            ) {
                // Seta para esquerda
                IconButton(onClick = {
                    val firstVisible = listState.firstVisibleItemIndex
                    val targetIndex = maxOf(0, firstVisible - 3)
                    coroutineScope.launch {
                        listState.animateScrollToItem(targetIndex)
                    }
                }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = stringResource(R.string.voltar_categorias), tint = Color.Gray)
                }

                LazyRow(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp) // Pequeno padding para os lados
                ) {
                    items(classificacoesDisponiveis) { classificacao ->
                        Button(
                            onClick = {
                                Log.d("TelaHome", "Filtro clicado: ${classificacao.nome} (ID: ${classificacao.id})")
                                fetchRecipes(null, classificacao.id)
                                searchTerm = ""
                            },
                            shape = RoundedCornerShape(50), // Botões de pílula
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF982829)), // Cor vermelha do Figma
                            modifier = Modifier.height(35.dp) // Altura menor para os botões
                        ) {
                            Text(
                                text = classificacao.nome,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold // Um pouco mais bold
                            )
                        }
                    }
                }

                // Seta para direita
                IconButton(onClick = {
                    val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                    val targetIndex = minOf(classificacoesDisponiveis.size - 1, lastVisible + 3)
                    coroutineScope.launch {
                        listState.animateScrollToItem(targetIndex)
                    }
                }) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = stringResource(R.string.avancar_categorias), tint = Color.Gray)
                }
            }

            // Removendo os botões "Ver Perfil" e "Publicar Receita" da parte visível da tela
            // conforme o design do Figma. Se eles forem para outra tela, a navegação já está
            // tratada pelo clique no avatar ou por um FAB/botão em outro lugar.
            // Se precisar mantê-los, pode colocá-los em um `BottomAppBar` ou em uma tela separada de perfil.

            Spacer(modifier = Modifier.height(24.dp)) // Espaçamento maior antes da grade de receitas

            // ** Grid de Receitas - Ajustado para o Figma **
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 colunas
                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp), // Padding interno do grid
                verticalArrangement = Arrangement.spacedBy(16.dp), // Espaçamento vertical entre os cards
                horizontalArrangement = Arrangement.spacedBy(16.dp), // Espaçamento horizontal entre os cards
                modifier = Modifier.fillMaxSize() // Ocupa o restante do espaço
            ) {
                items(receitasState) { receita ->
                    ReceitaCardGrid(
                        receita = receita,
                        onClick = { clickedReceita ->
                            // Use o operador Elvis para fornecer um valor padrão (ex: 0 ou -1)
                            // se clickedReceita.id_receita for nulo.
                            // Lembre-se: isso é um workaround, a solução ideal é que o ID não seja nulo.
                            val receitaId = clickedReceita.id_receita ?: 0 // Ou qualquer Int que faça sentido como "ID inválido"
                            navController?.navigate("detalhes_receita/${receitaId}")
                        }
                    )
                }
            }
        }
    }
}

// ** Novo Composable para o Card de Receita no Grid (Figma) **
@Composable
fun ReceitaCardGrid(receita: Receita, onClick: (Receita) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min) // Garante que o Card se ajuste ao conteúdo
            .clickable { onClick(receita) }, // Card clicável
        shape = RoundedCornerShape(12.dp), // Cantos arredondados
        colors = CardDefaults.cardColors(containerColor = Color.White), // Fundo branco
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Sombra leve
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Imagem da Receita
            AsyncImage(
                model = receita.fotoUrl, // URL da imagem da receita
                contentDescription = null,
                contentScale = ContentScale.Crop, // Corta para preencher
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp) // Altura fixa para a imagem
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)) // Apenas cantos superiores arredondados
            )

            Column(
                modifier = Modifier.padding(8.dp) // Padding interno para o texto
            ) {
                // Título da Receita
                Text(
                    text = receita.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black, // Cor do texto
                    maxLines = 1, // Limita a uma linha
                    overflow = TextOverflow.Ellipsis // Adiciona "..." se o texto for muito longo
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Tempo de Preparo com Ícone
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_timer), // Ícone de relógio, você precisará adicionar este drawable
                        contentDescription = stringResource(R.string.tempo_preparo),
                        tint = Color.Gray, // Cor do ícone
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${receita.tempo_preparo}", // Adapte se precisar formatar
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}


// Removido o ReceitaCard antigo que era para LazyColumn
// Se você ainda precisar de uma versão de lista, pode readaptar o ReceitaCardGrid
// ou criar um novo ReceitaCard_List, mas para o Figma, o grid é o principal.


@Preview(showSystemUi = true)
@Composable
fun TelaHomePreview() {
    TelaHome(navController = null)
}