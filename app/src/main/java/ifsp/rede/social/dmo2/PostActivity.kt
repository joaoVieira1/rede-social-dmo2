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
import ifsp.rede.social.dmo2.databinding.ActivityPostBinding
import ifsp.rede.social.dmo2.databinding.ActivityProfileBinding
import ifsp.rede.social.dmo2.utils.Base64Converter

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    val firebaseAuth = FirebaseAuth.getInstance()
    val galeria = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()){
            uri ->
                if(uri != null){
                    binding.imagePost.setImageURI(uri)
                }else{
                    Toast.makeText(this, getString(R.string.nobody_photo), Toast.LENGTH_SHORT).show()
                }
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configListeners()
    }

    private fun configListeners(){
        binding.buttonChangeImagePost.setOnClickListener {
            galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.buttonSavePost.setOnClickListener {
            savePost()
        }
    }

    private fun savePost(){
        if(firebaseAuth.currentUser != null){

            val email = firebaseAuth.currentUser!!.email.toString()
            val imagePost = Base64Converter.drawableToString(binding.imagePost.drawable)
            val descricaoPost = binding.editTextPost.text.toString()

            val db = Firebase.firestore

            val dados = hashMapOf(
                "email" to email,
                "imageString" to imagePost,
                "descricao" to descricaoPost,
                "timeStamp" to System.currentTimeMillis()
            )

            db.collection("posts")
                .add(dados)
                .addOnCompleteListener {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
        }

    }


}