package Principal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

public class ejer03 {
	static String password="cfgs";

	public static void main(String[] args) {
		// Hashmap cuya clave sea la moneda y valor la cantidad
		
		dinero("banco");

	}
	
	public static void dinero(String bbdd) {
		TreeMap<Double, Integer> lista =new TreeMap<>(); //Como hashmap pero sale ordenado
		String conexion="jdbc:mysql://localhost:3306/"+bbdd; //conecta con mysql
		String cadena= "SELECT * FROM CAJA";
		
		
		
		try( Connection conn=DriverManager.getConnection(conexion,"root",password); //Genera la conexion con mysql. conexion, usuario, contraseña
				PreparedStatement sentencia=conn.prepareStatement(cadena); // Prepara la sentencia de creación de la base de datos

				){
			
			ResultSet conjunto = sentencia.executeQuery(); //ResultSet: devuelve conjunto de registros de la sentencia
			while(conjunto.next()) { //mientras que existan datos en el conjunto
				
				lista.put(conjunto.getDouble("MONEDA"), conjunto.getInt("CANTIDAD"));
			}
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		for(Map.Entry<Double, Integer> monedas : lista.entrySet()){
			//System.out.println("Clave: " + monedas.getKey() + "     valor: " + monedas.getValue());
			
			System.out.printf("Clave=%10.2f valor=%4d\n", monedas.getKey(),monedas.getValue()); //ancho de 10 y 4 de ancho del entero
			
		}
	}

}
