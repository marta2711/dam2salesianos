package Clases;

import java.io.BufferedReader;
import java.io.IOException;

public class General {
	
	public static int menu(BufferedReader sc, String op[]) { //throws NumberFormatException, IOException  //se le pasa un escaner y un vector con las opciones, quitamos throws para tratarlo desde dentro, no desde fuera
		// ={"1.-Listar", "2.-Hacer compra", "3.-Salir"
		
		int n=0;
		
		do {
			for(String item:op) System.out.println(item); //for que recorra las opciones y las muestre en pantalla
			System.out.print("Elija una de ellas ");
			
			try { // poniendo el error aquí dentro, imprime el error pero no sale del menú
				n=Integer.valueOf(sc.readLine()); //si utilizamos Buffered no se pone nextLine(), importar primera opción
			}catch(Exception e) {
				System.out.println("Número entero");
			}
			 
			
		}while(n<1|| n>op.length);
		return n;
	}
	
	public static double redondear(double n, int d) { //cantidad a devolver, números de decimales que queremos. Ej 123,456 redondear a dos decimales = 123,46
		double p=Math.pow(10, d); 
		return Math.round(n*p)/p; //Redondea los decimales a dos 
	}

}
