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
package br.edu.unifei.gpesc.base.statistic;

/**
 * Notas para o Otavio.
 *
 * Esta classe calcula a distribuicao de frequencia
 * dividindo a ocorrencia de cada palavra pelo total de arquivos ham e spam.
 * Exemplificando:
 *
 * Temos 5 emails ham e 4 emails spam.
 *
 * a palavra "amor" aparece 10 vezes nos 5 emails ham e 2 vezes nos 4 emails spam,
 * logo o resultado sera': 10/5 + 2/4 = 2,5
 *
 * Na implementacao original do SAS, o resultado seria 10 + 2 = 12
 *
 * Em outro dia, eu falei que estava certo. Mas eu estava considerando
 * que os dois conjuntos possuem a mesma quantidade de arquivos.
 * Dessa forma eu calculava: (10 + 2) / (5 + 4). Mas como todos os demais
 * elementos tambem seriam calculados assim, nao haveria a necessidade de
 * calcular a divisao.
 *
 *
 * @author Isaac Caldas Ferreira
 */
public class FrequencyDistribution implements StatisticalDistribution {

    @Override
    public double compute(StatisticalData data, int... setSizeArray) {
        double result = 0.0;
        for (int i=0; i<setSizeArray.length; i++) {
            result += (data.getStatistic(i) / (double) setSizeArray[i]);
        }
        return result;
    }

}
