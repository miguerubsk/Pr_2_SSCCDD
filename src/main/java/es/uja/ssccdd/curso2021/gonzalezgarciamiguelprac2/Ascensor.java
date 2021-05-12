/*
 * Copyright (C) 2021 Miguel González García
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.uja.ssccdd.curso2021.gonzalezgarciamiguelprac2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Miguel González García
 */
public class Ascensor implements Runnable {

    //VARIABLES NECESARIAS
    private int id;
    private int direccion;
    private int planta;
    private ArrayList<Persona> personas;
    private Monitor mon;

    public Ascensor(Monitor m, int idd) { //INICIALIZACIÓN
        id = idd;
        mon = m;
        direccion = mon.SUBIDA;
        planta = mon.BAJO;
        personas = new ArrayList<Persona>();
    }

    public boolean addPersona(Persona p) { //AÑADIR PERSONA AL ASCENSOR
        personas.add(p);
        return true;
    }

    public Persona getPersona(int n) { //EXTRAER PERSONA DE LA LISTA
        return personas.get(n);
    }

    public void removePersona(int n) { //ELIMINAR PERSONA DE LA LISTA
        System.out.println("Persona " + personas.get(n).getID() + " se ha bajado del ascensor  " + id + " en la planta " + personas.get(n).getPlantaOrigen());
        personas.get(n).setAscensor(-1);
        personas.remove(n);
    }

    private int avanzar() { //METODO MOVIMIENTO ; SUBIDA Y BAJADA Y SI LLEGA AL ULTIMO O PRIMER PISO CAMBIO DE DIRECCIÓN
        if (planta == mon.NUM_PLANTAS) {
            direccion = 0;
        } else if (planta == mon.BAJO) {
            direccion = 1;
        }
        if (direccion == 1) {
            planta++;
        } else {
            planta--;
        }
        return planta;
    }

    private void esperaPlanta() throws InterruptedException {//SIMULACIÓN ESPERA
        TimeUnit.SECONDS.sleep(aleatorio(4, 6));
    }

    private int aleatorio(int a, int b) {//GENERADOR DE NUMERO ALEATORIO DENTRO DE UN RANGO
        return (int) Math.floor(Math.random() * (b - a + 1) + a);
    }

    public void impresionPersonas() {
        String s = "ASCENSOR (" + id + ") /// PLANTA (" + planta + ") /// ----> ";
        Iterator<Persona> it = personas.iterator();
        for (int i = 0; i < personas.size(); ++i) {
            Persona p = it.next();
            s = s + " Persona:" + p.getID() + "(Destino: " + p.getPlantaDestino() + ")    ";
        }
        System.out.println(s);
    }

    @Override
    public void run() {
        mon.addAscensor(this); //SE AÑADE EL ASCENSOR A LA LISTA DE ASCENSORES DEL MONITOR
        boolean ejecucion = true;
        while (ejecucion) {
            try {
                mon.esperaPlanta(this);//ESPERA EN PLANTA HASTA QUE SE SUBAN LAS PERSONAS
                int b = mon.personasABajar(this);//NUMERO DE PERSONAS QUE QUIEREN BAJARSE
                if (b > 0) {
                    mon.bajarPersonas(this);//DESBLOQUEAMOS SEMAFORO BAJADA(PERSONA)
                }
                mon.entradaPersonas(id);//DESBLOQUEAMOS  N VECES EL SEMAFORO PARA LA ENTRADA DE PERSONAS // N->NUMERO DE PERSONAS QUE DESEAN ENTRAR (CONTROLADO CON UN MAXIMO)
                esperaPlanta();//SIMULACIÓN ESPERA CIERRE DE PUERTAS
                impresionPersonas();//IMPRESIÓN ACTUAL SEMAFOROS
                System.out.println("--------------------------------------------------------------------------------------------");
                avanzar();//SIGUIENTE PLANTA
            } catch (InterruptedException ex) {
                System.out.println("Paramos el ascensor  " + id);
                ejecucion = false;
            }
        }
    }

    //GETTERS Y SETTERS A VARIABLES PRIVADAS
    public int personas() {
        return personas.size();
    }

    public int getDireccion() {
        return direccion;
    }

    public int getPlanta() {
        return planta;
    }

    public int getId() {
        return id;
    }

    public List<Persona> getPersonas() {
        return personas;
    }

    public Monitor getMon() {
        return mon;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDireccion(int direccion) {
        this.direccion = direccion;
    }

    public void setPlanta(int planta) {
        this.planta = planta;
    }

    public void setPersonas(ArrayList<Persona> personas) {
        this.personas = personas;
    }

    public void setMon(Monitor mon) {
        this.mon = mon;
    }

}
