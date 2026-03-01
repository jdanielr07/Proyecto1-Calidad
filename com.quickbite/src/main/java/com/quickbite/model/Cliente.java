package com.quickbite.model;

/**
 * Representa un cliente registrado en la plataforma QuickBite.
 */
public class Cliente {

    private String id;
    private String nombre;
    private String email;
    private String telefono;

    public Cliente(String id, String nombre, String email, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    @Override
    public String toString() {
        return "Cliente{id='" + id + "', nombre='" + nombre + "', email='" + email + "'}";
    }
}