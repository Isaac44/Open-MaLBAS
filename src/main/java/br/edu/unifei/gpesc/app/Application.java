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
package br.edu.unifei.gpesc.app;

import static br.edu.unifei.gpesc.app.Messages.*;
import java.io.IOException;


/**
 *
 * @author isaac
 */
public class Application {

    private static final String ARG_FILTER = "--filter";
    private static final String ARG_STATISTICS = "--statistics";
    private static final String ARG_MLP_VECTOR = "--mlp-vector";

    private static void printFilterHelp() {
        // Filter. Usage: --filter inputFolder outputFolder
        print("\t\t");
        printLog("Application.Help.FilterHeader");
        print(" ");
        print(ARG_FILTER);
        print(" ");
        printLog("Application.Help.InputFolder");
        print(" ");
        printlnLog("Application.Help.OutputFolder");
    }

    private static void printStatisticsHelp() {
        // Statistics. Usage: --statistics distributionMethod[MI|CHI2|DF] notSpamFolder spamFolder statisticsOutputFolder
        print("\t\t");
        printLog("Application.Help.StatisticsHeader");
        print(" ");
        print(ARG_STATISTICS);
        print(" ");
        printLog("Application.Help.Statistics.Distribution");
        print(":[MI|CHI2|DF] ");
        printLog("Application.Help.Statistics.NotSpamInputFolder");
        print(" ");
        printLog("Application.Help.Statistics.SpamInputFolder");
        print(" ");
        printlnLog("Application.Help.OutputFolder");
    }

    private static void printMlpVectorHelp() {
        // MlpVector. Usage: --mlp-vector mlpInputLayerLength --[append|new] hamPath spamPath statisticsPath outPath
        print("\t\t");
        printLog("Application.Help.MlpVectorHeader");
        print(" ");
        print(ARG_MLP_VECTOR);
        print(" ");
        printLog("Application.Help.MlpVector.InputLayerLength");
        print(" --[append|new] ");
        printLog("Application.Help.Statistics.NotSpamInputFolder");
        print(" ");
        printLog("Application.Help.Statistics.SpamInputFolder");
        print(" ");
        printLog("Application.Help.MlpVector.StatisticsFile");
        print(" ");
        printlnLog("Application.Help.OutputFolder");
    }

    private static void printHelp() {
        printlnLog("Application.Help.Header");

        // TrainMode
        print("\t"); printlnLog("Application.Help.TrainMode");

        printFilterHelp();
        printStatisticsHelp();

        // TestMode
        println("");
    }

    public static void main2(String... args) throws IOException {
        if (args.length == 0) printHelp();

        String module = args[0];

        if (module.equals(ARG_FILTER))
        {
            if (args.length == 3) {
                printlnLog("Application.TrainMode.SelectedFilter");
                TrainMode trainMode = new TrainMode();
                trainMode.doFilter(args[1], args[2]);
            } else {
                printFilterHelp();
            }
        }

        else if (module.equals(ARG_STATISTICS))
        {
            if (args.length == 5) {
                printlnLog("Application.TrainMode.SelectedStatistics");
                TrainMode trainMode = new TrainMode();
                trainMode.doStatistics(args[2], args[3], args[4], args[1].toUpperCase());
            } else {
                printStatisticsHelp();
            }
        }

        else if (module.equals(ARG_MLP_VECTOR))
        {
            if (args.length == 6) {
                printlnLog("Application.TrainMode.SelectedMlpVector");
                TrainMode trainMode = new TrainMode();
                trainMode.doMlpVector(args[2], args[3], args[4], Integer.parseInt(args[1]), args[5]);
            }
            else {
                printMlpVectorHelp();
            }
        }

        else {
            printHelp();
        }
    }

    public static void main(String[] args) throws IOException {
//        main2("--filter", "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/Febuary/base/ham", "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/May/clean/ham");
//        main2("--filter", "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/Febuary/base/spam", "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/May/clean/spam");

        String path = "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/May/clean/";

//        main2("--statistics", "MI", path+"ham", path+"spam", path);
        String pathVector = "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/May/vector/";
        main2("--mlp-vector", "2000", path+"ham", path+"spam", path+"statistics", pathVector);
    }

}