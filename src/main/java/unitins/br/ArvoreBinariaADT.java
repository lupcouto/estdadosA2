package unitins.br;

/**
 * TAD (Tipo Abstrato de Dados) para Árvore Binária.
 *
 * Esta interface define as operações básicas que uma árvore binária
 * deve implementar para o sistema de consulta de eleitorado.
 *
 * Os alunos devem criar uma classe que implemente esta interface.
 * Exemplos: ArvoreBinariaBusca, ArvoreAVL, ArvoreRubroNegra, etc.
 *
 * @param <T> Tipo da chave de busca (ex: Integer para código de cidade)
 */
public interface ArvoreBinariaADT<T extends Comparable<T>> {

    /**
     * Insere um registro de eleitor na árvore.
     *
     * @param chave Chave de busca (ex: código da cidade, número da zona)
     * @param registro Registro do perfil do eleitor a ser inserido
     */
    void inserir(T chave, PerfilEleitor registro);

    /**
     * Busca todos os registros associados a uma chave.
     *
     * @param chave Chave de busca
     * @return Array de registros encontrados, ou array vazio se não encontrar
     */
    PerfilEleitor[] buscar(T chave);

    /**
     * Verifica se a árvore contém uma determinada chave.
     *
     * @param chave Chave a ser verificada
     * @return true se a chave existe na árvore, false caso contrário
     */
    boolean contem(T chave);

    /**
     * Retorna a quantidade de nós (chaves distintas) na árvore.
     *
     * @return Número de nós na árvore
     */
    int tamanho();

    /**
     * Retorna a quantidade total de registros armazenados na árvore.
     *
     * @return Número total de registros
     */
    int totalRegistros();

    /**
     * Verifica se a árvore está vazia.
     *
     * @return true se a árvore está vazia, false caso contrário
     */
    boolean estaVazia();

    /**
     * Retorna a altura da árvore.
     * Útil para verificar o balanceamento.
     *
     * @return Altura da árvore (0 se vazia)
     */
    int altura();

    /**
     * Percorre a árvore em ordem (in-order) e retorna todas as chaves.
     * Útil para listar todas as cidades/zonas em ordem.
     *
     * @return Array com todas as chaves em ordem crescente
     */
    T[] emOrdem();

    /**
     * Limpa todos os dados da árvore.
     */
    void limpar();
}
