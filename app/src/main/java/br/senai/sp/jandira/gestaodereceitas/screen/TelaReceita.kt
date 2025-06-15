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
import br.senai.sp.jandira.gestaodereceitas.model.ClassificacaoReceita
import br.senai.sp.jandira.gestaodereceitas.R
import br.senai.sp.jandira.gestaodereceitas.firebase.FirebaseStorageService
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
    val categoriaIdSelecionada = remember { mutableIntStateOf(0) }
    val expanded = remember { mutableStateOf(false) }
    val dificuldadeExpandid = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isLoading = remember { mutableStateOf(false) }

    val classificacoesDisponiveis = listOf(
        ClassificacaoReceita(21, "DOCE"),
        ClassificacaoReceita(22, "SALGADO"),
        ClassificacaoReceita(23, "CARNE"),
        ClassificacaoReceita(24, "AVE"),
        ClassificacaoReceita(25, "PEIXE"),
        ClassificacaoReceita(26, "SEM GLÚTEN"),
        ClassificacaoReceita(27, "SEM LACTOSE")
    )
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
                // REMOVIDO: .align(Alignment.Center)
                modifier = Modifier
                    .padding(16.dp), // Apenas padding, o alinhamento é tratado pelo Scaffold ou Box pai se houver
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
                            focusedContainerColor = Color(0xFF325862),
                            unfocusedContainerColor = Color(0xFF325862),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        classificacoesDisponiveis.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item.nome, color = Color.White) },
                                onClick = {
                                    categoriaNomeSelecionada.value = item.nome
                                    categoriaIdSelecionada.intValue = item.id_classificacao
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
                            focusedContainerColor = Color(0xFF325862),
                            unfocusedContainerColor = Color(0xFF325862),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = dificuldadeExpandid.value,
                        onDismissRequest = { dificuldadeExpandid.value = false }
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
                                || categoriaIdSelecionada.intValue == 0
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
                                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                                    val formattedDate = currentDateTime.format(formatter)

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
                                        classificacao = listOf(categoriaIdSelecionada.intValue),
                                        classificacao_nome = null,
                                        usuario = null,
                                        classificacoes_detalhe = null
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
                                                    snackbarHostState.showSnackbar("Receita publicada com sucesso no seu backend!")
                                                }
                                                Log.i("TelaReceita", "Receita publicada com sucesso: ${response.body()}")

                                                titulo.value = ""
                                                ingrediente.value = ""
                                                modo_preparo.value = ""
                                                dificuldade.value = ""
                                                tempo_preparo.value = ""
                                                categoriaIdSelecionada.intValue = 0
                                                categoriaNomeSelecionada.value = ""
                                                imageUri.value = null

                                                navController?.navigate("home") {
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
                        enabled = !isLoading.value
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