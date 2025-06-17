package br.senai.sp.jandira.gestaodereceitas.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape // Importar para o avatar circular
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.senai.sp.jandira.gestaodereceitas.R
import br.senai.sp.jandira.gestaodereceitas.model.Receita
import br.senai.sp.jandira.gestaodereceitas.model.RespostaHome
import br.senai.sp.jandira.gestaodereceitas.service.RetrofitFactory
import br.senai.sp.jandira.gestaodereceitas.service.SharedPreferencesUtils
import coil.compose.AsyncImage
import com.google.firebase.storage.FirebaseStorage // Importar Firebase Storage
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPerfil(navController: NavController?) {

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var receitasPerfil by remember { mutableStateOf<List<Receita>>(emptyList()) }
    val userId = SharedPreferencesUtils.getUserId(context)

    val userName = remember { mutableStateOf("") }
    val userEmail = remember { mutableStateOf("") }

    // Estado para a URL da foto de perfil
    // Inicialize com a URL salva ou null (que será o avatar padrão)
    var profileImageUrl by remember { mutableStateOf<String?>(null) }

    // Estado para controlar a exibição do diálogo de opções de imagem
    var showImageOptionsDialog by remember { mutableStateOf(false) }

    // Lançador para selecionar imagem da galeria
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // Imagem selecionada. Agora faça o upload para o Firebase Storage
            uploadProfileImageToFirebase(
                context = context,
                imageUri = uri,
                userId = userId,
                onSuccess = { newImageUrl ->
                    profileImageUrl = newImageUrl // Atualiza o estado para exibir a nova imagem
                    scope.launch {
                        snackbarHostState.showSnackbar("Foto de perfil atualizada com sucesso!")
                    }
                },
                onFailure = { errorMessage ->
                    scope.launch {
                        snackbarHostState.showSnackbar("Erro ao atualizar foto: $errorMessage")
                    }
                    Log.e("TelaPerfil", "Erro ao fazer upload da imagem: $errorMessage")
                }
            )
        }
    }

    // Carregar dados do usuário e a URL da foto de perfil quando a tela é composta
    LaunchedEffect(Unit) {
        userName.value = SharedPreferencesUtils.getUserName(context)
        userEmail.value = SharedPreferencesUtils.getUserEmail(context)
        profileImageUrl = SharedPreferencesUtils.getProfileImageUrl(context) // Carrega a URL salva
        Log.d("TelaPerfil", "Nome do usuário carregado: ${userName.value}")
        Log.d("TelaPerfil", "Email do usuário carregado: ${userEmail.value}")
        Log.d("TelaPerfil", "URL da foto de perfil carregada: ${profileImageUrl}")
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

                    // Avatar e informações do usuário
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // IMAGEM DE PERFIL CLICÁVEL
                        Box(
                            modifier = Modifier
                                .size(75.dp)
                                .clip(CircleShape) // Avatar circular
                                .background(Color.LightGray) // Cor de fundo para o placeholder
                                .clickable { showImageOptionsDialog = true } // Abre o diálogo ao clicar
                        ) {
                            if (profileImageUrl != null && profileImageUrl!!.isNotEmpty()) {
                                AsyncImage(
                                    model = profileImageUrl,
                                    contentDescription = stringResource(R.string.avatar_description),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.avatar), // Avatar padrão
                                    contentDescription = stringResource(R.string.avatar_description),
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = userName.value,
                                color = Color.Black,
                                fontSize = 25.sp
                            )
                            Text(
                                text = userEmail.value,
                                color = Color.Black,
                                fontSize = 20.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(30.dp))

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

                Text(
                    text = stringResource(R.string.minhas_receitas),
                    fontSize = 20.sp,
                    color = Color(0xFF325862),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(receitasPerfil) { receita ->
                        ReceitaItem(
                            receita = receita,
                            onClick = { clickedReceita ->
                                val receitaId = clickedReceita.id_receita ?: 0
                                navController?.navigate("detalhes_receita/${receitaId}")
                            }
                        )
                    }
                }
            }
        }
    }

    // DIÁLOGO DE OPÇÕES DA IMAGEM DE PERFIL
    if (showImageOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showImageOptionsDialog = false },
            title = { Text("Foto de Perfil") },
            text = { Text("O que você gostaria de fazer com sua foto de perfil?") },
            confirmButton = {
                TextButton(onClick = {
                    imagePickerLauncher.launch("image/*") // Abre a galeria
                    showImageOptionsDialog = false
                }) {
                    Text("Trocar Foto")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    // Lógica para remover a foto de perfil:
                    profileImageUrl = null // Define o estado para null para exibir o avatar padrão
                    SharedPreferencesUtils.saveProfileImageUrl(context, null) // Limpa a URL no SharedPreferences
                    // Opcional: Chamar função para remover a imagem do Firebase Storage
                    deleteProfileImageFromFirebase(userId) // Chamei a função aqui
                    scope.launch {
                        snackbarHostState.showSnackbar("Foto de perfil removida.")
                    }
                    showImageOptionsDialog = false
                }) {
                    Text("Remover Foto")
                }
            }
        )
    }
}

// --- Funções de Upload/Remoção de Imagem (Fora do Composable) ---

// Função para fazer upload da imagem para o Firebase Storage
fun uploadProfileImageToFirebase(
    context: android.content.Context,
    imageUri: Uri,
    userId: Int,
    onSuccess: (String) -> Unit,
    onFailure: (String) -> Unit
) {
    if (userId == 0) {
        onFailure("ID do usuário inválido. Não é possível fazer upload da imagem.")
        return
    }

    val storageRef = FirebaseStorage.getInstance().reference
    // >>> IMPORTANTE: AQUI ESTÁ O CAMINHO COM A SUA NOVA PASTA 'fotoPerfil_mobile' <<<
    val profileImageRef = storageRef.child("fotoPerfil_mobile/${userId}.jpg") // Caminho único por usuário

    profileImageRef.putFile(imageUri)
        .addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()
                Log.d("FirebaseStorage", "Upload de perfil bem-sucedido. URL: $downloadUrl")
                // Salvar a URL no SharedPreferences
                SharedPreferencesUtils.saveProfileImageUrl(context, downloadUrl)
                onSuccess(downloadUrl)
            }
        }
        .addOnFailureListener { exception ->
            Log.e("FirebaseStorage", "Falha no upload da imagem de perfil: ${exception.message}", exception)
            onFailure(exception.message ?: "Erro desconhecido ao fazer upload.")
        }
}

// Função para remover a imagem do Firebase Storage
fun deleteProfileImageFromFirebase(userId: Int) {
    if (userId == 0) {
        Log.e("FirebaseStorage", "ID do usuário inválido para exclusão da imagem.")
        return
    }
    val storageRef = FirebaseStorage.getInstance().reference
    val profileImageRef = storageRef.child("fotoPerfil_mobile/${userId}.jpg") // Caminho da imagem a ser removida

    profileImageRef.delete()
        .addOnSuccessListener {
            Log.d("FirebaseStorage", "Foto de perfil do usuário $userId removida do Storage com sucesso.")
            // Você já está limpando a URL do SharedPreferences na TelaPerfil,
            // então não precisa fazer isso aqui novamente.
        }
        .addOnFailureListener { exception ->
            Log.e("FirebaseStorage", "Falha ao remover foto de perfil do usuário $userId: ${exception.message}", exception)
        }
}


@Composable
fun ReceitaItem(receita: Receita, onClick: (Receita) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick(receita) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = receita.fotoUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            )

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = receita.titulo,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF325862),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_timer),
                        contentDescription = stringResource(R.string.tempo_preparo),
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Tempo: ${receita.tempo_preparo}",
                        fontSize = 14.sp,
                    color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                    painter = painterResource(id = R.drawable.ic_timer), // Mantido como ic\_timer
                    contentDescription = stringResource(R.string.dificuldade_dois_pontos), // Garantir que essa string existe
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                    text = "</span>{receita.dificuldade}",
                    fontSize = 14.sp,
                    color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ingredientes: ${receita.ingrediente}",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TelaPerfilPreview() {
    TelaPerfil(navController = null)
}