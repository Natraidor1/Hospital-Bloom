package francisconataren.cesarlandaverde.hospitalbloomejerc.ui.notifications

import Modelo.ClaseConexion
import Modelo.dataClassPacientes
import RecylerViewHelpers.Adaptador
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import francisconataren.cesarlandaverde.hospitalbloomejerc.R
import francisconataren.cesarlandaverde.hospitalbloomejerc.databinding.FragmentNotificationsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.HaciaRegistro)
            }
        })

        val rcvPacientes = binding.rcvPacientes

        // Utilice un poco de la IA de Android se me olvidó la sintaxis del INNER JOIN
        fun innerDatos(): List<dataClassPacientes> {
            val objConexion = ClaseConexion().CadenaConexion()
            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery(
                "SELECT \n" +
                        "    E.idEnfermedad AS EnfermedadID, \n" +
                        "    E.Enfermedad AS Enfermedad, \n" +
                        "    H.idHabitacion AS HabitacionID, \n" +
                        "    H.numeroHabitacion AS NumHabitacion, \n" +
                        "    P.idPacientes AS id, \n" +
                        "    P.nombre AS Paciente, \n" +
                        "    P.tipoDeSangre AS TipoSangre, \n" +
                        "    P.fechaDeNacimiento AS FechaNacimiento, \n" +
                        "    P.numeroCama AS Cama, \n" +
                        "    P.medicamentoAsignado AS Medicamentos, \n" +
                        "    P.horaDeAplicacionDelMedicamento AS HoraMedicamentos, \n" +
                        "    P.telefono AS Telefono \n" +
                        "FROM \n" +
                        "    PACIENTESS P \n" +
                        "INNER JOIN \n" +
                        "    ENFERMEDADESS E ON P.idEnfermedad = E.idEnfermedad \n" +
                        "INNER JOIN \n" +
                        "    HABITACIONESS H ON P.idHabitacion = H.idHabitacion;\n"
            )!!

            val pacientes = mutableListOf<dataClassPacientes>()

            while (resultSet.next()) {
                val idPaciente = resultSet.getInt("id")
                val nombrePaciente = resultSet.getString("Paciente")
                val tipoSangre = resultSet.getString("TipoSangre")
                val numeroDeCama = resultSet.getInt("Cama")
                val medicamentosAsignados = resultSet.getString("Medicamentos")
                val horaDelMedicamento = resultSet.getString("HoraMedicamentos")
                val nombreEnfermedad = resultSet.getString("Enfermedad")
                val numeroDeHabitacion = resultSet.getInt("NumHabitacion")
                val telefono = resultSet.getInt("Telefono")
                val fechaNacimiento = resultSet.getString("FechaNacimiento")
                val idHabitacion = resultSet.getInt("HabitacionID")
                val idEnfermedad = resultSet.getString("EnfermedadID")

                val paciente = dataClassPacientes(
                    idPaciente, nombrePaciente, tipoSangre, numeroDeCama, medicamentosAsignados, horaDelMedicamento,
                    nombreEnfermedad, numeroDeHabitacion, telefono, fechaNacimiento, idHabitacion, idEnfermedad
                )

                pacientes.add(paciente)
            }
            return pacientes
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val registroDB = innerDatos()
                withContext(Dispatchers.Main) {
                    val adaptador = Adaptador(registroDB)
                    binding.rcvPacientes.adapter = adaptador
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
