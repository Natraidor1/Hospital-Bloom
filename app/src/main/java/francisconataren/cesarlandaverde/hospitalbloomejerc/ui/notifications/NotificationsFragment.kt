package francisconataren.cesarlandaverde.hospitalbloomejerc.ui.notifications

import Modelo.ClaseConexion
import Modelo.dataClassPacientes
import RecylerViewHelpers.Adaptador
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val rcvPacientes = binding.rcvPacientes
        rcvPacientes.layoutManager = LinearLayoutManager(context)

        cargarDatos()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun cargarDatos() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val registroDB = innerDatos()
                Log.d("NotificationsFragment", "Número de pacientes: ${registroDB.size}")

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
    }

    private fun actualizarDatos() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val registroDB = innerDatos()
                Log.d("NotificationsFragment", "Número de pacientes: ${registroDB.size}")

                withContext(Dispatchers.Main) {
                    val adaptador = binding.rcvPacientes.adapter as? Adaptador
                    adaptador?.apply {
                        Datos = registroDB
                        notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun manejarCambioEnDatos() {
        actualizarDatos()
    }

    private fun innerDatos(): List<dataClassPacientes> {
        val pacientes = mutableListOf<dataClassPacientes>()
        try {
            val objConexion = ClaseConexion().CadenaConexion()
            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery(
                "SELECT " +
                        "    P.idPacientes AS id, " +
                        "    P.nombre AS nombre, " +
                        "    P.tipoDeSangre AS tipoDeSangre, " +
                        "    P.numeroCama AS numeroCama, " +
                        "    P.medicamentoAsignado AS medicamentoAsignado, " +
                        "    P.horaDeAplicacionDelMedicamento AS horaDeAplicacionDelMedicamento, " +
                        "    E.Enfermedad AS Enfermedad, " +
                        "    H.numeroHabitacion AS numeroHabitacion, " +
                        "    P.telefono AS telefono, " +
                        "    P.fechaDeNacimiento AS fechaDeNacimiento, " +
                        "    P.idHabitacion AS idHabitacion, " +
                        "    E.idEnfermedad AS idEnfermedad " +
                        "FROM PACIENTESS P " +
                        "INNER JOIN ENFERMEDADESS E ON P.idEnfermedad = E.idEnfermedad " +
                        "INNER JOIN HABITACIONESS H ON P.idHabitacion = H.idHabitacion"
            )

            while (resultSet?.next() == true) {
                val paciente = dataClassPacientes(
                    idPacientes = resultSet.getInt("id"),
                    nombre = resultSet.getString("nombre"),
                    tipoDeSangre = resultSet.getString("tipoDeSangre"),
                    numeroCama = resultSet.getInt("numeroCama"),
                    medicamentoAsignado = resultSet.getString("medicamentoAsignado"),
                    horaDeAplicacionDelMedicamento = resultSet.getString("horaDeAplicacionDelMedicamento"),
                    Enfermedad = resultSet.getString("Enfermedad"),
                    numeroHabitacion = resultSet.getString("numeroHabitacion"),
                    telefono = resultSet.getInt("telefono"),
                    fechaDeNacimiento = resultSet.getString("fechaDeNacimiento"),
                    idHabitacion = resultSet.getInt("idHabitacion"),
                    idEnfermedad = resultSet.getInt("idEnfermedad")
                )
                pacientes.add(paciente)
            }
        } catch (e: Exception) {
            Log.e("NotificationsFragment", "Error al obtener datos: $e")
        }
        return pacientes
    }
}
