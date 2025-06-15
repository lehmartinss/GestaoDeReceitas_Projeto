package br.senai.sp.jandira.gestaodereceitas.service

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

fun uploadImageToFirebase(
    uri: Uri,
    onSuccess: (String) -> Unit,
    onError: (Exception) -> Unit
) {
    // Obter instância do Firebase Storage
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference

    // Criar referência única para a imagem com UUID
    val imageRef = storageRef.child("imagens/${UUID.randomUUID()}.jpg")

    // Iniciar upload do arquivo no Firebase Storage
    val uploadTask = imageRef.putFile(uri)

    // Listener sucesso upload
    uploadTask.addOnSuccessListener {
        // Obter URL para download da imagem
        imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
            onSuccess(downloadUri.toString()) // Retorna a URL para o callback de sucesso
        }.addOnFailureListener { exception ->
            onError(exception) // Falha ao obter URL de download
        }
    }.addOnFailureListener { exception ->
        onError(exception) // Falha no upload
    }
}


