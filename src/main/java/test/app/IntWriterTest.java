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
package test.app;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

/**
 *
 * @author isaac
 */
public class IntWriterTest {

    interface IntWriter {

        void write(int[] ints);
    }

    public static final int NUM_INTS = 1000000;
    public static final int NUM_TESTS = 3;
    public static final String[] TESTS = {"Buffered DataOutputStream",
        "DataOutputStream", "ObjectOutputStream", "FileChannel alt"};

    public static void main(String[] args) {
        // create buffer
        int[] ints = new int[NUM_INTS];
        Random r = new Random();
        for (int i = 0; i < NUM_INTS; i++) {
            ints[i] = r.nextInt();
        }

        // run tests
        double[][] results = new double[TESTS.length][NUM_TESTS];

        System.out.print("Running tests... ");
        for (int i = 0; i < NUM_TESTS; i++) {
            System.out.print(i + " ");

            results[0][i] = time("Buffered DataOutputStream", new IntWriter() {
                public void write(int[] ints) {
                    storeBDO(ints);
                }
            }, ints);

            results[1][i] = time("DataOutputStream", new IntWriter() {
                public void write(int[] ints) {
                    storeDO(ints);
                }
            }, ints);

            results[2][i] = time("ObjectOutputStream", new IntWriter() {
                public void write(int[] ints) {
                    storeOO(ints);
                }
            }, ints);

            results[3][i] = time("FileChannel alt", new IntWriter() {
                public void write(int[] ints) {
                    storeFCAlt(ints);
                }
            }, ints);
        }
        System.out.println();

        // print results
        for (int i = 0; i < TESTS.length; i++) {
            System.out.print(TESTS[i] + "\t");
            for (int j = 0; j < NUM_TESTS; j++) {
                System.out.print(results[i][j] + "\t");
            }
            System.out.println();
        }

                // time("FileChannel", new IntWriter() {
        // public void write(int[] ints) {
        // storeFC(ints);
        // }
        // }, ints);
    }

    private static double time(String name, IntWriter writer, int[] ints) {
        long start = System.nanoTime();
        writer.write(ints);
        long end = System.nanoTime();
        double ms = (end - start) / 1000000d;
        return ms;
                // System.out.printf("%s wrote %,d ints in %,.3f ms%n", name,
        // ints.length,
        // ms);
    }

        // ========================================================================
    // tests
    // ========================================================================
    private static void storeOO(int[] ints) {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream("object.out"));
            out.writeObject(ints);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            safeClose(out);
        }
    }

    private static void storeBDO(int[] ints) {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream("data.out")));
            for (int anInt : ints) {
                out.write(anInt);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            safeClose(out);
        }
    }

    private static void storeDO(int[] ints) {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new FileOutputStream("data.out"));
            for (int anInt : ints) {
                out.write(anInt);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            safeClose(out);
        }
    }

    private static void storeFC(int[] ints) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream("fc.out");
            FileChannel file = out.getChannel();
            ByteBuffer buf = file.map(FileChannel.MapMode.READ_WRITE, 0,
                    4 * ints.length);
            for (int i : ints) {
                buf.putInt(i);
            }
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            safeClose(out);
        }
    }

    private static void storeFCAlt(int[] ints) {
        ByteBuffer buffer = ByteBuffer.allocate(4 * ints.length);
        for (int i : ints) {
            buffer.putInt(i);
        }
        buffer.flip();

        FileChannel fc = null;
        try {
            fc = new FileOutputStream("fcalt.out").getChannel();
            fc.write(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            safeClose(fc);
        }
    }

        // safe close helpers
    private static void safeClose(OutputStream out) {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            // do nothing
        }
    }

    private static void safeClose(FileChannel out) {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            // do nothing
        }
    }

}