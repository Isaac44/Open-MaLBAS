package br.edu.unifei.gpesc.neural.mlp.core;


//package mlp;



import br.edu.unifei.gpesc.neural.mlp.util.LinkTrain;
import br.edu.unifei.gpesc.neural.mlp.util.NeuronTrain;
import br.edu.unifei.gpesc.neural.mlp.util.Result;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.*;
import java.util.Formatter;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;



/**
 *
 * @author Otavio Carpinteiro - GPESC
 */
@SuppressWarnings({"UnusedAssignment", "null", "UseSpecificCatch", "override", "StringEquality"})
public class MlpTrain extends Mlp {

   public int ntpatterns;   // numero de padroes de treinamento
   public long seed;   // semente usada para a geracao aleatoria dos bias e dos pesos
   public float lrate;   // taxa de aprendizagem
   public float momentum;   // taxa de momentum
   public NeuronTrain[] neuronio;   // neuronios da rede neural
   public float[] pattern;   // padrao de validacao
   public float[][] tpattern;   // padroes de treinamento
   public LinkTrain[][] conexao;   // conexoes da rede neural



   /**
   * Metodos:
   *
   *    Construtor:
   *        MlpTrain
   *
   *    Demais metodos:
   *        changeBiasWeights
   *        changeRates
   *        computeBedWed
   *        computeDbiasDweights
   *        computeError
   *        computeIncBedWed
   *        computeOutput            - metodo identico ao implementado na classe "MlpRun"
   *        copyFile
   *        createDir                - metodo implementado na classe base "Mlp"
   *        createLink
   *        createNeuron
   *        createPattern            - metodo identico ao implementado na classe "MlpRun"
   *        createTpattern
   *        deleteFile
   *        fLogsig                  - metodo implementado na classe base "Mlp"
   *        fTansig                  - metodo implementado na classe base "Mlp"
   *        genBiasWeights
   *        getPatternError          - metodo identico ao implementado na classe "MlpRun"
   *        getTpatternError
   *        initLink
   *        initNet
   *        initNeuron
   *        inputPattern             - metodo identico ao implementado na classe "MlpRun"
   *        inputTpattern
   *        printArchPar             - metodo implementado na classe base "Mlp"
   *        printBiasWeights         - metodo identico ao implementado na classe "MlpRun"
   *        printLink
   *        printNeuron
   *        printTpattern
   *        readArchPar              - metodo implementado na classe base "Mlp"
   *        readTpattern
   *        resetBedWed
   *        resetNetinputActivation
   *        runValSup
   *        trainByEpoch
   *        trainByPattern
   */



   /**
   * Construtor

   */
   public MlpTrain() {
   }



   /**
   * Atualiza os bias e os pesos.

   */
   public void changeBiasWeights() {

      for (int i = niH1; i <= nfH1; i++) {
         for (int j = niI; j <= nfI; j++)
            conexao[i][j].weight += conexao[i][j].dweight;
         neuronio[i].bias += neuronio[i].dbias;
      }
      for (int i = niH2; i <= nfH2; i++) {
         for (int j = niH1; j <= nfH1; j++)
            conexao[i][j].weight += conexao[i][j].dweight;
         neuronio[i].bias += neuronio[i].dbias;
      }
      for (int i = niO; i <= nfO; i++) {
         for (int j = niH2; j <= nfH2; j++)
            conexao[i][j].weight += conexao[i][j].dweight;
         neuronio[i].bias += neuronio[i].dbias;
      }
   }



   /**
   * Modifica, dependendo da variacao do erro total, a "lrate" e o "momentum".
   * @param errtot - erro total atual dos padroes de treinamento.
   * @param lerrtot - erro total anterior dos padroes de treinamento.
   */
   public void changeRates(float errtot, float lerrtot) {

      if (errtot >= lerrtot) {
         momentum = 0.0f;
         lrate /= 2.0f;
      }
      else {
         lrate *= 1.02f;
      }
   }



   /**
   * Computa o "bed" para cada bias e o "wed" para cada peso.

   */
   public void computeBedWed() {

      for (int i = niH1; i <= nfH1; i++) {
         for (int j = niI; j <= nfI; j++)
            conexao[i][j].wed = neuronio[i].delta * neuronio[j].activation;
         neuronio[i].bed = neuronio[i].delta;
      }
      for (int i = niH2; i <= nfH2; i++) {
         for (int j = niH1; j <= nfH1; j++)
            conexao[i][j].wed = neuronio[i].delta * neuronio[j].activation;
         neuronio[i].bed = neuronio[i].delta;
      }
      for (int i = niO; i <= nfO; i++) {
         for (int j = niH2; j <= nfH2; j++)
            conexao[i][j].wed = neuronio[i].delta * neuronio[j].activation;
         neuronio[i].bed = neuronio[i].delta;
      }
   }



   /**
   * Computa o "dbias" para cada bias e o "dweight" para cada peso.

   */
   public void computeDbiasDweights() {

      // Calculo de dweight e dbias usando momentum
      for (int i = niH1; i <= nfH1; i++) {
         for (int j = niI; j <= nfI; j++)
            conexao[i][j].dweight = lrate * conexao[i][j].wed + momentum * conexao[i][j].dweight;
         neuronio[i].dbias = lrate * neuronio[i].bed + momentum * neuronio[i].dbias;
      }
      for (int i = niH2; i <= nfH2; i++) {
         for (int j = niH1; j <= nfH1; j++)
            conexao[i][j].dweight = lrate * conexao[i][j].wed + momentum * conexao[i][j].dweight;
         neuronio[i].dbias = lrate * neuronio[i].bed + momentum * neuronio[i].dbias;
      }
      for (int i = niO; i <= nfO; i++) {
         for (int j = niH2; j <= nfH2; j++)
            conexao[i][j].dweight = lrate * conexao[i][j].wed + momentum * conexao[i][j].dweight;
         neuronio[i].dbias = lrate * neuronio[i].bed + momentum * neuronio[i].dbias;
      }
   }



   /**
   * Computa o "delta" para cada neuronio.
   * @param ntpat - numero do padrao de treinamento.
   */
   public void computeError(int ntpat) {

      float aux1;
      float aux2;

      // Calculo do delta dos neuronios na camada de saida
      for (int i = niO; i <= nfO; i++) {
         aux1 = tpattern[ntpat][i] - neuronio[i].activation;
         switch (funcO) {
            case 1: {   // linear
               neuronio[i].delta = aux1;
            } break;
            case 2: {   // logsig
               aux2 = neuronio[i].activation;
               neuronio[i].delta = aux1 * aux2 * (1.0f - aux2);
            } break;
            case 3: {   // tansig
               aux2 = neuronio[i].activation;
               neuronio[i].delta = aux1 * (float) (-1.0 * (Math.pow((double) aux2, 2.0) - 1.0));
            } break;
         }
      }

      // Calculo do delta dos neuronios na segunda camada escondida
      for (int i = niH2; i <= nfH2; i++) {
         aux1 = 0.0f;
         for (int k = niO; k <= nfO; k++)
            aux1 += neuronio[k].delta * conexao[k][i].weight;
         switch (funcH2) {
            case 1: {   // linear
               neuronio[i].delta = aux1;
            } break;
            case 2: {   // logsig
               aux2 = neuronio[i].activation;
               neuronio[i].delta = aux1 * aux2 * (1.0f - aux2);
            } break;
            case 3: {   // tansig
               aux2 = neuronio[i].activation;
               neuronio[i].delta = aux1 * (float) (-1.0 * (Math.pow((double) aux2, 2.0) - 1.0));
            } break;
         }
      }

      // Calculo do delta dos neuronios na primeira camada escondida
      for (int i = niH1; i <= nfH1; i++) {
         aux1 = 0.0f;
         for (int k = niH2; k <= nfH2; k++)
            aux1 += neuronio[k].delta * conexao[k][i].weight;
         switch (funcH1) {
            case 1: {   // linear
               neuronio[i].delta = aux1;
            } break;
            case 2: {   // logsig
               aux2 = neuronio[i].activation;
               neuronio[i].delta = aux1 * aux2 * (1.0f - aux2);
            } break;
            case 3: {   // tansig
               aux2 = neuronio[i].activation;
               neuronio[i].delta = aux1 * (float) (-1.0 * (Math.pow((double) aux2, 2.0) - 1.0));
            } break;
         }
      }
   }



   /**
   * Computa o incremento de "bed" para cada bias e o de "wed" para cada peso.

   */
   public void computeIncBedWed() {

      for (int i = niH1; i <= nfH1; i++) {
         for (int j = niI; j <= nfI; j++)
            conexao[i][j].wed += neuronio[i].delta * neuronio[j].activation;
         neuronio[i].bed += neuronio[i].delta;
      }
      for (int i = niH2; i <= nfH2; i++) {
         for (int j = niH1; j <= nfH1; j++)
            conexao[i][j].wed += neuronio[i].delta * neuronio[j].activation;
         neuronio[i].bed += neuronio[i].delta;
      }
      for (int i = niO; i <= nfO; i++) {
         for (int j = niH2; j <= nfH2; j++)
            conexao[i][j].wed += neuronio[i].delta * neuronio[j].activation;
         neuronio[i].bed += neuronio[i].delta;
      }
   }



   /**
   * Computa a ativacao dos neuronios da camada de saida da rede neural.

   */
   public void computeOutput() {

      // Calculo da ativacao dos neuronios na primeira camada escondida
      for (int i = niH1; i <= nfH1; i++) {
         neuronio[i].netinput = neuronio[i].bias;
         for (int j = niI; j <= nfI; j++)
            neuronio[i].netinput += conexao[i][j].weight * neuronio[j].activation;
         switch (funcH1) {
            case 1: {   // linear
               neuronio[i].activation = neuronio[i].netinput;
            } break;
            case 2: {   // logsig
               neuronio[i].activation = fLogsig(neuronio[i].netinput);
            } break;
            case 3: {   // tansig
               neuronio[i].activation = fTansig(neuronio[i].netinput);
            } break;
         }
      }

      // Calculo da ativacao dos neuronios na segunda camada escondida
      for (int i = niH2; i <= nfH2; i++) {
         neuronio[i].netinput = neuronio[i].bias;
         for (int j = niH1; j <= nfH1; j++)
            neuronio[i].netinput += conexao[i][j].weight * neuronio[j].activation;
         switch (funcH2) {
            case 1: {   // linear
               neuronio[i].activation = neuronio[i].netinput;
            } break;
            case 2: {   // logsig
               neuronio[i].activation = fLogsig(neuronio[i].netinput);
            } break;
            case 3: {   // tansig
               neuronio[i].activation = fTansig(neuronio[i].netinput);
            } break;
         }
      }

      // Calculo da ativacao dos neuronios na camada de saida
      for (int i = niO; i <= nfO; i++) {
         neuronio[i].netinput = neuronio[i].bias;
         for (int j = niH2; j <= nfH2; j++)
            neuronio[i].netinput += conexao[i][j].weight * neuronio[j].activation;
         switch (funcO) {
            case 1: {   // linear
               neuronio[i].activation = neuronio[i].netinput;
            } break;
            case 2: {   // logsig
               neuronio[i].activation = fLogsig(neuronio[i].netinput);
            } break;
            case 3: {   // tansig
               neuronio[i].activation = fTansig(neuronio[i].netinput);
            } break;
         }
      }
   }



   /**
   * Copia o arquivo "sourceFileName" para o arquivo "targetFileName".
   * Se o arquivo "targetFileName" ja' existir, ele e' sobrescrito.
   * @param sourceFileName - nome do arquivo a ser copiado.
   * @param targetFileName - nome do novo arquivo a ser criado.
   */
   public void copyFile(String sourceFileName, String targetFileName) {

      try {
         Path source = Paths.get(sourceFileName);
         Path target = Paths.get(targetFileName);
         Path result;
         result = Files.copy(source, target, REPLACE_EXISTING, COPY_ATTRIBUTES);
      }  // fim try
      catch (Exception e) {
         System.err.printf("\nErro na copia do arquivo: %s\n", sourceFileName);
         System.exit(1);
      }
   }



   /**
   * Cria o diretorio "dirName", caso nao exista. Caso o diretorio exista:
   * (a) seus arquivos usuais sao removidos; (b) seus subdiretorios e os arquivos
   * destes subdiretorios NAO sao removidos.
   * @param dirName - nome do diretorio a ser criado.
   * public void createDir(String dirName) - implementado na classe base "Mlp".
   */



   /**
   * Cria as conexoes da rede neural.

   */
   public void createLink() {

      conexao = new LinkTrain[ntneurons][ntneurons];
      for (int i = 0; i < ntneurons; i++) {
         for (int j = 0; j < ntneurons; j++)
            conexao[i][j] = new LinkTrain();
      }
   }



   /**
   * Cria os neuronios da rede neural.

   */
   public void createNeuron() {

      neuronio = new NeuronTrain[ntneurons];
      for (int i = 0; i < ntneurons; i++)
         neuronio[i] = new NeuronTrain();
   }



   /**
   * Cria os padroes de teste ou de validacao da rede neural.

   */
   public void createPattern() {

      pattern = new float[ntneurons];
   }



   /**
   * Cria os padroes de treinamento da rede neural.

   */
   public void createTpattern() {

      tpattern = new float[mxpat][ntneurons];
   }



   /**
   * Remove o arquivo "fileName", caso exista.
   * @param fileName - nome do arquivo a ser removido.
   */
   public void deleteFile(String fileName) {

      try {
         File arq = new File(fileName);
         if (arq.exists())
            arq.delete();
      }  // fim try
      catch (Exception e) {
         System.err.printf("\nErro na remocao do arquivo: %s\n", fileName);
         System.exit(1);
      }
   }



   /**
   * Implementa a funcao de transferencia logsig.
   * @param x - o valor de "x".
   * @return y - o valor da funcao sobre "x".
   * public float fLogsig(float x) - implementado na classe base "Mlp".
   */



   /**
   * Implementa a funcao de transferencia tansig.
   * @param x - o valor de "x".
   * @return y - o valor da funcao sobre "x".
   * public float fTansig(float x) - implementado na classe base "Mlp".
   */



   /**
   * Gera aleatoriamente bias e pesos para a rede neural.

   */
   public void genBiasWeights() {

      Random rnd = new Random(seed);

      // Gera os bias e os pesos
      for (int i = niH1; i <= nfH1; i++) {
         neuronio[i].bias = rnd.nextFloat() * mxwei;
         if (!rnd.nextBoolean())
            neuronio[i].bias *= -1.0f;
         for (int j = niI; j <= nfI; j++) {
            conexao[i][j].weight = rnd.nextFloat() * mxwei;
            if (!rnd.nextBoolean())
               conexao[i][j].weight *= -1.0f;
         }
      }
      for (int i = niH2; i <= nfH2; i++) {
         neuronio[i].bias = rnd.nextFloat() * mxwei;
         if (!rnd.nextBoolean())
            neuronio[i].bias *= -1.0f;
         for (int j = niH1; j <= nfH1; j++) {
            conexao[i][j].weight = rnd.nextFloat() * mxwei;
            if (!rnd.nextBoolean())
               conexao[i][j].weight *= -1.0f;
         }
      }
      for (int i = niO; i <= nfO; i++) {
         neuronio[i].bias = rnd.nextFloat() * mxwei;
         if (!rnd.nextBoolean())
            neuronio[i].bias *= -1.0f;
         for (int j = niH2; j <= nfH2; j++) {
            conexao[i][j].weight = rnd.nextFloat() * mxwei;
            if (!rnd.nextBoolean())
               conexao[i][j].weight *= -1.0f;
         }
      }

      // Gera uma nova semente para o proximo treino
      seed = rnd.nextLong();
   }



   /**
   * Calcula o erro do padrao armazenado em "pattern".

   * @return - o valor do erro.
   */
   public float getPatternError() {

      float aux1;
      double aux2;

      aux1 = 0.0f;
      for (int i = niO; i <= nfO; i++) {
         aux2 = (double) (pattern[i] - neuronio[i].activation);
         aux1 += (float) Math.abs(aux2);
      }
      return aux1;
   }



   /**
   * Calcula o erro do padrao "ntpat".
   * @param ntpat - numero do padrao.
   * @return - o valor do erro.
   */
   public float getTpatternError(int ntpat) {

      float aux1;
      double aux2;

      aux1 = 0.0f;
      for (int i = niO; i <= nfO; i++) {
         aux2 = (double) (tpattern[ntpat][i] - neuronio[i].activation);
         aux1 += (float) Math.abs(aux2);
      }
      return aux1;
   }



   /**
   * Inicializa as conexoes da rede neural.

   */
   public void initLink() {

      for (int i = 0; i < ntneurons; i++) {
         for (int j = 0; j < ntneurons; j++) {
            conexao[i][j].weight = 0.0f;
            conexao[i][j].wed = 0.0f;
            conexao[i][j].dweight = 0.0f;
         }
      }
   }



   /**
   * Realiza os procedimentos de inicializacao da rede neural.
   * @param archFileName - nome do arquivo de onde os parametros da arquitetura da RN serao lidos.
   * @param trainFileName - nome do arquivo de onde os padroes de treinamento serao lidos.
   */
   public void initNet(String archFileName, String trainFileName) {

      // Leitura dos parametros da arquitetura da rede neural
      readArchPar(archFileName);

      // Criacao dos neuronios e conexoes da rede neural
      createNeuron();
      createLink();

      // Inicializacao dos neuronios e conexoes da rede neural
      initNeuron();
      initLink();

      // Criacao dos padroes de validacao e de treinamento
      createPattern();
      createTpattern();

      // Leitura dos padroes de treinamento no arquivo "trainFileName"
      readTpattern(trainFileName);

      // Definicao da semente inicial para a geracao aleatoria dos bias e dos pesos
      if (seedI == 0)
         seed = System.currentTimeMillis();
      else
         seed = seedI;
      Random rnd = new Random(seed);
      seed = rnd.nextLong();

      // Geracao de arquivos de "trace" para verificar a ...
      if (trace) {
         printArchPar("../trace/trace-arch.log");   // leitura dos parametros da RN
         printNeuron("../trace/trace-neuron.log");   // inicializacao dos neuronios
         printLink("../trace/trace-link.log");   // inicializacao das conexoes
         printTpattern("../trace/trace-tpattern.log");   // leitura dos padroes de treinamento
      }
   }



   /**
   * Inicializa os neuronios da rede neural.

   */
   public void initNeuron() {

      for (int i = 0; i < ntneurons; i++) {
         neuronio[i].activation = 0.0f;
         neuronio[i].netinput = 0.0f;
         neuronio[i].bias = 0.0f;
         neuronio[i].delta = 0.0f;
         neuronio[i].bed = 0.0f;
         neuronio[i].dbias = 0.0f;
      }
   }



   /**
   * Entra com um padrao de execucao (ou teste ou validacao) na rede neural.

   */
   public void inputPattern() {

      for (int j = niI; j <= nfI; j++)
         neuronio[j].activation = pattern[j];
   }



   /**
   * Entra com o padrao de treinamento na rede neural.
   * @param ntpat - numero do padrao de treinamento.
   */
   public void inputTpattern(int ntpat) {

      for (int j = niI; j <= nfI; j++)
         neuronio[j].activation = tpattern[ntpat][j];
   }



   /**
   * Imprime os parametros da arquitetura da rede neural.
   * @param fileName - nome do arquivo onde os dados serao impressos.
   * public void printArchPar(String fileName) - implementado na classe base "Mlp".
   */



   /**
   * Imprime os bias e os pesos da rede neural.
   * @param fileType - "log": para imprimir os dados no formato de "log".
   * @param fileName - nome do arquivo onde os dados serao impressos.
   */
   public void printBiasWeights(String fileType, String fileName) {

      Formatter output = null;

      // cria o arquivo "fileName"
      try {
         // usa "Locale.US" para evitar "output.format(Locale.US, "...");
         output = new Formatter(fileName, Charset.defaultCharset().toString(), Locale.US);
      }
      catch (Exception e) {
         System.err.printf("\nErro na criacao do arquivo: %s\n", fileName);
         System.exit(1);
      }

      // Grava os bias e pesos da rede neural
      try {
         if (fileType == "log") {
            output.format("\n\nBias e Pesos\n\n");
            for (int i = 0; i < ntneurons; i++) {
               output.format("bias[%d]= %.10f\n", i, neuronio[i].bias);
               for (int j = 0; j < ntneurons; j++)
                  output.format("weight[%d][%d]= %.10f\n", i, j, conexao[i][j].weight);
               output.format("\n\n");
            }
         }
         else {
            for (int i = 0; i < ntneurons; i++) {
               output.format("   %.10f\n", neuronio[i].bias);
               for (int j = 0; j < ntneurons; j++)
                  output.format("   %.10f\n", conexao[i][j].weight);
               output.format("\n\n");
            }
         }
      }  // fim try
      catch (Exception e) {
         System.err.printf("\nErro na gravacao do arquivo: %s\n", fileName);
         System.exit(1);
      }

      // Fecha o arquivo "fileName"
      if (output != null)
         output.close();
   }



   /**
   * Imprime os dados das conexoes da rede neural.
   * @param fileName - nome do arquivo onde os dados serao impressos.
   */
   public void printLink(String fileName) {

      Formatter output = null;

      // cria o arquivo "fileName"
      try {
         // usa "Locale.US" para evitar "output.format(Locale.US, "...");
         output = new Formatter(fileName, Charset.defaultCharset().toString(), Locale.US);
      }
      catch (Exception e) {
         System.err.printf("\nErro na criacao do arquivo: %s\n", fileName);
         System.exit(1);
      }

      // Grava os dados das conexoes
      try {
         output.format("\n\nDados das Conexoes:\n\n\n");
         for (int i = 0; i < ntneurons; i++) {
            for (int j = 0; j < ntneurons; j++) {
               output.format("conexao[%d][%d].weight: %.3f\n", i, j, conexao[i][j].weight);
               output.format("conexao[%d][%d].wed: %.3f\n", i, j, conexao[i][j].wed);
               output.format("conexao[%d][%d].dweight: %.3f\n\n", i, j, conexao[i][j].dweight);
            }
         }
      }  // fim try
      catch (Exception e) {
         System.err.printf("\nErro na gravacao do arquivo: %s\n", fileName);
         System.exit(1);
      }

      // Fecha o arquivo "fileName"
      if (output != null)
         output.close();
   }



   /**
   * Imprime os dados dos neuronios da rede neural.
   * @param fileName - nome do arquivo onde os dados serao impressos.
   */
   public void printNeuron(String fileName) {

      Formatter output = null;

      // cria o arquivo "fileName"
      try {
         // usa "Locale.US" para evitar "output.format(Locale.US, "...");
         output = new Formatter(fileName, Charset.defaultCharset().toString(), Locale.US);
      }
      catch (Exception e) {
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
            output.format("neuronio[%d].delta: %.3f\n", i, neuronio[i].delta);
            output.format("neuronio[%d].bed: %.3f\n", i, neuronio[i].bed);
            output.format("neuronio[%d].dbias: %.3f\n\n", i, neuronio[i].dbias);
         }
      }  // fim try
      catch (Exception e) {
         System.err.printf("\nErro na gravacao do arquivo: %s\n", fileName);
         System.exit(1);
      }

      // Fecha o arquivo "fileName"
      if (output != null)
         output.close();
   }



   /**
   * Imprime os padroes de treinamento.
   * @param fileName - nome do arquivo onde os dados serao impressos.
   */
   public void printTpattern(String fileName) {

      Formatter output = null;

      // cria o arquivo "fileName"
      try {
         // usa "Locale.US" para evitar "output.format(Locale.US, "...");
         output = new Formatter(fileName, Charset.defaultCharset().toString(), Locale.US);
      }
      catch (Exception e) {
         System.err.printf("\nErro na criacao do arquivo: %s\n", fileName);
         System.exit(1);
      }

      // Imprime os padroes
      try {
         output.format("\n\nPadroes de Treinamento:\n\n\n");
         for (int p = 0; p < ntpatterns; p++) {
            output.format("Padrao %d:\n", p);
            for (int j = niI; j <= nfI; j++)
               output.format("   %.4f", tpattern[p][j]);
            output.format("      ");
            for (int j = niO; j <= nfO; j++)
               output.format("   %.4f", tpattern[p][j]);
            output.format("\n\n");
         }
      }  // fim try
      catch (Exception e) {
         System.err.printf("\nErro na gravacao do arquivo: %s\n", fileName);
         System.exit(1);
      }

      // Fecha o arquivo "fileName"
      if (output != null)
         output.close();
   }



   /**
   * Le os parametros da arquitetura da rede neural.
   * @param fileName - nome do arquivo de onde os dados serao lidos.
   * public void readArchPar(String fileName) - implementado na classe base "Mlp".
   */



   /**
   * Le os padroes de treinamento.
   * @param fileName - nome do arquivo de onde os padroes de treinamento serao lidos.
   */
   public void readTpattern(String fileName) {

      Scanner input = null;

      // Abertura do arquivo "fileName"
      try {
         input = new Scanner(new File(fileName));
      }
      catch (Exception e) {
         System.err.printf("\nErro na abertura do arquivo: %s\n", fileName);
         System.exit(1);
      }

      int j = 0;
      int npat = 0;

      // Le os valores das variaveis no arquivo "fileName"
      try {
         input.useLocale(Locale.US);

         while (input.hasNext()) {
            for (j = niI; j <= nfI; j++)
               tpattern[npat][j] = input.nextFloat();
            for (j = niO; j <= nfO; j++)
               tpattern[npat][j] = input.nextFloat();
            npat++;
         }  // fim while
         ntpatterns = npat;
      }  // fim try
      catch (Exception e) {
          System.out.println("e = " + e);
          System.out.println("line="+npat + " column="+j);

         System.err.printf("\nErro na leitura do arquivo: %s\n", fileName);
         System.err.printf("Numero de padroes lidos eh superior ao numero ");
         System.err.printf("maximo de padroes de treinamento (%d)\n", mxpat);
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
   * Inicializa com zeros os valores de bed e wed.

   */
   public void resetBedWed() {

      for (int i = 0; i < ntneurons; i++) {
         for (int j = 0; j < ntneurons; j++)
            conexao[i][j].wed = 0.0f;
         neuronio[i].bed = 0.0f;
      }
   }



   /**
   * Inicializa com zeros os valores de netinput e activation.

   */
   public void resetNetinputActivation() {

      for (int j = 0; j < ntneurons; j++) {
         neuronio[j].netinput = 0.0f;
         neuronio[j].activation = 0.0f;
      }
   }



   /**
   * Executa padroes de validacao, de forma supervisionada, na rede neural. A saida produzida
   * pela rede sobre cada um dos padroes e' comparada com a saida esperada, gerando um erro.
   * O erro total sobre os padroes de validacao e' retornado.
   * @param valFileName - nome do arquivo de onde os padroes de validacao serao lidos.
   * @return - o valor total do erro sobre os padroes de validacao.
   */
   public float runValSup(String valFileName) {

      float perr;   // erro de um padrao de validacao
      float errtot;   // erro total dos padroes de validacao
      Scanner input = null;

      // Abertura do arquivo de padroes de validacao "valFileName"
      try {
         input = new Scanner(new File(valFileName));
      }
      catch (Exception e) {
         System.err.printf("\nErro na abertura do arquivo: %s\n", valFileName);
         System.exit(1);
      }

      // Execucao dos padroes de validacao na rede neural
      errtot = 0.0f;
      while (input.hasNext()) {

         // Leitura do padrao de validacao
         try {
            input.useLocale(Locale.US);
            for (int j = niI; j <= nfI; j++)
               pattern[j] = input.nextFloat();
            for (int j = niO; j <= nfO; j++)
               pattern[j] = input.nextFloat();
         }
         catch (Exception e) {
            System.err.printf("\nErro na leitura do arquivo: %s\n", valFileName);
            input.reset();
            input.close();
            System.exit(1);
         }

         inputPattern();
         computeOutput();
         perr = getPatternError();
         errtot += perr;

      }  // fim while

      // Reinicializa o scanner e fecha o arquivo "valFileName"
      if (input != null) {
         input.reset();
         input.close();
      }

      // Retorna o erro total sobre os padroes de validacao
      return errtot;
   }



   /**
   * Treina, pelo metodo epoch-by-epoch, a rede neural.
   * @param valFileName - nome do arquivo de onde os padroes de validacao serao lidos.
   */
   public void trainByEpoch(String valFileName) {

      int passo;   // cada passo inclui "nepochs" epocas de treinamento
      float tperr;   // erro de um padrao de treinamento
      float errtot;   // erro total dos padroes de treinamento na epoca atual
      float lerrtot;   // erro total dos padroes de treinamento na epoca anterior
      float errtrain;   // erro total dos padroes de treinamento no passo atual
      float lerrtrain;   // erro total dos padroes de treinamento no passo anterior
      float errval;   // erro total dos padroes de validacao no passo atual
      float lerrval;   // erro total dos padroes de validacao no passo anterior
      String rootDir;   // nome do diretorio raiz que contem resultados do treinamento
      String trainDir;   // nome do diretorio que contem resultados de cada treino
      String logFile;   // nome do arquivo de "log" do treinamento
      Result[] resultados;   // resultados dos dez treinos
      Formatter output = null;

      // Criacao do vetor que armazenara' os resultados
      resultados = new Result[11];

      // Criacao do diretorio "rootDir"
      rootDir = "../train/entrada-" + ninputs + "/";
      createDir(rootDir);

      // Treinamento, em 10 vezes, da rede neural, com diferentes pesos iniciais
      for (int treino = 1; treino <= 10; treino++) {

         // Criacao do diretorio "trainDir"
         if (treino < 10)
            trainDir = rootDir + "treino-0";
         else
            trainDir = rootDir + "treino-";
         trainDir = trainDir + treino + "/";
         createDir(trainDir);

         // Criacao do arquivo "logFile" do treino
         logFile = trainDir + "trace-train.log";
         try {
            // usa "Locale.US" para evitar "output.format(Locale.US, "...");
            output = new Formatter(logFile, Charset.defaultCharset().toString(), Locale.US);
         }
         catch (Exception e) {
            System.err.printf("\nErro na criacao do arquivo: %s\n", logFile);
            System.exit(1);
         }

         // Geracao dos bias e pesos iniciais
         genBiasWeights();
         printBiasWeights("", trainDir + "wini.dat");

         // Geracao do arquivo de "trace" para verificar a geracao dos bias e pesos iniciais
         if (trace)
            printBiasWeights("log", trainDir + "trace-weights-ini.log");

         // Inicializacao da taxa de aprendizagem e momentum
         lrate = lrateI;
         momentum = momentumI;

         // Inicializacao dos erros sobre o conjunto de treinamento
         errtot = 1.0e+30f;
         lerrtot = 1.0e+30f;
         errtrain = 1.0e+30f;
         lerrtrain = 1.0e+30f;

         // Obtem o erro sobre o conjunto de validacao
         errval = runValSup(valFileName);
         lerrval = 1.0e+30f;

         // Treinamento de cada passo de "nepochs" epocas
         passo = 0;
         try {
            while (errval < lerrval) {
               passo++;
               output.format("\n\n\n\n\nPasso %d:\n\n\n", passo);

               // Salvamento dos bias e pesos iniciais do passo
               printBiasWeights("", trainDir + "wini-" + passo + ".dat");

               // Treinamento das "nepochs" epocas
               for (int ep = 1; ep <= nepochs; ep++) {
                  errtot = 0.0f;
                  resetBedWed();
                  resetNetinputActivation();
                  for (int p = 0; p < ntpatterns; p++) {
                     inputTpattern(p);
                     computeOutput();
                     computeError(p);
                     computeIncBedWed();
                     tperr = getTpatternError(p);
                     errtot += tperr;
                  }
                  computeDbiasDweights();
                  changeBiasWeights();
                  changeRates(errtot, lerrtot);
                  output.format("   ===>   Epoca %d:\n", ep);
                  output.format("      ===>   Erro Total (Epoca Anterior)= %.10f\n", lerrtot);
                  output.format("      ===>   Erro Total (Epoca Atual)= %.10f\n", errtot);
                  output.format("      ===>   momentum (Epoca Atual)= %.2f\n", momentum);
                  output.format("      ===>   lrate (Epoca Atual)= %.10f\n\n\n", lrate);
                  lerrtot = errtot;
               }   // fim for-ep

               // Salvamento dos bias e pesos finais do passo
               printBiasWeights("", trainDir + "wfin-" + passo + ".dat");

               // Obtencao do erro sobre o conjunto de treinamento
               lerrtrain = errtrain;
               errtrain = errtot;
               output.format("   ===>   Erro sobre o conjunto de treinamento ");
               output.format("(Passo Anterior)= %.10f\n", lerrtrain);
               output.format("   ===>   Erro sobre o conjunto de treinamento ");
               output.format("(Passo Atual)= %.10f\n", errtrain);

               // Obtencao do erro sobre o conjunto de validacao
               lerrval = errval;
               errval = runValSup(valFileName);
               output.format("   ===>   Erro sobre o conjunto de validacao ");
               output.format("(Passo Anterior)= %.10f\n", lerrval);
               output.format("   ===>   Erro sobre o conjunto de validacao ");
               output.format("(Passo Atual)= %.10f\n", errval);

            }   // fim while
         }  // fim try
         catch (Exception e) {
            System.err.printf("\nErro na gravacao do arquivo: %s\n", logFile);
            System.exit(1);
         }

         // Fechamento do arquivo "logFile" do treino
         if (output != null)
            output.close();

         // Remocao do ultimo passo, posto que seu erro e' maior do que o do passo anterior
         if (passo > 1) {
            deleteFile(trainDir + "wini-" + passo + ".dat");
            deleteFile(trainDir + "wfin-" + passo + ".dat");
            passo--;
            copyFile(trainDir + "wfin-" + passo + ".dat", trainDir + "wfin.dat");
         }

         // Salvamento dos dados do treino
         resultados[treino] = new Result();
         resultados[treino].npassos = passo;
         resultados[treino].nepochs = nepochs;
         resultados[treino].eptot = passo * nepochs;
         resultados[treino].errtrain = lerrtrain;
         resultados[treino].errval = lerrval;
         resultados[treino].wini = trainDir + "wini.dat";
         resultados[treino].wfin = trainDir + "wfin.dat";

      }   // fim for-treino

      // Verificacao de qual treino teve melhor desempenho
      int idx = 0;
      float minval = 1.0e+30f;
      for (int i = 1; i <= 10; i++) {
         if (resultados[i].errval < minval) {
            minval = resultados[i].errval;
            idx = i;
         }
      }

      // Salvamento dos resultados dos treinos
      logFile = rootDir + "resultados-treino.log";
      try {
         // usa "Locale.US" para evitar "output.format(Locale.US, "...");
         output = new Formatter(logFile, Charset.defaultCharset().toString(), Locale.US);
      }
      catch (Exception e) {
         System.err.printf("\nErro na criacao do arquivo: %s\n", logFile);
         System.exit(1);
      }
      try {
         output.format("\n\nTreino com melhor resultado:\n\n\n");
         output.format("Treino %d:\n", idx);
         output.format("   Numero de passos de treinamento:  %d\n", resultados[idx].npassos);
         output.format("   Numero de epocas em cada passo:  %d\n", resultados[idx].nepochs);
         output.format("   Numero total de epocas de treinamento:  %d\n", resultados[idx].eptot);
         output.format("   Erro total sobre os padroes de treinamento:  %.10f\n", resultados[idx].errtrain);
         output.format("   Erro total sobre os padroes de validacao:  %.10f\n", resultados[idx].errval);
         output.format("   Arquivo com os pesos iniciais do treinamento:  %s\n", resultados[idx].wini);
         output.format("   Arquivo com os pesos finais do treinamento:  %s\n", resultados[idx].wfin);
         output.format("\n\n\n\n==================================================\n\n\n");
         output.format("\n\nDemais treinos:\n\n\n");
         for (int i = 1; i <= 10; i++) {
            if (i != idx) {
               output.format("Treino %d:\n", i);
               output.format("   Numero de passos de treinamento:  %d\n", resultados[i].npassos);
               output.format("   Numero de epocas em cada passo:  %d\n", resultados[i].nepochs);
               output.format("   Numero total de epocas de treinamento:  %d\n", resultados[i].eptot);
               output.format("   Erro total sobre padroes de treinamento:  %.10f\n", resultados[i].errtrain);
               output.format("   Erro total sobre padroes de validacao:  %.10f\n", resultados[i].errval);
               output.format("   Arquivo com os pesos iniciais do treinamento:  %s\n", resultados[i].wini);
               output.format("   Arquivo com os pesos finais do treinamento:  %s\n\n\n", resultados[i].wfin);
            }
         }
      }  // fim try
      catch (Exception e) {
         System.err.printf("\nErro na gravacao do arquivo: %s\n", logFile);
         System.exit(1);
      }
      if (output != null)
         output.close();

   }



   /**
   * Treina, pelo metodo pattern-by-pattern, a rede neural.
   * @param valFileName - nome do arquivo de onde os padroes de validacao serao lidos.
   */
   public void trainByPattern(String valFileName) {

      int passo;   // cada passo inclui "nepochs" epocas de treinamento
      float tperr;   // erro de um padrao de treinamento
      float errtot;   // erro total dos padroes de treinamento na epoca atual
      float lerrtot;   // erro total dos padroes de treinamento na epoca anterior
      float errtrain;   // erro total dos padroes de treinamento no passo atual
      float lerrtrain;   // erro total dos padroes de treinamento no passo anterior
      float errval;   // erro total dos padroes de validacao no passo atual
      float lerrval;   // erro total dos padroes de validacao no passo anterior
      String rootDir;   // nome do diretorio raiz que contem resultados do treinamento
      String trainDir;   // nome do diretorio que contem resultados de cada treino
      String logFile;   // nome do arquivo de "log" do treinamento
      Result[] resultados;   // resultados dos dez treinos
      Formatter output = null;

      // Criacao do vetor que armazenara' os resultados
      resultados = new Result[11];

      // Criacao do diretorio "rootDir"
      rootDir = "../train/entrada-" + ninputs + "/";
      createDir(rootDir);

      // Treinamento, em 10 vezes, da rede neural, com diferentes pesos iniciais
      for (int treino = 1; treino <= 10; treino++) {

         // Criacao do diretorio "trainDir"
         if (treino < 10)
            trainDir = rootDir + "treino-0";
         else
            trainDir = rootDir + "treino-";
         trainDir = trainDir + treino + "/";
         createDir(trainDir);

         // Criacao do arquivo "logFile" do treino
         logFile = trainDir + "trace-train.log";
         try {
            // usa "Locale.US" para evitar "output.format(Locale.US, "...");
            output = new Formatter(logFile, Charset.defaultCharset().toString(), Locale.US);
         }
         catch (Exception e) {
            System.err.printf("\nErro na criacao do arquivo: %s\n", logFile);
            System.exit(1);
         }

         // Geracao dos bias e pesos iniciais
         genBiasWeights();
         printBiasWeights("", trainDir + "wini.dat");

         // Geracao do arquivo de "trace" para verificar a geracao dos bias e pesos iniciais
         if (trace)
            printBiasWeights("log", trainDir + "trace-weights-ini.log");

         // Inicializacao da taxa de aprendizagem e momentum
         lrate = lrateI;
         momentum = momentumI;

         // Inicializacao dos erros sobre o conjunto de treinamento
         errtot = 1.0e+30f;
         lerrtot = 1.0e+30f;
         errtrain = 1.0e+30f;
         lerrtrain = 1.0e+30f;

         // Obtem o erro sobre o conjunto de validacao
         errval = runValSup(valFileName);
         lerrval = 1.0e+30f;

         // Treinamento de cada passo de "nepochs" epocas
         passo = 0;
         try {
            while (errval < lerrval) {
               passo++;
               output.format("\n\n\n\n\nPasso %d:\n\n\n", passo);

               // Salvamento dos bias e pesos iniciais do passo
               printBiasWeights("", trainDir + "wini-" + passo + ".dat");

               // Treinamento das "nepochs" epocas
               for (int ep = 1; ep <= nepochs; ep++) {
                  errtot = 0.0f;
                  for (int p = 0; p < ntpatterns; p++) {
                     inputTpattern(p);
                     computeOutput();
                     computeError(p);
                     computeBedWed();
                     computeDbiasDweights();
                     changeBiasWeights();
                     tperr = getTpatternError(p);
                     errtot += tperr;
                  }
                  changeRates(errtot, lerrtot);
                  output.format("   ===>   Epoca %d:\n", ep);
                  output.format("      ===>   Erro Total (Epoca Anterior)= %.10f\n", lerrtot);
                  output.format("      ===>   Erro Total (Epoca Atual)= %.10f\n", errtot);
                  output.format("      ===>   momentum (Epoca Atual)= %.2f\n", momentum);
                  output.format("      ===>   lrate (Epoca Atual)= %.10f\n\n\n", lrate);
                  lerrtot = errtot;
               }   // fim for-ep

               // Salvamento dos bias e pesos finais do passo
               printBiasWeights("", trainDir + "wfin-" + passo + ".dat");

               // Obtencao do erro sobre o conjunto de treinamento
               lerrtrain = errtrain;
               errtrain = errtot;
               output.format("   ===>   Erro sobre o conjunto de treinamento ");
               output.format("(Passo Anterior)= %.10f\n", lerrtrain);
               output.format("   ===>   Erro sobre o conjunto de treinamento ");
               output.format("(Passo Atual)= %.10f\n", errtrain);

               // Obtencao do erro sobre o conjunto de validacao
               lerrval = errval;
               errval = runValSup(valFileName);
               output.format("   ===>   Erro sobre o conjunto de validacao ");
               output.format("(Passo Anterior)= %.10f\n", lerrval);
               output.format("   ===>   Erro sobre o conjunto de validacao ");
               output.format("(Passo Atual)= %.10f\n", errval);

            }   // fim while
         }  // fim try
         catch (Exception e) {
            System.err.printf("\nErro na gravacao do arquivo: %s\n", logFile);
            System.exit(1);
         }

         // Fechamento do arquivo "logFile" do treino
         if (output != null)
            output.close();

         // Remocao do ultimo passo, posto que seu erro e' maior do que o do passo anterior
         if (passo > 1) {
            deleteFile(trainDir + "wini-" + passo + ".dat");
            deleteFile(trainDir + "wfin-" + passo + ".dat");
            passo--;
            copyFile(trainDir + "wfin-" + passo + ".dat", trainDir + "wfin.dat");
         }

         // Salvamento dos dados do treino
         resultados[treino] = new Result();
         resultados[treino].npassos = passo;
         resultados[treino].nepochs = nepochs;
         resultados[treino].eptot = passo * nepochs;
         resultados[treino].errtrain = lerrtrain;
         resultados[treino].errval = lerrval;
         resultados[treino].wini = trainDir + "wini.dat";
         resultados[treino].wfin = trainDir + "wfin.dat";

      }   // fim for-treino

      // Verificacao de qual treino teve melhor desempenho
      int idx = 0;
      float minval = 1.0e+30f;
      for (int i = 1; i <= 10; i++) {
         if (resultados[i].errval < minval) {
            minval = resultados[i].errval;
            idx = i;
         }
      }

      // Salvamento dos resultados dos treinos
      logFile = rootDir + "resultados-treino.log";
      try {
         // usa "Locale.US" para evitar "output.format(Locale.US, "...");
         output = new Formatter(logFile, Charset.defaultCharset().toString(), Locale.US);
      }
      catch (Exception e) {
         System.err.printf("\nErro na criacao do arquivo: %s\n", logFile);
         System.exit(1);
      }
      try {
         output.format("\n\nTreino com melhor resultado:\n\n\n");
         output.format("Treino %d:\n", idx);
         output.format("   Numero de passos de treinamento:  %d\n", resultados[idx].npassos);
         output.format("   Numero de epocas em cada passo:  %d\n", resultados[idx].nepochs);
         output.format("   Numero total de epocas de treinamento:  %d\n", resultados[idx].eptot);
         output.format("   Erro total sobre os padroes de treinamento:  %.10f\n", resultados[idx].errtrain);
         output.format("   Erro total sobre os padroes de validacao:  %.10f\n", resultados[idx].errval);
         output.format("   Arquivo com os pesos iniciais do treinamento:  %s\n", resultados[idx].wini);
         output.format("   Arquivo com os pesos finais do treinamento:  %s\n", resultados[idx].wfin);
         output.format("\n\n\n\n==================================================\n\n\n");
         output.format("\n\nDemais treinos:\n\n\n");
         for (int i = 1; i <= 10; i++) {
            if (i != idx) {
               output.format("Treino %d:\n", i);
               output.format("   Numero de passos de treinamento:  %d\n", resultados[i].npassos);
               output.format("   Numero de epocas em cada passo:  %d\n", resultados[i].nepochs);
               output.format("   Numero total de epocas de treinamento:  %d\n", resultados[i].eptot);
               output.format("   Erro total sobre padroes de treinamento:  %.10f\n", resultados[i].errtrain);
               output.format("   Erro total sobre padroes de validacao:  %.10f\n", resultados[i].errval);
               output.format("   Arquivo com os pesos iniciais do treinamento:  %s\n", resultados[i].wini);
               output.format("   Arquivo com os pesos finais do treinamento:  %s\n\n\n", resultados[i].wfin);
            }
         }
      }  // fim try
      catch (Exception e) {
         System.err.printf("\nErro na gravacao do arquivo: %s\n", logFile);
         System.exit(1);
      }
      if (output != null)
         output.close();

   }

}
