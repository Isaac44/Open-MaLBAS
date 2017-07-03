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

package br.edu.unifei.gpesc.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Isaac C. Ferreira
 */
public class TimeMark {

    private static Writer mWriter;
    private static long mMarkedTime = 0;

    private static long mTotalTime;

    public static void init(File file) {
        try {
            mWriter = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void write(String str1, String str2) {
        try {
            mWriter.append(str1).append(str2).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // ---------------------------------------------------------------------------------------------
    // Mark
    // ---------------------------------------------------------------------------------------------

    private static final SimpleDateFormat SDF = new SimpleDateFormat();

    public static void start() {
        write("1. E-Mail chegou em ", SDF.format(new Date(System.currentTimeMillis())));
        mMarkedTime = System.currentTimeMillis();
        mTotalTime = 0;
    }

    public static void finish(int mailLength) {
        write("8. Fim. | E-mail com ", mailLength + " bytes | Total = " + mTotalTime + "ms\n------------------------------------------\n");
    }

    public static void mark(String label) {
        long time = System.currentTimeMillis();
        long dt = (time - mMarkedTime);

        String sTime =  " = " + dt + " ms";
        write(label, sTime);

        mMarkedTime = System.currentTimeMillis();
        mTotalTime += dt;
    }

}
