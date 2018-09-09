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

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Isaac C. Ferreira
 */
public class Marker {

    // -----------------------------------------------------------------------------------------
    // Attributes
    // -----------------------------------------------------------------------------------------

    private long mMarkedTime;
    private long mTotalTime = 0;

    private final long mStartTime;
    private final List<String> mLog = new LinkedList<String>();

    // -----------------------------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------------------------

    public Marker() {
        mMarkedTime = mStartTime = System.currentTimeMillis();
        log("1. Processamento iniciado", mStartTime);
    }

    // -----------------------------------------------------------------------------------------
    // Log
    // -----------------------------------------------------------------------------------------

    private void log(String msg, long value) {
        mLog.add(msg);
        mLog.add(" = ");
        mLog.add(String.valueOf(value));
        mLog.add(" ms\n");
    }

    // -----------------------------------------------------------------------------------------
    // Mark
    // -----------------------------------------------------------------------------------------

    public void mark(String msg) {
        long time = System.currentTimeMillis();
        long dt = (time - mMarkedTime);

        log(msg, dt);

        mTotalTime += dt;
        mMarkedTime = System.currentTimeMillis();
    }

    public void finish(Writer writer, int mailLength) throws IOException {
        mLog.add("8. Fim. | E-mail com ");
        mLog.add(String.valueOf(mailLength));
        log(" bytes | Total", mTotalTime);
        log("9. Tempor percorrido (c/ benchmarks) ", (System.currentTimeMillis() - mStartTime));
        mLog.add("------------------------------------------\n");

        for (String str : mLog) {
            writer.append(str);
        }
    }
}
