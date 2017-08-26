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
import br.edu.unifei.gpesc.evaluation.TimeMark;
import br.edu.unifei.gpesc.util.Configuration;
import br.edu.unifei.gpesc.util.TransactionalInputStream;
import java.io.File;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.server.SMTPServer;

/**
 *
 * @author Isaac C. Ferreira
 */
public class AntiSpamService {

    // classificar os e-mails e enviar para a storage correta
    private final Sender mBackupSender, mSpamSender;

    private final int mPort;

    /**
     * The SAS classificator .
     */
    private final AntiSpamFactory mAntiSpamFactory;

    public AntiSpamService(AntiSpamFactory asFactory, int port, Sender spamSender, Sender backupSender) {
        mSpamSender = spamSender;
        mBackupSender = backupSender;
        mPort = port;
        mAntiSpamFactory = asFactory;

        TimeMark.init(new File("TimeMark.log"));
    }

    private static boolean isDirect(String type) {
        if (type != null) {
            return type.toUpperCase().equals("DIRECT");
        }
        return false;
    }

    private static Sender createSender(Configuration c, String sType) {
        String server = c.getProperty("STORAGE_" + sType + "_SERVER", null);
        boolean direct = isDirect(c.getProperty("STORAGE_" + sType + "_SERVER", null));

        if (server != null) {
            int port = c.getIntegerProperty("STORAGE_" + sType + "_PORT");
            return direct ? new DirectSender(server, port) : new SmtpSender(server, port);
        } else {
            return null;
        }
    }

    public static AntiSpamService from(Configuration c) {
        Sender spamSender = createSender(c, "SPAM");
        Sender backupSender = createSender(c, "BACKUP");
        int port = c.getIntegerProperty("POSTFIX_PORT");
        return new AntiSpamService(AntiSpamFactory.from(c), port, spamSender, backupSender);
    }

    public void startService() {
        SMTPServer server = new SMTPServer(new HandlerFactory());
        server.setSoftwareName("SAS Anti-Spam");
        server.setHostName("SAS Anti-Spam");
        server.setPort(mPort);
        server.setMaxConnections(1000);
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
        protected void onDataReceived(String from, String to, TransactionalInputStream tin) {
            TimeMark.mark("2. Leitura dos dados do e-mail via MTA");

            AntiSpam.Result result = mmAntiSpam.processMail(tin);

            String mailFile = createEmailFileName();
            if (result == AntiSpam.Result.SPAM) {
                mSpamSender.silentSendMail(mailFile, from, to, tin.getData(), tin.getCount());
                TimeMark.mark("7.1. Armazena Spam");
            }

            mBackupSender.silentSendMail(mailFile, from, to, tin.getData(), tin.getCount());
            TimeMark.mark("7.2. Armazena Backup");
            TimeMark.finish(tin.getCount());
        }
    }

    /**
     * Creates a name for the email based on the current time.
     *
     * @return The hash of the current time in milliseconds.
     */
    private String createEmailFileName() {
        return Long.toUnsignedString(System.currentTimeMillis(), Character.MAX_RADIX) + ".eml";
    }
}
