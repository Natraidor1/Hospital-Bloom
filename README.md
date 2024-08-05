CREATE TABLE ENFERMEDADESS(
idEnfermedad INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
Enfermedad VARCHAR(50) NOT NULL
)

CREATE TABLE HABITACIONESS(
idHabitacion INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
numeroHabitacion NUMBER NOT NULL

)

CREATE TABLE PACIENTESS(
idPacientes INT GENERATED ALWAYS AS IDENTITY,
idEnfermedad NUMBER,
idHabitacion NUMBER,
nombre VARCHAR(50) NOT NULL,
tipoDeSangre VARCHAR(50) NOT NULL,
telefono NUMBER NOT NULL,
medicamentoAsignado VARCHAR(50) NOT NULL,
fechaDeNacimiento DATE NOT NULL,
horaDeAplicacionDelMedicamento VARCHAR(50),
numeroCama NUMBER NOT NULL,

CONSTRAINT fk_enfermedad FOREIGN KEY (idEnfermedad) REFERENCES ENFERMEDADESS (idEnfermedad),
CONSTRAINT fk_habitacion FOREIGN KEY (idHabitacion) REFERENCES HABITACIONESS (idHabitacion)

)

INSERT INTO ENFERMEDADESS (Enfermedad) VALUES ('SIDA');

INSERT INTO HABITACIONESS (numeroHabitacion) VALUES (1);

SELECT * FROM PACIENTESS
select * from HABITACIONESS

INSERT INTO PACIENTESS (idEnfermedad,idHabitacion,nombre,tipoDeSangre,telefono,medicamentoAsignado,fechaDeNacimiento,horaDeAplicacionDelMedicamento,numeroCama) VALUES (1,1,'jose','O+', 76564312, 'paracetamol', TO_DATE('2024-08-05', 'YYYY-MM-DD'), 'Las 12 AM de cada dia', 1);

SELECT 
    P.idPacientes AS id, 
    P.nombre AS nombre, 
    P.tipoDeSangre AS tipoDeSangre, 
    P.numeroCama AS numeroCama, 
    P.medicamentoAsignado AS medicamentoAsignado, 
    P.horaDeAplicacionDelMedicamento AS horaDeAplicacionDelMedicamento, 
    E.Enfermedad AS Enfermedad, 
    H.numeroHabitacion AS numeroHabitacion, 
    P.telefono AS telefono, 
    P.fechaDeNacimiento AS fechaDeNacimiento, 
    P.idHabitacion AS idHabitacion, 
    E.idEnfermedad AS idEnfermedad 
FROM PACIENTESS P 
INNER JOIN ENFERMEDADESS E ON P.idEnfermedad = E.idEnfermedad 
INNER JOIN HABITACIONESS H ON P.idHabitacion = H.idHabitacion
SELECT * FROM PACIENTESS
