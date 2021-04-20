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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Miguel González García
 */
public class GonzalezGarciaMiguelPrac2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("El hilo principal empieza su ejecución");
        // Se crea el servicio executor
        ExecutorService executor = (ExecutorService) Executors.newFixedThreadPool(8);

        int personas = 8; //NUMERO DE PERSONAS QUE SE LANZARAN
        int ascensores = 2; //NUMERO DE ASCENSORES CON LOS QUE CONTAREMOS

        Monitor monitor = new Monitor(personas, ascensores);//MONITOR

        for (int i = 0; i < ascensores; i++) {
            executor.execute(new Ascensor(monitor, i));
        }

        for (int i = 0; i < personas; i++) {
            executor.execute(new Persona(monitor, i));
        }

        monitor.bloquear();
        System.out.println("############# CANCELANDO LOS ASCENSORES #############");
        executor.shutdownNow();
        System.out.println("Finaliza el hilo principal");

    }

}
