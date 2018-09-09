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

package br.edu.unifei.gpesc.core.modules;

import br.edu.unifei.gpesc.core.filter.OccurrencesMap;
import br.edu.unifei.gpesc.core.statistic.Characteristics;
import br.edu.unifei.gpesc.core.statistic.Characterization;
import br.edu.unifei.gpesc.core.statistic.FeatureScalingNormalization;
import br.edu.unifei.gpesc.core.statistic.Normalization;
import java.util.Map;

/**
 *
 * @author Isaac C. Ferreira
 */
public class Vectorization {

    private final Characterization<String> mCharacterization;
    private final double[] mNormalized;

    private final Normalization mNormalization = new FeatureScalingNormalization();

    public Vectorization(Characteristics<String> characteristics) {
        mCharacterization = new Characterization<String>(characteristics);
        mNormalized = new double[mCharacterization.getCharacterizationArray().length];
    }

    public double[] createVector(OccurrencesMap map) {
        mCharacterization.cleanArray();

        for (Map.Entry<String, Integer> entry : map) {
            mCharacterization.insertData(entry.getKey(), entry.getValue());
        }


        mNormalization.normalize(mCharacterization.getCharacterizationArray(), mNormalized);
        return mNormalized;
    }
}
