package bdScott;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
public class menu {
	
	//Variables estáticas
	static String bbdd = "bd_scott";
	static String password = "cfgs";
	static String conexion="jdbc:mysql://localhost:3306/" + bbdd;
	static BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
	
	public static void listarEmpleados() {
		String cadena = "SELECT * FROM EMP";
		try(Connection conn=DriverManager.getConnection(conexion,"root", password); 
				PreparedStatement sentencia = conn.prepareStatement(cadena);
				){
			ResultSet conjunto = sentencia.executeQuery(); //ResusltSet:resultado del select
			
			// Cabecera
			System.out.printf("%5s %10s %10s %5s %10s %10s %5s %12s\n",
					"EMPNO", "ENAME", "JOB", "MGR", "SAL", "COMM", "DEPT", "HIREDATE");
			System.out.println("--------------------------------------------------------------------------");
			
			while(conjunto.next()) { //recorre ResultSet
				System.out.printf("%5d %10s %10s %5d %10.2f %10.2f %5d %12s\n",
						conjunto.getInt("EMPNO"), //Recorre el resultado lee cada columna
						conjunto.getString("ENAME"),
						conjunto.getString("JOB"),
						conjunto.getInt("MGR"),
						conjunto.getFloat("SAL"),
						conjunto.getFloat("COMM"),
						conjunto.getInt("DEPTNO"),
						conjunto.getDate("HIREDATE").toString()
						);
			}
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void insertarEmpleados() {	
		String cadena="insert into emp(empno, ename, job, mgr, sal, comm, deptno, hiredate) values (?,?,?,?,?,?,?,?)";
		try( Connection conn=DriverManager.getConnection(conexion,"root",password);
				PreparedStatement sentencia=conn.prepareStatement(cadena);
				){

			int empno = Siguiente_empno(); //MAX(EmpleadoNumero)+1
			sentencia.setInt(1, empno);

			System.out.println("Introducir nombre empleado");
			String ename = introducirNombre();
			sentencia.setString(2, ename);

			System.out.println("Introducir trabajo"); //Lista trabajos, puede ser nulo
			String job = elegirTrabajo(); 
			sentencia.setString(3, job);

			System.out.println("Introducir manager"); //Número empleado del jefe inmediato, manager. Lista de empleados sin nulos.
			int mgr = elegirMGR();
			sentencia.setInt(4, mgr);

			float sal;
			do {
				System.out.println("Introducir salario"); //Número positivo
				sal = Float.parseFloat(sc.readLine());
				if(sal<0) {
					System.out.println("No se puede introducir salario negativo.");
				}
			}while(sal<0);
			sentencia.setFloat(5, sal);

			float comm;
			do {
				System.out.println("Introducir comisión"); //Número positivo
				comm = Float.parseFloat(sc.readLine());
				if(comm<0) {
					System.out.println("No se puede introducir comisión negativa.");
				}
			}while(comm<0);
			sentencia.setFloat(6, comm);

			System.out.println("Introducir número departamento"); //Lista, puede ser nulo
			Integer deptno=elegirDept();
			//Si la sentencia es nula imprime null
			if(deptno==null) {
				sentencia.setNull(7, java.sql.Types.INTEGER); //Constante que indica el tipo SQL de valor nulo
			}else{
				sentencia.setInt(7, deptno);
			}

			java.sql.Date date = fechaContrato();
			sentencia.setDate(8, date);

			sentencia.executeUpdate(); //ejecuta la cadena (insert)
			System.out.println("Empleado introducido correctamente");

		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static int Siguiente_empno() {
		String cadena="select max(empno)+1 ne from emp";
		int n=-1; //si hay error devuelve -1
		
		try(Connection conn=DriverManager.getConnection(conexion,"root",password); //hace conexion
				PreparedStatement sentencia = conn.prepareStatement(cadena); //prepara candena
				){
			ResultSet conjunto = sentencia.executeQuery(); //ResultSet: devuelve conjunto de registros de la sentencia
			while(conjunto.next()) n=conjunto.getInt("ne");
			
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return n;
	}
	
	//Comprueba si existe empleado, se utiliza en actualizarEmpleado y borrarEmpleado
	public static boolean ExisteEmpleado(int num, String name) {
		String cadena = "SELECT * FROM EMP WHERE EMPNO=? AND ENAME=?";
		
		try(Connection conn=DriverManager.getConnection(conexion,"root",password); //hace conexion
				PreparedStatement sentencia = conn.prepareStatement(cadena); //prepara candena
				){
			sentencia.setInt(1, num);
			sentencia.setString(2, name);
			ResultSet conjunto = sentencia.executeQuery(); //ResultSet: devuelve conjunto de registros de la sentencia
			return conjunto.next(); //true si hay al menos una fila
			
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	//Comprueba si la fecha tiene el formato java.sql.Date (yyyy-mm-dd). Bucle.
	public static java.sql.Date fechaContrato() throws IOException{
		java.sql.Date date=null;
		do {
			System.out.println("Introducir fecha de contrato (yyyy-mm-dd)");
			String hiredate = sc.readLine().trim();
			try {
				date = java.sql.Date.valueOf(hiredate);
			} catch (IllegalArgumentException e) {
				System.out.println("El formato de fecha es incorrecto. Formato correcto: yyyy-mm-dd");
			}
			
		}while(date==null);
		return date;
	}

	public static void ActualizarEmpleado() {
		//Obtiene los valores actuales
		String mostrar = "SELECT JOB, MGR, SAL, COMM, DEPTNO, HIREDATE FROM EMP WHERE EMPNO=? AND ENAME=?";
		//Sentencia sql para actualizar los datos
		String actualizar="UPDATE EMP SET JOB=?, MGR=?, SAL=?, COMM=?, DEPTNO=?, HIREDATE=? WHERE EMPNO=? AND ENAME=?";

		try(Connection conn=DriverManager.getConnection(conexion,"root",password); //hace conexion
				PreparedStatement sentenciaActualizar = conn.prepareStatement(actualizar);
				PreparedStatement sentenciaMostrar = conn.prepareStatement(mostrar); //prepara candena
				){

			// Pregunta por el número y nombre del empleado que se quiere actualizar
			// Pregunta por el nombre para no actualizar ningún empleado por error
			System.out.println("Introducir número empleado");
			int empno = Integer.parseInt(sc.readLine());
			sentenciaActualizar.setInt(7, empno);
			System.out.println("Introducir nombre empleado");
			String ename = introducirNombre();
			sentenciaActualizar.setString(8, ename);

			//Si el empleado existe pregunta por el dato que se quiere actualizar
			if(ExisteEmpleado(empno, ename)) {
				sentenciaMostrar.setInt(1, empno);
				sentenciaMostrar.setString(2, ename);
				ResultSet conjunto = sentenciaMostrar.executeQuery();
				if(conjunto.next()) {
					//Carga los valores actuales en UPDATE, si no se cambian esos valores queda igual
					sentenciaActualizar.setObject(1, conjunto.getObject("JOB"));
					sentenciaActualizar.setObject(2, conjunto.getObject("MGR"));
					sentenciaActualizar.setObject(3, conjunto.getObject("SAL"));
					sentenciaActualizar.setObject(4, conjunto.getObject("COMM"));
					sentenciaActualizar.setObject(5, conjunto.getObject("DEPTNO"));
					sentenciaActualizar.setObject(6, conjunto.getObject("HIREDATE"));
				}

				//Submenú, pregunta por los datos a actualizar
				boolean salir=true;
				do {
					System.out.println("¿Qué quieres actualizar?");
					String t[]= {"1.- Trabajo", "2.- Manager", "3.- Salario", "4.- Comisión", "5.- Número de departamento", "6.- Fecha de contrato",
							"7.- Todos los datos","8.- Salir"};

					int opcion=GeneralM.menu(sc, t);

					switch(opcion) {
					case 1:
						System.out.println("Introducir trabajo");
						String job = elegirTrabajo();
						sentenciaActualizar.setString(1, job);
						break;
					case 2:
						System.out.println("Introducir manager");
						int mgr = elegirMGR();
						sentenciaActualizar.setInt(2, mgr);
						break;
					case 3:
						float sal;
						do {
							System.out.println("Introducir salario"); //Número positivo
							sal = Float.parseFloat(sc.readLine());
							if(sal<0) {
								System.out.println("No se puede introducir salario negativo.");
							}
						}while(sal<0);
						sentenciaActualizar.setFloat(3, sal);
						break;
					case 4:
						float comm;
						do {
							System.out.println("Introducir comisión"); //Número positivo
							comm = Float.parseFloat(sc.readLine());
							if(comm<0) {
								System.out.println("No se puede introducir comisión negativa.");
							}
						}while(comm<0);
						sentenciaActualizar.setFloat(4, comm);
						break;
					case 5:
						System.out.println("Introducir número departamento");
						Integer deptno = elegirDept();
						if(deptno==null) {
							sentenciaActualizar.setNull(5, java.sql.Types.INTEGER);
						}else {
							sentenciaActualizar.setInt(5, deptno);
						}
						break;
					case 6:
						java.sql.Date date = fechaContrato();
						sentenciaActualizar.setDate(6, date);
						break;
					case 7:
						System.out.println("Introducir trabajo");
						job = elegirTrabajo();
						sentenciaActualizar.setString(1, job);

						System.out.println("Introducir manager");
						mgr = elegirMGR();
						sentenciaActualizar.setInt(2, mgr);

						do {
							System.out.println("Introducir salario"); //Número positivo
							sal = Float.parseFloat(sc.readLine());
							if(sal<0) {
								System.out.println("No se puede introducir salario negativo.");
							}
						}while(sal<0);
						sentenciaActualizar.setFloat(3, sal);

						do {
							System.out.println("Introducir comisión"); //Número positivo
							comm = Float.parseFloat(sc.readLine());
							if(comm<0) {
								System.out.println("No se puede introducir comisión negativa.");
							}
						}while(comm<0);
						sentenciaActualizar.setFloat(4, comm);

						System.out.println("Introducir número departamento");
						deptno = elegirDept();
						if(deptno==null) {
							sentenciaActualizar.setNull(5, java.sql.Types.INTEGER);
						}else {
							sentenciaActualizar.setInt(5, deptno);
						}
						
						date = fechaContrato();
						sentenciaActualizar.setDate(6, date);
						break;
					case 8:
						salir=false;
						break;

					}
				} while(salir);

				//Actualiza los datos al salir
				sentenciaActualizar.executeUpdate();
				System.out.println("Se han realizado los cambios correctamente");
			
			//El empleado no existe
			}else {
				System.out.println("El empleado no existe");
			}
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void BorrarEmpleados() {
		String cadena="DELETE FROM EMP WHERE EMPNO=? AND ENAME=?"; //Se puede poner solo el empno
		try(Connection conn=DriverManager.getConnection(conexion,"root",password); //hace conexion
				PreparedStatement sentencia = conn.prepareStatement(cadena); //prepara candena
				){
			
			// Pregunta por el número y nombre del empleado que se quiere borrar 
			// Pregunta por el nombre para no borrar ningún empleado por error
			System.out.println("Introducir número empleado a borrar");
			int empno = Integer.parseInt(sc.readLine());
			sentencia.setInt(1, empno);
			System.out.println("Introducir nombre empleado a borrar");
			String ename = introducirNombre();
			sentencia.setString(2, ename);
			
			//Comprueba si existe el empleado introducido
			if(ExisteEmpleado(empno, ename)) {
				sentencia.executeUpdate();
				System.out.println("El empleado se ha eliminado correctamente");
			}else {
				System.out.println("El empleado introducido no existe");
			}
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void listarDepartamentos() {
		String cadena = "SELECT * FROM DEPT";
		try(Connection conn=DriverManager.getConnection(conexion,"root", password);
				PreparedStatement sentencia = conn.prepareStatement(cadena);
				){
			ResultSet conjunto = sentencia.executeQuery();
			
			// Cabecera
			System.out.printf("%-7s %-10s %5s\n", "DEPT", "DNAME", "LOC");
			System.out.println("---------------------------");
			while(conjunto.next()) {
				System.out.printf("%-5d %-10s %10s\n",
						conjunto.getInt("DEPTNO"),
						conjunto.getString("DNAME"),
						conjunto.getString("LOC")
						);
			}
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static int Siguiente_deptno() {
		String cadena="select max(deptno)+1 ne from dept";
		int n=-1;
		
		try(Connection conn=DriverManager.getConnection(conexion,"root",password); //hace conexion
				PreparedStatement sentencia = conn.prepareStatement(cadena); //prepara candena
				){
			ResultSet conjunto = sentencia.executeQuery(); //ResultSet: devuelve conjunto de registros de la sentencia
			while(conjunto.next()) n=conjunto.getInt("ne");
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return n;
	}

	public static void insertarDepartamentos() throws NumberFormatException, IOException {
		String cadena="insert into dept values(?,?,?)";
		
		int numero = Siguiente_deptno(); 
		
		System.out.println("Introducir nombre del departamento");
		String nombre = introducirNombre();
		
		System.out.println("Introducir ciudad del departamento");
		String ciudad = introducirNombre();
		
		try( Connection conn=DriverManager.getConnection(conexion,"root",password);
				PreparedStatement sentencia=conn.prepareStatement(cadena);
				){
			sentencia.setInt(1, numero);
			sentencia.setString(2, nombre);
			sentencia.setString(3, ciudad);
			sentencia.executeUpdate();
			System.out.println("Departamento añadido.");
			
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static boolean ExisteDepartamentos(int deptno) {
		String cadena = "SELECT * FROM DEPT WHERE DEPTNO=?";
		
		try(Connection conn=DriverManager.getConnection(conexion,"root",password); //hace conexion
				PreparedStatement sentencia = conn.prepareStatement(cadena); //prepara candena
				){
			sentencia.setInt(1, deptno);
			ResultSet conjunto = sentencia.executeQuery(); //ResultSet: devuelve conjunto de registros de la sentencia
			return conjunto.next();
			
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	public static void ActualizarDepartamentos() {
		//Obtiene los valores actuales
		String mostrar = "SELECT DNAME, LOC FROM DEPT WHERE DEPTNO=?";
		//Sentencia para actualizar los datos
		String actualizar="UPDATE DEPT SET DNAME=?, LOC=? WHERE DEPTNO=?";
		
		try(Connection conn=DriverManager.getConnection(conexion,"root",password); //hace conexion
				PreparedStatement sentenciaActualizar = conn.prepareStatement(actualizar); //prepara candena
				PreparedStatement sentenciaMostrar = conn.prepareStatement(mostrar);
				){
			
			//Pide número de departamento
			System.out.println("Introducir número departamento");
			int deptno = Integer.parseInt(sc.readLine());
			sentenciaActualizar.setInt(3, deptno);
			
			//Si existe el departamento, pregunta por los datos a actualizar
			if(ExisteDepartamentos(deptno)) {

				sentenciaMostrar.setInt(1, deptno);
				ResultSet conjunto = sentenciaMostrar.executeQuery();
				
				if(conjunto.next()) {
					//Carga los valores actuales en UPDATE, si no se cambian esos valores queda igual
					sentenciaActualizar.setObject(1, conjunto.getObject("DNAME"));
					sentenciaActualizar.setObject(2, conjunto.getObject("LOC"));
				}

					boolean salir=true;
					do {
						System.out.println("¿Qué datos quieres actualizar?");
						String t[]= {"1.- Nombre", "2.- Localidad","3.- Todos los datos","4.- Salir"};

						int opcion=GeneralM.menu(sc, t);

						//Submenú, pregunta por los datos a actualizar
						switch(opcion) {
						case 1:
							System.out.println("Introducir nombre del departamento");
							String dname = introducirNombre();
							sentenciaActualizar.setString(1, dname);
							break;
						case 2:
							System.out.println("Introducir localidad");
							String loc = introducirNombre();
							sentenciaActualizar.setString(2, loc);
							break;
						case 3:
							System.out.println("Introducir nombre departamento");
							dname = introducirNombre();
							sentenciaActualizar.setString(1, dname);
							System.out.println("Introducir localidad");
							loc = introducirNombre();
							sentenciaActualizar.setString(2, loc);
							break;
						case 4:
							salir=false;
							break;
						}

					}while(salir);

					//Actualiza los datos al salir
					sentenciaActualizar.executeUpdate();
					System.out.println("Se han realizado los cambios correctamente");
					
				//El departamento no existe	
				}else {
					System.out.println("El número de departamento introducido no existe");
				}

		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void BorrarDepartamentos() {
		String cadena="DELETE FROM DEPT WHERE DEPTNO=?";
		try(Connection conn=DriverManager.getConnection(conexion,"root",password); //hace conexion
				PreparedStatement sentencia = conn.prepareStatement(cadena); //prepara candena
				){
			
			//Pide número de departamento a borrar
			System.out.println("Introducir número de departamento a borrar");
			int deptno = Integer.valueOf(sc.readLine());
			sentencia.setInt(1, deptno);
			
			//Si el departamento existe lo elimina
			if(ExisteDepartamentos(deptno)) {
				sentencia.executeUpdate();
				System.out.println("El departamento se ha eliminado correctamente");
			//Si no existe imprime mensaje
			}else {
				System.out.println("El departamento no existe");
			}
			//También se puede poner así sin ExisteDept
			/*int row=sentencia.executeUpdate();
				if(row==0) {
					System.out.println("El departamento se ha eliminado correctamente");
				}else {
				System.out.println("El departamento no existe");
				}*/
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static ArrayList<String> submenuDept() {
		//Obtiene nombre, número de departamento y null
		String cadena ="(SELECT DNAME, DEPTNO FROM DEPT ORDER BY DNAME)"
		+ " UNION "
		+ " (SELECT null, null)";
		
		//Guarda texto de cada fila
		String registro;
		//Lista de String
		ArrayList<String> lista = new ArrayList<String>();
		
		try(Connection conn=DriverManager.getConnection(conexion,"root", password);
				PreparedStatement sentencia = conn.prepareStatement(cadena);
				){
			
			ResultSet conjunto = sentencia.executeQuery();
			while(conjunto.next()) {
				//Nombre-número
				registro=conjunto.getString("DNAME")+"-"+conjunto.getInt("DEPTNO");
				//Si el trabajo es nulo imprime:
				if(conjunto.wasNull()) {
					registro="Sin departamento-null";
				}
				//Lo añade a la lista
				lista.add(registro);
			}
			
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return lista;
	}

	public static Integer elegirDept() {
		//Obtiene la lista nombre-número
		ArrayList<String> lista = submenuDept();
		//Devuelve opción elegida
		int op=GeneralM.menu(sc, lista);
		//Obtiene el texto, op -1 porque ArrayList empieza por 0
		String cadena=lista.get(op-1);
		//Se queda con lo que hay después del guión (número)
		String trab= cadena.substring(cadena.indexOf("-") +1);
		
		//Si el trabajo es null
		if(trab.equals("null")) {
			return null;
		}
		
		return Integer.valueOf(trab);
	}

	public static ArrayList<String> submenuTrabajo() {
		//Obtiene los trabajos sin repetir y null
		String cadena = "select * from "
				+ " (select distinct job from emp where job is not null order by 1 asc) a"
				+ " union "
				+ " select null";
		//Guarda texto de cada fila
		String registro;
		//Lista de String
		ArrayList<String> lista = new ArrayList<String>();
		
		try(Connection conn=DriverManager.getConnection(conexion,"root", password);
				PreparedStatement sentencia = conn.prepareStatement(cadena);
				){
			
			ResultSet conjunto = sentencia.executeQuery();
			while(conjunto.next()) {
				registro=conjunto.getString("JOB");
				lista.add(registro);
			}
			
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		return lista;
	}

	public static String elegirTrabajo() {
		//Obtiene la lista nombre-número
		ArrayList<String> lista = submenuTrabajo();
		//Devuelve opción elegida
		int op=GeneralM.menu(sc, lista);
		//Obtiene el texto, op -1 porque ArrayList empieza por 0
		String cadena=lista.get(op-1);
		
		return cadena;
	}

	public static ArrayList<String> submenuMGR() {
		//Obtiene el número y nombre del empleado ordenado por número ascendente
		String cadena = "SELECT EMPNO, ENAME FROM EMP ORDER BY EMPNO ASC";
		//Guarda texto de cada fila
		String registro;
		//Lista de String
		ArrayList<String> lista = new ArrayList<String>();
		
		try(Connection conn=DriverManager.getConnection(conexion,"root", password);
				PreparedStatement sentencia = conn.prepareStatement(cadena);
				){
			
			ResultSet conjunto = sentencia.executeQuery();
			while(conjunto.next()) {
				//Nombre-número
				registro=conjunto.getString("ENAME")+"-"+conjunto.getInt("EMPNO");
				lista.add(registro);
			}
			
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return lista;
	}

	public static int elegirMGR() {
		//Obtiene la lista nombre-número
		ArrayList<String> lista = submenuMGR();
		//Devuelve opción elegida
		int op=GeneralM.menu(sc, lista);
		//Obtiene el texto, op -1 porque ArrayList empieza por 0
		String cadena=lista.get(op-1);
		//Se queda con lo que hay después del guión (número) y lo convierte a int
		int mgr= Integer.valueOf(cadena.substring(cadena.indexOf("-") +1));
		
		return mgr;
	}
	
	//No permite insertar números en las variables: nombre del empleado, nombre y localidad de departamento
	public static String introducirNombre() throws IOException {
		String nombre;
		boolean valido;
		
		do {
			nombre=sc.readLine().trim(); //trim elimina espacios del principio y del final
			
			//Nombre puede contener una o más palabras formadas por letras separadas por un espacio. No permite números ni símbolos.
			valido= nombre.matches("[a-zA-ZñÑ]+( [a-zA-ZñÑ]+)*"); //+: acepta más de una letra, * :se pueda poner 0 o más palabras después de un espacio
			if(!valido) {
				System.out.println("El nombre no puede contener números ni símbolos");
				System.out.println("Introducir nombre");
			}
		}while(!valido);
		return nombre;
	}


	public static void main(String[] args) throws IOException {
		/*Menú para:
				1.- Listar empleados
				2.- Insertar empleados
				3.- Actualizar empleados
				4.- Borrar empleados
				5.- Listar departamentos
				6.- Insertar departamentos
				7.- Actualizar departamentos
				8.- Borrar departamentos
				9.- Salir
		 */
		
		String t[]= {"1.- Listar empleados", "2.- Insertar empleados", "3.- Actualizar empleados", "4.- Borrar empleados", "5.- Listar departamentos",
				"6.- Insertar departamentos", "7.- Actualizar departamentos", "8.- Borrar departamentos", "9.- Salir."};
		
		int opcion;
		boolean salir=true;
		
		while(salir) {
			System.out.println();
			System.out.println("***********************************************************************************");
			System.out.println("Base de datos " + bbdd);
			opcion=GeneralM.menu(sc, t);
			
			switch(opcion) {
			case 1:
				listarEmpleados();
				break;
			case 2:
				insertarEmpleados();
				break;
			case 3:
				ActualizarEmpleado();
				break;
			case 4:
				BorrarEmpleados();
				break;
			case 5:
				listarDepartamentos();
				break;
			case 6:
				insertarDepartamentos();
				break;
			case 7:
				ActualizarDepartamentos();
				break;
			case 8:
				BorrarDepartamentos();
				break;
			case 9:
				salir=false;
				break;
			}
		}
	}
}
