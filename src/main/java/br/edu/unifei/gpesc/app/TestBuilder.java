/*
 * Copyright (C) 2015 - GEPESC - Universidade Federal de Itajuba
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

import br.edu.unifei.gpesc.core.mlp.PatternLayer;
import br.edu.unifei.gpesc.core.mlp.RunMlp;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class TestBuilder {

    private static PatternLayer[] loadLayers(File folder) throws IOException {
        PatternLayer[] spams = TrainBuilder.loadTrainMlp(new File(folder, "spam"), TrainBuilder.SPAM);
        PatternLayer[] hams = TrainBuilder.loadTrainMlp(new File(folder, "ham"), TrainBuilder.HAM);

        return TrainBuilder.merge(hams, spams);
    }
    
    public static void main(String[] args) throws IOException {
        String path = "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/September/neural/";
        RunMlp runMlp = RunMlp.loadMlp(new File(path, "train_1.dat"));
        runMlp.runTestSup(loadLayers(new File("/home/isaac/Unifei/Mestrado/SAS/Mail_Test/September/otavio-nn/otavio_test/")));
    }

//    public static void main(String[] args) throws IOException {
//        String path = "/home/isaac/Unifei/CodeStation/AntiSpamMestrado/neural/dat/";
//        File mlpData = new File(path, "wfin");
//        RunMlp mlp = RunMlp.loadMlp(mlpData);
//        mlp.runTestSup(loadLayers(new File("/home/isaac/Unifei/Mestrado/SAS/Mail_Test/September/otavio-nn/otavio_test/")));
//
//    }

}
