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

class Adaptador (var Datos: List<dataClassPacientes>):RecyclerView.Adapter<ViewHolder>(){



    fun actualizacionEstado(id: Int, newNombre: String, newEnfermedad: String) {
        val index = Datos.indexOfFirst { it.idPacientes == id }
        if (index != -1) {
            Datos[index].nombre = newNombre
            Datos[index].Enfermedad = newEnfermedad
            notifyItemChanged(index)
        }
    }

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

    fun eliminarDatos(nombreDelPaciente: String, position: Int) {

        val listaDatos = Datos.toMutableList()

        listaDatos.removeAt(position)

        GlobalScope.launch(Dispatchers.IO) {

            val objConexion = ClaseConexion().CadenaConexion()

            val EliminarDato =
                objConexion?.prepareStatement("DELETE PACIENTESS WHERE nombre = ?")!!

            EliminarDato.setString(1, nombreDelPaciente )
            EliminarDato.executeUpdate()

            val commit = objConexion.prepareStatement("commit")
            commit.executeUpdate()
        }

        Datos = listaDatos.toList()
        notifyItemRemoved(position)
        notifyDataSetChanged()


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_card, parent, false)

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




    fun obtenerHabitaciones():List<dataClassHabitaciones> {
        try {
            val objConexion = ClaseConexion().CadenaConexion()

            val statment = objConexion?.createStatement()

            val resultSet = statment?.executeQuery("select * from HABITACIONESS")!!

            val listadoDeResultados = mutableListOf<dataClassHabitaciones>()

            while (resultSet.next()){
                val idHabitacion = resultSet.getInt("idHabitacion")

                val numeroHabitacion = resultSet.getString("numeroHabitacion")

                val resultadohabi = dataClassHabitaciones(idHabitacion, numeroHabitacion)

                listadoDeResultados.add(resultadohabi)


            }
            return listadoDeResultados
        }catch (e:Exception) {

            println("El error es $e")
            return emptyList()
        }
    }

    fun obtenerEnfermedades():List<dataClassEnfermedades> {
        try {
            val objConexion = ClaseConexion().CadenaConexion()

            val statment = objConexion?.createStatement()

            val resultSet = statment?.executeQuery("select * from ENFERMEDADESS")!!

            val listadoDeResultados = mutableListOf<dataClassEnfermedades>()

            while (resultSet.next()){
                val idEnfermedad = resultSet.getInt("idEnfermedad")

                val nombreEnfermedad = resultSet.getString("Enfermedad")

                val resultadoEnfe = dataClassEnfermedades(idEnfermedad, nombreEnfermedad)

                listadoDeResultados.add(resultadoEnfe)


            }
            return listadoDeResultados
        }catch (e:Exception) {

            println("El error es $e")
            return emptyList()
        }
    }
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

        val tiposSangre = arrayOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "y O-")
        spSangre.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, tiposSangre)

        CoroutineScope(Dispatchers.IO).launch {
            val enfermedades = obtenerEnfermedades()
            val habitaciones = obtenerHabitaciones()

            val nombreEnfermedad = enfermedades.map { it.Enfermedad }
            val numHabitacion = habitaciones.map { it.numeroHabitacion }

            withContext(Dispatchers.Main) {
                spEnfermedades.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, nombreEnfermedad)
                spHabitaciones.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, numHabitacion)
            }
        }

        txtNombre.setText(paciente.nombre)
        txtTelefono.setText(paciente.telefono)
        txtFechaNacimiento.setText(paciente.fechaDeNacimiento)
        txtNumeroCama.setText(paciente.numeroCama)
        txtMedicamentoAsignado.setText(paciente.medicamentoAsignado)
        txtHora.setText(paciente.horaDeAplicacionDelMedicamento)

        spSangre.setSelection(tiposSangre.indexOf(paciente.tipoDeSangre))

        MaterialAlertDialogBuilder(context)
            .setTitle("Actualizar Paciente")
            .setView(dialogView)
            .setPositiveButton("Actualizar") { dialog, which ->
                val nombrePaciente = txtNombre.text.toString()
                val tipoSangre = spSangre.selectedItem.toString()
                val nuevaEnfermedad = spEnfermedades.selectedItem.toString()

                CoroutineScope(Dispatchers.IO).launch {
                    val enfermedades = obtenerEnfermedades()
                    val habitaciones = obtenerHabitaciones()

                    val idEnfermedad = if (enfermedades.isNotEmpty()) {
                        enfermedades[spEnfermedades.selectedItemPosition].idEnfermedad
                    } else {
                        -1
                    }
                    val idHabitacion = if (habitaciones.isNotEmpty()) {
                        habitaciones[spHabitaciones.selectedItemPosition].idHabitacion
                    } else {
                        -1
                    }

                    val telefono = txtTelefono.text.toString().toInt()
                    val fechaNacimiento = txtFechaNacimiento.text.toString()
                    val numeroCama = txtNumeroCama.text.toString().toInt()
                    val medicamentoAsignados = txtMedicamentoAsignado.text.toString()
                    val horaMed = txtHora.text.toString()

                    val pacienteActualizado = dataClassPacientes(
                        idPacientes = paciente.idPacientes,
                        nombre = nombrePaciente,
                        tipoDeSangre = tipoSangre,
                        telefono = telefono,
                        fechaDeNacimiento = fechaNacimiento,
                        idHabitacion = idHabitacion,
                        idEnfermedad = idEnfermedad,
                        numeroCama = numeroCama,
                        medicamentoAsignado = medicamentoAsignados,
                        horaDeAplicacionDelMedicamento = horaMed,
                        Enfermedad = nuevaEnfermedad,
                        numeroHabitacion = paciente.numeroHabitacion
                    )

                    val realizado = actualizarPacientes(pacienteActualizado)
                    withContext(Dispatchers.Main) {
                        if (realizado) {
                            actualizacionEstado(paciente.idPacientes, nombrePaciente, nuevaEnfermedad)
                        } else {
                            println("Error al actualizar el paciente")
                        }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


}