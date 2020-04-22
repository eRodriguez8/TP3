package com.example.a38464978.myapplication;

public class Ejemplo {
	// private variables
	int _id;
	int codigo;
	String nombre;

	public Ejemplo() {
		this.codigo = 0;
		this.nombre = "";
	}

	public Ejemplo(int codigo, String nombre) {
		this.codigo = codigo;
		this.nombre = nombre;
	}

	public Ejemplo(int _id, int codigo, String nombre) {
		this._id = _id;
		this.codigo = codigo;
		this.nombre = nombre;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
