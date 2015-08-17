/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.uniandes.edu.ecos.plataforma;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Camilo Marroquin
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Perro perro = new Perro();
        perro.setNombre("rex");
        perro.setEdad(2);
        perro.setRaza("pitbull");
        Perro perro2 = new Perro();
        perro2.setNombre("firulais");
        perro2.setEdad(2);
        perro2.setRaza("fifi");
        perro.setPapas(new ArrayList<Perro>());
        List<Perro> p = new ArrayList<>();
        p.add(perro2);
        perro.setPapas(p);
        
        PerroGrande perroGrande = Mapper.copyCompleto(perro, PerroGrande.class, true);
        System.out.println(perroGrande.getNombre() );
        System.out.println(perroGrande.getEdad() );
        System.out.println(perroGrande.getRaza());
        // TODO code application logic here
    }
    
}
class Perro {

    private String nombre;
    private String raza;
    private int edad;
    private List<Perro> papas;

    public List<Perro> getPapas() {
        return papas;
    }

    public void setPapas(List<Perro> papas) {
        this.papas = papas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

}

class PerroGrande {

    private String nombre;
    private String raza;
    private int edad;

    private List<Perro> papas;

    public List<Perro> getPapas() {
        return papas;
    }

    public void setPapas(List<Perro> papas) {
        this.papas = papas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

}