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

import br.edu.unifei.gpesc.core.mlp.RunMlp;
import br.edu.unifei.gpesc.core.modules.Filter;
import br.edu.unifei.gpesc.core.modules.Vector;
import br.edu.unifei.gpesc.core.statistic.Characteristics;
import java.io.File;
import java.io.InputStream;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class Grader {

    public static final int HAM = 0;
    public static final int SPAM = 1;
    public static final int NEITHER = 2;

    private final RunMlp mMlp;
    private final Filter mFilter = new Filter();
    private final Characteristics<String> mCharacteristics;

    public Grader(RunMlp mlp, Characteristics<String> characteristics) {
        mMlp = mlp;
        mCharacteristics = characteristics;
    }

    private int getClassification(double o1, double o2) {
        if ((o1 >= 0.6) && (o2 <= 0.4)) {
            return HAM;
        }
        else if ((o1 <= 0.4) && (o2 >= 0.6)) {
            return SPAM;
        }
        else {
            return NEITHER;
        }
    }

    private int process(String mail) {
        double[] vector = Vector.getVector(mCharacteristics, mail);
        double[] output = mMlp.runTestNonSup(vector);
        return getClassification(output[0], output[1]);
    }

    public int processMail(InputStream mailStream) {
        String filtered = mFilter.filterMail(mailStream);
        return process(filtered);
    }

    public int processMail(File mailFile) {
        String filtered = mFilter.filterMail(mailFile.getAbsolutePath());
        return process(filtered);
    }


}
