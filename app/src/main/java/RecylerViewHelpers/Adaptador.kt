package RecylerViewHelpers

import Modelo.ClaseConexion
import Modelo.dataClassEnfermedades
import Modelo.dataClassHabitaciones
import Modelo.dataClassPacientes
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import francisconataren.cesarlandaverde.hospitalbloomejerc.R
import francisconataren.cesarlandaverde.hospitalbloomejerc.informaciondelpaciente
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Adaptador(var Datos: List<dataClassPacientes>) : RecyclerView.Adapter<ViewHolder>() {

    // Función para actualizar el estado de un paciente en el adaptador
    fun actualizacionEstado(id: Int, newNombre: String, newEnfermedad: String) {
        val index = Datos.indexOfFirst { it.idPacientes == id }
        if (index != -1) {
            val pacienteActualizado = Datos[index].copy(
                nombre = newNombre,
                Enfermedad = newEnfermedad
            )
            Datos = Datos.toMutableList().apply {
                set(index, pacienteActualizado)
            }
            notifyItemChanged(index)
        }
    }

    // Función para obtener las habitaciones desde la base de datos
    fun obtenerHabitaciones(): List<dataClassHabitaciones> {
        try {
            val objConexion = ClaseConexion().CadenaConexion()
            val statment = objConexion?.createStatement()
            val resultSet = statment?.executeQuery("SELECT * FROM HABITACIONESS")
            val listadoDeResultados = mutableListOf<dataClassHabitaciones>()
            while (resultSet?.next() == true) {
                val idHabitacion = resultSet.getInt("idHabitacion")
                val numeroHabitacion = resultSet.getString("numeroHabitacion")
                val resultadohabi = dataClassHabitaciones(idHabitacion, numeroHabitacion)
                listadoDeResultados.add(resultadohabi)
            }
            return listadoDeResultados
        } catch (e: Exception) {
            println("El error es $e")
            return emptyList()
        }
    }

    // Función para obtener las enfermedades desde la base de datos
    fun obtenerEnfermedades(): List<dataClassEnfermedades> {
        try {
            val objConexion = ClaseConexion().CadenaConexion()
            val statment = objConexion?.createStatement()
            val resultSet = statment?.executeQuery("SELECT * FROM ENFERMEDADESS")
            val listadoDeResultados = mutableListOf<dataClassEnfermedades>()
            while (resultSet?.next() == true) {
                val idEnfermedad = resultSet.getInt("idEnfermedad")
                val nombreEnfermedad = resultSet.getString("Enfermedad")
                val resultadoEnfe = dataClassEnfermedades(idEnfermedad, nombreEnfermedad)
                listadoDeResultados.add(resultadoEnfe)
            }
            return listadoDeResultados
        } catch (e: Exception) {
            println("El error es $e")
            return emptyList()
        }
    }

    // Función para actualizar un paciente en la base de datos
    fun actualizarPacientes(paciente: dataClassPacientes): Boolean {
        val objConexion = ClaseConexion().CadenaConexion() ?: run {
            Log.e("Adaptador", "La conexión a la base de datos es nula.")
            return false
        }

        val ActuPaciente = objConexion.prepareStatement(
            "UPDATE PACIENTESS SET " +
                    "nombre = ?, " +
                    "tipoDeSangre = ?, " +
                    "telefono = ?, " +
                    "fechaDeNacimiento = ?, " +
                    "idHabitacion = ?, " +
                    "idEnfermedad = ?, " +
                    "numeroCama = ?, " +
                    "medicamentoAsignado = ?, " +
                    "horaDeAplicacionDelMedicamento = ? " +
                    "WHERE idPacientes = ?"
        ) ?: run {
            Log.e("Adaptador", "Error: El PreparedStatement es nulo.")
            return false
        }

        try {
            ActuPaciente.setString(1, paciente.nombre)
            ActuPaciente.setString(2, paciente.tipoDeSangre)
            ActuPaciente.setInt(3, paciente.telefono)
            ActuPaciente.setString(4, paciente.fechaDeNacimiento)
            ActuPaciente.setInt(5, paciente.idHabitacion)
            ActuPaciente.setInt(6, paciente.idEnfermedad)
            ActuPaciente.setInt(7, paciente.numeroCama)
            ActuPaciente.setString(8, paciente.medicamentoAsignado)
            ActuPaciente.setString(9, paciente.horaDeAplicacionDelMedicamento)
            ActuPaciente.setInt(10, paciente.idPacientes)

            val result = ActuPaciente.executeUpdate()
            return result > 0
        } catch (e: Exception) {
            Log.e("Adaptador", "Error al actualizar paciente: $e")
            return false
        } finally {
            ActuPaciente.close()
        }
    }

    // Función para eliminar datos de un paciente
    fun eliminarDatos(nombreDelPaciente: String, position: Int) {
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(position)

        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().CadenaConexion()
            val EliminarDato = objConexion?.prepareStatement("DELETE FROM PACIENTESS WHERE nombre = ?")!!
            EliminarDato.setString(1, nombreDelPaciente)
            EliminarDato.executeUpdate()
            val commit = objConexion.prepareStatement("COMMIT")
            commit.executeUpdate()
        }

        Datos = listaDatos.toList()
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card, parent, false)
        return ViewHolder(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pacientes = Datos[position]
        holder.txtNombre.text = pacientes.nombre
        holder.lblEnfermedad.text = pacientes.Enfermedad

        holder.imgBorrar.setOnClickListener {
            val context = holder.itemView.context
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Eliminación Parcial")
            builder.setMessage("¿Desea eliminar el paciente?")
            builder.setPositiveButton("Si") { dialog, which ->
                eliminarDatos(pacientes.nombre, position)
            }
            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }

        holder.imgEditar.setOnClickListener {
            alertDialog(holder.itemView.context, pacientes)
        }

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("nombrePaciente", pacientes.nombre)
                putString("tipoSangre", pacientes.tipoDeSangre)
                putInt("telefono", pacientes.telefono)
                putString("fechaNacimiento", pacientes.fechaDeNacimiento)
                putInt("numeroCama", pacientes.numeroCama)
                putString("medicamentoAsignados", pacientes.medicamentoAsignado)
                putString("horaMedicamento", pacientes.horaDeAplicacionDelMedicamento)
                putString("nombreEnfermedad", pacientes.Enfermedad)
                putString("numeroHabitacion", pacientes.numeroHabitacion)
            }
            val fragment = informaciondelpaciente().apply {
                arguments = bundle
            }
            (holder.itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    // Función para mostrar el diálogo de actualización
    private fun alertDialog(context: Context, paciente: dataClassPacientes) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.datosactualizados, null)
        val txtNombre: EditText = dialogView.findViewById(R.id.txtNombrePaciente)
        val txtTelefono: EditText = dialogView.findViewById(R.id.txtTelefonoPaciente)
        val txtFechaNacimiento: EditText = dialogView.findViewById(R.id.txtFechaDeNacimiento)
        val txtNumeroCama: EditText = dialogView.findViewById(R.id.txtNumeroDeCama)
        val txtMedicamentoAsignado: EditText = dialogView.findViewById(R.id.txtMedicamentoAsignado)
        val txtHora: EditText = dialogView.findViewById(R.id.txtHora)
        val spSangre: Spinner = dialogView.findViewById(R.id.spSangre)
        val spEnfermedades: Spinner = dialogView.findViewById(R.id.spEnfermedades)
        val spHabitaciones: Spinner = dialogView.findViewById(R.id.spHabitacion)

        val tiposSangre = arrayOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        spSangre.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, tiposSangre)

        CoroutineScope(Dispatchers.IO).launch {
            val enfermedades = obtenerEnfermedades()
            val habitaciones = obtenerHabitaciones()
            val nombreEnfermedad = enfermedades.map { it.Enfermedad }
            val numHabitacion = habitaciones.map { it.numeroHabitacion.toString() }

            withContext(Dispatchers.Main) {
                spEnfermedades.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, nombreEnfermedad)
                spHabitaciones.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, numHabitacion)
            }
        }

        txtNombre.setText(paciente.nombre)
        txtTelefono.setText(paciente.telefono.toString())
        txtFechaNacimiento.setText(paciente.fechaDeNacimiento)
        txtNumeroCama.setText(paciente.numeroCama.toString())
        txtMedicamentoAsignado.setText(paciente.medicamentoAsignado)
        txtHora.setText(paciente.horaDeAplicacionDelMedicamento)

        spSangre.setSelection(tiposSangre.indexOf(paciente.tipoDeSangre))

        MaterialAlertDialogBuilder(context)
            .setTitle("Actualizar Paciente")
            .setView(dialogView)
            .setPositiveButton("Actualizar") { dialog, _ ->
                val nombrePaciente = txtNombre.text.toString()
                val telefono = txtTelefono.text.toString().toInt()
                val fechaNacimiento = txtFechaNacimiento.text.toString()
                val numeroCama = txtNumeroCama.text.toString().toInt()
                val medicamentoAsignado = txtMedicamentoAsignado.text.toString()
                val horaMedicamento = txtHora.text.toString()
                val tipoSangre = spSangre.selectedItem.toString()
                val idEnfermedad = obtenerEnfermedades().firstOrNull { it.Enfermedad == spEnfermedades.selectedItem.toString() }?.idEnfermedad ?: paciente.idEnfermedad
                val idHabitacion = obtenerHabitaciones().firstOrNull { it.numeroHabitacion.toString() == spHabitaciones.selectedItem.toString() }?.idHabitacion ?: paciente.idHabitacion

                val pacienteActualizado = dataClassPacientes(
                    idPacientes = paciente.idPacientes,
                    nombre = nombrePaciente,
                    tipoDeSangre = tipoSangre,
                    telefono = telefono,
                    fechaDeNacimiento = fechaNacimiento,
                    numeroCama = numeroCama,
                    medicamentoAsignado = medicamentoAsignado,
                    horaDeAplicacionDelMedicamento = horaMedicamento,
                    idEnfermedad = idEnfermedad,
                    idHabitacion = idHabitacion,
                    Enfermedad = spEnfermedades.selectedItem.toString(),
                    numeroHabitacion = spHabitaciones.selectedItem.toString()
                )

                // Actualizar en la base de datos y en la lista
                if (actualizarPacientes(pacienteActualizado)) {
                    actualizacionEstado(paciente.idPacientes, nombrePaciente, spEnfermedades.selectedItem.toString())
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
