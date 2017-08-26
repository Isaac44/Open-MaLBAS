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
package br.edu.unifei.gpesc.core.neural;

import br.edu.unifei.gpesc.mlp.Mlp;
import br.edu.unifei.gpesc.mlp.layer.PatternLayer;
import br.edu.unifei.gpesc.mlp.log.MlpLogger;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class TestBuilder {

    public static PatternLayer[] loadLayers(File folder) throws IOException {
        PatternLayer[] spams = TrainBuilder.createPatterns(new File(folder, "spam"), TrainBuilder.SPAM);
        PatternLayer[] hams = TrainBuilder.createPatterns(new File(folder, "ham"), TrainBuilder.HAM);

        return TrainBuilder.merge(hams, spams);
    }

    public static void main(String[] args) throws IOException {
        String path = "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/Testes_Organizados/";

        String path2 = path + "05_mlp_weights/2000/old/";
        Mlp runMlp = Mlp.loadMlp(new File(path2, "train_2.dat"));


        MlpLogger logger = new MlpLogger(new File(path2, "result.log"));

        System.out.println("> Started");

        runMlp.setLogger(logger);
        runMlp.runTestSup(loadLayers(new File(path, "04_vectors/2000/")));

        logger.close();


    }
}
