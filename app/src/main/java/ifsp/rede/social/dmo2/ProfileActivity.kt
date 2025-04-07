package ifsp.rede.social.dmo2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import ifsp.rede.social.dmo2.databinding.ActivityProfileBinding
import ifsp.rede.social.dmo2.utils.Base64Converter

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProfileBinding
    val firebaseAuth = FirebaseAuth.getInstance()

    val galeria = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()) {
            uri ->
        if (uri != null) {
            binding.image.setImageURI(uri)
        } else {
            Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configListeners()

    }

    private fun configListeners(){
        binding.buttonChangePhoto.setOnClickListener {
            galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.buttonSavePhoto.setOnClickListener {
            persistenceDataBase()
        }

    }

    private fun persistenceDataBase(){
        if(firebaseAuth.currentUser != null){
            val email = firebaseAuth.currentUser!!.email.toString()
            val username = binding.editTextNameUser.text.toString()
            val completeName = binding.editTextCompleteName.text.toString()
            val photo = Base64Converter.drawableToString(binding.image.drawable)

            val db = Firebase.firestore

            val dados = hashMapOf(
                "username" to username,
                "nomeCompleto" to completeName,
                "fotoPerfil" to photo
            )

            db.collection("usuarios").document(email)
                .set(dados)
                .addOnCompleteListener {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
        }



    }


}