package com.ort.mirtalapsenzon.taller5.entities;

public class Persona {


    private int id;
    private String nombre;
    private String apellido;

    public Persona (String nombre, String apellido){

    if (nombre!=null || !nombre.isEmpty()){
        this.nombre = nombre;
    }else {
        throw new IllegalArgumentException("El Nombre no puede estar vacio");
    }
    if (apellido!=null || !apellido.isEmpty()){
        this.apellido = apellido;
    }else {
        throw new IllegalArgumentException("El apellido no puede estar vacio");
    }


    }

public Persona (int id, String nombre, String apellido){
        this.id=id;
        this.nombre=nombre;
        this.apellido=apellido;

}


    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }
    public int getId() {
        return id;
    }
}
