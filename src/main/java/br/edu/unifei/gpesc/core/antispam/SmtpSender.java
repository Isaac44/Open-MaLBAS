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

import br.edu.unifei.gpesc.util.TraceLog;
import java.io.IOException;
import org.subethamail.smtp.client.SmartClient;

/**
 *
 * @author Isaac C. Ferreira
 */
public class SmtpSender {

    private final SmartClient mClient;

    public SmtpSender(String server, int port) throws IOException {
        mClient = new SmartClient(server, port, "localhost");
    }

    public synchronized void sendMail(String from, String to, byte[] data, int dataLen) throws IOException {
        mClient.from(from);
        mClient.to(to);

        mClient.dataStart();
        mClient.dataWrite(data, dataLen);
        mClient.dataEnd();
    }

    public synchronized void silentSendMail(String from, String to, byte[] data, int dataLen) {
        try {
            sendMail(from, to, data, dataLen);
        } catch (IOException e) {
            TraceLog.logE(e);
        }
    }
}