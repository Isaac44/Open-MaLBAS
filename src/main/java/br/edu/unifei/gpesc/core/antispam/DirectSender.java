/*
 * Copyright (C) 2017 - GEPESC - Universidade Federal de Itajuba
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

package br.edu.unifei.gpesc.core.antispam;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Isaac C. Ferreira
 */
public class DirectSender extends Sender {

    private final String mServer;
    private final int mPort;

    public DirectSender(String server, int port) {
        mServer = server;
        mPort = port;
    }

    @Override
    public synchronized void sendMail(String mailFile, String from, String to, byte[] data, int dataLen) throws IOException {
        Socket socket = new Socket(mServer, mPort);

        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        dout.writeUTF(mailFile);
        dout.writeUTF(from);
        dout.writeUTF(to);
        dout.writeInt(dataLen);
        dout.write(data, 0, dataLen);
        dout.flush();

        socket.close();
    }
}
