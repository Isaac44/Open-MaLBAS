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

import br.edu.unifei.gpesc.base.statistic.Census;
import br.edu.unifei.gpesc.base.statistic.SimpleFrequencyDistribution;
import br.edu.unifei.gpesc.base.statistic.StatisticalData;
import br.edu.unifei.gpesc.base.statistic.Statistics;
import br.edu.unifei.gpesc.sas.SASStatistics;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author isaac
 */
public class StatisticsTest {

    public static void main(String[] args) throws IOException {
        SASStatistics antispamStatistics = new SASStatistics();
        String path = "/home/isaac/Unifei/Mestrado/SAS/Statistics/DataSample/";
        antispamStatistics.processSpamAndHam(new File(path, "ham"), new File(path, "spam"));

        Statistics<String> statistics = antispamStatistics.getStatistics();

        Census<String> census = new Census<String>(statistics);
        census.computeDistribution(new SimpleFrequencyDistribution());
        census.sortData(new Census.DistributionSort());

        FileWriter fileWriter = new FileWriter(path+"statistics.txt");
        for (StatisticalData<String> data : census.getStatisticalDataList()) {
            fileWriter.append(data.getElement()).append("\t").append(Double.toString(data.getStatisticalDistribution())).append("\n");
        }
        fileWriter.flush();
        fileWriter.close();
    }

}
