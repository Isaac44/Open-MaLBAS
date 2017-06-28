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

package br.edu.unifei.gpesc.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Isaac C. Ferreira
 */
public class TransactionalInputStream extends ByteArrayInputStream {

    private final byte[] mAuxiliar;

    public TransactionalInputStream(int initialCapacity, int auxiliarCapacity) {
        super(new byte[initialCapacity]);
        mAuxiliar = new byte[auxiliarCapacity];
    }

    public String getDataAsString() {
        return new String(buf, 0, count);
    }

    public byte[] getData() {
        return buf;
    }

    public int getCount() {
        return count;
    }

    public int getOffset() {
        return pos;
    }

    public void copyData(InputStream in) throws IOException {
        reset();

        byte[] aux = mAuxiliar;
        int read;

        count = 0;
        while ((read = in.read(aux)) != -1) {
            ensureDataLength(count + read);
            System.arraycopy(aux, 0, buf, count, read);
            count += read;
        }
    }

    private void ensureDataLength(int required) {
        if (required >= buf.length) {
            byte[] newBuf = new byte[(int) (required * 1.2f)];
            System.arraycopy(buf, 0, newBuf, 0, count);
            buf = newBuf;
        }
    }
}
