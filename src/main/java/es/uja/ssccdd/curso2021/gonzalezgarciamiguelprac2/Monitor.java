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
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Miguel González García
 */
public class Monitor {

    
    public final int BAJO = 0;
    public final int NUM_PLANTAS = 4;
    public final int SUBIDA = 1;
    public final int BAJADA = 0;

    
    private Semaphore[][] controlSubida;
    private ArrayList<Semaphore> controlBajada;
    private ArrayList<Ascensor> listaA;
    private ArrayList<Semaphore> bloqueoAscensorPlanta;
    private int NUM_ASCENSORES;
    private int NUM_PERSONAS;
    private int NUM_OPERACIONES;
    private int CAPACIDAD_ASCENSOR;
    private int fin;
    private Semaphore finS;

    
    public Monitor(int nPers, int nAscensores) {
        CAPACIDAD_ASCENSOR = 5;
        fin = 0;
        NUM_ASCENSORES = nAscensores;
        NUM_PERSONAS = nPers;
        finS = new Semaphore(0);
        controlSubida = new Semaphore[2][NUM_PLANTAS + 1];
        controlBajada = new ArrayList<Semaphore>(NUM_PLANTAS);
        for (int i = 0; i < 2; i++) {
            for (int z = 0; z < NUM_PLANTAS + 1; z++) {
                controlSubida[i][z] = new Semaphore(0);
            }
        }
        for (int z = 0; z < NUM_PLANTAS; z++) {
            controlBajada.add(new Semaphore(0));
        }
        listaA = new ArrayList<Ascensor>(NUM_ASCENSORES);
        bloqueoAscensorPlanta = new ArrayList<Semaphore>(NUM_ASCENSORES);
        for (int i = 0; i < NUM_ASCENSORES; i++) {
            bloqueoAscensorPlanta.add(new Semaphore(1));
            listaA.add(new Ascensor(this, i));
        }
    }

    public void addAscensor(Ascensor a) {
        listaA.set(a.getId(), a);
    }

    public void esperarAscensor(Persona p) {
        try {
            controlSubida[p.getDireccion()][p.getPlantaOrigen()].acquire();         
            subirPersonaAscensor(p);
        } catch (InterruptedException ex) {
            System.out.println("Fallo sección crítica de espera de una persona");
        }
    }

    public void bajarPersona(Persona p) throws InterruptedException {
        controlBajada.get(p.getPlantaDestino()).acquire();
    }

    public void bajarPersonas(Ascensor a) {
        for (int i = 0; i < a.personas(); i++) {
            Persona pp = a.getPersona(i);
            if (pp.getPlantaDestino() == a.getPlanta()) {
                bloqueoAscensorPlanta.get(a.getId()).release();
                pp.setPlantaOrigen(pp.getPlantaDestino());
                controlBajada.get(pp.getPlantaOrigen()).release();
                a.removePersona(i);
                i--;
            }
        }
    }

    
    public synchronized void subirPersonaAscensor(Persona p) throws InterruptedException {
        for (int i = 0; i < listaA.size(); i++) {
            if ((listaA.get(i).getPlanta() == p.getPlantaOrigen()) && (listaA.get(i).getDireccion() == p.direccion())
                    && (listaA.get(i).personas() < CAPACIDAD_ASCENSOR) && p.getAscensor() == -1) {
                listaA.get(i).addPersona(p);
                p.setAscensor(i);
                int n = listaA.get(i).personas();
                System.out.println("Persona " + p.getID() + " subida al ascensor " + p.getAscensor());
            }
        }
    }

    
    public void entradaPersonas(int idasc) {
        Ascensor a = listaA.get(idasc);
        int acceso = CAPACIDAD_ASCENSOR - a.personas();
        if (a.getPlanta() == 0) {
            a.setDireccion(1);
        } else if (a.getPlanta() == NUM_PLANTAS - 1) {
            a.setDireccion(0);
        }
        boolean suficientesPersonas = controlSubida[a.getDireccion()][a.getPlanta()].getQueueLength() >= acceso;
        boolean personasEnEspera = controlSubida[a.getDireccion()][a.getPlanta()].getQueueLength() > 0;
        int cargar = 0;
        if (personasEnEspera) {
            if (suficientesPersonas) {
                cargar = acceso;
            } else {
                cargar = controlSubida[a.getDireccion()][a.getPlanta()].getQueueLength();
            }
            for (int i = 0; i < cargar; i++) {
                controlSubida[a.getDireccion()][a.getPlanta()].release();
            }
        }
        bloqueoAscensorPlanta.get(a.getId()).release();
    }

    
    public void esperaPlanta(Ascensor a) throws InterruptedException {
        bloqueoAscensorPlanta.get(a.getId()).acquire();
    }


    public int personasABajar(Ascensor a) {
        int cont = 0;
        for (int i = 0; i < a.personas(); i++) {
            if (a.getPersona(i).getPlantaDestino() == a.getPlanta()) {
                cont++;
            }
        }
        return cont;
    }

    
    public void bloquear() {
        try {
            finS.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public void desbloquear() {
        finS.release(1);
    }

    public void fin() {
        fin++;
        if (fin == NUM_PERSONAS) {
            desbloquear();
        }
    }

    
    public int getNUM_ASCENSORES() {
        return NUM_ASCENSORES;
    }

    public int getCAPACIDAD_ASCENSOR() {
        return CAPACIDAD_ASCENSOR;
    }

    public int getNUM_PERSONAS() {
        return NUM_PERSONAS;
    }

    public int getNUM_OPERACIONES() {
        return NUM_OPERACIONES;
    }

    public void setNUM_ASCENSORES(int NUM_ASCENSORES) {
        this.NUM_ASCENSORES = NUM_ASCENSORES;
    }

    public void setCAPACIDAD_ASCENSOR(int CAPACIDAD_ASCENSOR) {
        this.CAPACIDAD_ASCENSOR = CAPACIDAD_ASCENSOR;
    }

    public void setNUM_PERSONAS(int NUM_PERSONAS) {
        this.NUM_PERSONAS = NUM_PERSONAS;
    }

    public void setNUM_OPERACIONES(int NUM_OPERACIONES) {
        this.NUM_OPERACIONES = NUM_OPERACIONES;
    }

}
