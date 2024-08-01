package Modelo

data class dataClassPacientes(

    val idPacientes: Int,
    val nombre: String,
    val tipoDeSangre: String,
    val telefono: Int,
    val medicamentoAsignado: String,
    val fechaDeNacimiento: String,
    val horaDeAplicacionDelMedicamento: String,
    val idEnfermedad: Int,
    val idHabitacion:Int,
    val Enfermedad:String,
    val numeroCama:Int

)
