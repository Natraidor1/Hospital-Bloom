package francisconataren.cesarlandaverde.hospitalbloomejerc

import Modelo.ClaseConexion
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Enfermedades.newInstance] factory method to
 * create an instance of this fragment.
 */
class Enfermedades : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_enfermedades, container, false)

        val enfermedad = root.findViewById<EditText>(R.id.txtEnfermedad)

        val registrarEnfermedad = root.findViewById<Button>(R.id.btnRegistrarEnf)

        val imgRegresar = root.findViewById<ImageView>(R.id.imgRegresar)

        imgRegresar.setOnClickListener {
            findNavController().navigate(R.id.irHabitaciones)
        }

        try {
        registrarEnfermedad.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val objConexion = ClaseConexion().CadenaConexion()

                val ingresarNuevaEnfermedad = objConexion?.prepareStatement("insert into ENFERMEDADESS (Enfermedad) values (?)")!!
                ingresarNuevaEnfermedad.setString(1, enfermedad.text.toString())
                ingresarNuevaEnfermedad.executeUpdate()
                withContext(Dispatchers.Main){
                    enfermedad.setText("")
                    Toast.makeText(requireContext(), "Se agrego correctamente", Toast.LENGTH_SHORT).show()
                  }
             }

        }

    }
        catch (e:Exception){
            println("El error es: $e")
        }

        return root
}



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Enfermedades.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Enfermedades().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}