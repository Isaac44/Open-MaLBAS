/*
 * Copyright (C) 2015 Universidade Federal de Itajuba
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author isaac
 */
public class FileIntOutput {

    private final ByteBuffer mByteBuffer;
    private final FileChannel mFileChannel;

    public FileIntOutput(File file, int arrayLength) throws FileNotFoundException {
        mByteBuffer = ByteBuffer.allocate(4 * arrayLength); // int32 => 4 bytes
        mFileChannel = new FileOutputStream(file).getChannel();
    }

    public void writeAt(long position, int... values) throws IOException {
        long oldPosition = mFileChannel.position();
        mFileChannel.position(position);
        write(values);
        mFileChannel.position(oldPosition);
    }

    public void write(double[] array, double base) throws IOException {
        mByteBuffer.clear();
        for (double value : array) {
            mByteBuffer.putInt((int) (value * base));
        }
        mByteBuffer.flip();
        mFileChannel.write(mByteBuffer);
    }

    public void write(int... array) throws IOException {
        mByteBuffer.clear();
        for (int value : array) {
            mByteBuffer.putInt(value);
        }
        mByteBuffer.flip();
        mFileChannel.write(mByteBuffer);
    }

    public void close() throws IOException {
        mFileChannel.close();
    }

}
