package francisconataren.cesarlandaverde.hospitalbloomejerc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController

//TERMINADO THX GOD

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [informaciondelpaciente.newInstance] factory method to
 * create an instance of this fragment.
 */
class informaciondelpaciente : Fragment() {
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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.haciaRegistrar)
            }
        })

        val root = inflater.inflate(R.layout.fragment_informaciondelpaciente, container, false)

        val nombrePaciente = arguments?.getString("nombrePaciente")
        val numeroHabitacion = arguments?.getString("numeroHabitacion")
        val numeroTelefono = arguments?.getString("numeroTelefono")
        val tipoDeSangre = arguments?.getString("tipoDeSangre")
        val fechaDeNacimiento = arguments?.getString("fechaDeNacimiento")
        val numeroDeCama = arguments?.getString("NumeroDeCama")
        val medicamentoAsignado = arguments?.getString("medicamentoAsignado")
        val horaMedicamento = arguments?.getString("horaMedicamento")
        val nombreEnfermedad = arguments?.getString("nombreEnfermedad")

        root.findViewById<TextView>(R.id.lblNombrepaciente).text = nombrePaciente
        root.findViewById<TextView>(R.id.lblNumeroHabitacion).text = numeroHabitacion
        root.findViewById<TextView>(R.id.lblTelefono).text = numeroTelefono
        root.findViewById<TextView>(R.id.lblTipodeSangre).text = tipoDeSangre
        root.findViewById<TextView>(R.id.lblFechaDeNacimiento).text = fechaDeNacimiento
        root.findViewById<TextView>(R.id.lblNumeroDeCama).text = numeroDeCama
        root.findViewById<TextView>(R.id.lblMedicamentoAsignado).text = medicamentoAsignado
        root.findViewById<TextView>(R.id.lblHoraMedicamento).text = horaMedicamento
        root.findViewById<TextView>(R.id.lblNombreEnfermedad).text = nombreEnfermedad


        return  root


    }







    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment informaciondelpaciente.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            informaciondelpaciente().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}