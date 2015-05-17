package br.edu.unifei.gpesc.neural.mlp.core;

//package mlp;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Formatter;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Otavio Carpinteiro - GPESC
 */
public class Principal {

    private static int operacao;   // tipo de operacao solicitada aa rede neural

    /**
     * Metodos: main printOperation readOperation
     */
    /**
     * Metodo principal.
     *
     * @param args - argumentos passados na linha de comando, se exsitirem.
     */
    public static void main(String[] args) {

        // Constantes
        final String traceDirName = "neural/trace/";
        final String opFileName = "neural/dat/operation.dat";
        final String logFileName = "neural/trace/trace-operation.log";
        final String archFileName = "neural/dat/data.dat";
        final String weiFileName = "neural/dat/wfin.dat";
        final String trainFileName = "neural/dat/tpat.dat";
        final String valFileName = "neural/dat/vpat.dat";
        final String testFileName = "neural/dat/pat.dat";

        // Cria o diretorio para armazenar arquivos de "trace"
        try {
            File dir = new File(traceDirName);
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
            System.err.printf("\nErro na criacao do diretorio: %s\n", traceDirName);
            System.exit(1);
        }

        // Leitura da operacao
        readOperation(opFileName);

        // Descomentar a linha abaixo para verificar a leitura da operacao
        printOperation(logFileName);

        // Executa a operacao solicitada aa rede neural
        switch (operacao) {

            case 1: {   // treinar (pattern basis);

                // Criacao da rede neural
                MlpTrain mlp = new MlpTrain();

            // Inicializacao da rede neural. Compreende:
                //    (a) leitura dos parametros da arquitetura;
                //    (b) criacao e inicializacao dos neuronios e conexoes;
                //    (c) criacao dos padroes de validacao e de treinamento;
                //    (d) leitura dos padroes de treinamento;
                //    (e) geracao de arquivos de "trace".
                mlp.initNet(archFileName, trainFileName);

                // Treinamento (pattern basis)
                mlp.trainByPattern(valFileName);
            }
            break;

            case 2: {   // treinar (epoch basis);

                // Criacao da rede neural
                MlpTrain mlp = new MlpTrain();

            // Inicializacao da rede neural. Compreende:
                //    (a) leitura dos parametros da arquitetura;
                //    (b) criacao e inicializacao dos neuronios e conexoes;
                //    (c) criacao dos padroes de validacao e de treinamento;
                //    (d) leitura dos padroes de treinamento;
                //    (e) geracao de arquivos de "trace".
                mlp.initNet(archFileName, trainFileName);

                // Treinamento (epoch basis)
                mlp.trainByEpoch(valFileName);
            }
            break;

            case 3: {   // executar (supervisionado);

                // Criacao da rede neural
                MlpRun mlp = new MlpRun();

            // Inicializacao da rede neural. Compreende:
                //    (a) leitura dos parametros da arquitetura;
                //    (b) criacao e inicializacao dos neuronios e conexoes;
                //    (c) criacao dos padroes de teste;
                //    (d) leitura dos valores dos bias e dos pesos;
                //    (e) geracao de arquivos de "trace".
                mlp.initNet(archFileName, weiFileName);

                // Execucao (supervisionado)
                mlp.runTestSup(testFileName);
            }
            break;

            case 4: {   // executar (nao-supervisionado);

                // Criacao da rede neural
                MlpRun mlp = new MlpRun();

            // Inicializacao da rede neural. Compreende:
                //    (a) leitura dos parametros da arquitetura;
                //    (b) criacao e inicializacao dos neuronios e conexoes;
                //    (c) criacao dos padroes de teste;
                //    (d) leitura dos valores dos bias e dos pesos;
                //    (e) geracao de arquivos de "trace".
                mlp.initNet(archFileName, weiFileName);

                // Execucao (nao-supervisionado) de 10 padroes gerados aleatoriamente
                long seed = System.currentTimeMillis();
                Random rnd = new Random(seed);

                // obtem o numero do neuronio inicial e final na camada de entrada
                int niI = mlp.getNumNeuIniInp();
                int nfI = mlp.getNumNeuFinInp();

                // obtem o numero do neuronio inicial e final na camada de saida
                int niO = mlp.getNumNeuIniOut();
                int nfO = mlp.getNumNeuFinOut();

                for (int p = 1; p <= 10; p++) {

                    // armazenar o padrao gerado em "pattern"
                    float aux;
                    for (int j = niI; j <= nfI; j++) {
                        aux = rnd.nextFloat() * 100.0f;
                        if (!rnd.nextBoolean()) {
                            aux *= -1.0f;
                        }
                        mlp.setPattern(j, aux);
                    }

                    // executar o padrao gerado
                    mlp.runTestNonSup();

                    // Imprimir o padrao gerado e a saida produzida pela rede
                    System.out.printf(Locale.US, "\n\n\n");
                    System.out.printf(Locale.US, "Padrao:\n");
                    for (int j = niI; j <= nfI; j++) {
                        System.out.printf(Locale.US, "   %.4f", mlp.getPattern(j));
                    }
                    System.out.printf(Locale.US, "\n   ===> Saida Obtida:  ");
                    for (int j = niO; j <= nfO; j++) {
                        System.out.printf(Locale.US, "   %.4f", mlp.getNeuActiv(j));
                    }
                    System.out.printf(Locale.US, "\n");

                }  // fim for
            }
            break;

        }  // fim switch
    }

    /**
     * Imprime o valor da operacao.
     *
     * @param fileName - nome do arquivo onde os dados serao impressos.
     */
    public static void printOperation(String fileName) {

        Formatter output = null;

        // Cria o arquivo "fileName"
        try {
            // usa "Locale.US" para evitar "output.format(Locale.US, "neural.");
            output = new Formatter(fileName, Charset.defaultCharset().toString(), Locale.US);
        } catch (FileNotFoundException ex) {
            System.err.printf("\nErro na criacao do arquivo: %s\n", fileName);
            System.exit(1);
        } catch (UnsupportedEncodingException e) {
            System.err.printf("\nErro na criacao do arquivo: %s\n", fileName);
            System.exit(1);
        }

        // Grava o valor da operacao
        try {
            output.format("\n\nOperacao\n");
            output.format("   tipo: %d\n\n\n", operacao);
            switch (operacao) {
                case 1: {   // treinar (pattern basis);
                    output.format("//      1- treinar (pattern basis)\n");
                }
                break;
                case 2: {   // treinar (epoch basis);
                    output.format("//      2- treinar (epoch basis)\n");
                }
                break;
                case 3: {   // executar (supervisionado);
                    output.format("//      3- executar (supervisionado)\n");
                }
                break;
                case 4: {   // executar (nao-supervisionado);
                    output.format("//      4- executar (nao-supervisionado)\n");
                }
                break;
            }  // fim switch
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
     * Le o valor da operacao.
     *
     * @param fileName - nome do arquivo de onde os dados serao lidos.
     */
    public static void readOperation(String fileName) {

        String str;
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
            str = input.next();   // le e descarta "Operacao"
            str = input.next();   // le e descarta "tipo:"
            operacao = input.nextInt();
        } catch (Exception e) {
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

}
