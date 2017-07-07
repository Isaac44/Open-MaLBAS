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

package br.edu.unifei.gpesc.core.antispam;

import br.edu.unifei.gpesc.core.modules.Filter;
import br.edu.unifei.gpesc.core.modules.Spam;
import br.edu.unifei.gpesc.core.modules.Vectorization;
import br.edu.unifei.gpesc.core.statistic.Characteristics;
import br.edu.unifei.gpesc.evaluation.TimeMark;
import br.edu.unifei.gpesc.mlp.Mlp;
import java.io.File;
import java.io.InputStream;

/**
 *
 * @author Isaac C. Ferreira
 */
public class AntiSpamWithMlp implements AntiSpam {

    private final Mlp mMlp;
    private final Filter mFilter = new Filter();
    private final Vectorization mVectorization;

    public AntiSpamWithMlp(Mlp mlp, Characteristics<String> characteristics) {
        mMlp = mlp;
        mVectorization = new Vectorization(characteristics);
    }

    private Result getClassification(double v1, double v2) {
        if (Spam.isSpam(v1, v2)) {
            return Result.SPAM;
        }
        else if (Spam.isHam(v1, v2)) {
            return Result.HAM;
        }
        else {
            return Result.UNKNOWN;
        }
    }

    private Result doProcess() {
        double[] vector = mVectorization.createVector(mFilter.getOccurrencesMap());
        TimeMark.mark("5. Vetorização");

        double[] output = mMlp.runTestNonSup(vector);
        TimeMark.mark("6. MLP");

        return getClassification(output[0], output[1]);
    }

    @Override
    public Result processMail(InputStream mailStream) {
        if (mFilter.filterMail(mailStream)) {
            return doProcess();
        }
        return Result.UNKNOWN;
    }

    @Override
    public Result processMail(File mailFile) {
        if (mFilter.filterMail(mailFile)) {
            return doProcess();
        }
        return Result.UNKNOWN;
    }

}
