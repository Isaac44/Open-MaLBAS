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

import br.edu.unifei.gpesc.core.postfix.RecyclerHandler;
import br.edu.unifei.gpesc.core.postfix.RecyclerHandlerFactory;
import br.edu.unifei.gpesc.util.Configuration;
import br.edu.unifei.gpesc.util.TraceLog;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.server.SMTPServer;

/**
 *
 * @author Isaac C. Ferreira
 */
public class AntiSpamService {

    // classificar os e-mails e enviar para a storage correta
    private final SmtpSender mBackupSender, mSpamSender;

    private final int mPort;

    /**
     * The SAS classficator.
     */
    private AntiSpamFactory mAntiSpamFactory;

    public AntiSpamService(AntiSpamFactory asFactory, int port, SmtpSender spamSender, SmtpSender backupSender) {
        mSpamSender = spamSender;
        mBackupSender = backupSender;
        mPort = port;
    }

    private static SmtpSender createSender(Configuration c, String sType) {
        String server = c.getProperty("STORAGE_" + sType + "_SERVER", null);
        if (server != null) {
            int port = c.getIntegerProperty("STORAGE_" + sType + "_PORT");
            try {
                return new SmtpSender(server, port);
            } catch (IOException e) {
                TraceLog.logE(e);
                throw new RuntimeException("Storage daemon can't be reached. host=" + server + ", port=" + port);
            }
        } else {
            return null;
        }
    }

    public static AntiSpamService from(Configuration c) {
        SmtpSender spamSender = createSender(c, "SPAM");
        SmtpSender backupSender = createSender(c, "BACKUP");
        int port = c.getIntegerProperty("POSTFIX_PORT");
        return new AntiSpamService(AntiSpamFactory.from(c), port, backupSender, spamSender);
    }

    public void startService() {
        SMTPServer server = new SMTPServer(new HandlerFactory());
        server.setSoftwareName("SAS Anti-Spam");
        server.setHostName("SAS Anti-Spam");
        server.setPort(mPort);
        server.start();
    }

    private class HandlerFactory extends RecyclerHandlerFactory {

        @Override
        protected MessageHandler createHandler() {
            return new Handler(this);
        }

    }

    private class Handler extends RecyclerHandler {

        private final AntiSpam mmAntiSpam;

        public Handler(RecyclerHandlerFactory factory) {
            super(factory);
            mmAntiSpam = mAntiSpamFactory.newAntiSpam();
        }

        @Override
        protected void onDataReceived(String from, String to, byte[] data, int dataLen) {
            AntiSpam.Result result = mmAntiSpam.processMail(new ByteArrayInputStream(data, 0, dataLen));

            if (result == AntiSpam.Result.SPAM) {
                mSpamSender.silentSendMail(from, to, data, dataLen);
            }

            mBackupSender.silentSendMail(from, to, data, dataLen);
        }

    }

}
