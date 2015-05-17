package br.edu.unifei.gpesc.neural.mlp.util;

/**
 *
 * @author Otavio Carpinteiro - GPESC
 */
public class NeuronTrain {

    public float activation;   // valor da ativacao do neuronio
    public float netinput;   // netinput do neuronio
    public float bias;   // conexao bias para o neuronio
    public float delta;   // derivada do erro do neuronio
    public float bed;   // derivada de erro da conexao bias
    public float dbias;   // delta do bias

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
