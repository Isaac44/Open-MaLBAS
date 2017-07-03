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
import br.edu.unifei.gpesc.core.modules.Vector;
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
    private final Characteristics<String> mCharacteristics;

    public AntiSpamWithMlp(Mlp mlp, Characteristics<String> characteristics) {
        mMlp = mlp;
        mCharacteristics = characteristics;
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

    private Result process(String mail) {
        double[] vector = Vector.getVector(mCharacteristics, mail);
        TimeMark.mark("5. Vetorização");

        double[] output = mMlp.runTestNonSup(vector);
        TimeMark.mark("6. MLP");

        return getClassification(output[0], output[1]);
    }

    @Override
    public Result processMail(InputStream mailStream) {
        String filtered = mFilter.filterMail(mailStream);
        return process(filtered);
    }

    @Override
    public Result processMail(File mailFile) {
        String filtered = mFilter.filterMail(mailFile.getAbsolutePath());
        return process(filtered);
    }

}
