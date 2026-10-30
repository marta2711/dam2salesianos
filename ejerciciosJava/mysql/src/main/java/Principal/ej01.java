package Principal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

import Clases.Empleado;

public class ej01 {

		
		//CREAR BASE DE DATOS PARA PODER TRABAJAR CON ELLA
		public static void CrearBBDD() {
			String conexion="jdbc:mysql://localhost:3306/"; //cadena de conexion de mysql
			String cadena="CREATE DATABASE IF NOT EXISTS CLASE";
			
			try( Connection conn=DriverManager.getConnection(conexion,"root","cfgs"); //Genera la conexion con mysql. conexion, usuario, contraseña
					PreparedStatement sentencia=conn.prepareStatement(cadena); // Prepara la sentencia de creación de la base de datos
					){
				sentencia.execute();
				System.out.println("Base de datos creada o ya existente");
				
			}catch(SQLException e) {
				System.out.println(e.getMessage());
			}catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		public static void CrearTabla() {
			String conexion="jdbc:mysql://localhost:3306/CLASE"; //cadena de conexion de mysql, clase
			String cadena="CREATE TABLE EMPLEADOS("
					+ "ID INT auto_increment,"
					+ "NOMBRE VARCHAR(100) NOT NULL,"
					+ "SALARIO DECIMAL(10,2) NOT NULL,"
					+ "FECHA DATETIME NOT NULL DEFAULT current_timestamp,"
					+ "PRIMARY KEY(ID)"
					+ ")"; //Crear tabla de la forma de mysql
			
			try( Connection conn=DriverManager.getConnection(conexion,"root","cfgs"); //Genera la conexion con mysql. conexion, usuario, contraseña
					PreparedStatement sentencia=conn.prepareStatement(cadena); // Prepara la sentencia de creación de la base de datos
					){
				sentencia.execute(); //si se ejecuta sin ningún error imprime el mensaje
				System.out.println("Tabla creada");
				
			}catch(SQLException e) {
				System.out.println(e.getMessage());
			}catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		public static void insertarFilas() {
			String conexion="jdbc:mysql://localhost:3306/CLASE";
			String cadena="insert into empleados(nombre, salario, fecha) values(?,?,?)"; //? la primera es el nombre, la segunda salario y tercera la fecha
			ArrayList<Empleado> lista = new ArrayList<Empleado>();
			lista.add(new Empleado(1l, "maria", 3000, LocalDateTime.of(2000, 10,20,10,10))); //(año, mes. día, hora, minutos)
			lista.add(new Empleado(2l, "jose", 5000, LocalDateTime.of(2000, 10,20,10,10)));
			lista.add(new Empleado(3l, "juan", 6000, LocalDateTime.of(2000, 10,20,10,10)));
			lista.add(new Empleado(4l, "andres", 7000, LocalDateTime.of(2000, 10,20,10,10)));
			lista.add(new Empleado(5l, "ana", 8000, LocalDateTime.of(2000, 10,20,10,10)));
			
			int filas=0;
			
			try( Connection conn=DriverManager.getConnection(conexion,"root","cfgs"); 
					PreparedStatement sentencia=conn.prepareStatement(cadena); 
					){
				for (Empleado emp : lista) { //for each para recorrer lista de empleados. Asignará el valor de cada empleado
					sentencia.setString(1, emp.getNombre()); //sustituye los valores en cada hueco, en el primero pondrá el nombre
					sentencia.setDouble(2, emp.getSalario());
					sentencia.setTimestamp(3, Timestamp.valueOf(emp.getFecha()));
					filas += sentencia.executeUpdate(); //executeUpdate devuelve el número de filas insertadas. Luego imprimir por pantalla el número
				}
				System.out.println("El número de filas insertadas es " + filas); //imprime número de filas insertadas
				
			}catch(SQLException e) {
				System.out.println(e.getMessage());
			}catch(Exception e) {
				System.out.println(e.getMessage());
			}
			
		}

		public static void main(String[] args) {
			CrearBBDD();
			CrearTabla();
			insertarFilas();

		}

	

}
