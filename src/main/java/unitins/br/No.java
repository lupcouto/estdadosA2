package unitins.br;

public class No<T extends Comparable<T>> {

    public T chave;
    public PerfilEleitor[] registros;
    public int qtdRegistros;
    public int capacidade;

    public No<T> esquerda;
    public No<T> direita;

    // criando construtor que inicia o nó com a chave e uma capacidade inicial
    public No(T chave) {
        this.chave = chave;
        this.capacidade = 10; // capacidade inicial
        this.registros = new PerfilEleitor[this.capacidade];
        this.qtdRegistros = 0;
        this.esquerda = null;
        this.direita = null;
    }

    // adicionando um novo registro ao nó
    public void novoRegistro(PerfilEleitor registro) {

        // verificando a capacidade do array, se ele estiver cheio, duplica o tamanho
        if (qtdRegistros >= capacidade) {
            capacidade *= 2;
            PerfilEleitor[] novoArray = new PerfilEleitor[capacidade];
            System.arraycopy(registros, 0, novoArray, 0, qtdRegistros);
            registros = novoArray;
        }

        registros[qtdRegistros++] = registro;
    }
    
}