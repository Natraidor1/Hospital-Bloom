package francisconataren.cesarlandaverde.hospitalbloomejerc.ui.dashboard

import Modelo.ClaseConexion
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import francisconataren.cesarlandaverde.hospitalbloomejerc.R
import francisconataren.cesarlandaverde.hospitalbloomejerc.databinding.FragmentDashboardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnIngresarHabitacion.setOnClickListener {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val objConexion = ClaseConexion().CadenaConexion()
                    val ingresarHabitacion = objConexion?.prepareStatement("insert into HABITACIONESS(numeroHabitacion) values (?)")!!
                    ingresarHabitacion.setString(1, binding.txtHabitaciondh.text.toString())
                    ingresarHabitacion.executeUpdate()
                    withContext(Dispatchers.Main) {
                        binding.txtHabitaciondh.setText("")
                        Toast.makeText(requireContext(), "La habitación se ha insertado correctamente", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                println("El error es: $e")
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}