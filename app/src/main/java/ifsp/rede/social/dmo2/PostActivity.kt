package ifsp.rede.social.dmo2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import ifsp.rede.social.dmo2.databinding.ActivityPostBinding
import ifsp.rede.social.dmo2.utils.Base64Converter
import ifsp.rede.social.dmo2.utils.LocalizacaoHelper

class PostActivity : AppCompatActivity(), LocalizacaoHelper.Callback{

    private lateinit var binding: ActivityPostBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    private var imagePost: String = ""
    private var descricaoPost: String = ""
    private var username: String = ""
    private var email: String = ""

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
            prepararDados()
        }
    }

    private fun prepararDados(){
        if(firebaseAuth.currentUser != null){
            email = firebaseAuth.currentUser!!.email.toString()
            imagePost = Base64Converter.drawableToString(binding.imagePost.drawable)
            descricaoPost = binding.editTextPost.text.toString()

            buscarUsernameDoUsuario()
        }
    }

    private fun buscarUsernameDoUsuario() {
        firestore.collection("usuarios")
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    username = document.getString("username").toString()
                    obterLocalizacaoDoUsuario()
                }
            }
    }

    private fun obterLocalizacaoDoUsuario(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }else{
            val helper = LocalizacaoHelper(applicationContext)
            helper.obterLocalizacao(this)
        }

    }


    override fun onLocalizacaoRecebida(endereco: Address, latitude: Double, longitude: Double) {
        salvarPost(endereco, latitude, longitude)
    }

    override fun onErro(mensagem: String) {
        Toast.makeText(this, "Erro na localização: $mensagem", Toast.LENGTH_SHORT).show()
    }

    private fun salvarPost(endereco: Address, latitude: Double, longitude: Double){
        val dados = hashMapOf(
            "username" to username,
            "email" to email,
            "descricao" to descricaoPost,
            "imageString" to imagePost,
            "data" to Timestamp.now(),
            "cidade" to endereco.subAdminArea
        )

        firestore.collection("posts")
            .add(dados)
            .addOnCompleteListener {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
    }

}