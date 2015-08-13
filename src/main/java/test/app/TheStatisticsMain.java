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
package test.app;

import br.edu.unifei.gpesc.statistic.Census;
import br.edu.unifei.gpesc.statistic.StatisticalData;
import br.edu.unifei.gpesc.statistic.Statistics;
import br.edu.unifei.gpesc.sas.modules.SASStatistics;
import br.edu.unifei.gpesc.statistic.MutualInformationDistribution;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author isaac
 */
public class TheStatisticsMain {

    public static void main(String[] args) throws IOException {
        SASStatistics antispamStatistics = new SASStatistics();
//        String path = "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/Febuary/clean/";
        String path = "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/Febuary/clean/";


        antispamStatistics.processSpamAndHam(new File(path, "spam"), new File(path, "ham"));

        Statistics<String> statistics = antispamStatistics.getStatistics();

        Census<String> census = new Census<String>(statistics);
        census.computeDistribution(new MutualInformationDistribution());
//        census.computeDistribution(new FrequencyDistribution());
//        census.computeDistribution(new ChiSquaredDistribution());

        census.sortData(new Census.DecrescentDistributionSort());

        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(path+"statistics_mi"));
        for (StatisticalData<String> data : census.getStatisticalDataList()) {
            fileWriter.append(data.getElement()).append("\t").append(Double.toString(data.getStatisticalDistribution())).append("\n");
        }
        fileWriter.close();
    }

}
