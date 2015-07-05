package br.edu.unifei.gpesc.neural.mlp.util;

/**
 *
 * @author Otavio Carpinteiro - GPESC
 */
public class NeuronRun {

    public double activation;   // valor da ativacao do neuronio
    public double netinput;   // netinput do neuronio
    public double bias;   // conexao bias para o neuronio

    /**
     * Construtor
     *
     * @param - sem parametros.
     */
    public NeuronRun() {
    }

   //
    // Por questoes de desempenho:
    //    1- os atributos sao publicos;
    //    2- nao ha' funcoes get e set;
    //    3- nao foi criada uma classe NeuronRunArray (vetor de neuronios).
    //
}
