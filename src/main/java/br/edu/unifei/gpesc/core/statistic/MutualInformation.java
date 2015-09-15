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
package br.edu.unifei.gpesc.core.statistic;

import static java.lang.Math.log10;

/**
 *
 * @author isaac
 */
public class MutualInformation implements Distribution {

    @Override
    public double compute(Data data, Statistic statistics) {

        double probabilidade_set = 1 / (double) statistics.getSetCount();

        // probabilidade para HAM
        int total_ham = statistics.getElementSetTotalCount(0);

        int quantidade_ham = data.getStatistic(0);
        int quantidade_nao_ham = total_ham - quantidade_ham;

        // probabilidade de ser HAM

        double p_elemento_ser_ham = probabilidade_set * (quantidade_ham / (double) total_ham);
        double p_elemento_nao_ser_ham = probabilidade_set * (quantidade_nao_ham / (double) total_ham);

        // probabilidade para SPAM
        int total_spam = statistics.getElementSetTotalCount(1);

        int quantidade_spam = data.getStatistic(1);
        int quantidade_nao_spam = total_spam - quantidade_spam;

        // probabilidade de ser SPAM
        double p_elemento_ser_spam = probabilidade_set * (quantidade_spam / (double) total_spam);
        double p_elemento_nao_ser_spam = probabilidade_set * (quantidade_nao_spam / (double) total_spam);


        // probabilidade TOTAL
        int total = total_ham + total_spam;

        int quantidade_total = quantidade_ham + quantidade_spam;
        int quantidade_nao_total = quantidade_nao_ham + quantidade_nao_spam;

        // probabildade para TOTAL
        double p_elemento_total = 1.0 * (quantidade_total / (double) total);
        double p_elemento_nao_total = 1.0 * (quantidade_nao_total / (double) total);

        double peso_elemento = p_elemento_total * probabilidade_set;
        double peso_nao_elemento = p_elemento_nao_total * probabilidade_set;

        // mutual information

        double mi_ham = p_elemento_ser_ham * log2(p_elemento_ser_ham / peso_elemento);
        double mi_nao_ham = p_elemento_nao_ser_ham * log2(p_elemento_nao_ser_ham / peso_nao_elemento);
        double mi_spam = p_elemento_ser_spam * log2(p_elemento_ser_spam / peso_elemento);
        double mi_nao_spam = p_elemento_nao_ser_spam * log2(p_elemento_nao_ser_spam / peso_nao_elemento);

        double result = mi_ham + mi_nao_ham + mi_spam + mi_nao_spam;

        return (Double.isNaN(result)) ? Double.NEGATIVE_INFINITY : result;
    }

    private static final double LOG10_2 = log10(2);
    private static double log2(double value) {
            return log10(value) / LOG10_2;
    }

}
