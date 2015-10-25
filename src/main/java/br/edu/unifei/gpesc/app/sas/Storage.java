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

import br.edu.unifei.gpesc.app.Configuration;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Important: Missing the file collision check!
 * @author Isaac Caldas Ferreira
 */
public class Storage {

    /**
     * Mail charset.
     */
    private static final Charset ASCII = Charset.forName("ASCII");

    /**
     * The folder to store all e-mails.
     */
    private final File mBackupFolder;

    /**
     * The folder to store only spam e-mails
     */
    private final File mSpamFolder;

    /**
     * Initizalizes the backup and spam folder for storage. <br>
     * The folders may be null, in this case when .store() is called, nothing
     * will happen.
     *
     * @param backupFolder The folder to store all e-mails.
     * @param spamFolder The folder to store all spam e-mails.
     */
    public Storage(File backupFolder, File spamFolder) {
        mBackupFolder = backupFolder;
        mSpamFolder = spamFolder;
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
     * @return The hash of the current time in milliseconds.
     */
    private String getTimeHash() {
        return Long.toUnsignedString(System.currentTimeMillis(), Character.MAX_RADIX);
    }

    /**
     * Stores the mail onto a system file.
     * @param file The file.
     * @param mail The mail.
     */
    private void store(File file, String mail) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(mail.getBytes(ASCII));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Creates the file to store the mail.
     * <p>
     * TODO:
     * Important: this method do NOT check for file collision!
     * </p>
     *
     * @param root The root folder.
     * @param user The user folder name.
     * @param fileName The file-to-be-saved name.
     * @param mail The mail data.
     */
    private void store(File root, String user, String fileName, String mail) {
        if (root != null) {
            File folder = getFolder(root, user);
            File file = new File(folder, fileName);
            store(file, mail);
        }
    }

    /**
     * Stores the file on the backup folder and the spam folder, if they exists.
     * @param user The user e-mail address.
     * @param mail The mail data.
     * @param isSpam The anti-spam results. It must be true for the cases
     * where the e-mail is spam or unknown.
     */
    public void store(String user, String mail, boolean isSpam) {
        String fileName = getTimeHash() + ".eml";
        store(mBackupFolder, user, fileName, mail);

        if (isSpam) {
            store(mSpamFolder, user, fileName, mail);
        }
    }

    /**
     * Creates a {@link Storage} using the {@link Configuration} properties:
     * STORE_BACKUP_FOLDER and STORE_SPAM_FOLDER.
     * @return The Storage.
     */
    public static Storage buildFromConfiguration() {
        File bkpFolder = createFolder("STORE_BACKUP_FOLDER");
        File spamFolder = createFolder("STORE_SPAM_FOLDER");
        return new Storage(bkpFolder, spamFolder);
    }

    /**
     * Creates a folder using a {@link Configuration} property. <br>
     * This method will try to build de path to the folder. If its not possible,
     * null will be returned.
     * @param key The {@link Configuration} property.
     * @return The folder or null.
     */
    private static File createFolder(String key) {
        String folderPath = Configuration.getInstance().getProperty(key, null);

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
}
