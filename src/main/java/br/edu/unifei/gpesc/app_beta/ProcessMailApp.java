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
package br.edu.unifei.gpesc.app_beta;

import br.edu.unifei.gpesc.core.modules.NeuralCharacteristic;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author isaac
 */
public class ProcessMailApp {

    public static void main(String[] args) throws IOException {
//        int messageSize = Integer.parseInt(args[0]) * 2;
        int messageSize = 1024*1024;

        FileWriter writer = new FileWriter("/tmp/SocketSAS");
        writer.write("\nAbrindo socket..."); writer.flush();

        Socket socket = new Socket("localhost", 34645);

        writer.write("ok!\nLendo buffer..."); writer.flush();

        byte[] buffer = new byte[messageSize];
        int readed = System.in.read(buffer);

        writer.write("ok!\nEnviando buffer..."); writer.flush();

        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(buffer, 0, readed);
//        outputStream.close(); // necessario para que a "MailProcessor" finalize


        writer.write("ok!"); writer.flush();

        // resposta
        try {
            int result = socket.getInputStream().read();

            writer.write("Result = " + NeuralCharacteristic.values()[result].NAME);
            writer.flush();

            System.exit((result == NeuralCharacteristic.SPAM.ordinal()) ? 2 : 0);
        } catch (IOException e) {
            writer.write("ERRO: " + e.getMessage());
        }

        writer.close();
    }

}
