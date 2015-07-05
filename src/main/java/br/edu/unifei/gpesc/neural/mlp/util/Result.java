package br.edu.unifei.gpesc.neural.mlp.util;

/**
 *
 * @author Otavio Carpinteiro - GPESC
 */
public class Result {

    public int npassos;   // numero de passos de treinamento
    public int nepochs;   // numero de epocas em cada passo
    public int eptot;   // numero total de epocas de treinamento
    public double errtrain;   // erro total sobre os padroes de treinamento
    public double errval;   // erro total sobre os padroes de validacao
    public String wini;   // arquivo com os pesos iniciais do treinamento
    public String wfin;   // arquivo com os pesos finais do treinamento

    /**
     * Construtor
     *
     * @param - sem parametros.
     */
    public Result() {
    }

   //
    // Por questoes de desempenho:
    //    1- os atributos sao publicos;
    //    2- nao ha' funcoes get e set.
    //
}
