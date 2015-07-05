package br.edu.unifei.gpesc.neural.mlp.util;

/**
 *
 * @author Otavio Carpinteiro - GPESC
 */
public class NeuronTrain {

    public double activation;   // valor da ativacao do neuronio
    public double netinput;   // netinput do neuronio
    public double bias;   // conexao bias para o neuronio
    public double delta;   // derivada do erro do neuronio
    public double bed;   // derivada de erro da conexao bias
    public double dbias;   // delta do bias

    /**
     * Construtor
     *
     * @param - sem parametros.
     */
    public NeuronTrain() {
    }

   //
    // Por questoes de desempenho:
    //    1- os atributos sao publicos;
    //    2- nao ha' funcoes get e set;
    //    3- nao foi criada uma classe NeuronTrainArray (vetor de neuronios).
    //
}
