package Clases;

import java.time.LocalDateTime;

public class Empleado {

	private Long id;
	private String nombre;
	private double salario;
	private LocalDateTime fecha;
	public Empleado(Long id, String nombre, double salario, LocalDateTime fecha) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.salario = salario;
		this.fecha = fecha;
	}
	public Long getId() {
		return id;
	}
	public String getNombre() {
		return nombre;
	}
	public double getSalario() {
		return salario;
	}
	public LocalDateTime getFecha() {
		return fecha;
	}
	@Override
	public String toString() {
		return "Empleado [id=" + id + ", nombre=" + nombre + ", salario=" + salario + ", fecha=" + fecha + "]";
	}



}
