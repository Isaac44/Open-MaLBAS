package br.edu.unifei.gpesc.neural.mlp.util;

/**
 *
 * @author Otavio Carpinteiro - GPESC
 */
public class LinkTrain {

    public double weight;   // peso
    public double wed;   // derivada de erro do peso
    public double dweight;   // delta do peso

    /**
     * Construtor
     *
     * @param - sem parametros.
     */
    public LinkTrain() {
    }

   //
    // Por questoes de desempenho:
    //    1- os atributos sao publicos;
    //    2- nao ha' funcoes get e set;
    //    3- nao foi criada uma classe LinkTrainMatrix (matriz de conexoes).
    //
}
