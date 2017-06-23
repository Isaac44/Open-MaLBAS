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
package br.edu.unifei.gpesc.core.postfix;

import br.edu.unifei.gpesc.util.Configuration;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TODO: File collision check.
 *
 * @author Isaac Caldas Ferreira
 */
public class Storage {

    /**
     * Storers. [0]: backup, [1]: spam
     */
    private final Storer[] mStorers = new Storer[2];

    /**
     * Background store executor.
     */
    private final ExecutorService mExecutor;

    /**
     * Initizalizes the backup and spam folder for storage. <br>
     * The folders may be null, in this case when .store() is called, nothing will happen.
     *
     * @param backupFolder The folder to store all e-mails received (spams or not spams).
     * @param spamFolder The folder to store only spam e-mails.
     * @param asyncStorage
     */
    public Storage(File backupFolder, File spamFolder, boolean asyncStorage) {
        mStorers[0] = (backupFolder != null) ? new Storer(backupFolder) : new EmptyStorer();
        mStorers[1] = (spamFolder != null) ? new Storer(spamFolder) : new EmptyStorer();
        mExecutor = asyncStorage ? Executors.newSingleThreadExecutor() : null;
    }

    private File getFolder(File root, String user) {
        File file = new File(root, user);

        if (!file.isDirectory()) {
            file.mkdir();
        }

        return file;
    }

    /**
     * Creates a name for the email based on the current time.
     *
     * @return The hash of the current time in milliseconds.
     */
    private String createEmailFileName() {
        return Long.toUnsignedString(System.currentTimeMillis(), Character.MAX_RADIX) + ".eml";
    }

    /**
     * Checks if the e-mail fileName exists on the backup folder and/or the spam folder.
     * @param fileName
     * @return
     */
    public boolean exists(String fileName) {
        for (Storer storer : mStorers) {
            if (storer.exists(fileName)) {
                return true;
            }
        }
        return false;
    }

    private static String userToPath(String userMail) {
        int atSign = userMail.indexOf('@');
        String user = userMail.substring(0, atSign);
        String domain = userMail.substring(atSign+1);

        StringBuilder strBuilder = new StringBuilder(user.length() * 2 + domain.length());
        strBuilder.append(domain);

        for (char c : user.toCharArray()) {
            strBuilder.append(File.separatorChar).append(c);
        }

        return strBuilder.toString();
    }

    /**
     * Stores the file on the backup folder and the spam folder, if they exists.
     *
     * @param user The user e-mail address.
     * @param mailData The mail data.
     * @param isSpam The anti-spam results. It must be false for the cases where the e-mail is ham
     * or unknown, and true for spam.
     */

    private synchronized void storeMail(String user, String mailData, boolean isSpam) {
        // Create a name for the e-mail
        String fileName = createEmailFileName();

        // Spam path
        mStorers[1].setSubFolder(userToPath(user));

        // Check if exists (avoid collision)
        if (exists(fileName)) {
            // rename until find a non-existent name
            do {
                fileName = createEmailFileName();
            } while (exists(fileName));
        }

        // Backup Storage
        mStorers[0].store(fileName, mailData);

        // Spam Storage
        if (isSpam) {
            mStorers[1].store(fileName, mailData);
        }
    }

    /**
     * Stores the file on the backup folder and the spam folder, if they exists.
     * <p>
     * This method is synchonized for multi-threads purposes.</p>
     *
     * @param user The user e-mail address.
     * @param mail The mail data.
     * @param isSpam The anti-spam results. It must be false for the cases where the e-mail is ham
     * or unknown, and true for spam.
     */
    public synchronized void store(String user, String mail, boolean isSpam) {
        if (mExecutor != null) {
            mExecutor.execute(new AsyncStore(user, mail, isSpam));
        } else {
            storeMail(user, mail, isSpam);
        }
    }

    /**
     * Creates a {@link Storage} using the {@link Configuration} properties: STORE_BACKUP_FOLDER and
     * STORE_SPAM_FOLDER.
     *
     * @return The Storage.
     * @throws java.io.IOException
     */
    public static Storage buildFromConfiguration() throws IOException {
        Configuration c = new Configuration("config" + File.separator + "sas.properties");
        File bkpFolder = createFolder(c, "STORE_BACKUP_FOLDER");
        File spamFolder = createFolder(c, "STORE_SPAM_FOLDER");
        return new Storage(bkpFolder, spamFolder, true);
    }

    /**
     * Creates a folder using a {@link Configuration} property. <br>
     * This method will try to build de path to the folder. If its not possible, null will be
     * returned.
     *
     * @param key The {@link Configuration} property.
     * @return The folder or null.
     */
    private static File createFolder(Configuration c, String key) {
        String folderPath = c.getProperty(key, null);

        if (folderPath != null) {
            File folder = new File(folderPath);
            if (!folder.isDirectory()) {
                if (folder.mkdirs()) {
                    return folder;
                }
            } else {
                return folder;
            }
        }

        return null;
    }

    void setLogFileName(String time) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private class AsyncStore implements Runnable {

        private final String mmMailData;
        private final String mmUserAddress;
        private final boolean mmIsSpam;

        public AsyncStore(String userAddress, String mailData, boolean isSpam) {
            mmMailData = mailData;
            mmUserAddress = userAddress;
            mmIsSpam = isSpam;
        }

        @Override
        public void run() {
            System.out.println("saving mail=" + mmUserAddress);
            storeMail(mmUserAddress, mmMailData, mmIsSpam);
        }
    }

    public static void main(String[] args) throws ParseException {
        String root = "/home/isaac/Desktop/BACKUP/Test";
        Storage s = new Storage(new File(root, "backup"), new File(root, "spam"), true);

        s.store("hamtaro@unife.edu.br", "meu e-mail simples", true);
        s.store("hamtaro@unife.edu.br", "meu e-mail simples", true);
        s.store("hamtaro@unife.edu.br", "meu e-mail simples", true);
        s.store("hamtaro@unife.edu.br", "meu e-mail simples", true);
        s.store("hamtaro@unife.edu.br", "meu e-mail simples", true);
        s.store("hamtaro@unife.edu.br", "meu e-mail simples", true);

    }
}
