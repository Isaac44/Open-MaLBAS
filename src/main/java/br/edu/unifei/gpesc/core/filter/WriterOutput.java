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

import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author Isaac C. Ferreira
 */
public class WriterOutput implements FilterOutput {

    private final Writer mWriter;
    private IOException mLastException;

    public WriterOutput(Writer writer) {
        mWriter = writer;
    }

    private void silentAppend(String str) {
        try {
            mWriter.append(str);
        }
        catch (IOException e) {
            mLastException = e;
        }
    }

    @Override
    public FilterOutput append(String str) {
        silentAppend(str);
        return this;
    }

    public boolean hasException() {
        return mLastException != null;
    }

    public IOException getLastException() {
        return mLastException;
    }

    public void close() throws IOException {
        mWriter.close();
    }

}
