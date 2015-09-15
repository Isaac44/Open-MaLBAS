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

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class ChiSquared implements Distribution {

    @Override
    public double compute(Data data, Statistic statistics) {

        int[] amostralSize = statistics.getAmostralSizeArray();
        int total_amostral_size = 0;
        for (int value : amostralSize) {
            total_amostral_size += value;
        }

        int ham_quantity = amostralSize[0];
        int spam_quantity = amostralSize[1];

        // HAM

        int ham_k = data.getAmostralOccurrences(0); // HAM
        int ham_l = data.getAmostralOccurrences(1); // SPAM

        int ham_m = ham_quantity - ham_k;
        int ham_n = spam_quantity - ham_l;

        int ham_size = amostralSize[0];

        double p_ham = ham_size / (double) total_amostral_size;

        int aux1 = (ham_k  * ham_n - ham_m * ham_l);
        int ham_N = ham_size * (aux1 * aux1);
        int ham_D = (ham_k + ham_m) * (ham_l + ham_n) * (ham_k + ham_l) * (ham_m + ham_n);

        double ham_chi2;

        if (ham_D != 0.0) {
            ham_chi2 = (ham_N / (double) ham_D);
        } else {
            ham_chi2 = (ham_N == 0.0) ? -1 : Double.MAX_VALUE;
        }

        // SPAM
        int spam_k = data.getAmostralOccurrences(1);
        int spam_l = data.getAmostralOccurrences(0);

        int spam_m = spam_quantity - spam_k;
        int spam_n = ham_quantity - spam_l;

        int spam_size = amostralSize[1];

        double p_spam = spam_size / (double) total_amostral_size;

        int aux2 = (spam_k  * spam_n - spam_m * spam_l);
        int spam_N = spam_size * (aux2 * aux2);
        int spam_D = (spam_k + spam_m) * (spam_l + spam_n) * (spam_k + spam_l) * (spam_m + spam_n);

        double spam_chi2;

        if (spam_D != 0.0) {
            spam_chi2 = (spam_N / (double) spam_D);
        } else {
            spam_chi2 = (spam_N == 0.0) ? -1 : Double.MAX_VALUE;
        }

        // calcs
        double chi2_ham = ham_chi2 * p_ham;
        double chi2_spam = spam_chi2 * p_spam;

        return chi2_ham + chi2_spam;
    }

}
