/*
 * Copyright (C) 2015 - GEPESC - Universidade Federal de Itajuba
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
package br.edu.unifei.gpesc.app.sas;

import br.edu.unifei.gpesc.app.Configuration;
import br.edu.unifei.gpesc.core.mlp.RunMlp;
import br.edu.unifei.gpesc.core.statistic.Characteristics;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class Server {

    private Grader mGrader;
    private final byte[] mClientData = new byte[100 * 1024 * 1024]; // 100 MiB buffer

    private Characteristics<String> createCharacteristics(String file, int len) throws FileNotFoundException {
        Scanner scan = new Scanner(new File(file));

        Characteristics<String> characteristics = new Characteristics<String>();
        for (int i=0; i<len; i++) {
            characteristics.insertData(scan.next());
            scan.nextLine();
        }

        return characteristics;
    }

    private void createGrader() throws IOException {
        System.out.println("Creating grader");

        Configuration c = Configuration.getInstance();

        String weightsFile = c.getProperty("WEIGHTS_FILE");
        String statisticsFile = c.getProperty("STATISTICS_FILE");

        RunMlp mlp = RunMlp.loadMlp(new File(weightsFile));
        Characteristics<String> characteristics =
                createCharacteristics(statisticsFile, mlp.getInputLayerLength());

        mGrader = new Grader(mlp, characteristics);
    }

    private void processClient(Socket socket) {
        try {
            int readed = socket.getInputStream().read(mClientData);
            int result = mGrader.processMail(new ByteArrayInputStream(mClientData, 0, readed));

            System.out.println("result = " + result);

            OutputStream outStream = socket.getOutputStream();
            outStream.write(result);
            outStream.flush();

            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() throws IOException {
        createGrader();

        ServerSocket serverSocket = new ServerSocket(34645);

        while (true) {
            System.out.println("Waiting connection...");
            Socket socket = serverSocket.accept();
            System.out.println("handling client");
            processClient(socket);
        }
    }


    // -------------------------------------------------------------------------
    // Test
    // -------------------------------------------------------------------------

    public static void main(String[] args) throws IOException {
        new Server().run();
    }

}
