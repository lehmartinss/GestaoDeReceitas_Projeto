package br.senai.sp.jandira.gestaodereceitas.screens

import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.navigation.NavController
import br.senai.sp.jandira.gestaodereceitas.model.ClassificacaoReceita // Importar o modelo correto
import br.senai.sp.jandira.gestaodereceitas.R
import br.senai.sp.jandira.gestaodereceitas.firebase.FirebaseStorageService
import br.senai.sp.jandira.gestaodereceitas.model.ClassificacaoReceitaEnvio
import br.senai.sp.jandira.gestaodereceitas.model.Receita
import br.senai.sp.jandira.gestaodereceitas.model.RespostaReceita
import br.senai.sp.jandira.gestaodereceitas.service.RetrofitFactory
import br.senai.sp.jandira.gestaodereceitas.service.SharedPreferencesUtils
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaReceita(navController: NavController?) {

    val titulo = remember { mutableStateOf("") }
    val ingrediente = remember { mutableStateOf("") }
    val modo_preparo = remember { mutableStateOf("") }
    val dificuldade = remember { mutableStateOf("") }
    val tempo_preparo = remember { mutableStateOf("") }
    val categoriaNomeSelecionada = remember { mutableStateOf("") }
    val categoriaIdSelecionada = remember { mutableIntStateOf(0) } // Usado para armazenar o ID INT da categoria
    val expanded = remember { mutableStateOf(false) }
    val dificuldadeExpandid = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isLoading = remember { mutableStateOf(false) }

    // Listagem de classificações (categorias) para o dropdown
    // Usando ClassificacaoReceita que tem 'id' e 'nome' para o menu
    val classificacoesDisponiveis = remember {
        mutableStateListOf(
            ClassificacaoReceita(id = 21, nome = "DOCE"),
            ClassificacaoReceita(id = 22, nome = "SALGADO"),
            ClassificacaoReceita(id = 23, nome = "CARNE"),
            ClassificacaoReceita(id = 24, nome = "AVE"),
            ClassificacaoReceita(id = 25, nome = "PEIXE"),
            ClassificacaoReceita(id = 26, nome = "SEM GLÚTEN"),
            ClassificacaoReceita(id = 27, nome = "SEM LACTOSE")
        )
    }

    // Você também pode carregar as classificações do backend aqui,
    // como você fez na TelaHome, para garantir que estejam atualizadas.
    // Exemplo:
    /*
    val context = LocalContext.current // Já definido abaixo
    LaunchedEffect(Unit) {
        RetrofitFactory().getCadastroService().listarTodasClassificacoes().enqueue(object : Callback<RespostaClassificacao> {
            override fun onResponse(call: Call<RespostaClassificacao>, response: Response<RespostaClassificacao>) {
                if (response.isSuccessful) {
                    response.body()?.classificacoes?.let {
                        classificacoesDisponiveis.clear()
                        classificacoesDisponiveis.addAll(it)
                    }
                } else {
                    Log.e("TelaReceita", "Erro ao carregar classificações: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }
            override fun onFailure(call: Call<RespostaClassificacao>, t: Throwable) {
                Log.e("TelaReceita", "Erro de rede ao carregar classificações: ${t.message}")
            }
        })
    }
    */


    val dificuldades = listOf("Fácil", "Médio", "Difícil")

    val context = LocalContext.current
    val imageUri = remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri.value = uri
        } else {
            Toast.makeText(context, "Nenhuma imagem foi selecionada.", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(16.dp),
                snackbar = { data ->
                    Snackbar(
                        containerColor = Color(0xFFFFC56C),
                        contentColor = Color.Black,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .height(70.dp)
                    ) {
                        Text(text = data.visuals.message, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 18.sp)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F0D6))
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start,
            ) {
                Row(
                    modifier = Modifier.padding(top = 18.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(75.dp)
                    )
                    Text(
                        text = "Publicar Receita",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(top = 20.dp, start = 18.dp),
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Foto", fontSize = 18.sp, modifier = Modifier.padding(top = 6.dp))
                if (imageUri.value == null) {
                    Button(
                        onClick = { launcher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF325862)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(text = "Selecionar foto", color = Color.White)
                    }
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUri.value),
                        contentDescription = "Foto da receita",
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .height(95.dp)
                            .width(180.dp)
                            .clip(RoundedCornerShape(25.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Text(text = "Nome da Receita", fontSize = 18.sp, modifier = Modifier.padding(top = 5.dp))
                OutlinedTextField(
                    value = titulo.value,
                    onValueChange = { titulo.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF325862),
                        unfocusedContainerColor = Color(0xFF325862),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Text(text = "Ingredientes", fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp))
                OutlinedTextField(
                    value = ingrediente.value,
                    onValueChange = { ingrediente.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF325862),
                        unfocusedContainerColor = Color(0xFF325862),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Text(text = "Modo de Preparo", fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp))
                OutlinedTextField(
                    value = modo_preparo.value,
                    onValueChange = { modo_preparo.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF325862),
                        unfocusedContainerColor = Color(0xFF325862),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Text(text = "Categoria", fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded.value,
                    onExpandedChange = { expanded.value = !expanded.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                ) {
                    OutlinedTextField(
                        value = categoriaNomeSelecionada.value,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text(text = "Selecione", color = Color.White, fontSize = 14.sp) },
                        modifier = Modifier
                            .menuAnchor()
                            .height(55.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = TextStyle.Default.copy(fontSize = 14.sp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF325862), // Mantido como estava ou ajustado para sua cor da caixa
                            unfocusedContainerColor = Color(0xFF325862), // Mantido como estava ou ajustado para sua cor da caixa
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false },
                        modifier = Modifier.background(Color(0xFF982829)) // Cor de fundo do menu
                    ) {
                        classificacoesDisponiveis.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item.nome, color = Color.White) },
                                onClick = {
                                    categoriaNomeSelecionada.value = item.nome
                                    categoriaIdSelecionada.intValue = item.id
                                    expanded.value = false
                                }
                            )
                        }
                    }
                }

                Text(text = "Dificuldade", fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp))
                ExposedDropdownMenuBox(
                    expanded = dificuldadeExpandid.value,
                    onExpandedChange = { dificuldadeExpandid.value = !dificuldadeExpandid.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                ) {
                    OutlinedTextField(
                        value = dificuldade.value,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text(text = "Selecione", color = Color.White, fontSize = 14.sp) },
                        modifier = Modifier
                            .menuAnchor()
                            .height(55.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = TextStyle.Default.copy(fontSize = 14.sp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF325862), // Mantido como estava ou ajustado para sua cor da caixa
                            unfocusedContainerColor = Color(0xFF325862), // Mantido como estava ou ajustado para sua cor da caixa
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = dificuldadeExpandid.value,
                        onDismissRequest = { dificuldadeExpandid.value = false },
                        modifier = Modifier.background(Color(0xFF982829)) // Cor de fundo do menu
                    ) {
                        dificuldades.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item, color = Color.White) },
                                onClick = {
                                    dificuldade.value = item
                                    dificuldadeExpandid.value = false
                                }
                            )
                        }
                    }
                }

                Text(text = "Tempo de Preparo", fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp))
                OutlinedTextField(
                    value = tempo_preparo.value,
                    onValueChange = { tempo_preparo.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF325862),
                        unfocusedContainerColor = Color(0xFF325862),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Button(
                        onClick = {
                            if (isLoading.value) return@Button

                            if (titulo.value.isBlank()
                                || ingrediente.value.isBlank()
                                || modo_preparo.value.isBlank()
                                || dificuldade.value.isBlank()
                                || tempo_preparo.value.isBlank()
                                || categoriaIdSelecionada.intValue == 0 // Verifica se uma categoria foi selecionada
                                || imageUri.value == null
                            ) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Preencha todos os campos e selecione uma foto.")
                                }
                                return@Button
                            }

                            isLoading.value = true

                            FirebaseStorageService.uploadImageToFirebase(
                                uri = imageUri.value!!,
                                onSuccess = { urlDaImagem ->
                                    val userId = SharedPreferencesUtils.getUserId(context)

                                    if (userId == 0) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Erro: ID do usuário não encontrado. Faça login novamente.")
                                            isLoading.value = false
                                        }
                                        Log.e("TelaReceita", "ID do usuário é 0. Login inválido ou não realizado.")
                                        return@uploadImageToFirebase
                                    }

                                    val currentDateTime = LocalDateTime.now()
                                    // Formato de data/hora mais comum para APIs (ISO 8601)
                                    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                                    val formattedDate = currentDateTime.format(formatter)

                                    // **CORREÇÃO AQUI**
                                    // Criando o objeto ClassificacaoReceita com o idClassificacao
                                    val classificacaoParaEnvio = ClassificacaoReceita(
                                        idClassificacao = categoriaIdSelecionada.intValue
                                    )

                                    val receitaParaBackend = Receita(
                                        id_receita = null,
                                        titulo = titulo.value,
                                        tempo_preparo = tempo_preparo.value,
                                        fotoUrl = urlDaImagem,
                                        ingrediente = ingrediente.value,
                                        modo_preparo = modo_preparo.value,
                                        dificuldade = dificuldade.value,
                                        id_usuario = userId,
                                        data_publicacao = formattedDate,
                                        // Agora o tipo corresponde: List<ClassificacaoReceita>
                                        classificacao = listOf(classificacaoParaEnvio),
                                        classificacao_nome = null,
                                        usuario = null
                                    )

                                    val call = RetrofitFactory()
                                        .getCadastroService()
                                        .publicar(receitaParaBackend)

                                    call.enqueue(object : Callback<RespostaReceita> {
                                        override fun onResponse(
                                            call: Call<RespostaReceita>,
                                            response: Response<RespostaReceita>
                                        ) {
                                            isLoading.value = false
                                            if (response.isSuccessful) {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Receita publicada com sucesso!")
                                                }
                                                Log.i("TelaReceita", "Receita publicada com sucesso: ${response.body()}")

                                                // Limpar campos após sucesso
                                                titulo.value = ""
                                                ingrediente.value = ""
                                                modo_preparo.value = ""
                                                dificuldade.value = ""
                                                tempo_preparo.value = ""
                                                categoriaIdSelecionada.intValue = 0
                                                categoriaNomeSelecionada.value = ""
                                                imageUri.value = null

                                                navController?.navigate("home") {
                                                    // Limpa a back stack para que o usuário não volte para a tela de publicação
                                                    popUpTo("tela_receita") { inclusive = true }
                                                }
                                            } else {
                                                val errorBody = response.errorBody()?.string()
                                                Log.e("TelaReceita", "Erro ao publicar receita no backend: ${response.code()} - $errorBody")
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Erro ao publicar receita: ${response.code()}. Detalhes: $errorBody")
                                                }
                                            }
                                        }

                                        override fun onFailure(call: Call<RespostaReceita>, t: Throwable) {
                                            isLoading.value = false
                                            Log.e("TelaReceita", "Falha na requisição para o backend: ${t.message}", t)
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Erro de conexão com o backend. Verifique sua internet.")
                                            }
                                        }
                                    })
                                },
                                onError = { e ->
                                    isLoading.value = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Erro ao fazer o upload da imagem para o Firebase Storage.")
                                    }
                                    Log.e("TelaReceita", "Erro no upload da imagem: ${e.message}", e)
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF982829)),
                        modifier = Modifier
                            .padding(top = 20.dp, bottom = 4.dp)
                            .width(130.dp),
                        enabled = !isLoading.value // Desabilita o botão enquanto estiver carregando
                    ) {
                        if (isLoading.value) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(text = "Publicar", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewTelaReceita(){
    TelaReceita(navController = null)
}