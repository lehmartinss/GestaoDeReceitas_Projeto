package br.senai.sp.jandira.gestaodereceitas.screens // Correção: 'screens' no plural

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.senai.sp.jandira.gestaodereceitas.R
import br.senai.sp.jandira.gestaodereceitas.model.Receita
import br.senai.sp.jandira.gestaodereceitas.model.RespostaReceita
import br.senai.sp.jandira.gestaodereceitas.service.RetrofitFactory
import coil.compose.rememberAsyncImagePainter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun TelaDetalhesReceita(navController: NavController?, idReceita: Int?) {
    val context = LocalContext.current
    var receita by remember { mutableStateOf<Receita?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(idReceita) {
        if (idReceita == null || idReceita == 0) {
            errorMessage = "ID da receita inválido."
            isLoading = false
            return@LaunchedEffect
        }

        val service = RetrofitFactory().getCadastroService()
        service.buscarReceitaPorId(idReceita).enqueue(object : Callback<RespostaReceita> {
            override fun onResponse(call: Call<RespostaReceita>, response: Response<RespostaReceita>) {
                isLoading = false
                if (response.isSuccessful) {
                    receita = response.body()?.receita // <<-- CORREÇÃO AQUI (se você alterou RespostaReceita)
                    if (receita == null) {
                        errorMessage = "Receita não encontrada."
                    }
                    Log.d("DetalhesReceita", "Receita carregada: ${receita?.titulo}")
                } else {
                    errorMessage = "Erro ao carregar detalhes da receita: ${response.code()} - ${response.errorBody()?.string()}"
                    Log.e("DetalhesReceita", errorMessage!!)
                    Toast.makeText(context, "Erro ao carregar detalhes.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RespostaReceita>, t: Throwable) {
                isLoading = false
                errorMessage = "Erro de conexão: ${t.localizedMessage}"
                Log.e("DetalhesReceita", errorMessage!!, t)
                Toast.makeText(context, "Erro de conexão ao carregar detalhes.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F0D6))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Permite rolagem
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController?.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.voltar),
                        tint = Color(0xFF325862),
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = stringResource(R.string.logo_description),
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = stringResource(R.string.detalhes_receita),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp),
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF982829))
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = errorMessage!!, color = Color.Red, fontSize = 18.sp)
                }
            } else { // Este bloco será executado se isLoading for false e errorMessage for null
                receita?.let { currentReceita -> // <<-- Uso seguro de 'let' aqui
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        // Título da Receita
                        Text(
                            text = currentReceita.titulo,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF325862),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Imagem da Receita
                        currentReceita.fotoUrl.let { url ->
                            Image(
                                painter = rememberAsyncImagePainter(model = url),
                                contentDescription = stringResource(R.string.foto_da_receita),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Classificação
                        val classificacaoNome = currentReceita.classificacoes_detalhe?.firstOrNull()?.nome ?:
                        currentReceita.classificacao_nome ?: "N/A"
                        Text(
                            text = stringResource(R.string.classificacao_dois_pontos, classificacaoNome),
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Dificuldade
                        Text(
                            text = stringResource(R.string.dificuldade_dois_pontos, currentReceita.dificuldade),
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Tempo de Preparo
                        Text(
                            text = stringResource(R.string.tempo_preparo_dois_pontos, currentReceita.tempo_preparo),
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Ingredientes
                        Text(
                            text = stringResource(R.string.ingredientes_dois_pontos_completo),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF982829)
                        )
                        Text(
                            text = currentReceita.ingrediente,
                            fontSize = 16.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Modo de Preparo
                        Text(
                            text = stringResource(R.string.modo_preparo_dois_pontos_completo),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF982829)
                        )
                        Text(
                            text = currentReceita.modo_preparo,
                            fontSize = 16.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Publicado por
                        val nomeUsuario = currentReceita.usuario?.nome_usuario ?: "Desconhecido" // <<-- Agora currentReceita é garantido não ser nulo
                        Text(
                            text = stringResource(R.string.publicado_por, nomeUsuario),
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                } ?: run { // Este bloco 'else' do 'let' é executado se receita for null (após o carregamento e sem erro)
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = stringResource(R.string.nenhuma_receita_encontrada), fontSize = 18.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}