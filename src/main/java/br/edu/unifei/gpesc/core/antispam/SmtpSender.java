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

import java.io.IOException;
import org.subethamail.smtp.client.SmartClient;

/**
 *
 * @author Isaac C. Ferreira
 */
public class SmtpSender extends Sender {

    public SmtpSender(String server, int port) {
        super(server, port);
    }

    @Override
    public synchronized void sendMail(String mailFile, String from, String to, byte[] data, int dataLen) throws IOException {
        SmartClient client = new SmartClient(mServer, mPort, "GPESC SAS");
        client.from(from);
        client.to(to);
        client.dataStart();
        client.dataWrite(data, dataLen);
        client.dataEnd();
        client.quit();
        client.close();
    }
}
