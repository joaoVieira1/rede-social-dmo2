package ifsp.rede.social.dmo2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import ifsp.rede.social.dmo2.databinding.ActivityHomeBinding
import ifsp.rede.social.dmo2.utils.Base64Converter

class HomeActivity: AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configListeners()
        setDataBase()

    }

    private fun configListeners(){
        binding.buttonLogoff.setOnClickListener {
            logoff()
        }

        binding.buttonProfile.setOnClickListener {
            getProfileActivity()
        }

    }

    private fun logoff(){
        firebaseAuth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun getProfileActivity(){
        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }

    private fun setDataBase(){
        val db = Firebase.firestore
        val email = firebaseAuth.currentUser!!.email.toString()

        db.collection("usuarios").document(email).get()
            .addOnCompleteListener {
                task ->
                    if(task.isSuccessful){
                        val document = task.result
                        val imageString = document.data!!["fotoPerfil"].toString()
                        val bitmap = Base64Converter.stringToBitmap(imageString)
                        binding.imageLogo.setImageBitmap(bitmap)
                        binding.username.text = document.data!!["username"].toString()
                        binding.nameComplete.text = document.data!!["nomeCompleto"].toString()
                    }
            }

    }

}