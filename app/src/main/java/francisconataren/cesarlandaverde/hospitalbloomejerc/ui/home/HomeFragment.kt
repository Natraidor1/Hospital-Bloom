package francisconataren.cesarlandaverde.hospitalbloomejerc.ui.home

import Modelo.ClaseConexion
import Modelo.dataClassEnfermedades
import Modelo.dataClassHabitaciones
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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


        val tipoDeSangre = arrayOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        val adaptadorSp = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, tipoDeSangre)
        binding.spTipoDeSangre.adapter = adaptadorSp


        binding.datepickerFechaDeNacimiento.setOnClickListener {
            showDatePicker()
        }


        binding.VerControl.setOnClickListener {
            findNavController().navigate(R.id.HaciaRegistro)
        }


        fun obtenerEnfermedades(): List<dataClassEnfermedades> {
            return try {
                val objConexion = ClaseConexion().CadenaConexion()
                val statement = objConexion?.createStatement()
                val resultSet = statement?.executeQuery("SELECT * FROM ENFERMEDADESS")!!
                val listResultado = mutableListOf<dataClassEnfermedades>()

                while (resultSet.next()) {
                    val idEnfermedad = resultSet.getInt("idEnfermedad")
                    val enfermedad = resultSet.getString("Enfermedad")
                    listResultado.add(dataClassEnfermedades(idEnfermedad, enfermedad))
                }
                listResultado
            } catch (e: Exception) {
                println("El error es : $e")
                emptyList()
            }
        }


        fun obtenerHabitaciones(): List<dataClassHabitaciones> {
            return try {
                val objConexion = ClaseConexion().CadenaConexion()
                val statement = objConexion?.createStatement()
                val resultSet = statement?.executeQuery("SELECT * FROM HABITACIONESS")!!
                val listResultadosHabitacion = mutableListOf<dataClassHabitaciones>()

                while (resultSet.next()) {
                    val idHabitacion = resultSet.getInt("idHabitacion")
                    val numeroHabitacion = resultSet.getString("numeroHabitacion")
                    listResultadosHabitacion.add(dataClassHabitaciones(idHabitacion, numeroHabitacion))
                }
                listResultadosHabitacion
            } catch (e: Exception) {
                println("El error es $e")
                emptyList()
            }
        }


        try {
            CoroutineScope(Dispatchers.IO).launch {
                val enfermedades = obtenerEnfermedades()
                val nombreEnfermedad = enfermedades.map { it.Enfermedad }
                withContext(Dispatchers.Main) {
                    val adaptadorEnfermedad = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombreEnfermedad)
                    binding.spEnfermedad.adapter = adaptadorEnfermedad
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                val habitaciones = obtenerHabitaciones()
                val numeroHabitacion = habitaciones.map { it.numeroHabitacion }
                withContext(Dispatchers.Main) {
                    val adaptadorHabitacion = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, numeroHabitacion)
                    binding.spNumeroHabitacion.adapter = adaptadorHabitacion
                }
            }
        } catch (e: Exception) {
            println("El error es $e")
        }


        binding.registrarPaciente.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val objConexion = ClaseConexion().CadenaConexion()
                    val habitaciones = obtenerHabitaciones()
                    val enfermedades = obtenerEnfermedades()
                    val agregarPaciente = objConexion?.prepareStatement(
                        "INSERT INTO PACIENTESS (nombre, tipoDeSangre, telefono, fechaDeNacimiento, idHabitacion, idEnfermedad, numeroCama, medicamentoAsignado, horaDeAplicacionDelMedicamento) VALUES (?,?,?,?,?,?,?,?,?)"
                    )

                    agregarPaciente?.apply {
                        setString(1, binding.lblNombre.text.toString())
                        setString(2, binding.spTipoDeSangre.selectedItem.toString())
                        setString(3, binding.txtTelefono.text.toString())

                        val fechaNacimientoStr = binding.datepickerFechaDeNacimiento.text.toString()
                        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                        try {
                            val date = format.parse(fechaNacimientoStr)
                            val sqlDate = java.sql.Date(date.time)
                            setDate(4, sqlDate)
                        } catch (e: Exception) {
                            println("Error al convertir la fecha: $e")
                        }

                        setInt(5, habitaciones[binding.spNumeroHabitacion.selectedItemPosition].idHabitacion)
                        setInt(6, enfermedades[binding.spEnfermedad.selectedItemPosition].idEnfermedad)
                        setString(7, binding.txtNumeroCama.text.toString())
                        setString(8, binding.txtMedicamentoAsignados.text.toString())
                        setString(9, binding.txtHoraDeAplicacionDelMedicamento.text.toString())

                        executeUpdate()
                    }

                    withContext(Dispatchers.Main) {
                        binding.txtHoraDeAplicacionDelMedicamento.setText("")
                        binding.lblNombre.setText("")
                        binding.txtMedicamentoAsignados.setText("")
                        binding.txtNumeroCama.setText("")
                        binding.datepickerFechaDeNacimiento.setText("")
                        binding.txtTelefono.setText("")

                        Toast.makeText(requireContext(), "El paciente ha sido insertado con éxito", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error al insertar el paciente: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                    println("El error es $e")
                }
            }
        }

        return root
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = format.format(selectedDate.time)
                binding.datepickerFechaDeNacimiento.setText(formattedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}