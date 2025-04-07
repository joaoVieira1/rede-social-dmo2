package ifsp.rede.social.dmo2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import ifsp.rede.social.dmo2.databinding.ActivitySignupBinding

class SignUpAcitivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    val fireBase = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configListeners()

    }

    private fun configListeners(){
        binding.buttonSignUp.setOnClickListener {
            createUser()
        }

    }

    private fun createUser(){
        var email = binding.editTextEmail.text.toString()
        var password = binding.editTextSenha.text.toString()
        var confirmPassword = binding.editTextSenhaAgain.text.toString()
        if(email.isNotEmpty() and password.isNotEmpty()){
            if(password == confirmPassword){
                firebaseCreateUser(email, password)
            }else{
                Toast.makeText(this, getString(R.string.error_register_passwords), Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, getString(R.string.error_register_edittexts), Toast.LENGTH_SHORT).show()
        }

    }

    private fun firebaseCreateUser(email: String, password: String){
        fireBase
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }

    }




}
