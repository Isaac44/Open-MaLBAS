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
import br.edu.unifei.gpesc.util.TraceLog;
import br.edu.unifei.gpesc.util.TransactionalInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Isaac C. Ferreira
 */
public class StorageService {

    public enum Type {
        SPAM, BACKUP
    }

    /**
     * Where and how the e-mail will be saved.
     */
    private final Storage mStorage;

    private final int mPort;

    private final ExecutorService mClientExecutor = Executors.newFixedThreadPool(10);
    private final ExecutorService mStorageExecutor;
    private final List<ClientHandler> mRecycler = new LinkedList<ClientHandler>();

    /**
     *
     * @param port This service SMTP port.
     * @param storage
     * @param asyncStore
     */
    public StorageService(Storage storage, int port, boolean asyncStore) {
        mStorage = storage;
        mPort = port;
        mStorageExecutor = asyncStore ? Executors.newFixedThreadPool(10) : null;
    }

    public static StorageService createFrom(Configuration c, Type type) {
        String sType = (type == Type.SPAM) ? "SPAM" : "BACKUP";
        String path = c.getProperty("STORAGE_" + sType + "_FOLDER", null);
        Storage storage = (path != null) ? new Storage(new File(path)) : null;

        int port = c.getIntegerProperty("STORAGE_" + sType + "_PORT");

        return new StorageService(storage, port, true);
    }

    /**
     * Start this service.
     * @throws java.io.IOException
     */
    public void startService() throws IOException {
        ServerSocket server = new ServerSocket(mPort);

        while (true) {
            try {
                Socket socket = server.accept();

                ClientHandler handler = getHandler();
                handler.setClient(socket);

                mClientExecutor.execute(handler);
            }
            catch (IOException e) {
                TraceLog.logE(e);
            }
        }

    }

    private ClientHandler getHandler() {
        synchronized (mRecycler) {
            return mRecycler.isEmpty() ? new ClientHandler() : mRecycler.remove(0);
        }
    }

    private void recycleHandler(ClientHandler handler) {
        synchronized (mRecycler) {
            mRecycler.add(handler);
        }
    }

    private class ClientHandler implements Runnable {

        private final TransactionalInputStream mTStream = new TransactionalInputStream(1024 * 1024, 8 * 1024);

        private Socket mmSocket;

        public void setClient(Socket socket) {
            mmSocket = socket;
        }

        @Override
        public void run() {
            try {
                mTStream.copyData(mmSocket.getInputStream());
                silentClose(mmSocket);

                DataInputStream din = new DataInputStream(mTStream);
                final String fileName = din.readUTF();
                String from = din.readUTF();
                final String to = din.readUTF();
                final int dataLen = din.readInt();

                // Async
                if (mStorageExecutor != null) {
                    mStorageExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            mStorage.silentStore(fileName, to, mTStream.getData(), mTStream.getOffset(), dataLen);
                            recycleHandler(ClientHandler.this);
                        }
                    });
                }
                // Sync
                else {
                    mStorage.silentStore(fileName, to, mTStream.getData(), mTStream.getOffset(), dataLen);
                    recycleHandler(this);
                }


            }
            catch (IOException e) {
                TraceLog.logE(e);
            }
        }
    }

    public static void silentClose(Socket socket) {
        try {
            socket.close();
        } catch (IOException ignore) {
        }
    }
}
