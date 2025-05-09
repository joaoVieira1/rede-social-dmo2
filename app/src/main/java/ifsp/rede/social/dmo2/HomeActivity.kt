package ifsp.rede.social.dmo2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import ifsp.rede.social.dmo2.adapter.PostAdapter
import ifsp.rede.social.dmo2.databinding.ActivityHomeBinding
import ifsp.rede.social.dmo2.model.Post
import ifsp.rede.social.dmo2.utils.Base64Converter

class HomeActivity: AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    val firebaseAuth = FirebaseAuth.getInstance()

    private var ultimoDocumento: DocumentSnapshot? = null
    private val posts = ArrayList<Post>()
    private val LIMITE_PAGINACAO = 5

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

        var query = db.collection("posts")
            .orderBy("data", Query.Direction.DESCENDING)
            .limit(LIMITE_PAGINACAO.toLong())

        ultimoDocumento?.let {
            query = query.startAfter(it)
        }

        query.get().addOnCompleteListener {
            task ->
                if(task.isSuccessful){
                    val result = task.result

                    if(result == null || result.isEmpty){
                        Toast.makeText(this, "Não há mais posts", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }

                    ultimoDocumento = result.documents.last()

                    for(document in result.documents){
                        val imageString = document.data!!["imageString"].toString()
                        val imageBitMap = Base64Converter.stringToBitmap(imageString)
                        val descricao = document.data!!["descricao"].toString()
                        val localizacao = document.data!!["cidade"].toString()
                        val username = document.data!!["username"].toString()
                        posts.add(Post(descricao, imageBitMap, localizacao, username))
                    }

                    if(binding.recycleView.adapter == null){
                        binding.recycleView.layoutManager = LinearLayoutManager(this)
                        binding.recycleView.adapter = PostAdapter(posts.toTypedArray())
                    }else{
                        binding.recycleView.adapter = PostAdapter(posts.toTypedArray())
                    }
                }
        }

        posts.clear()
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