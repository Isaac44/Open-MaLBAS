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
package br.edu.unifei.gpesc.log;

import static br.edu.unifei.gpesc.app.Messages.i18n;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class MlpLogger extends AsyncWriter {

    private static final String STEP        = i18nF("\n>"     , "Step"     , " ");
    private static final String EPOCH       = i18nF("\n\n\t> ", "Epoch"    , " "  );
    private static final String ERROR       = i18nF("\n\t\t> ", "Error"    , " = ");
    private static final String MOMENTUM    = i18nF("\n\t\t> ", "Momentum" , " = ");
    private static final String LEARN_RATE  = i18nF("\n\t\t> ", "LearnRate", " = ");

    public MlpLogger(ExecutorService executor, File file) throws FileNotFoundException {
        super(executor, file);
    }

    public void logStep(int step) {
        append(STEP + step);
    }

    public void logEpoch(int epoch, double error, double momentum, double learnRate) {
        append(EPOCH + epoch);
        append(ERROR + error);
        append(MOMENTUM + momentum);
        append(LEARN_RATE + learnRate);
        append("\n");
    }

    public void logEpoch(int epoch) {
        append(EPOCH + epoch);
    }

    public void logError(double error) {
        append(ERROR + error);
    }

    public void logMomentum(double momentum) {
        append(MOMENTUM + momentum);
    }

    public void logLearnRate(double learnRate) {
        append(LEARN_RATE + learnRate);
    }

    public void logHead(int h1Len, int h2Len, String h1F, String h2F, String outF, long seed, int epochs, double momentum, double learnRate) {
        StringBuilder sb = new StringBuilder();

        sb.append("MLP");

        // first hidden layer
        sb.append("\n\t> ").append(i18nF("FirstHiddenLayer"))
            .append("\n\t\t> ").append(i18nF("Length")).append(" = ").append(h1Len)
            .append("\n\t\t> ").append(i18nF("Function")).append(" = ").append(h1F);

        // second hidden layer
        sb.append("\n\t> ").append(i18nF("SecondHiddenLayer"))
            .append("\n\t\t> ").append(i18nF("Length")).append(" = ").append(h2Len)
            .append("\n\t\t> ").append(i18nF("Function")).append(" = ").append(h2F);

        // output layer
        sb.append("\n\t> ").append(i18nF("OutputLayer"))
            .append("\n\t\t> ").append(i18nF("Function")).append(" = ").append(outF);

        // config
        sb.append("\n\t> ").append(i18nF("Seed")).append(" = ").append(seed);
        sb.append("\n\t> ").append(i18nF("Epochs")).append(" = ").append(epochs);
        sb.append("\n\t> ").append(i18nF("InitMomentum")).append(" = ").append(momentum);
        sb.append("\n\t> ").append(i18nF("InitLearnRate")).append(" = ").append(learnRate);

        // end
        sb.append("\n\n");

        // write log
        append(sb.toString());

    }

    private static String i18nF(String key) {
        return i18n("Log.Neural.Mlp." + key);
    }

    private static String i18nF(String before, String key, String after) {
        return before + i18nF(key) + after;
    }

}
