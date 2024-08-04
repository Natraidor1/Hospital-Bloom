package RecylerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import francisconataren.cesarlandaverde.hospitalbloomejerc.R


class ViewHolder (view:View):RecyclerView.ViewHolder(view){

    val txtNombre:TextView = view.findViewById(R.id.lblNombre)
    val imgBorrar:ImageView = view.findViewById(R.id.imgBorrar)
    val imgEditar:ImageView = view.findViewById(R.id.imgEdit)
    val lblEnfermedad: TextView = view.findViewById(R.id.txtEnfermedadcard)



}