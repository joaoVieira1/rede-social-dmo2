package ifsp.rede.social.dmo2.model

import android.graphics.Bitmap

class Post(private val descricao: String, private val foto: Bitmap){

    public fun getDescricao(): String{
        return descricao
    }

    public fun getFoto(): Bitmap{
        return foto
    }

}