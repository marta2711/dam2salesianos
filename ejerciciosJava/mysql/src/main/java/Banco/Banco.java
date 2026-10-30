package Banco;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

import Clases.General;

public class Banco {

	static String bbdd = ""; //lo ponemos por si trabajamos con más de una base de datos
	static String password="cfgs";
	//static Scanner sc = new Scanner(System.in);
	static BufferedReader sc = new BufferedReader(new InputStreamReader(System.in)); //Se pone si scanner da problemas, importar de .io

	public static void CrearBBDD(String bd) {
		String conexion="jdbc:mysql://localhost:3306/"; 
		String cadena="CREATE DATABASE " + bd; //La pimera vez poner "CREATE DATABASE BANCO", en vez de "CREATE DATABASE IF NOT EXISTS"

		try(Connection conn=DriverManager.getConnection(conexion,"root",password);
				PreparedStatement sentencia = conn.prepareStatement(cadena);
				){
			sentencia.execute();
			System.out.println("Base de datos creada");

		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void CrearTabla(String bd) {
		String conexion="jdbc:mysql://localhost:3306/" + bd;
		String cadena = "CREATE TABLE CAJA("
				+ " MONEDA DECIMAL(10,2) PRIMARY KEY,"
				+ " CANTIDAD INT CHECK(CANTIDAD>=0)"
				+ " )";

		try(Connection conn=DriverManager.getConnection(conexion,"root",password);
				PreparedStatement sentencia=conn.prepareStatement(cadena);
				){
			sentencia.execute();
			System.out.println("Tabla creada");
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public static void insertarFilas(String bd) {

		String conexion="jdbc:mysql://localhost:3306/" + bd;
		String cadena="insert into caja(moneda, cantidad) values(?,?)";

		ArrayList<Caja> moneda = new ArrayList<Caja>();

		int tb[]= {5,2,1}; //monedas de 5,2,1
		int filas=0;

		try( Connection conn=DriverManager.getConnection(conexion,"root",password);
				PreparedStatement sentencia=conn.prepareStatement(cadena);
				){
			//for(int i =2; i>=-2;i--) {
			for(int i =-2; i<=2;i++) {
				for(int j=0;j<tb.length;j++) {
					sentencia.setDouble(1, tb[j]*Math.pow(10, i));
					sentencia.setInt(2, 10); //segundo parámetro, 10 unidades
					filas += sentencia.executeUpdate(); 
				}
			}

			System.out.println("El número de filas insertadas es " + filas); //imprime número de filas insertadas

		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}

	}
	
	public static void Seleccion() {
		String conexion="jdbc:mysql://localhost:3306/"+bbdd; 
		String cadena="SELECT * FROM CAJA ORDER BY MONEDA DESC"; //ORDENA LAS MONEDAS DE MANERA DESCENDENTE EN LA CONSOLA

		try(Connection conn=DriverManager.getConnection(conexion,"root",password);
				PreparedStatement sentencia = conn.prepareStatement(cadena);
				){
			ResultSet conjunto = sentencia.executeQuery(); //ResultSet: devuelve conjunto de registros de la sentencia
			while(conjunto.next()) { //mientras que existan datos en el conjunto
				System.out.printf("%.2f %d\n",
				conjunto.getDouble("MONEDA"),
				conjunto.getInt("CANTIDAD")
						);
			}

		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static boolean isExisteMoneda(double moneda) {
		String conexion="jdbc:mysql://localhost:3306/"+bbdd; 
		String cadena="SELECT COUNT(*) NF FROM CAJA WHERE MONEDA=?"; //Sentencia que cuenta el número de filas de la caja donde moneda= a un valor
		int n=0;

		try(Connection conn=DriverManager.getConnection(conexion,"root",password); //hace conexion
				PreparedStatement sentencia = conn.prepareStatement(cadena); //prepara candena
				){
			sentencia.setDouble(1, moneda); //1 porque las monedas están en la primera columna, moneda: parámetro que pasamos
			ResultSet conjunto = sentencia.executeQuery(); //ResultSet: devuelve conjunto de registros de la sentencia
			
			while(conjunto.next()) { //mientras que existan datos en el conjunto
				n=conjunto.getInt("NF"); //NF, parámetro de cadena
			}
			
			if(n!=0)return true; //si n es distinto de 0, existen monedas de ese valor

		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return false;
		
	}

	public static double Montante() { //no lo hemos utilizado
		String conexion="jdbc:mysql://localhost:3306/"+bbdd; 
		String cadena="SELECT SUM(MONEDA*CANTIDAD) TOTAL FROM CAJA"; //Sentencia que calcula el monto total de la caja
		double n=0;

		try(Connection conn=DriverManager.getConnection(conexion,"root",password); //hace conexion
				PreparedStatement sentencia = conn.prepareStatement(cadena); //prepara candena
				){
			
			ResultSet conjunto = sentencia.executeQuery(); //ResultSet: devuelve conjunto de registros de la sentencia
			
			while(conjunto.next()) { //mientras que existan datos en el conjunto
				n=conjunto.getDouble("TOTAL"); //NF, parámetro de cadena
			}
			
			return n; //devuelve el dinero total de la caja

		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return n;
		
	}

	public static void Actualizar(double moneda, int cantidad) {
		String conexion="jdbc:mysql://localhost:3306/"+bbdd; 
		String cadena="UPDATE CAJA SET CANTIDAD=CANTIDAD+? WHERE MONEDA=?"; //Sentencia que actualiza la caja, añade x cantidad y se suma a la cantidad existente de esa moneda
		int n;

		try(Connection conn=DriverManager.getConnection(conexion,"root",password); //hace conexion
				PreparedStatement sentencia = conn.prepareStatement(cadena); //prepara candena
				){
			sentencia.setInt(1, cantidad);
			sentencia.setDouble(2, moneda);
			n=sentencia.executeUpdate();
			System.out.println("El número de filas actualizadas es " + n);

		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
	}

	public static void Introducir_dinero() throws IOException {
		//String cadena = "1-500#1-50#1-20#2-0.2"; //Cadena que tenga 1 billete de 500, 1 de 50, 1 de 20 y 2 de 0.02
		System.out.print("Dime el dinero a introducir ");
		String cadena=sc.readLine();
		
		String trozos[]=cadena.split("#"), valores[]; //Matriz que en la posición 0 saca 1 de 500, en la 1 saca 1 de 50..., la cadena se separa por #
		int n;
		double moneda, suma=0;
		
		for(String t:trozos) {
			valores=t.split("-"); //en t tiene el intervalo (1-500), imprime los valores con espacio donde hay -
			n = Integer.valueOf(valores[0]);
			moneda = Double.valueOf(valores[1]);
			
			if(isNatural(n) && isExisteMoneda(moneda)) {//si la moneda es natural y existe, la añadimos con actualizar
				Actualizar(moneda,n);
				suma += n*moneda;
			}

		}
		System.out.println("El valor de lo introducido es " + suma);
	}
	
	public static boolean isNatural(double a) {
		
		if(a>=0 && (int)a==a) return true; //si a es número positivo y entero devuelve true
		return false;
	}
	
	public static boolean ExisteBBDD(String base) {
		String conexion="jdbc:mysql://localhost:3306/"; //conecta con mysql
		String cadena="select count(*) NU from information_schema.schemata where schema_name = ?"; //Compueba si existe la bbdd en mysql
		int n=0;

		try(Connection conn=DriverManager.getConnection(conexion,"root",password); //hace conexion
				PreparedStatement sentencia = conn.prepareStatement(cadena); //prepara candena
				){
			sentencia.setString(1, base); 
			ResultSet conjunto = sentencia.executeQuery(); //ResultSet: devuelve conjunto de registros de la sentencia
			
			while(conjunto.next()) { //mientras que existan datos en el conjunto
				n=conjunto.getInt("NU"); //NU, parámetro de cadena
			}
			
			if(n!=0)return true; //si n es distinto de 0, existe la bbdd

		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return false;
		
	}
	
	//Comprueba si existe la tabla
	public static boolean ExisteTB(String base, String tb) { 
		String conexion="jdbc:mysql://localhost:3306/"; //conecta con mysql
		String cadena="select count(*) NU" 
				+ " from information_schema.tables"
				+ " where table_schema = ? and table_name = ?"; //Compueba si existe la bbdd y la tabla en mysql
		int n=0;

		try(Connection conn=DriverManager.getConnection(conexion,"root",password); //hace conexion
				PreparedStatement sentencia = conn.prepareStatement(cadena); //prepara candena
				){
			sentencia.setString(1, base); 
			sentencia.setString(2, tb); 
			ResultSet conjunto = sentencia.executeQuery(); //ResultSet: devuelve conjunto de registros de la sentencia
			
			while(conjunto.next()) { //mientras que existan datos en el conjunto
				n=conjunto.getInt("NU"); //NU, parámetro de cadena
			}
			
			if(n!=0)return true; //si n es distinto de 0, existe

		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return false;
		
	}
	
	public static void cambiarBBDD() throws IOException {
		System.out.println("Dime la BBDD ");
		String bd = sc.readLine();
		
		if(!ExisteBBDD(bd)) {
			CrearBBDD(bd);
			CrearTabla(bd);
			insertarFilas(bd);
		}
		bbdd=bd;
	}
	
	public static void Transferir() throws IOException {
		System.out.println("Indique el origen de la transacción.");
		String origen=sc.readLine();
		if(!ExisteBBDD(origen)) {
			System.out.println("La base de datos " + origen + " no existe");
			return;
		}
		
		System.out.println("Indique el destino de la transacción.");
		String destino=sc.readLine();
		if(!ExisteBBDD(destino)) {
			System.out.println("La base de datos " + destino + " no existe");
			return;
		}
		
		/************************************************************************************/
		String conexion="jdbc:mysql://localhost:3306/";
		
		//Mueve el dinero de un banco a otro
		String cadena1 = "update " +destino+".caja A set cantidad=cantidad+"
				+ "(select cantidad from "+origen+".caja B where A.moneda=B.moneda)";
		
		//Deja a 0 la cuenta (origen) desde la que se ha transferido el dinero
		String cadena2="update "+origen+".caja set cantidad=0";
		
		
		try{Connection conn=DriverManager.getConnection(conexion,"root",password); //hace conexion
				PreparedStatement sentencia1 = conn.prepareStatement(cadena1); //prepara candena
				PreparedStatement sentencia2 = conn.prepareStatement(cadena2);
			
			conn.setAutoCommit(false); //se hacen las dos sentencias o ninguna. No se ejecutan las dos líneas de abajo hasta que comprueba que las dos se pueden hacer. Tiene que estar false
			int row1=sentencia1.executeUpdate();
			int row2=sentencia2.executeUpdate();
			conn.commit(); //Ejecuta las dos sentencias de arriba
			return;

		}catch(SQLException e) {
			//conn.rollback();
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	public static void Realizar_Compra() throws NumberFormatException, IOException {
		TreeMap<Double, Integer> caja = new TreeMap<Double,Integer>();
		//Importe compra
		System.out.print("Dime el importe de la compra ");
		double importe = Double.valueOf(sc.readLine());
		//Ejemplo importe 235.50
		System.out.print("Dime la forma de pago ");
		String pago=sc.readLine();
		//Ejemplo 1-200#1-50
		
		double suma=0,moneda,devolver;
		int cantidad;
		
		//Lo que han pagado
		for(String item:pago.split("#")) {
			String partes2[]=item.split("-");
			moneda=Double.valueOf(partes2[1]);
			cantidad=Integer.valueOf(partes2[0]);
			if(isNatural(cantidad) && isExisteMoneda(moneda)) {
				caja.put(moneda, cantidad);
				suma += moneda*cantidad; //Lo que nos han pagado
			}
		}
		//Cambio
		devolver=General.redondear(suma-importe,2);
		if(devolver<0) { //Si han pagado menos de la compra
			System.out.println("Falta dinero " + Math.abs(devolver)); //Math.abs para que no salga número negativo
			return; //sale
		}
		//Sentencias mysql
		String query="update caja set cantidad=cantidad+? where moneda=?";
		String query1="select * from caja where moneda<=? and cantidad>0 order by moneda desc"; //recorre desde la cantidad igual y menor de la cantidad que tiene que devolver. Ej si devuelve 14.5, recorre del 10 hacia abajo
		
		//Conectar con bbdd
		String conexion="jdbc:mysql://localhost:3306/"+bbdd;
		try(Connection conn=DriverManager.getConnection(conexion,"root",password);
				PreparedStatement pu = conn.prepareStatement(query);
				PreparedStatement pu1 = conn.prepareStatement(query1);
				){
			conn.setAutoCommit(false); //Principio de la transacción
			
			for(Entry<Double,Integer> item: caja.entrySet()) {
				pu.setInt(1, item.getValue()); //cantidad
				pu.setDouble(2, item.getKey()); //moneda
				pu.addBatch(); //Añade al conn, hasta que no termina todo no se inserta 
			}
			int[] rows=pu.executeBatch(); //Devuelve la matriz de todas las insercciones. int[nº de billetes actualizados, en este ejemplo 1 de 200 y luego 1 de 50. Hay 1 billete más de los dos billetes en sql]
			
			if(devolver>=0 && devolver<0.01) { //hay que ponerlo así porque no da siempre 0 exacto
				System.out.println("Precio exacto");
				conn.commit(); //Hace los cambios en sql
				return; //Sale porque no hay que hacer cambio, han pagado importe exacto
			}
			
			//Query1
			pu1.setDouble(1, devolver);
			ResultSet r=pu1.executeQuery();
			String adevolver="A devolver " + devolver + "\n";
			int n;
			while(r.next() && devolver>0.009) {
				moneda=r.getDouble("MONEDA");
				cantidad=r.getInt("CANTIDAD");
				if(!(moneda>devolver || cantidad==0)) { 
					n= (int)(devolver/moneda); //Ej 14.5/10 = 10 - billete de 10. Si no hay billete de 10 14.5/5 = 2, si solo hay un billete de 5, devuelve 1 y monedas
					devolver=General.redondear(devolver-(n*moneda),2); //Ej ya se ha restado el 10, devuelver= 4.5
					pu.setInt(1, -n); //resta la cantidad de monedas que se devuelven
					pu.setDouble(2, moneda); // de la moneda que se devuelve
					adevolver += String.format("%2d %6.2f", n, moneda)+"\n";
					pu.addBatch();
				}
			}
			
			rows=pu.executeBatch();
			if(devolver>0.01) {
				System.out.println("No tengo cambio");
				conn.rollback(); // conn no hace ningún cambio porque no se puede devolver el cambio, la base de datos se queda como al principio
				return;
			}
			conn.commit();
			System.out.println(adevolver);

		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		bbdd="BANCO";
		//CrearBBDD();
		//CrearTabla();
		//insertarFilas();
		//Seleccion();
		//System.out.println(isExisteMoneda(500));
		//System.out.println(isExisteMoneda(33));
		//System.out.println(Montante() + "€");
		//Actualizar(50,33);
		//Seleccion();
		//double total=prueba("1-500#1-50");
		//System.out.println(total);
		//Seleccion();
		//System.out.println(isNatural(12.0));
		
		String t[]={"1.-Listar", "2.-Hacer compra", "3.-Introducir dinero", "4.-Transferir fondo", "5.-Cambiar BBDD", "6.-Salir"};
		int op;
		
		
		boolean salir=true;
		
		//Si no existe la bbdd crearla
		if(!ExisteBBDD(bbdd)) {
			CrearBBDD(bbdd);
			CrearTabla(bbdd);
			insertarFilas(bbdd);
		}
		
		
		while(salir) {
			System.out.println("Base de datos " + bbdd);
			op=General.menu(sc, t);
			switch(op) {
			case 1: //listar
				Seleccion();
				break;
			case 2: //hacer compra
				Realizar_Compra();
				break;
			case 3: //Introducir dinero
				Introducir_dinero();
				break;
			case 4: //Transferir dinero
				Transferir();
				break;
			case 5: //Cambiar BBDD
				cambiarBBDD();
				break;
			case 6: //salir
				salir=false;
				break;
			
			}
		}
		
		
		
	}
}
