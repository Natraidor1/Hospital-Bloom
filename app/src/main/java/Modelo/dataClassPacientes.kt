package Modelo

data class dataClassPacientes(

    val idPacientes: Int,
    var nombre: String,
    var tipoDeSangre: String,
    var telefono: Int,
    var medicamentoAsignado: String,
    var fechaDeNacimiento: String,
    var horaDeAplicacionDelMedicamento: String,
    var idEnfermedad: Int,
    var idHabitacion:Int,
    var Enfermedad:String,
    var numeroCama:Int,
    var numeroHabitacion:String

)
