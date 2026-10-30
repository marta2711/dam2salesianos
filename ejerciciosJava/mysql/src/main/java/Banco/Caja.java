package Banco;

public class Caja {
	
	private double moneda;
	private int cantidad;
	
	
	public Caja(double moneda, int cantidad) {
		super();
		this.moneda = moneda;
		this.cantidad = cantidad;
	}


	public double getMoneda() {
		return moneda;
	}


	public int getCantidad() {
		return cantidad;
	}


	@Override
	public String toString() {
		return "Caja [moneda=" + moneda + ", cantidad=" + cantidad + "]";
	}
	
	

}
