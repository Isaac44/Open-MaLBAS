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

import br.edu.unifei.gpesc.neural.mlp.core.MlpRun;
import br.edu.unifei.gpesc.sas.modules.NeuralCharacteristic;
import br.edu.unifei.gpesc.sas.modules.SASFilter;
import br.edu.unifei.gpesc.statistic.StatisticalCharacteristic;
import static br.edu.unifei.gpesc.sas.modules.SASVectorization.getVectorArray;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/**
 *
 * @author isaac
 */
public class ProcessMail {

    private final MlpRun mMlpRun = new MlpRun();
    private final SASFilter mMailFilter = new SASFilter();
    private final StatisticalCharacteristic<String> mCharacteristic = new StatisticalCharacteristic<String>();

    public ProcessMail(String archFileName, String weiFileName, File characteristicFile) throws FileNotFoundException {
        mMlpRun.initNet(archFileName, weiFileName);

        Scanner scanner = new Scanner(characteristicFile);
        // MUDAR!! ANEXAR A QUANTIDADE AO ARQUIVO
        for (int i=0; i<2000; i++) {
            mCharacteristic.insertData(scanner.next());
            scanner.nextLine();
        }
    }

    private NeuralCharacteristic process(String filteredMail) {
        int[] vectorArray = getVectorArray(mCharacteristic, filteredMail);

        mMlpRun.setPatternArray(vectorArray);
        mMlpRun.runTestNonSup();

        int niO = mMlpRun.getNumNeuIniOut();
        int nfO = mMlpRun.getNumNeuFinOut();

        return NeuralCharacteristic.getCharacteristic(mMlpRun.getNeuActiv(niO), mMlpRun.getNeuActiv(nfO));
    }

    public NeuralCharacteristic processMail(InputStream inputStream) {
        String filteredMail = mMailFilter.filterMail(inputStream);
        return process(filteredMail);
    }

    public NeuralCharacteristic processMail(File file) {
        String filePath = file.getAbsolutePath();
        String filteredMail = mMailFilter.filterMail(filePath);
        return process(filteredMail);
    }

}
