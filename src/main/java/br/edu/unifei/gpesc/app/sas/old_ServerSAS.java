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

import br.edu.unifei.gpesc.core.antispam.AntiSpam;
import br.edu.unifei.gpesc.core.antispam.AntiSpamWithMlp;
import br.edu.unifei.gpesc.core.postfix.Storage;
import br.edu.unifei.gpesc.core.statistic.Characteristics;
import br.edu.unifei.gpesc.mlp.Mlp;
import br.edu.unifei.gpesc.util.Configuration;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class old_ServerSAS {

    /**
     * The mail classificator.
     */
    private AntiSpam mClassificator;

    /**
     * The service to store the client mail data.
     */
    private final ExecutorService mStorageExecutor = Executors.newFixedThreadPool(1);

    /**
     * The Storage rules.
     */
    private final Storage mStorage;

    /**
     * The ultra-large mail buffer.
     */
    private final byte[] mClientData = new byte[100 * 1024 * 1024]; // 100 MiB buffer

    public old_ServerSAS() throws IOException {
//        mStorage = Storage.buildFromConfiguration();
        mStorage = null;
    }

    /**
     * Create the characteristics that are used to build the mail vector.
     * @param file The file with the features.
     * @param quantity The quantity of features to be used.
     * @return The Characteristics.
     * @throws FileNotFoundException
     */
    private Characteristics<String> createCharacteristics(String file, int quantity)
            throws FileNotFoundException
    {
        Scanner scan = new Scanner(new File(file));

        Characteristics<String> characteristics = new Characteristics<String>();
        for (int i=0; i<quantity; i++) {
            characteristics.insertData(scan.next());
            scan.nextLine();
        }

        return characteristics;
    }

    private void createCommunication(Configuration c) {
        int port = c.getIntegerProperty("POSTFIX_PORT");
        int maxConn = c.getIntegerProperty("POSTFIX_MAX_CONNECTIONS");
        String server = c.getProperty("POSTFIX_SERVER");



    }

    /**
     * Creates the classificator.
     * @throws IOException If any error occurs when openning the input files.
     */
    private void createClassificator() throws IOException {
        System.out.println("Creating classificator");

        Configuration c = new Configuration("config" + File.separator + "sas.properties");

        String weightsFile = c.getProperty("WEIGHTS_FILE");
        String statisticsFile = c.getProperty("S_STATISTICS_FILE");

        Mlp mlp = Mlp.loadMlp(new File(weightsFile));
        Characteristics<String> characteristics =
                createCharacteristics(statisticsFile, mlp.getInputLayerLength());

        mClassificator = new AntiSpamWithMlp(mlp, characteristics);
    }

    private static Object[] readString(byte[] data, int off) {
        // read length
        int length = (data[off++] << 24) | (data[off++] << 16) | (data[off++] << 8) | (data[off++]) ;

        // string
        String str = new String(data, off, length);

        return new Object[] {str, off+length};
    }

    /**
     * Process the client.
     * <p>
     * First it will read the client data, process and return the results.
     * Then the data will be saved.
     * </p>
     * @param socket The socket with the connection to the client.
     */
    private void processClient(Socket socket) {
        try {
            // read all data
            int readed = socket.getInputStream().read(mClientData);

            // read user
            Object[] user = readString(mClientData, 0);

            String userAddress = (String) user[0];
            Integer offset = (Integer) user[1];

            // process mail data
            AntiSpam.Result result = mClassificator.processMail(
                    new ByteArrayInputStream(mClientData, offset, readed-offset));

            System.out.println("result = " + result);

            // send result
            OutputStream outStream = socket.getOutputStream();
            outStream.write(result.ordinal());
            outStream.flush();

            socket.close();

            // async store mail data
            mStorageExecutor.execute(
                    new StorageAsync(userAddress,
                            new String(mClientData, offset, readed-offset), result != AntiSpam.Result.HAM));
        }
        catch (IOException ex) {
            Logger.getLogger(old_ServerSAS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Starts the server service.
     */
    public void start() {
        try {
            createClassificator();

            ServerSocket serverSocket = new ServerSocket(34645);

            while (true) {
                System.out.println("Waiting connection...");
                Socket socket = serverSocket.accept();
                System.out.println("handling client");
                processClient(socket);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class StorageAsync implements Runnable {

        private final String mmMailData;
        private final String mmUserAddress;
        private final boolean mmIsSpam;

        public StorageAsync(String userAddress, String mailData, boolean isSpam) {
            mmMailData = mailData;
            mmUserAddress = userAddress;
            mmIsSpam = isSpam;
        }

        @Override
        public void run() {
            System.out.println("saving mail=" + mmUserAddress);
//            mStorage.store(mmUserAddress, mmMailData, mmIsSpam);
        }
    }

    // -------------------------------------------------------------------------
    // Test
    // -------------------------------------------------------------------------

    public static void main(String[] args) throws IOException {
        new old_ServerSAS().start();
    }

}
