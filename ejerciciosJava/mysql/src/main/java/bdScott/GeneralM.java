package bdScott;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;


public class GeneralM {

	//Se pasa BufferReader y vector con las opciones
	// ={"1.-Listar", "2.-Insertar"... "9.-Salir"
	public static int menu(BufferedReader sc, String op[]) {  

		int n=0;

		do {
			//for que recorre las opciones y las muestre en pantalla
			for(String item:op) System.out.println(item); 
			
			System.out.print("Elija una de ellas ");

			try { // poniendo el error aquí dentro, imprime el error pero no sale del menú
				n=Integer.valueOf(sc.readLine()); 
				
				//Si se pone un número fuera de rango imprime mensaje
				if(n<1|| n>op.length)
					System.out.println("La opción introducida está fuera del rango. Introduce un número de 1 al " + op.length);
				
			}catch(Exception e) {
				System.out.println("Debes introducir un número entero");
			}

		}while(n<1|| n>op.length);
		
		return n;
	}

	//Listas de sql (elegirTrabajo, elegirMGR, elegirDept)
	//1.-ANALYST, 2.-CLERK...
	public static int menu(BufferedReader sc, ArrayList<String> op) { 
		
		int n=0, c=0;

		do {
			c=0; //si se vuelve a imprimir el menú empieza desde 0
			
			//Recorre las opciones y las muestre en pantalla
			for(String item:op) System.out.println((++c)+" "+item); 
			
			System.out.print("Elija una de ellas ");


			try { // poniendo el error aquí dentro, imprime el error pero no sale del menú
				n=Integer.valueOf(sc.readLine());
				
				//Si se pone un número fuera de rango imprime mensaje
				if(n<1|| n>op.size())
					System.out.println("La opción introducida está fuera del rango. Introduce un número de 1 al " + op.size());
				
			}catch(Exception e) {
				System.out.println("Debes introducir un número entero");
			}

		}while(n<1|| n>op.size());
		
		return n;
	}


}
