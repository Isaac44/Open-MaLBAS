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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 *
 * @author Isaac C. Ferreira
 */
public class Storer {

    /**
     * Mail charset.
     */
    private static final Charset ASCII = Charset.forName("ASCII");

    private final File mRoot;

    private File mFolder;

    public Storer(File root) {
        mFolder = mRoot = root;
    }

    public Storer() {
        this(null);
    }

    public void setSubFolder(String subFolder) {
        mFolder = new File(mRoot, subFolder);
        mFolder.mkdirs();
    }

    /**
     * Checks if the e-mail exists.
     * @param fileName
     * @return
     */
    public boolean exists(String fileName) {
        return (new File(mFolder, fileName)).exists();
    }

    /**
     * Stores the mail.
     *
     * @param fileName The file name.
     * @param mailData The mail.
     */
    public void store(String fileName, String mailData) {
        try {
            FileOutputStream out = new FileOutputStream(new File(mFolder, fileName));
            out.write(mailData.getBytes(ASCII));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
