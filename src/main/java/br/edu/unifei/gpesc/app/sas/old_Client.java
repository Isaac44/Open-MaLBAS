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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
//import org.apache.commons.io.IOUtils;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class old_Client {



    private static void sendString(OutputStream outStream, String str) throws IOException {
        byte[] bytes = str.getBytes();

        // write length
        outStream.write(bytes.length >> 24);
        outStream.write(bytes.length >> 16);
        outStream.write(bytes.length >>  8);
        outStream.write(bytes.length);

        // write data
        outStream.write(bytes);
    }

    public static int run(String userAddress) {
        try {
            // open connection
            Socket socket = new Socket("localhost", 34645);

            // send mail data
            BufferedOutputStream outStream = new BufferedOutputStream(socket.getOutputStream());

            // send user email
            sendString(outStream, userAddress);

            // send mail
//            IOUtils.copy(System.in, outStream);

            // flush
            outStream.flush();

            // get process result
            return socket.getInputStream().read();
        }
        catch (IOException ignore) {}

        return 0; // not happen
    }
}
