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
package br.edu.unifei.gpesc.statistic;

import static java.lang.Math.log10;

/**
 *
 * @author isaac
 */
public class MutualInformationDistribution implements StatisticalDistribution {

//    @Override
    public double compute2(StatisticalData data, Statistics statistics) {
        double mi = 0.0;

        // pega a somat�ria do n�mero de vezes que certa palavra ocorreu em todos os emails
        // [0] spam e [1] ham

        double spams_palavra     = (double) data.getStatistic(0);
        double spams_nao_palavra = (double) statistics.getElementSetTotalCount(0) - spams_palavra;

        double hams_palavra      = (double) data.getStatistic(1);
        double hams_nao_palavra  = (double) statistics.getElementSetTotalCount(1) - hams_palavra;

        double numTotalSpams     = (double) statistics.getElementSetTotalCount(0);
        double numTotalHams      = (double) statistics.getElementSetTotalCount(1);
        double numTotal          = numTotalSpams + numTotalHams;

        double P_spam    = numTotalSpams / numTotal;
        double P_ham     = numTotalHams  / numTotal;

        // P(palavra, spam) = P(spam) * P(palavra/spam) : probabilidade da palavra ser encontrada em spams
        double P_palavra_spam = P_spam * (spams_palavra / numTotalSpams);

        // P(nao_palavra, spam) = probabilidade da a palavra nao ocorrer em todos spams
        double P_nao_palavra_spam = P_spam * ((numTotalSpams - spams_palavra) / numTotalSpams);

        // P(palavra, ham): probabilidade da palavra ser encontrada em hams
        double P_palavra_ham = P_ham * (hams_palavra / numTotalHams);

        // P(nao_palavra, ham) = probabilidade da a palavra nao ocorrer em todos hams
        double P_nao_palavra_ham = P_ham * ((numTotalHams - hams_palavra) / numTotalHams);

        // P(palavra): probabilidade da palavra ocorrer
        double P_palavra = (spams_palavra + hams_palavra) / numTotal;
        double P_nao_palavra = 1 - P_palavra;

        // Faz o somat�rio de mi (ver equa��o do MI de Saemi et al, 1994)
        if (P_palavra_spam != 0) {
            mi += P_palavra_spam     * log10 ( P_palavra_spam     / (P_palavra     * P_spam) );
        }
        if (P_palavra_ham != 0) {
            mi += P_palavra_ham      * log10 ( P_palavra_ham      / (P_palavra     * P_ham ) );
        }
        if (P_palavra_spam != 0) {
            mi += P_nao_palavra_spam * log10 ( P_nao_palavra_spam / (P_nao_palavra * P_spam) );
        }
        if (P_palavra_ham != 0) {
            mi += P_nao_palavra_ham  * log10 ( P_nao_palavra_ham  / (P_nao_palavra * P_ham ) );
        }

        return mi;
    }

    @Override
    public double compute(StatisticalData data, Statistics statistics) {
        double dataProbability = 0.0;
        double totalDataOccurrence = 0;

        for (int i=0; i<statistics.getSetCount(); i++) {
            dataProbability += data.getStatistic(i);
            totalDataOccurrence += statistics.getElementSetTotalCount(i);
        }
        dataProbability /= totalDataOccurrence;

        double mi = 0.0;
        double setProbability; // probability of the set(i)

        double dataSetProbability; // probability of the data be in the set(i)
        double notDataSetProbability; // probability of the data not be in the set(i)

        for (int i=0; i<statistics.getSetCount(); i++) {
            setProbability = statistics.getElementSetTotalCount(i) / totalDataOccurrence;

            // probability of the data belong to set
            dataSetProbability = setProbability * (data.getStatistic(i) / (double) statistics.getElementSetTotalCount(i));
            if (dataSetProbability != 0) {
                mi += (dataSetProbability * log10(dataSetProbability / (dataProbability * setProbability)));
            }

            // probability of the data not belong to set
            notDataSetProbability = setProbability * ( 1 - (data.getStatistic(i) / (double) statistics.getElementSetTotalCount(i)) );
            if (dataSetProbability != 0) {
                mi += (notDataSetProbability * log10(notDataSetProbability / ((1-dataProbability) * setProbability)));
            }
        }

        return mi;
    }

//        @Override
//    public double compute(StatisticalData data, int... setSizeArray) {
//        double size0 = setSizeArray[0];
//        double in0Count = data.getStatistic(0);
//        double notIn0Count = size0 - in0Count;
//
//        double size1 = setSizeArray[1];
//        double in1Count = data.getStatistic(1);
//        double notIn1Count = size1 - in1Count;
//
//        double size = size0 + size1;
//
//        double p0 = size0 / size;
//        double p1 = size1 / size;
//
//        double pIn0 = p0 * (in0Count / size0);
//        double pNotIn0 = p0 * (notIn0Count / size0);
//
//        double pIn1 = p1 * (in1Count / size1);
//        double pNotIn1 = p1 * (notIn1Count / size1);
//
//        double pData = (in0Count + in1Count) / size;
//        double pNotData = 1 - pData;
//
//        // summ
//        double mi = 0.0;
//
//        if (pIn0 != 0) {
//            mi += (pIn0 * log10(pIn0 / (pData * p0)));
//        }
//
//        if (pNotIn0 != 0) {
//            mi += (pNotIn0 * log10(pNotIn0 / (pNotData * p0)));
//        }
//
//        if (pIn1 != 1) {
//            mi += (pIn1 * log10(pIn1 / (pData * p1)));
//        }
//
//        if (pNotIn1 != 1) {
//            mi += (pNotIn1 * log10(pNotIn1 / (pNotData * p1)));
//        }
//
//        return mi;
//    }



}
