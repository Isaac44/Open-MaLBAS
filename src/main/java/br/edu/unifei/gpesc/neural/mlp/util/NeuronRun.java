package br.edu.unifei.gpesc.neural.mlp.util;

/**
 *
 * @author Otavio Carpinteiro - GPESC
 */
public class NeuronRun {

    public float activation;   // valor da ativacao do neuronio
    public float netinput;   // netinput do neuronio
    public float bias;   // conexao bias para o neuronio

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
