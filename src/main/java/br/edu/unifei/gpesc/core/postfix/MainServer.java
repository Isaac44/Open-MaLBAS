/*
 * Copyright (C) 2016 - GEPESC - Universidade Federal de Itajuba
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

import br.edu.unifei.gpesc.core.antispam.AntiSpamFactory;
import java.util.LinkedList;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.server.SMTPServer;

/**
 *
 * @author isaac
 */
public class MainServer {

    private static class HandlerFactory implements MessageHandlerFactory {

        private final Object LOCK = new Object();

        private final Storage mStorage;
        private final AntiSpamFactory mFactory;
        private final int mMaxMailLength;

        private final LinkedList<DataHandler> mRecycleHandlers = new LinkedList<DataHandler>();

        public HandlerFactory(Storage storage, AntiSpamFactory factory, int maxMailLength) {
            mStorage = storage;
            mFactory = factory;
            mMaxMailLength = maxMailLength;
        }

        private DataHandler getHandler() {
            if (mRecycleHandlers.isEmpty()) {
                return new DataHandler(mStorage, mFactory.newAntiSpam(), mMaxMailLength, mHandlerCallback);
            } else {
                return mRecycleHandlers.removeLast();
            }
        }

        private void recycleHandler(DataHandler handler) {
            mRecycleHandlers.addLast(handler);
        }

        @Override
        public MessageHandler create(MessageContext mc) {
            synchronized (LOCK) {
                return getHandler();
            }
        }

        private final DataHandler.Callback mHandlerCallback = new DataHandler.Callback() {
            @Override
            public void handlerFinished(DataHandler handler) {
                synchronized (LOCK) {
                    recycleHandler(handler);
                }
            }
        };

    }

    public static void start(Storage storage, AntiSpamFactory factory, int maxMailLen) {
        SMTPServer smtpServer = new SMTPServer(new HandlerFactory(storage, factory, maxMailLen));

        smtpServer.setSoftwareName("SAS - GPESC AntiSpam");

        smtpServer.setHostName("localhost");
        smtpServer.setPort(10025);
        smtpServer.start();
    }

}
