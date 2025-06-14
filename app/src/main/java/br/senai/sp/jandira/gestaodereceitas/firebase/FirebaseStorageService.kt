package br.senai.sp.jandira.gestaodereceitas.firebase

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*

object FirebaseStorageService {

    fun uploadImageToFirebase(
        uri: Uri,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("imagens/${UUID.randomUUID()}.jpg")

        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                onSuccess(downloadUri.toString())
            }.addOnFailureListener { onError(it) }
        }.addOnFailureListener { onError(it) }
    }
}
