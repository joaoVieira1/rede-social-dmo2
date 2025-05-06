package ifsp.rede.social.dmo2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import ifsp.rede.social.dmo2.adapter.PostAdapter
import ifsp.rede.social.dmo2.databinding.ActivityHomeBinding
import ifsp.rede.social.dmo2.model.Post
import ifsp.rede.social.dmo2.utils.Base64Converter

class HomeActivity: AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configListeners()
        setData()
    }

    private fun configListeners(){
        binding.buttonLogoff.setOnClickListener {
            logoff()
        }

        binding.buttonProfile.setOnClickListener {
            getProfileActivity()
        }

        binding.buttonLoadFeed.setOnClickListener {
            loadFeed()
        }

        binding.buttonPost.setOnClickListener {
            getPostActivity()
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

    private fun getPostActivity(){
        startActivity(Intent(this, PostActivity::class.java))
        finish()
    }

    private fun loadFeed(){
        val db = Firebase.firestore

        db.collection("posts").get()
            .addOnCompleteListener {
                task ->
                    if(task.isSuccessful){
                        val document = task.result
                        val posts = ArrayList<Post>()

                        for(document in document.documents){
                            val imageString = document.data!!["imageString"].toString()
                            val imageBitMap = Base64Converter.stringToBitmap(imageString)
                            val descricao = document.data!!["descricao"].toString()
                            val localizacao = document.data!!["cidade"].toString()
                            val username = document.data!!["username"].toString()
                            posts.add(Post(descricao, imageBitMap, localizacao, username))
                        }

                        val adapter = PostAdapter(posts.toTypedArray())
                        binding.recycleView.layoutManager = LinearLayoutManager(this)
                        binding.recycleView.adapter = adapter
                    }
            }

    }

    private fun setData(){
        val db = Firebase.firestore
        val email = firebaseAuth.currentUser!!.email.toString()

        db.collection("usuarios").document(email).get()
            .addOnCompleteListener {
                task ->
                        if(task.isSuccessful){
                            val document = task.result
                            val imageString = document.data!!["fotoPerfil"].toString()
                            val imageBitMap = Base64Converter.stringToBitmap(imageString)
                            binding.imageUser.setImageBitmap(imageBitMap)
                            binding.username.text = document.data!!["username"].toString()
                            binding.nameComplete.text = document.data!!["nomeCompleto"].toString()
                        }
            }
    }

}