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

package br.edu.unifei.gpesc.core.filter;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Isaac C. Ferreira
 */
public class OccurrencesMap implements Iterable<Map.Entry<String, Integer>> {

    protected final HashMap<String, Integer> mOccurrencesMap;

    public OccurrencesMap(int initialCapacity) {
        mOccurrencesMap = new HashMap<String, Integer>(initialCapacity);
    }

    public OccurrencesMap() {
        this(200);
    }

    public void clear() {
        mOccurrencesMap.clear();
    }

    public void add(String str) {
        Integer value = mOccurrencesMap.get(str);
        mOccurrencesMap.put(str, value != null ? value + 1 : 1);
    }

    public void toFile(File file) throws IOException {
        Writer writer = new BufferedWriter(new FileWriter(file));

        for (Map.Entry<String, Integer> entry : mOccurrencesMap.entrySet()) {
            writer.append(String.valueOf(entry.getValue())).append("_").append(entry.getKey()).append("\t");
        }

        writer.close();
    }

//    public void toFile(File file) throws IOException {
//        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
//
//        for (Map.Entry<String, Integer> entry : mOccurrencesMap.entrySet()) {
//            dout.writeUTF(entry.getKey());
//            dout.writeInt(entry.getValue());
//        }
//
//        dout.close();
//    }

    public void fromFile(File file) throws IOException {
        clear();

        DataInputStream din = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

        while (din.available() > 0) {
            mOccurrencesMap.put(din.readUTF(), din.readInt());
        }

        din.close();
    }

    @Override
    public Iterator<Map.Entry<String, Integer>> iterator() {
        return mOccurrencesMap.entrySet().iterator();
    }
}
