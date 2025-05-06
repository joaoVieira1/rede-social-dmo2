package ifsp.rede.social.dmo2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import ifsp.rede.social.dmo2.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configListeners()
        saveLogin()

    }

    private fun configListeners(){
        binding.buttonLogin.setOnClickListener {
            login()
        }

        binding.buttonRegister.setOnClickListener {
            getSignUpActivity()
        }

    }

    private fun login(){
        var email = binding.editTextEmail.text.toString()
        var senha = binding.editTextSenha.text.toString()

        if(email.isNotEmpty() || senha.isNotEmpty()){
            firebaseSignUser(email, senha)
        }else{
            Toast.makeText(this, R.string.error_login, Toast.LENGTH_SHORT).show()
        }


    }

    private fun saveLogin(){
        if(firebaseAuth.currentUser != null){
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

    }

    private fun getSignUpActivity(){
        startActivity(Intent(this, SignUpAcitivity::class.java))
        finish()

    }

    private fun firebaseSignUser(email: String, password: String){
        firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this, getString(R.string.error_login), Toast.LENGTH_SHORT).show()
                }
            }

    }

}