package br.senai.sp.jandira.gestaodereceitas.screens

import android.net.Uri // Necessário para Uri da imagem


import androidx.activity.compose.rememberLauncherForActivityResult // Usado para ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts // Usado para ActivityResultContracts

import androidx.compose.foundation.layout.Box // Necessário para Box
import androidx.compose.foundation.layout.Column // Necessário para Column
import androidx.compose.foundation.layout.Row // Necessário para Row
import androidx.compose.foundation.layout.fillMaxSize // Necessário para Modifier.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth // Necessário para Modifier.fillMaxWidth
import androidx.compose.foundation.layout.height // Necessário para Modifier.height
import androidx.compose.foundation.layout.padding // Necessário para Modifier.padding
import androidx.compose.foundation.layout.width // Necessário para Modifier.width
import androidx.compose.foundation.background // Necessário para Modifier.background
import androidx.compose.foundation.Image // Necessário para Image composable

import androidx.compose.foundation.shape.RoundedCornerShape // Para RoundedCornerShape

import androidx.compose.runtime.* // Para remember, mutableStateOf, LaunchedEffect, rememberCoroutineScope
import androidx.compose.ui.Modifier // Para Modifier
import androidx.compose.ui.Alignment // Para Alignment.Start, Alignment.BottomEnd, Alignment.Center
import androidx.compose.ui.graphics.Color // Para Color
import androidx.compose.ui.layout.ContentScale // Para ContentScale
import androidx.compose.ui.platform.LocalContext // Para LocalContext.current
import androidx.compose.ui.res.painterResource // Para painterResource
import androidx.compose.ui.text.style.TextAlign // Para TextAlign
import androidx.compose.ui.tooling.preview.Preview // Para @Preview
import androidx.compose.ui.unit.dp // Para .dp
import androidx.compose.ui.unit.sp // Para .sp
import androidx.compose.ui.draw.clip // Para Modifier.clip
import androidx.compose.foundation.layout.Arrangement // Para Arrangement.Start
import androidx.compose.foundation.layout.size

import androidx.compose.material3.Button // Necessário para Button
import androidx.compose.material3.ButtonDefaults // Necessário para ButtonDefaults
import androidx.compose.material3.DropdownMenuItem // Necessário para DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api // Necessário para @OptIn
import androidx.compose.material3.ExposedDropdownMenuBox // Necessário para ExposedDropdownMenu
import androidx.compose.material3.LocalTextStyle // Necessário para LocalTextStyle
import androidx.compose.material3.OutlinedTextField // Necessário para OutlinedTextField
import androidx.compose.material3.Snackbar // Necessário para Snackbar
import androidx.compose.material3.SnackbarHost // Necessário para SnackbarHost
import androidx.compose.material3.SnackbarHostState // Necessário para SnackbarHostState
import androidx.compose.material3.Text // Necessário para Text
import androidx.compose.material3.TextFieldDefaults // Necessário para TextFieldDefaults

import kotlinx.coroutines.launch // Para scope.launch

import coil.compose.rememberAsyncImagePainter // Para carregar imagens de Uri

import androidx.navigation.NavController // Para NavController

import br.senai.sp.jandira.gestaodereceitas.model.ClassificacaoReceita // Importa a data class do seu pacote de modelo

import br.senai.sp.jandira.gestaodereceitas.R // Para acessar recursos como R.drawable.logo, R.string.publicar_receita


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
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Simulação das classificações disponíveis (id e nome)
    val classificacoesDisponiveis = listOf(
        ClassificacaoReceita(4, "SALGADO"),
        ClassificacaoReceita(5, "DOCE"),
        ClassificacaoReceita(6, "CARNE"),
        ClassificacaoReceita(7, "AVE"),
        ClassificacaoReceita(8, "PEIXE"),
        ClassificacaoReceita(9, "SEM GLÚTEN"),
        ClassificacaoReceita(10, "SEM LACTOSE")
    )

    val context = LocalContext.current
    val imageUri = remember { mutableStateOf<Uri?>(null) } // Apenas pra exibir, não faz nada pra API

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri.value = uri
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F0D6)) // Cor de fundo da tela
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                modifier = Modifier.padding(top = 18.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    // Usando um recurso de logo do seu projeto, ajuste R.drawable.logo se for diferente
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo", // Use stringResource(R.string.logo_description) se definido
                    modifier = Modifier.size(75.dp)
                )
                Text(
                    text = "Publicar Receita", // Use stringResource(R.string.publicar_receita)
                    fontSize = 24.sp,
                    modifier = Modifier.padding(top = 20.dp, start = 18.dp),
                    color = Color.Black
                )
            }
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(text = "Foto", fontSize = 18.sp, modifier = Modifier.padding(top = 6.dp))

                if (imageUri.value == null) {
                    Button(
                        onClick = { launcher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF325862)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(text = "Selecionar foto", color = Color.White) // Use stringResource(R.string.selecionar_foto)
                    }
                }

                imageUri.value?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(model = uri),
                        contentDescription = "Foto da receita",
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .height(95.dp)
                            .width(180.dp)
                            .clip(RoundedCornerShape(25.dp)),
                        contentScale = ContentScale.Fit
                    )
                }

                Text(text = "Nome da Receita", fontSize = 18.sp, modifier = Modifier.padding(top = 5.dp)) // Use stringResource(R.string.nome_receita)

                OutlinedTextField(
                    value = titulo.value,
                    onValueChange = { titulo.value = it },
                    modifier = Modifier
                        .width(300.dp)
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

                Text(text = "Ingredientes", fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp)) // Use stringResource(R.string.ingredientes)

                OutlinedTextField(
                    value = ingrediente.value,
                    onValueChange = { ingrediente.value = it },
                    modifier = Modifier
                        .width(300.dp)
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

                Text(text = "Modo de Preparo", fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp)) // Use stringResource(R.string.modo_preparo)

                OutlinedTextField(
                    value = modo_preparo.value,
                    onValueChange = { modo_preparo.value = it },
                    modifier = Modifier
                        .width(300.dp)
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

                Text(text = "Categoria", fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp)) // Use stringResource(R.string.categoria)

                ExposedDropdownMenuBox(
                    expanded = expanded.value,
                    onExpandedChange = { expanded.value = !expanded.value },
                    modifier = Modifier
                        .width(200.dp)
                        .padding(top = 5.dp)
                ) {
                    OutlinedTextField(
                        value = categoriaNomeSelecionada.value,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text(text = "Selecione", color = Color.White, fontSize = 14.sp) }, // Use stringResource(R.string.selecione)
                        modifier = Modifier
                            .menuAnchor()
                            .height(55.dp),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
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

                Text(text = "Dificuldade", fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp)) // Use stringResource(R.string.nivel_dificuldade)

                OutlinedTextField(
                    value = dificuldade.value,
                    onValueChange = { dificuldade.value = it },
                    modifier = Modifier
                        .width(200.dp)
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

                Text(text = "Tempo de Preparo", fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp)) // Use stringResource(R.string.tempo_preparo)

                OutlinedTextField(
                    value = tempo_preparo.value,
                    onValueChange = { tempo_preparo.value = it },
                    modifier = Modifier
                        .width(275.dp)
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

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 16.dp, bottom = 4.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Button(
                        onClick = {
                            if (titulo.value.isBlank()
                                || ingrediente.value.isBlank()
                                || modo_preparo.value.isBlank()
                                || dificuldade.value.isBlank()
                                || tempo_preparo.value.isBlank()
                                || categoriaIdSelecionada.intValue == 0
                                || imageUri.value == null
                            ) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Preencha todos os campos.")
                                }
                            } else {
                                // Apenas exibe um snackbar de "sucesso" como se tivesse publicado
                                scope.launch {
                                    snackbarHostState.showSnackbar("Receita publicada com sucesso.")
                                    // Limpando o formulário
                                    titulo.value = ""
                                    ingrediente.value = ""
                                    modo_preparo.value = ""
                                    dificuldade.value = ""
                                    tempo_preparo.value = ""
                                    categoriaIdSelecionada.intValue = 0
                                    categoriaNomeSelecionada.value = ""
                                    imageUri.value = null
                                    // Se desejar, também pode usar:
                                    // navController?.navigate("home")
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF982829)),
                        modifier = Modifier
                            .padding(top = 20.dp, bottom = 4.dp)
                            .width(130.dp)
                    ) {
                        Text(text = "Publicar", color = Color.White) // Use stringResource(R.string.publicar_receita_botao)
                    }
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
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
}

@Preview(showBackground = true)
@Composable
fun PreviewTelaReceita(){
    TelaReceita(navController = null)
}