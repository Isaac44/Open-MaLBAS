/*
 * Copyright (C) 2015 Universidade Federal de Itajuba
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
package br.edu.unifei.gpesc.app;

import br.edu.unifei.gpesc.sas.modules.NeuralCharacteristic;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author isaac
 */
public class ProcessMailSocket {

    public static void main(String[] args) throws IOException {

        // socket
        ServerSocket serverSocket = new ServerSocket(34645);
        Socket socket = null;

        System.out.print("Starting... "); System.out.flush();

        String basePath = "/home/isaac/Unifei/CodeStation/SistemaAntiSPAM_SAS/NN-Otavio/dat/";
        File statisticsFile = new File("/home/isaac/Unifei/Mestrado/SAS/Mail_Test/Febuary/clean/", "statistics.txt");
        ProcessMail processMail = new ProcessMail(basePath + "data.dat", basePath + "wfin.dat", statisticsFile);

        System.out.println("done.");

        NeuralCharacteristic result;

        byte[] buffer = new byte[1024*1024*10];
        int readed;

        while (true) {
            System.out.print("Waiting for connection..."); System.out.flush();

            try {
//                if (socket != null) socket.close();

                socket = serverSocket.accept();

                System.out.println(" client connected.");

                readed = socket.getInputStream().read(buffer);

                result = processMail.processMail(new ByteArrayInputStream(buffer, 0, readed));

                System.out.println("Process result: " + result.NAME);

                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(result.ordinal());
                outputStream.flush();
            }
            catch (IOException e) {
                socket = null;
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

}

