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

import java.util.concurrent.TimeUnit;

/**
 *
 * @author Miguel González García
 */
public class Persona implements Runnable {

    //VARIABLES NECESARIAS
    private int plantaOrigen;
    private int plantaDestino;
    private int max_operaciones;
    private int operacion;
    private int direccion;
    private int ascensor;
    private Monitor mon;
    private int ID;

    //INICALIZACIÓN
    public Persona(Monitor m, int id) {
        ID = id;
        plantaOrigen = 0;
        mon = m;
        plantaDestino = aleatorio(0, mon.NUM_PLANTAS - 1);
        max_operaciones = 3;
        operacion = 0;
        ascensor = -1;
        direccion = 1;
    }

    //NUMERO ALEATORIO DENTRO DE UN RANGO
    private int aleatorio(int a, int b) {
        return (int) Math.floor(Math.random() * (b - a + 1) + a);
    }

    //CALCULO DIRECCIÓN Y EVITAR QUE UNA PERSONA ELIJA DE PLANTA DESTINO LA MISMA EN LA QUE SE ENCUENTRA
    public int direccion() {
        while (plantaOrigen == plantaDestino) {
            plantaDestino = aleatorio(0, mon.NUM_PLANTAS - 1);
        }
        if (plantaOrigen < plantaDestino) {
            return 1;
        } else {
            return 0;
        }
    }

    //SIMULACIÓN DE UN TIEMPO DE TRABAJO EN LA PLANTA
    private void trabajoEnPlanta() throws InterruptedException {
        TimeUnit.SECONDS.sleep(aleatorio(5, 15));
    }

    //CALCULOS PREVIOS
    private void inicio() {
        if (max_operaciones == operacion) {
            plantaDestino = mon.BAJO;
        } else {
            plantaDestino = aleatorio(0, mon.NUM_PLANTAS - 1);
        }
        direccion = direccion();
    }

    //ESPERA PARA BAJARSE DEL ASCENSOR
    private void bajarme() throws InterruptedException {
        mon.bajarPersona(this);
    }

    @Override
    public void run() {
        while (plantaDestino != mon.BAJO) {
            try {
                inicio();
                mon.esperarAscensor(this);
                bajarme();
                trabajoEnPlanta();
                operacion++;
            } catch (InterruptedException ex) {
                System.out.println("Se ha averiado los ascensores y " + ID + " usa las escaleras");
            }
        }
        System.out.println("La persona " + ID + " ha salido del edificio.");
        mon.fin();
    }

    //GETTERS Y SETTERS
    public void setPlantaOrigen(int plantaOrigen) {
        this.plantaOrigen = plantaOrigen;
    }

    public void setPlantaDestino(int plantaDestino) {
        this.plantaDestino = plantaDestino;
    }

    public void setMax_operaciones(int max_operaciones) {
        this.max_operaciones = max_operaciones;
    }

    public void setOperacion(int operacion) {
        this.operacion = operacion;
    }

    public void setDireccion(int direccion) {
        this.direccion = direccion;
    }

    public void setAscensor(int ascensor) {
        this.ascensor = ascensor;
    }

    public void setMon(Monitor mon) {
        this.mon = mon;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getPlantaOrigen() {
        return plantaOrigen;
    }

    public int getPlantaDestino() {
        return plantaDestino;
    }

    public int getMax_operaciones() {
        return max_operaciones;
    }

    public int getOperacion() {
        return operacion;
    }

    public int getDireccion() {
        return direccion;
    }

    public int getAscensor() {
        return ascensor;
    }

    public Monitor getMon() {
        return mon;
    }

    public int getID() {
        return ID;
    }

}
