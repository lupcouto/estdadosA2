package unitins.br;

public class ArvoreBinaria<T extends Comparable<T>> implements ArvoreBinariaADT<T> {

    private No<T> raiz;
    private int tamanho; // quantidade de nós
    private int totalRegistros; // quantidade de registros armazenados

    public ArvoreBinaria() {
        this.raiz = null;
        this.tamanho = 0;
        this.totalRegistros = 0;
    }

    @Override
    public void inserir(T chave, PerfilEleitor registro) {
        raiz = inserirRecursivo(raiz, chave, registro);
    }

    private No<T> inserirRecursivo(No<T> atual, T chave, PerfilEleitor registro) {

        // caso base
        if (atual == null) {
            No<T> novo = new No<>(chave);
            novo.novoRegistro(registro);
            tamanho++;
            totalRegistros++;
            return novo;
        }
        
        int cmp = chave.compareTo(atual.chave);

        // chave igual = adiciona um registro
        if (cmp == 0) {
            atual.novoRegistro(registro);
            totalRegistros++;
            return atual;
        }

        if (cmp < 0) {
            atual.esquerda = inserirRecursivo(atual.esquerda, chave, registro);
        } else {
            atual.direita = inserirRecursivo(atual.direita, chave, registro);
        }

        return atual;
    }

    @Override
    public PerfilEleitor[] buscar(T chave) {
        No<T> no = buscarNo(raiz, chave);

        if (no == null) {
            return null;
        }

        return no.registros;
    }

    private No<T> buscarNo(No<T> atual, T chave) {
        if (atual == null) {
            return null;
        }

        int cmp = chave.compareTo(atual.chave);

        if (cmp == 0) {
            return atual;
        }

        if (cmp < 0) {
            return buscarNo(atual.esquerda, chave);
        }

        return buscarNo(atual.direita, chave);
    }

    @Override
    public boolean contem(T chave) {
        return buscarNo(raiz, chave) != null;
    }

    @Override
    public int tamanho() {
        return tamanho;
    }

    @Override
    public int totalRegistros() {
        return totalRegistros;
    }

    @Override
    public boolean estaVazia() {
        return raiz == null;
    }

    @Override
    public int altura() {
        return alturaRecursiva(raiz);
    }

    private int alturaRecursiva(No<T> atual) {
        if (atual == null) {
            return 0;
        }

        int alturaEsquerda = alturaRecursiva(atual.esquerda);
        int alturaDireita = alturaRecursiva(atual.direita);

        return 1 + Math.max(alturaEsquerda, alturaDireita);
    }

    @Override
    public T[] emOrdem() {
        @SuppressWarnings("unchecked")
        T[] lista = (T[]) new Comparable[tamanho];
        index = 0;
        preencherEmOrdem(raiz, lista);
        
        return lista;
    }

    private int index = 0;

    private void preencherEmOrdem(No<T> atual, T[] lista) {
        if (atual == null) {
            return;
        }

        preencherEmOrdem(atual.esquerda, lista);
        lista[index++] = atual.chave;
        preencherEmOrdem(atual.direita, lista);
    }

    @Override
    public void limpar() {
        raiz = null;
        tamanho = 0;
        totalRegistros = 0;
    }
    
}