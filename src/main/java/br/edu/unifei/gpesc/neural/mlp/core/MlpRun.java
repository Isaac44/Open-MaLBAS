package br.edu.unifei.gpesc.neural.mlp.core;

import br.edu.unifei.gpesc.neural.mlp.util.LinkRun;
import br.edu.unifei.gpesc.neural.mlp.util.NeuronRun;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Formatter;
import java.util.Locale;
import java.util.Scanner;

/**
 *
 * @author Otavio Carpinteiro - GPESC
 */
@SuppressWarnings({"UnusedAssignment", "null", "UseSpecificCatch", "override", "StringEquality"})
public class MlpRun extends Mlp {

    public NeuronRun[] neuronio;   // neuronios da rede neural
    public double[] pattern;   // padrao de execucao ou de teste
    public LinkRun[][] conexao;   // conexoes da rede neural

    /**
     * Metodos:
     *
     * Construtor: MlpRun
     *
     * Getters e Setters: getNeuActiv getNumNeuFinInp getNumNeuFinOut
     * getNumNeuIniInp getNumNeuIniOut getPattern setPattern
     *
     * Demais metodos: computeOutput - metodo identico ao implementado na classe
     * "MlpTrain" createDir - metodo implementado na classe base "Mlp"
     * createLink createNeuron createPattern - metodo identico ao implementado
     * na classe "MlpTrain" fLogsig - metodo implementado na classe base "Mlp"
     * fTansig - metodo implementado na classe base "Mlp" getPatternError -
     * metodo identico ao implementado na classe "MlpTrain" initBiasWeights
     * initLink initNet initNeuron inputPattern - metodo identico ao
     * implementado na classe "MlpTrain" printArchPar - metodo implementado na
     * classe base "Mlp" printBiasWeights - metodo identico ao implementado na
     * classe "MlpTrain" printLink printNeuron readArchPar - metodo implementado
     * na classe base "Mlp" runTestNonSup runTestSup
     */
    /**
     * Construtor
     *

     */
    public MlpRun() {
    }


    public double getNeuActiv(int neuronNumber) {
        return neuronio[neuronNumber].activation;
    }

    public int getNumNeuFinInp() {
        return nfI;
    }

    public int getNumNeuFinOut() {
        return nfO;
    }

    public int getNumNeuIniInp() {
        return niI;
    }

    public int getNumNeuIniOut() {
        return niO;
    }

    public double getPattern(int neuronNumber) {
        return pattern[neuronNumber];
    }

    public void setPattern(int neuronNumber, double value) {
        pattern[neuronNumber] = value;
    }

    public void setPatternArray(double[] patternArray) {
        for (int i = 0; i < patternArray.length; i++) {
            pattern[i] = patternArray[i];
        }
    }

    public void setPatternArray(int[] patternArray) {
        for (int i = 0; i < patternArray.length; i++) {
            pattern[i] = patternArray[i];
        }
    }

    /**
     * Computa a ativacao dos neuronios da camada de saida da rede neural.
     *

     */
    public void computeOutput() {

        // Calculo da ativacao dos neuronios na primeira camada escondida
        for (int i = niH1; i <= nfH1; i++) {
            neuronio[i].netinput = neuronio[i].bias;
            for (int j = niI; j <= nfI; j++) {
                neuronio[i].netinput += conexao[i][j].weight * neuronio[j].activation;
            }
            switch (funcH1) {
                case 1: {   // linear
                    neuronio[i].activation = neuronio[i].netinput;
                }
                break;
                case 2: {   // logsig
                    neuronio[i].activation = fLogsig(neuronio[i].netinput);
                }
                break;
                case 3: {   // tansig
                    neuronio[i].activation = fTansig(neuronio[i].netinput);
                }
                break;
            }
        }

        // Calculo da ativacao dos neuronios na segunda camada escondida
        for (int i = niH2; i <= nfH2; i++) {
            neuronio[i].netinput = neuronio[i].bias;
            for (int j = niH1; j <= nfH1; j++) {
                neuronio[i].netinput += conexao[i][j].weight * neuronio[j].activation;
            }
            switch (funcH2) {
                case 1: {   // linear
                    neuronio[i].activation = neuronio[i].netinput;
                }
                break;
                case 2: {   // logsig
                    neuronio[i].activation = fLogsig(neuronio[i].netinput);
                }
                break;
                case 3: {   // tansig
                    neuronio[i].activation = fTansig(neuronio[i].netinput);
                }
                break;
            }
        }

        // Calculo da ativacao dos neuronios na camada de saida
        for (int i = niO; i <= nfO; i++) {
            neuronio[i].netinput = neuronio[i].bias;
            for (int j = niH2; j <= nfH2; j++) {
                neuronio[i].netinput += conexao[i][j].weight * neuronio[j].activation;
            }
            switch (funcO) {
                case 1: {   // linear
                    neuronio[i].activation = neuronio[i].netinput;
                }
                break;
                case 2: {   // logsig
                    neuronio[i].activation = fLogsig(neuronio[i].netinput);
                }
                break;
                case 3: {   // tansig
                    neuronio[i].activation = fTansig(neuronio[i].netinput);
                }
                break;
            }
        }
    }

    /**
     * Cria o diretorio "dirName", caso nao exista. Caso o diretorio exista: (a)
     * seus arquivos usuais sao removidos; (b) seus subdiretorios e os arquivos
     * destes subdiretorios NAO sao removidos.
     *
     * @param dirName - nome do diretorio a ser criado. public void
     * createDir(String dirName) - implementado na classe base "Mlp".
     */
    /**
     * Cria as conexoes da rede neural.
     *

     */
    public void createLink() {

        conexao = new LinkRun[ntneurons][ntneurons];
        for (int i = 0; i < ntneurons; i++) {
            for (int j = 0; j < ntneurons; j++) {
                conexao[i][j] = new LinkRun();
            }
        }
    }

    /**
     * Cria os neuronios da rede neural.
     *

     */
    public void createNeuron() {

        neuronio = new NeuronRun[ntneurons];
        for (int i = 0; i < ntneurons; i++) {
            neuronio[i] = new NeuronRun();
        }
    }

    /**
     * Cria os padroes de teste ou de validacao da rede neural.
     *

     */
    public void createPattern() {

        pattern = new double[ntneurons];
    }

    /**
     * Implementa a funcao de transferencia logsig.
     *
     * @param x - o valor de "x".
     * @return y - o valor da funcao sobre "x". public double fLogsig(double x) -
     * implementado na classe base "Mlp".
     */
    /**
     * Implementa a funcao de transferencia tansig.
     *
     * @param x - o valor de "x".
     * @return y - o valor da funcao sobre "x". public double fTansig(double x) -
     * implementado na classe base "Mlp".
     */
    /**
     * Calcula o erro do padrao armazenado em "pattern".
     *

     * @return - o valor do erro.
     */
    public double getPatternError() {

        double aux1;
        double aux2;

        aux1 = 0.0f;
        for (int i = niO; i <= nfO; i++) {
            aux2 = (double) (pattern[i] - neuronio[i].activation);
            aux1 += (double) Math.abs(aux2);
        }
        return aux1;
    }

    /**
     * Inicializa os bias e os pesos da rede neural.
     *
     * @param fileName - nome do arquivo de onde os dados serao lidos.
     */
    public void initBiasWeights(String fileName) {

        Scanner input = null;

        // Abertura do arquivo "fileName"
        try {
            input = new Scanner(new File(fileName));
        } catch (Exception e) {
            System.err.printf("\nErro na abertura do arquivo: %s\n", fileName);
            System.exit(1);
        }

        // Le os valores das variaveis no arquivo "fileName"
        try {
            input.useLocale(Locale.US);
            for (int i = 0; i < ntneurons; i++) {
                neuronio[i].bias = input.nextDouble();
                for (int j = 0; j < ntneurons; j++) {
                    conexao[i][j].weight = input.nextDouble();
                }
            }
        } // fim try
        catch (Exception e) {
            System.err.printf("\nErro na leitura do arquivo: %s\n", fileName);
            input.reset();
            input.close();
            System.exit(1);
        }

        // Reinicializa o scanner e fecha o arquivo "fileName"
        if (input != null) {
            input.reset();
            input.close();
        }
    }

    /**
     * Inicializa as conexoes da rede neural.
     *

     */
    public void initLink() {

        for (int i = 0; i < ntneurons; i++) {
            for (int j = 0; j < ntneurons; j++) {
                conexao[i][j].weight = 0.0f;
            }
        }
    }

    /**
     * Realiza os procedimentos de inicializacao da rede neural.
     *
     * @param archFileName - nome do arquivo de onde os parametros da
     * arquitetura da RN serao lidos.
     * @param weiFileName - nome do arquivo de onde os pesos serao lidos.
     */
    public void initNet(String archFileName, String weiFileName) {

        // Leitura dos parametros da arquitetura da rede neural
        readArchPar(archFileName);

        // Criacao dos neuronios e conexoes da rede neural
        createNeuron();
        createLink();

        // Inicializacao dos neuronios e conexoes da rede neural
        initNeuron();
        initLink();

        // Criacao dos padroes de teste
        createPattern();

        // Leitura dos valores dos bias e dos pesos no arquivo "weiFileName"
        initBiasWeights(weiFileName);

        // Geracao de arquivos de "trace" para verificar a ...
        if (trace) {
            printArchPar("../trace/trace-arch.log");   // leitura dos parametros da RN
            printNeuron("../trace/trace-neuron.log");   // inicializacao dos neuronios
            printLink("../trace/trace-link.log");   // inicializacao das conexoes
            printBiasWeights("log", "../trace/trace-weight.log");   // leitura dos bias e pesos
        }
    }

    /**
     * Inicializa os neuronios da rede neural.
     *

     */
    public void initNeuron() {

        for (int i = 0; i < ntneurons; i++) {
            neuronio[i].activation = 0.0f;
            neuronio[i].netinput = 0.0f;
            neuronio[i].bias = 0.0f;
        }
    }

    /**
     * Entra com um padrao de execucao (ou teste ou validacao) na rede neural.
     *

     */
    public void inputPattern() {

        for (int j = niI; j <= nfI; j++) {
            neuronio[j].activation = pattern[j];
        }
    }

    /**
     * Imprime os parametros da arquitetura da rede neural.
     *
     * @param fileName - nome do arquivo onde os dados serao impressos. public
     * void printArchPar(String fileName) - implementado na classe base "Mlp".
     */
    /**
     * Imprime os bias e os pesos da rede neural.
     *
     * @param fileType - "log": para imprimir os dados no formato de "log".
     * @param fileName - nome do arquivo onde os dados serao impressos.
     */
    public void printBiasWeights(String fileType, String fileName) {

        Formatter output = null;

        // cria o arquivo "fileName"
        try {
            // usa "Locale.US" para evitar "output.format(Locale.US, "...");
            output = new Formatter(fileName, Charset.defaultCharset().toString(), Locale.US);
        } catch (Exception e) {
            System.err.printf("\nErro na criacao do arquivo: %s\n", fileName);
            System.exit(1);
        }

        // Grava os bias e pesos da rede neural
        try {
            if (fileType == "log") {
                output.format("\n\nBias e Pesos\n\n");
                for (int i = 0; i < ntneurons; i++) {
                    output.format("bias[%d]= %.10f\n", i, neuronio[i].bias);
                    for (int j = 0; j < ntneurons; j++) {
                        output.format("weight[%d][%d]= %.10f\n", i, j, conexao[i][j].weight);
                    }
                    output.format("\n\n");
                }
            } else {
                for (int i = 0; i < ntneurons; i++) {
                    output.format("   %.10f\n", neuronio[i].bias);
                    for (int j = 0; j < ntneurons; j++) {
                        output.format("   %.10f\n", conexao[i][j].weight);
                    }
                    output.format("\n\n");
                }
            }
        } // fim try
        catch (Exception e) {
            System.err.printf("\nErro na gravacao do arquivo: %s\n", fileName);
            System.exit(1);
        }

        // Fecha o arquivo "fileName"
        if (output != null) {
            output.close();
        }
    }

    /**
     * Imprime os dados das conexoes da rede neural.
     *
     * @param fileName - nome do arquivo onde os dados serao impressos.
     */
    public void printLink(String fileName) {

        Formatter output = null;

        // cria o arquivo "fileName"
        try {
            // usa "Locale.US" para evitar "output.format(Locale.US, "...");
            output = new Formatter(fileName, Charset.defaultCharset().toString(), Locale.US);
        } catch (Exception e) {
            System.err.printf("\nErro na criacao do arquivo: %s\n", fileName);
            System.exit(1);
        }

        // Grava os dados das conexoes
        try {
            output.format("\n\nDados das Conexoes:\n\n\n");
            for (int i = 0; i < ntneurons; i++) {
                for (int j = 0; j < ntneurons; j++) {
                    output.format("conexao[%d][%d].weight: %.3f\n", i, j, conexao[i][j].weight);
                }
            }
        } // fim try
        catch (Exception e) {
            System.err.printf("\nErro na gravacao do arquivo: %s\n", fileName);
            System.exit(1);
        }

        // Fecha o arquivo "fileName"
        if (output != null) {
            output.close();
        }
    }

    /**
     * Imprime os dados dos neuronios da rede neural.
     *
     * @param fileName - nome do arquivo onde os dados serao impressos.
     */
    public void printNeuron(String fileName) {

        Formatter output = null;

        // cria o arquivo "fileName"
        try {
            // usa "Locale.US" para evitar "output.format(Locale.US, "...");
            output = new Formatter(fileName, Charset.defaultCharset().toString(), Locale.US);
        } catch (Exception e) {
            System.err.printf("\nErro na criacao do arquivo: %s\n", fileName);
            System.exit(1);
        }

        // Grava os dados dos neuronios
        try {
            output.format("\n\nDados dos Neuronios:\n\n\n");
            for (int i = 0; i < ntneurons; i++) {
                output.format("neuronio[%d].activation: %.3f\n", i, neuronio[i].activation);
                output.format("neuronio[%d].netinput: %.3f\n", i, neuronio[i].netinput);
                output.format("neuronio[%d].bias: %.3f\n", i, neuronio[i].bias);
            }
        } // fim try
        catch (Exception e) {
            System.err.printf("\nErro na gravacao do arquivo: %s\n", fileName);
            System.exit(1);
        }

        // Fecha o arquivo "fileName"
        if (output != null) {
            output.close();
        }
    }

    /**
     * Le os parametros da arquitetura da rede neural.
     *
     * @param fileName - nome do arquivo de onde os dados serao lidos. public
     * void readArchPar(String fileName) - implementado na classe base "Mlp".
     */
    /**
     * Executa um padrao de teste, de forma nao-supervisionada, na rede neural.
     * A saida produzida pela rede sobre o padrao de teste "pattern" e'
     * retornada em: "neuronio[niO].activation" ... "neuronio[nfO].activation".
     *

     */
    public void runTestNonSup() {

        // Execucao do padrao de teste na rede neural
        inputPattern();
        computeOutput();
    }

    /**
     * Executa padroes de teste, de forma supervisionada, na rede neural. A
     * saida produzida pela rede sobre cada um dos padroes e' comparada com a
     * saida esperada, gerando um erro. Os padroes de teste e seus respectivos
     * erros sao impressos em um arquivo de "log".
     *
     * @param testFileName - nome do arquivo de onde os padroes de teste serao
     * lidos.
     */
    public void runTestSup(String testFileName) {

        int npat;   // numero de um padrao de teste
        double perr;   // erro de um padrao de teste
        double errtot;   // erro total dos padroes de teste
        String testDir;   // nome do diretorio que contem resultados da execucao
        String logFile;   // nome do arquivo de "log" da execucao
        Scanner input = null;
        Formatter output = null;

        // Criacao do diretorio "testDir"
        testDir = "../test/entrada-" + ninputs + "/";
        createDir(testDir);

        // Criacao do arquivo "logFile" da execucao
        logFile = testDir + "trace-run.log";
        try {
            // usa "Locale.US" para evitar "output.format(Locale.US, "...");
            output = new Formatter(logFile, Charset.defaultCharset().toString(), Locale.US);
        } catch (Exception e) {
            System.err.printf("\nErro na criacao do arquivo: %s\n", logFile);
            System.exit(1);
        }

        // Abertura do arquivo de padroes de teste "testFileName"
        try {
            input = new Scanner(new File(testFileName));
        } catch (Exception e) {
            System.err.printf("\nErro na abertura do arquivo: %s\n", testFileName);
            System.exit(1);
        }

        // Execucao dos padroes de teste na rede neural
        npat = 0;
        errtot = 0.0f;
        while (input.hasNext()) {
            npat++;

            // Leitura do padrao de teste
            try {
                input.useLocale(Locale.US);
                for (int j = niI; j <= nfI; j++) {
                    pattern[j] = input.nextDouble();
                }
                for (int j = niO; j <= nfO; j++) {
                    pattern[j] = input.nextDouble();
                }
            } catch (Exception e) {
                System.err.printf("\nErro na leitura do arquivo: %s\n", testFileName);
                input.reset();
                input.close();
                System.exit(1);
            }

            inputPattern();
            computeOutput();
            perr = getPatternError();
            errtot += perr;

            // Impressao do padrao de teste e de seu erro
            try {
                output.format("Padrao %d:\n", npat);
                for (int j = niI; j <= nfI; j++) {
                    output.format("   %.4f", pattern[j]);
                }
                output.format("\n   ===> Saida Esperada:  ");
                for (int j = niO; j <= nfO; j++) {
                    output.format("   %.4f", pattern[j]);
                }
                output.format("\n   ===> Saida Obtida:  ");
                for (int j = niO; j <= nfO; j++) {
                    output.format("   %.4f", neuronio[j].activation);
                }
                output.format("\n   ===> Erro do padrao de teste:  %.4f\n\n\n\n", perr);
            } // fim try
            catch (Exception e) {
                System.err.printf("\nErro na gravacao do arquivo: %s\n", logFile);
                System.exit(1);
            }

        }  // fim while

        // Impressao do erro total dos padroes de teste
        try {
            output.format("\n\n\nErro total dos padroes de teste:  %.4f\n", errtot);
        } // fim try
        catch (Exception e) {
            System.err.printf("\nErro na gravacao do arquivo: %s\n", logFile);
            System.exit(1);
        }

        // Reinicializa o scanner e fecha o arquivo "testFileName"
        if (input != null) {
            input.reset();
            input.close();
        }

        // Fecha o arquivo "logFile"
        if (output != null) {
            output.close();
        }
    }

}
