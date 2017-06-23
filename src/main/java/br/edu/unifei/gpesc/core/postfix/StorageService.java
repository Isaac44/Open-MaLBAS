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
package br.edu.unifei.gpesc.core.postfix;

import br.edu.unifei.gpesc.util.Configuration;
import java.io.File;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.server.SMTPServer;

/**
 *
 * @author Isaac C. Ferreira
 */
public class StorageService {

    public static final int TYPE_SPAM = 1;
    public static final int TYPE_BACKUP = 2;

    /**
     * The SMTP's hostname.
     */
    private static final String HOSTNAME = "SAS Storage";

    /**
     * The SMTP server used to communicate with other applications throught SMTP, like the SAS or
     * Postfix.
     */
    private final SMTPServer mServer;

    /**
     * Where and how the e-mail will be saved.
     */
    private final Storage mStorage;

    private final int mType;

    private final HandlerFactory mHandlerFactory;

    /**
     *
     * @param port This service SMTP port.
     * @param storage
     * @param type
     */
    public StorageService(Storage storage, int port, int type) {
        mStorage = storage;
        mType = type;

        SMTPServer server = mServer = new SMTPServer(mHandlerFactory = new HandlerFactory());
        server.setSoftwareName(HOSTNAME);
        server.setHostName(HOSTNAME);
        server.setPort(port);
    }

    public static StorageService createFrom(Configuration c, int type) {
        String backupPath = c.getProperty("STORAGE_BACKUP", null);
        File backupFolder = (backupPath != null) ? new File(backupPath) : null;

        String spamPath = c.getProperty("STORAGE_SPAM", null);
        File spamFolder = (spamPath != null) ? new File(spamPath) : null;

        Storage storage = new Storage(backupFolder, spamFolder, true);

        String sType = (type == TYPE_SPAM) ? "SPAM" : "BACKUP";

        int port = c.getIntegerProperty("STORAGE_" + sType + "_PORT");
        return new StorageService(storage, port, type);
    }

    /**
     * Start this service.
     */
    public void start() {
        mServer.start();
    }

    /**
     * Handles all new income connections.
     */
    private class HandlerFactory extends RecyclerHandlerFactory {

        @Override
        protected MessageHandler createHandler() {
            return new CustomHandler(this);
        }
    }

    /**
     * Handler for the income e-mail.
     */
    public class CustomHandler extends RecyclerHandler {

        public CustomHandler(RecyclerHandlerFactory factory) {
            super(factory);
        }

        @Override
        protected void onDataReceived(String from, String to, byte[] data, int dataLen) {
            mStorage.store(to, new String(data, 0, dataLen), mType == TYPE_SPAM);
            mHandlerFactory.recycleHandler(this); // callback
        }
    }
}
