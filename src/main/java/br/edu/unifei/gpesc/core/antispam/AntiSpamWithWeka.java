/*
 * Copyright (C) 2017 - GEPESC - Universidade Federal de Itajuba
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

import br.edu.unifei.gpesc.core.modules.Filter;
import br.edu.unifei.gpesc.core.modules.Vectorization;
import br.edu.unifei.gpesc.core.statistic.Characteristics;
import br.edu.unifei.gpesc.evaluation.TimeMark;
import br.edu.unifei.gpesc.util.TraceLog;
import io.github.marcelovca90.data.ClassType;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author Isaac C. Ferreira
 */
public class AntiSpamWithWeka implements AntiSpam {

    private final Classifier mClassifier;
    private final Filter mFilter = new Filter();
    private final Vectorization mVectorization;

    private final Instance mInstance;
    
    public AntiSpamWithWeka(Classifier classifier, Characteristics<String> characteristics) {
        mClassifier = classifier;
        mVectorization = new Vectorization(characteristics);
        
        // weka instance
        int numberOfAttributes = characteristics.getObjectCount();
        Instances dataset = new Instances("testdata", createAttributes(numberOfAttributes), 1);
        dataset.setClassIndex(numberOfAttributes);
        
        mInstance = new DenseInstance(numberOfAttributes + 1);
        mInstance.setDataset(dataset);
    }
    
    // copied from io.github.marcelovca90.data.DatasetHelper
    private ArrayList<Attribute> createAttributes(long featureAmount)
    {
        ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        for (long i = 0; i < featureAmount; i++)
            attributes.add(new Attribute("x" + i));
        attributes.add(new Attribute("y", Arrays.asList(ClassType.HAM.name(), ClassType.SPAM.name())));

        return attributes;
    }

    private Result getClassification(double result) {
        if (result == ClassType.SPAM.ordinal()) {
            return Result.SPAM;
        } 
        else if (result == ClassType.HAM.ordinal()) {
            return Result.HAM;
        }        
        return Result.UNKNOWN;
    }

    private Instance toInstance(double[] vector) {
        for (int i = 0; i < vector.length; i++) {
            mInstance.setValue(i, vector[i]);
        }
        return mInstance;
    }
    
    private Result doProcess() throws Exception {
        double[] vector = mVectorization.createVector(mFilter.getOccurrencesMap());
        TimeMark.mark("5. Vetorização");
        
        Instance instance = toInstance(vector);
        instance.setClassMissing();
        
        double result = mClassifier.classifyInstance(instance);
        
        return getClassification(result);        
    }
    
    private Result process() {
        try {
            return doProcess();
        } 
        catch (Exception e) {
            TraceLog.logE(e);
            return Result.UNKNOWN;
        }
    }

    @Override
    public Result processMail(InputStream mailStream) {
        if (mFilter.filterMail(mailStream)) {
            return process();
        }
        return Result.UNKNOWN;
    }

    @Override
    public Result processMail(File mailFile) {
        if (mFilter.filterMail(mailFile)) {
            return process();
        }
        return Result.UNKNOWN;
    }

}
