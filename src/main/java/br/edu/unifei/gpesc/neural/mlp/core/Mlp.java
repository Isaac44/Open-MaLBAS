package br.edu.unifei.gpesc.neural.mlp.core;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Formatter;
import java.util.Locale;
import java.util.Scanner;

/**
 *
 * @author Otavio Carpinteiro - GPESC
 */
public abstract class Mlp {

    public boolean trace;   // modo trace
    public int ntneurons;   // numero total de neuronios da rede neural

    public int ninputs;   // numero de neuronios na camada de entrada
    public int niI, nfI;   // numero do neuronio inicial e final na camada de entrada

    public int nhiddens1;   // numero de neuronios na primeira camada escondida
    public int niH1, nfH1;   // numero do neuronio inicial e final na primeira camada escondida

    public int nhiddens2;   // numero de neuronios na segunda camada escondida
    public int niH2, nfH2;   // numero do neuronio inicial e final na segunda camada escondida

    public int noutputs;   // numero de neuronios de saida
    public int niO, nfO;   // numero do neuronio inicial e final na camada de saida
    public int funcH1;   // funcao de transferencia na primeira camada escondida
    public int funcH2;   // funcao de transferencia na segunda camada escondida
    public int funcO;   // funcao de transferencia na camada de saida
    public int nepochs;   // numero de epocas em cada passo
    public int mxpat;   // numero maximo de padroes de treinamento
    public long seedI;   // semente inicial usada para a geracao aleatoria dos bias e dos pesos
    public float mxwei;   // valor maximo positivo do peso
    public float lrateI;   // taxa de aprendizagem inicial
    public float momentumI;   // taxa de momentum inicial

    /**
     * Metodos: createDir createLink - abstrato createNeuron - abstrato fLogsig
     * fTansig initLink - abstrato initNeuron - abstrato printArchPar printLink
     * - abstrato printNeuron - abstrato readArchPar
     */
    /**
     * Cria o diretorio "dirName", caso nao exista. Caso o diretorio exista: (a)
     * seus arquivos usuais sao removidos; (b) seus subdiretorios e os arquivos
     * destes subdiretorios NAO sao removidos.
     *
     * @param dirName - nome do diretorio a ser criado.
     */
    public void createDir(String dirName) {

        try {
            File dir = new File(dirName);
            if (!dir.exists()) {
                dir.mkdirs();
            } else {
                File fids[] = dir.listFiles();
                for (int i = 0; i < fids.length; i++) {
                    if (!fids[i].isDirectory()) {
                        fids[i].delete();
                    }
                }
            }
        } // fim try
        catch (Exception e) {
            System.err.printf("\nErro na criacao do diretorio: %s\n", dirName);
            System.exit(1);
        }
    }

    /**
     * Cria as conexoes da rede neural.
     *
     * @param - sem parametros.
     */
    public abstract void createLink();

    /**
     * Cria os neuronios da rede neural.
     *
     * @param - sem parametros.
     */
    public abstract void createNeuron();

    /**
     * Implementa a funcao de transferencia logsig.
     *
     * @param x - o valor de "x".
     * @return y - o valor da funcao sobre "x".
     */
    public float fLogsig(float x) {

        double aux;

        // logsig(x) = 1 / (1 + exp(-x))
        aux = (double) (-1.0 * x);
        return (float) (1.0 / (1.0 + Math.pow(Math.E, aux)));
    }

    /**
     * Implementa a funcao de transferencia tansig.
     *
     * @param x - o valor de "x".
     * @return y - o valor da funcao sobre "x".
     */
    public float fTansig(float x) {

        double aux;

        // tansig(x) = 2 / (1 + exp(-2 * x)) - 1
        aux = (double) (-2.0 * x);
        return (float) (2.0 / (1.0 + Math.pow(Math.E, aux)) - 1.0);
    }

    /**
     * Inicializa as conexoes da rede neural.
     *
     * @param - sem parametros.
     */
    public abstract void initLink();

    /**
     * Inicializa os neuronios da rede neural.
     *
     * @param - sem parametros.
     */
    public abstract void initNeuron();

    /**
     * Imprime os parametros da arquitetura da rede neural.
     *
     * @param fileName - nome do arquivo onde os dados serao impressos.
     */
    public void printArchPar(String fileName) {

        Formatter output = null;

        // cria o arquivo "fileName"
        try {
            // usa "Locale.US" para evitar "output.format(Locale.US, "...");
            output = new Formatter(fileName, Charset.defaultCharset().toString(), Locale.US);
        } catch (Exception e) {
            System.err.printf("\nErro na criacao do arquivo: %s\n", fileName);
            System.exit(1);
        }

        // grava os dados da configuracao da rede neural
        try {
            output.format("\n\nTrace\n");
            output.format("   trace: %b\n", trace);
            output.format("\n\nArquitetura\n");
            output.format("   numero de unidades na camada de entrada: %d\n", ninputs);
            output.format("   numero de unidades na primeira camada escondida: %d\n", nhiddens1);
            output.format("   numero de unidades na segunda camada escondida: %d\n", nhiddens2);
            output.format("   numero de unidades na camada de saida: %d\n", noutputs);
            output.format("   funcao de transferencia na primeira camada escondida: %d", funcH1);
            output.format("   (1- linear;   2- logsig;   3- tansig)\n");
            output.format("   funcao de transferencia na segunda camada escondida: %d", funcH2);
            output.format("   (1- linear;   2- logsig;   3- tansig)\n");
            output.format("   funcao de transferencia na camada de saida: %d", funcO);
            output.format("   (1- linear;   2- logsig;   3- tansig)\n");
            output.format("\n\nGeracao de pesos\n");
            output.format("   semente inicial: %d", seedI);
            output.format("   (0- semente e' gerada aleatoriamente;   ou <valor inteiro>)\n");
            output.format("   valor maximo positivo do peso: %.3f\n", mxwei);
            output.format("\n\nTreinamento\n");
            output.format("   numero maximo de padroes de treinamento: %d\n", mxpat);
            output.format("   numero de epocas a cada passo de treinamento: %d\n", nepochs);
            output.format("   taxa de aprendizagem inicial: %.10f\n", lrateI);
            output.format("   momentum inicial: %.10f\n", momentumI);
        } // fim try
        catch (Exception e) {
            System.err.printf("\nErro na gravacao do arquivo: %s\n", fileName);
            System.exit(1);
        }

        // fecha o arquivo "fileName"
        if (output != null) {
            output.close();
        }
    }

    /**
     * Imprime os dados das conexoes da rede neural.
     *
     * @param fileName - nome do arquivo onde os dados serao impressos.
     */
    public abstract void printLink(String fileName);

    /**
     * Imprime os dados dos neuronios da rede neural.
     *
     * @param fileName - nome do arquivo onde os dados serao impressos.
     */
    public abstract void printNeuron(String fileName);

    /**
     * Le os parametros da arquitetura da rede neural.
     *
     * @param fileName - nome do arquivo de onde os dados serao lidos.
     */
    public void readArchPar(String fileName) {

        String str;
        Scanner input = null;

        // abertura do arquivo "fileName"
        try {
            input = new Scanner(new File(fileName));
        } catch (Exception e) {
            System.err.printf("\nErro na abertura do arquivo: %s\n", fileName);
            System.exit(1);
        }

        // le os valores das variaveis no arquivo "fileName"
        try {
            input.useLocale(Locale.US);
            // le o valor do "Trace"
            str = input.next();   // le e descarta "Trace"
            str = input.nextLine();
            str = input.findInLine(":");   // le e descarta "trace:"
            trace = input.nextBoolean();
            str = input.nextLine();
            // le os valores da "Arquitetura"
            str = input.next();   // le e descarta "Arquitetura"
            str = input.nextLine();
            str = input.findInLine(":");   // le e descarta "numero ... de entrada:"
            ninputs = input.nextInt();
            str = input.nextLine();
            str = input.findInLine(":");   // le e descarta "numero ... escondida:"
            nhiddens1 = input.nextInt();
            str = input.nextLine();
            str = input.findInLine(":");   // le e descarta "numero ... escondida:"
            nhiddens2 = input.nextInt();
            str = input.nextLine();
            str = input.findInLine(":");   // le e descarta "numero ... de saida:"
            noutputs = input.nextInt();
            str = input.nextLine();
            str = input.findInLine(":");   // le e descarta "funcao ... escondida:"
            funcH1 = input.nextInt();
            str = input.nextLine();
            str = input.findInLine(":");   // le e descarta "funcao ... escondida:"
            funcH2 = input.nextInt();
            str = input.nextLine();
            str = input.findInLine(":");   // le e descarta "funcao ... saida:"
            funcO = input.nextInt();
            str = input.nextLine();
            // le os valores da "Geracao de pesos"
            str = input.next();   // le e descarta "Geracao de pesos"
            str = input.nextLine();
            str = input.findInLine(":");   // le e descarta "semente inicial:"
            seedI = input.nextLong();
            str = input.nextLine();
            str = input.findInLine(":");   // le e descarta "valor ... peso:"
            mxwei = input.nextFloat();
            str = input.nextLine();
            // le os valores do "Treinamento"
            str = input.next();   // le e descarta "Treinamento"
            str = input.nextLine();
            str = input.findInLine(":");   // le e descarta "numero ... treinamento:"
            mxpat = input.nextInt();
            str = input.nextLine();
            str = input.findInLine(":");   // le e descarta "numero ... treinamento:"
            nepochs = input.nextInt();
            str = input.nextLine();
            str = input.findInLine(":");   // le e descarta "taxa de ... inicial:"
            lrateI = input.nextFloat();
            str = input.nextLine();
            str = input.findInLine(":");   // le e descarta "momentum inicial:"
            momentumI = input.nextFloat();
            str = input.nextLine();
        } // fim try
        catch (Exception e) {
            System.err.printf("\nErro na leitura do arquivo: %s\n", fileName);
            input.reset();
            input.close();
            System.exit(1);
        }

        // reinicializa o scanner e fecha o arquivo "fileName"
        if (input != null) {
            input.reset();
            input.close();
        }

        // Calcula o numero total de neuronios da rede neural
        ntneurons = ninputs + nhiddens1 + nhiddens2 + noutputs;

        // Calcula o numero do neuronio inicial e final na camada de entrada
        niI = 0;
        nfI = ninputs - 1;

        // Calcula o numero do neuronio inicial e final na primeira camada escondida
        niH1 = nfI + 1;
        nfH1 = niH1 + nhiddens1 - 1;

        // Calcula o numero do neuronio inicial e final na segunda camada escondida
        niH2 = nfH1 + 1;
        nfH2 = niH2 + nhiddens2 - 1;

        // Calcula o numero do neuronio inicial e final na camada de saida
        niO = nfH2 + 1;
        nfO = niO + noutputs - 1;
    }

}
