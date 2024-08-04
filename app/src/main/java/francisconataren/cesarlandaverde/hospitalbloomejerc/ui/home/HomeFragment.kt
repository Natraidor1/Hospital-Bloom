package francisconataren.cesarlandaverde.hospitalbloomejerc.ui.home

import Modelo.ClaseConexion
import Modelo.dataClassEnfermedades
import Modelo.dataClassHabitaciones
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import francisconataren.cesarlandaverde.hospitalbloomejerc.R
import francisconataren.cesarlandaverde.hospitalbloomejerc.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val TipoDeSangre = arrayOf(
            "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "y O-"
        )

        val AdaptadorSp = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, TipoDeSangre)
        binding.spTipoDeSangre.adapter = AdaptadorSp

        binding.VerControl.setOnClickListener {
            findNavController().navigate(R.id.HaciaRegistro)
        }

        fun obtenerEnfermedades(): List<dataClassEnfermedades> {
            try {
                val objConexion = ClaseConexion().CadenaConexion()
                val statement = objConexion?.createStatement()
                val resultSet = statement?.executeQuery("select * from ENFERMEDADESS")!!
                val listResultado = mutableListOf<dataClassEnfermedades>()

                while (resultSet.next()) {
                    val idEnfermedad = resultSet.getInt("idEnfermedad")
                    val enfermedad = resultSet.getString("Enfermedad")
                    val identificador = dataClassEnfermedades(idEnfermedad, enfermedad)
                    listResultado.add(identificador)
                }
                return listResultado

            } catch (e: Exception) {
                println("El error es : $e")
                return emptyList()
            }
        }

        try {
            CoroutineScope(Dispatchers.IO).launch {
                val listResultado = obtenerEnfermedades()
                val nombreEnfermedad = listResultado.map { it.Enfermedad }
                withContext(Dispatchers.Main) {
                    val miAdaptador = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombreEnfermedad)
                    binding.spEnfermedad.adapter = miAdaptador
                }
            }
        } catch (e: Exception) {
            println("El error es $e")
        }

        fun obtenerHabitaciones(): List<dataClassHabitaciones> {
            try {
                val objConexion = ClaseConexion().CadenaConexion()
                val statment = objConexion?.createStatement()
                val resultSet = statment?.executeQuery("select * from HABITACIONESS")!!
                val listResultadosHabitacion = mutableListOf<dataClassHabitaciones>()

                while (resultSet.next()) {
                    val idHabitacion = resultSet.getInt("idHabitacion")
                    val numeroHabitacion = resultSet.getString("numeroHabitacion")
                    val identificadorHabitaciones = dataClassHabitaciones(idHabitacion, numeroHabitacion)
                    listResultadosHabitacion.add(identificadorHabitaciones)
                }
                return listResultadosHabitacion
            } catch (e: Exception) {
                println("El error es $e")
                return emptyList()
            }
        }

        try {
            CoroutineScope(Dispatchers.IO).launch {
                val listadodeResultados = obtenerHabitaciones()
                val numeroHabitacion = listadodeResultados.map { it.numeroHabitacion }
                withContext(Dispatchers.Main) {
                    val miAdaptador = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, numeroHabitacion)
                    binding.spNumeroHabitacion.adapter = miAdaptador
                }
            }
        } catch (e: Exception) {
            println("El error es $e")
        }

        try {
            binding.registrarPaciente.setOnClickListener {
                try {
                    CoroutineScope(Dispatchers.IO).launch {
                        val objConexion = ClaseConexion().CadenaConexion()
                        val habitacioness = obtenerHabitaciones()
                        val enfermedadess = obtenerEnfermedades()
                        val agregarPaciente = objConexion?.prepareStatement("Insert into Pacientes (nombre, tipoDeSangre, telefono, fechaDeNacimiento, idHabitacion, idEnfermedad, numeroCama, medicamentoAsignado, horaDeAplicacionDelMedicamento) VALUES (?,?,?,?,?,?,?,?,?)")!!

                        agregarPaciente.setString(1, binding.lblNombre.text.toString())
                        agregarPaciente.setString(2, binding.spTipoDeSangre.selectedItem.toString())
                        agregarPaciente.setString(3, binding.txtTelefono.text.toString())
                        agregarPaciente.setString(4, binding.txtFechaDeNacimientos.text.toString())
                        agregarPaciente.setInt(5, habitacioness[binding.spNumeroHabitacion.selectedItemPosition].idHabitacion)
                        agregarPaciente.setInt(6, enfermedadess[binding.spEnfermedad.selectedItemPosition].idEnfermedad)
                        agregarPaciente.setString(7, binding.txtNumeroCama.text.toString())
                        agregarPaciente.setString(8, binding.txtMedicamentoAsignados.text.toString())
                        agregarPaciente.setString(9, binding.txtHoraDeAplicacionDelMedicamento.text.toString())

                        agregarPaciente.executeUpdate()

                        withContext(Dispatchers.Main) {
                            binding.txtHoraDeAplicacionDelMedicamento.setText("")
                            binding.lblNombre.setText("")
                            binding.txtMedicamentoAsignados.setText("")
                            binding.txtNumeroCama.setText("")
                            binding.txtFechaDeNacimientos.setText("")
                            binding.txtTelefono.setText("")

                            Toast.makeText(requireContext(), "El paciente ha sido insertado con éxito", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    println("El error es $e")
                }
            }
        } catch (e: Exception) {
            println("El error es $e")
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}