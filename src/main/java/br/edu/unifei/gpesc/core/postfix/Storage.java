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

import br.edu.unifei.gpesc.util.TraceLog;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * TODO: File collision check.
 *
 * @author Isaac Caldas Ferreira
 */
public class Storage {

    private final File mFolder;

    /**
     * Initizalizes the backup and spam folder for storage. <br>
     * The folders may be null, in this case when .store() is called, nothing will happen.
     *
     * @param folder The root folder to store all e-mails received.
     */
    public Storage(File folder) {
        mFolder = folder;
    }

    private static String userToPath(String userMail) {
        int atSign = userMail.indexOf('@');

        if (atSign == -1) {
            return userMail;
        }

        String user = userMail.substring(0, atSign);
        String domain = userMail.substring(atSign + 1);

        StringBuilder strBuilder = new StringBuilder(user.length() * 2 + domain.length());
        strBuilder.append(domain);

        for (char c : user.toCharArray()) {
            strBuilder.append(File.separatorChar).append(c);
        }

        return strBuilder.toString();
    }

    protected File getStorageFolder(File root, String user) {
        return new File(root, userToPath(user));
    }

    /**
     * Stores the file on the backup folder and the spam folder, if they exists.
     *
     * @param fileName
     * @param user
     * @param data
     * @param dataOffset
     * @param dataLen
     * @throws java.io.IOException
     */
    public void store(String fileName, String user, byte[] data, int dataOffset, int dataLen) throws IOException {
        File folder = getStorageFolder(mFolder, user);
        folder.mkdirs();

        FileOutputStream fout = new FileOutputStream(new File(folder, fileName));
        fout.write(data, dataOffset, dataLen);
        fout.close();
    }

    public void silentStore(String fileName, String user, byte[] data, int dataOffset, int dataLen) {
        try {
            store(fileName, user, data, dataOffset, dataLen);
        } catch (IOException e) {
            TraceLog.logE(e);
        }
    }
}
