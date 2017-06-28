/*
 * Copyright (C) 2016 - GEPESC - Universidade Federal de Itajuba
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
package br.edu.unifei.gpesc.core.antispam;

import br.edu.unifei.gpesc.core.statistic.Characteristics;
import br.edu.unifei.gpesc.core.statistic.CharacteristicsHelper;
import br.edu.unifei.gpesc.mlp.Mlp;
import br.edu.unifei.gpesc.util.Configuration;
import br.edu.unifei.gpesc.util.TraceLog;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class AntiSpamFactory {

    private final AntiSpam mAntiSpam;

    public AntiSpamFactory(AntiSpam antiSpam, boolean singleInstance) {
        if (singleInstance) {
            mAntiSpam = new SyncAntiSpam(antiSpam);
        } else {
            mAntiSpam = antiSpam;
        }
    }

    public AntiSpam newAntiSpam() {
        return mAntiSpam;
    }

    private static AntiSpamFactory createAntiSpamWithMlp(Configuration c) throws IOException {
        final String weightsFile = c.getProperty("MLP_WEIGHTS_FILE");
        String statisticsFile = c.getProperty("MLP_STATISTICS_FILE");

        Mlp mlp = Mlp.loadMlp(new File(weightsFile));
        final Characteristics<String> characteristics = CharacteristicsHelper.fromFile(new File(statisticsFile), mlp.getInputLayerLength());

        final AntiSpam lastOptionAS = new SyncAntiSpam(new AntiSpamWithMlp(mlp, characteristics));

        return new AntiSpamFactory(null, false) {
            @Override
            public AntiSpam newAntiSpam() {
                try {
                    Mlp mlp = Mlp.loadMlp(new File(weightsFile));
                    return new AntiSpamWithMlp(mlp, characteristics);
                } catch (Exception e) {
                    TraceLog.logE(e);
                    return lastOptionAS;
                }
            }
        };
    }

    private static AntiSpamFactory createFrom(Configuration c) throws Exception {
        String classificator = c.getProperty("CLASSIFICATOR");

        if (classificator.contains("MLP")) {
            return createAntiSpamWithMlp(c);
        }

        return null;
    }

    public static AntiSpamFactory from(Configuration c) {
        try {
            return createFrom(c);
        } catch (Exception e) {
            TraceLog.logE(e);
            throw new RuntimeException("Error when creating the AntiSpam. Check all the parameters to fix the problem.");
        }
    }

    private static class SyncAntiSpam implements AntiSpam {

        private static final Object LOCK = new Object();

        private final AntiSpam mAntiSpam;

        public SyncAntiSpam(AntiSpam antiSpam) {
            mAntiSpam = antiSpam;
        }

        @Override
        public AntiSpam.Result processMail(File mailFile) {
            synchronized (LOCK) {
                return mAntiSpam.processMail(mailFile);
            }
        }

        @Override
        public AntiSpam.Result processMail(InputStream mailStream) {
            synchronized (LOCK) {
                return mAntiSpam.processMail(mailStream);
            }
        }
    }

}
