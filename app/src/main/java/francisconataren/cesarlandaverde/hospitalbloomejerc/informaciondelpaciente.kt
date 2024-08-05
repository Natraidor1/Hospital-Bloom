package francisconataren.cesarlandaverde.hospitalbloomejerc

import Modelo.dataClassPacientes
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_informaciondelpaciente, container, false)

        val hacia2 = root.findViewById<ImageView>(R.id.imgRegistro2)

        try {
            hacia2.setOnClickListener {
                findNavController().navigate(R.id.HaciaRegistro2)
            }
        }
        catch (e:Exception){
            println("el error es: $e")
        }


        val nombrePaciente = arguments?.getString("nombrePaciente")
        val numeroHabitacion = arguments?.getString("numeroHabitacion")
        val numeroTelefono = arguments?.getString("telefono")
        val tipoDeSangre = arguments?.getString("tipoSangre")
        val fechaDeNacimiento = arguments?.getString("fechaDeNacimiento")
        val numeroDeCama = arguments?.getString("numeroCama")
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

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(paciente: dataClassPacientes) =
            informaciondelpaciente().apply {
                arguments = Bundle().apply {
                    putString("nombrePaciente", paciente.nombre)
                    putString("numeroHabitacion", paciente.numeroHabitacion)
                    putString("telefono", paciente.telefono.toString())
                    putString("tipoSangre", paciente.tipoDeSangre)
                    putString("fechaDeNacimiento", paciente.fechaDeNacimiento)
                    putString("numeroCama", paciente.numeroCama.toString())
                    putString("medicamentoAsignado", paciente.medicamentoAsignado)
                    putString("horaMedicamento", paciente.horaDeAplicacionDelMedicamento)
                    putString("nombreEnfermedad", paciente.Enfermedad)
                }
            }
    }
}
