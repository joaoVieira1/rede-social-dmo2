package ifsp.rede.social.dmo2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ifsp.rede.social.dmo2.R
import ifsp.rede.social.dmo2.model.Post

class PostAdapter(private val posts: Array<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val imgPost : ImageView = view.findViewById(R.id.image_view_card)
        val username: TextView = view.findViewById(R.id.text_nome_usuario)
        val descricaoPost : TextView = view.findViewById(R.id.text_descricao)
        val localizacao: TextView = view.findViewById(R.id.text_localizacao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imgPost.setImageBitmap(posts[position].getFoto())
        holder.descricaoPost.text = posts[position].getDescricao()
        holder.localizacao.text = posts[position].getLocalizacao()
        holder.username.text = posts[position].getUsername()
    }


}