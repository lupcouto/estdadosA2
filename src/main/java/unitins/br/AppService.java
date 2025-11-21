package unitins.br;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Classe de serviço com a lógica de negócio da aplicação.
 *
 * IMPORTANTE: Os alunos devem modificar APENAS esta classe para
 * implementar a árvore binária e melhorar as buscas.
 *
 * TODO para os alunos: Adicionar a árvore binária aqui e usá-la
 * nos métodos de busca para melhorar a performance.
 */
public class AppService {

    // Array simples para armazenar os dados (implementação base)
    private PerfilEleitor[] eleitores;
    private int totalRegistros = 0;

    // Tamanho inicial e fator de crescimento do array
    private static final int TAMANHO_INICIAL = 100000;
    private static final double FATOR_CRESCIMENTO = 1.5;

    // TODO: Adicionar aqui a instância da árvore binária
    // private ArvoreBinariaADT<Integer> arvorePorCidade;

    // Estados brasileiros válidos
    private static final String[] ESTADOS = {
        "AC", "AL", "AM", "AP", "BA", "CE", "DF", "ES", "GO", "MA",
        "MG", "MS", "MT", "PA", "PB", "PE", "PI", "PR", "RJ", "RN",
        "RO", "RR", "RS", "SC", "SE", "SP", "TO", "ZZ"
    };

    /**
     * Verifica se um estado é válido.
     */
    public boolean estadoValido(String estado) {
        for (String uf : ESTADOS) {
            if (uf.equals(estado.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retorna a lista de estados válidos.
     */
    public String[] getEstados() {
        return ESTADOS;
    }

    /**
     * Verifica se há dados carregados.
     */
    public boolean temDados() {
        return eleitores != null && totalRegistros > 0;
    }

    /**
     * Retorna o estado dos dados carregados.
     */
    public String getEstadoCarregado() {
        if (temDados()) {
            return eleitores[0].estado();
        }
        return "";
    }

    /**
     * Retorna o total de registros carregados.
     */
    public int getTotalRegistros() {
        return totalRegistros;
    }

    /**
     * Carrega os dados de um estado específico.
     *
     * @param estado Sigla do estado (ex: "AC", "SP")
     * @return true se carregou com sucesso
     */
    public boolean carregarDados(String estado) {
        estado = estado.toUpperCase();

        Logger.info("Iniciando carregamento de dados do estado: " + estado);

        // 1. Download do arquivo
        String url = "https://cdn.tse.jus.br/estatistica/sead/odsele/perfil_eleitor_secao/perfil_eleitor_secao_ATUAL_" + estado + ".zip";
        String arquivoZip = "dados/perfil_eleitor_secao_" + estado + ".zip";
        String arquivoCsv = "dados/perfil_eleitor_secao_ATUAL_" + estado + ".csv";

        if (!Arquivo.baixarArquivo(url, arquivoZip)) {
            return false;
        }

        // 2. Extrair arquivo ZIP
        if (!Arquivo.extrairZip(arquivoZip, "dados")) {
            return false;
        }

        // 3. Ler arquivo CSV
        return lerArquivoCsv(arquivoCsv);
    }

    /**
     * Lê o arquivo CSV e carrega os dados em memória.
     */
    private boolean lerArquivoCsv(String arquivo) {
        System.out.println("\nLendo arquivo CSV...");
        System.out.println("(Arquivos grandes podem levar vários minutos)");

        long inicio = System.currentTimeMillis();

        try {
            eleitores = new PerfilEleitor[TAMANHO_INICIAL];
            totalRegistros = 0;

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(arquivo), "ISO-8859-1"), 131072)) {

                br.readLine(); // Pular cabeçalho
                String linha;

                while ((linha = br.readLine()) != null) {
                    try {
                        String[] campos = linha.split(";");
                        if (campos.length >= 31) {
                            if (totalRegistros >= eleitores.length) {
                                expandirArray();
                            }

                            eleitores[totalRegistros] = PerfilEleitor.fromCsv(campos);
                            totalRegistros++;
                        }
                    } catch (Exception e) {
                        // Ignorar linhas com erro
                    }

                    if (totalRegistros % 1000000 == 0 && totalRegistros > 0) {
                        System.out.printf("  Processados: %,d registros...%n", totalRegistros);
                    }
                }
            }

            // Compactar array
            if (totalRegistros < eleitores.length) {
                PerfilEleitor[] arrayCompacto = new PerfilEleitor[totalRegistros];
                System.arraycopy(eleitores, 0, arrayCompacto, 0, totalRegistros);
                eleitores = arrayCompacto;
            }

            long tempo = System.currentTimeMillis() - inicio;
            Logger.registrar(String.format("Leitura do CSV concluída (%,d registros)", totalRegistros), tempo);

            // TODO: Após carregar os dados, popular a árvore binária aqui
            // arvorePorCidade = new SuaArvore<>();
            // for (int i = 0; i < totalRegistros; i++) {
            //     arvorePorCidade.inserir(eleitores[i].codCidade(), eleitores[i]);
            // }

            return true;

        } catch (IOException e) {
            Logger.erro("Erro ao ler CSV: " + e.getMessage());
            return false;
        }
    }

    /**
     * Expande o array de eleitores.
     */
    private void expandirArray() {
        int novoTamanho = (int) (eleitores.length * FATOR_CRESCIMENTO);
        System.out.printf("  Expandindo array: %,d -> %,d%n", eleitores.length, novoTamanho);

        PerfilEleitor[] novoArray = new PerfilEleitor[novoTamanho];
        System.arraycopy(eleitores, 0, novoArray, 0, totalRegistros);
        eleitores = novoArray;
    }

    /**
     * Retorna as cidades disponíveis no estado carregado.
     *
     * ATENÇÃO ALUNOS: Esta implementação é propositalmente didática e ineficiente.
     * Ela usa um array de tamanho fixo e uma busca linear aninhada para encontrar
     * cidades únicas, além de um Bubble Sort para ordenar.
     *
     * Complexidade atual:
     * - Encontrar cidades únicas: O(N * M), onde N é totalRegistros e M é o número de cidades únicas.
     * - Ordenação: O(M^2), onde M é o número de cidades únicas.
     *
     * Esta é uma "problematização" para que vocês entendam a importância de estruturas
     * de dados mais eficientes.
     *
     * Abaixo, segue um exemplo de uma implementação mais eficiente usando HashMap e ArrayList,
     * que reduz a complexidade para O(N + M log M).
     *
     * // Exemplo de implementação mais eficiente (para estudo):
     * /*
     * public String[][] getCidadesDisponiveisOtimizado() {
     *     if (!temDados()) return new String[0][0];
     *
     *     // Usar um Map para encontrar cidades únicas de forma eficiente (O(N))
     *     Map<Integer, String> cidadesUnicas = new HashMap<>();
     *     for (int i = 0; i < totalRegistros; i++) {
     *         cidadesUnicas.putIfAbsent(eleitores[i].codCidade(), eleitores[i].nomeCidade());
     *     }
     *
     *     // Converter para lista para ordenação
     *     List<Map.Entry<Integer, String>> listaCidades = new ArrayList<>(cidadesUnicas.entrySet());
     *
     *     // Ordenar a lista pelo código da cidade (chave do map) - O(M log M)
     *     listaCidades.sort(Map.Entry.comparingByKey());
     *
     *     // Criar o array de resultado no formato String[][]
     *     String[][] resultado = new String[listaCidades.size()][2];
     *     for (int i = 0; i < listaCidades.size(); i++) {
     *         resultado[i][0] = String.valueOf(listaCidades.get(i).getKey());
     *         resultado[i][1] = listaCidades.get(i).getValue();
     *     }
     *
     *     return resultado;
     * }
     */

    /*
     * TODO para os alunos (extra): Após implementar a sua ArvoreBinariaADT,
     * você pode usar o método 'emOrdem()' da sua árvore (se a chave for o
     * código da cidade) para obter as cidades já ordenadas de forma ainda
     * mais eficiente (O(k), onde k é o número de nós/cidades).
     * Isso eliminaria a necessidade de um HashMap e da ordenação explícita.
     */

    /**
     * @return Array bidimensional com [código, nome] de cada cidade
     */
    public String[][] getCidadesDisponiveis() {
        if (!temDados()) return new String[0][0];

        // Array simples para guardar cidades (máx 1000 por estado)
        int[] codigos = new int[1000];
        String[] nomes = new String[1000];
        int qtd = 0;

        for (int i = 0; i < totalRegistros && qtd < 1000; i++) {
            int cod = eleitores[i].codCidade();
            boolean existe = false;

            for (int j = 0; j < qtd; j++) {
                if (codigos[j] == cod) {
                    existe = true;
                    break;
                }
            }

            if (!existe) {
                codigos[qtd] = cod;
                nomes[qtd] = eleitores[i].nomeCidade();
                qtd++;
            }
        }

        // Ordenar por código (bubble sort simples)
        for (int i = 0; i < qtd - 1; i++) {
            for (int j = i + 1; j < qtd; j++) {
                if (codigos[i] > codigos[j]) {
                    int tempCod = codigos[i];
                    codigos[i] = codigos[j];
                    codigos[j] = tempCod;

                    String tempNome = nomes[i];
                    nomes[i] = nomes[j];
                    nomes[j] = tempNome;
                }
            }
        }

        // Criar resultado
        String[][] resultado = new String[qtd][2];
        for (int i = 0; i < qtd; i++) {
            resultado[i][0] = String.valueOf(codigos[i]);
            resultado[i][1] = nomes[i];
        }

        return resultado;
    }

    /**
     * Calcula a quantidade de eleitores com base nos filtros.
     *
     * TODO para os alunos: Implementar usando a interface ArvoreBinariaADT
     * para melhorar a eficiência das buscas.
     *
     * Exemplo de como usar a árvore:
     *   if (filtroAbrangencia.equals("CIDADE")) {
     *       PerfilEleitor[] registrosCidade = arvorePorCidade.buscar(codigoCidade);
     *       // Processar apenas os registros da cidade (muito mais rápido!)
     *   }
     */
    public long calcularEleitores(
            String filtroAbrangencia, int codigoCidade, int numeroZona,
            int numeroSecao, int numeroLocal,
            String filtroPerfil, String valorPerfil) {

        long inicio = System.currentTimeMillis();
        long total = 0;

        for (int i = 0; i < totalRegistros; i++) {
            PerfilEleitor e = eleitores[i];

            // Verificar abrangência
            boolean passaAbrangencia = false;

            switch (filtroAbrangencia) {
                case "ESTADO":
                    passaAbrangencia = true;
                    break;
                case "CIDADE":
                    passaAbrangencia = (e.codCidade() == codigoCidade);
                    break;
                case "LOCAL":
                    passaAbrangencia = (e.codCidade() == codigoCidade &&
                                       e.nrZona() == numeroZona &&
                                       e.nrLocalVotacao() == numeroLocal);
                    break;
                case "SECAO":
                    passaAbrangencia = (e.codCidade() == codigoCidade &&
                                       e.nrZona() == numeroZona &&
                                       e.nrSecao() == numeroSecao);
                    break;
            }

            if (!passaAbrangencia) continue;

            // Verificar perfil e somar eleitores
            switch (filtroPerfil) {
                case "TODOS":
                    total += e.qtEleitoresPerfil();
                    break;

                case "OBRIGATORIEDADE":
                    if (e.tpObrigatoriedadeVoto().equalsIgnoreCase(valorPerfil)) {
                        total += e.qtEleitoresPerfil();
                    }
                    break;

                case "GENERO":
                    if (e.dsGenero().equalsIgnoreCase(valorPerfil)) {
                        total += e.qtEleitoresPerfil();
                    }
                    break;

                case "FAIXA_ETARIA":
                    if (verificarFaixaEtaria(e.cdFaixaEtaria(), valorPerfil)) {
                        total += e.qtEleitoresPerfil();
                    }
                    break;

                case "ESCOLARIDADE":
                    if (verificarEscolaridade(e.cdGrauEscolaridade(), valorPerfil)) {
                        total += e.qtEleitoresPerfil();
                    }
                    break;

                case "ESTADO_CIVIL":
                    if (verificarEstadoCivil(e.cdEstadoCivil(), valorPerfil)) {
                        total += e.qtEleitoresPerfil();
                    }
                    break;

                case "RACA_COR":
                    if (verificarRacaCor(e.cdRacaCor(), valorPerfil)) {
                        total += e.qtEleitoresPerfil();
                    }
                    break;

                case "DEFICIENCIA":
                    total += e.qtEleitoresDeficiencia();
                    break;

                case "BIOMETRIA":
                    total += e.qtEleitoresBiometria();
                    break;
            }
        }

        long tempo = System.currentTimeMillis() - inicio;
        Logger.registrar("Consulta de eleitores (" + filtroAbrangencia + "/" + filtroPerfil + ")", tempo);

        return total;
    }

    /**
     * Calcula estatísticas gerais dos dados carregados.
     *
     * @return Array com [totalEleitores, totalBiometria, totalDeficiencia, totalNomeSocial]
     */
    public long[] calcularEstatisticas() {
        if (!temDados()) return new long[4];

        long inicio = System.currentTimeMillis();

        long totalEleitores = 0;
        long totalBiometria = 0;
        long totalDeficiencia = 0;
        long totalNomeSocial = 0;

        for (int i = 0; i < totalRegistros; i++) {
            totalEleitores += eleitores[i].qtEleitoresPerfil();
            totalBiometria += eleitores[i].qtEleitoresBiometria();
            totalDeficiencia += eleitores[i].qtEleitoresDeficiencia();
            totalNomeSocial += eleitores[i].qtEleitoresIncNmSocial();
        }

        long tempo = System.currentTimeMillis() - inicio;
        Logger.registrar("Cálculo de estatísticas gerais", tempo);

        return new long[]{totalEleitores, totalBiometria, totalDeficiencia, totalNomeSocial};
    }

    /**
     * Retorna os primeiros N registros.
     */
    public PerfilEleitor[] listarRegistros(int quantidade) {
        if (!temDados()) return new PerfilEleitor[0];

        long inicio = System.currentTimeMillis();

        int limite = Math.min(quantidade, totalRegistros);
        PerfilEleitor[] resultado = new PerfilEleitor[limite];
        System.arraycopy(eleitores, 0, resultado, 0, limite);

        long tempo = System.currentTimeMillis() - inicio;
        Logger.registrar("Listagem de " + limite + " registros", tempo);

        return resultado;
    }

    // ========== Métodos auxiliares de verificação ==========

    // Códigos das faixas etárias conforme padrão do TSE
    // Índice 0 = opção 1 (16 anos), índice 1 = opção 2 (17 anos), etc.
    private static final int[] CODIGOS_FAIXA_ETARIA = {
        1600, 1700, 1800, 2100, 2500, 3000, 3500, 4000, 4500,
        5000, 5500, 6000, 6500, 7000, 7500, 8000, 8500, 9000, 9500, 10000
    };

    private boolean verificarFaixaEtaria(int codigo, String opcao) {
        try {
            int opcaoNum = Integer.parseInt(opcao);
            if (opcaoNum < 1 || opcaoNum > CODIGOS_FAIXA_ETARIA.length) {
                return false;
            }
            return codigo == CODIGOS_FAIXA_ETARIA[opcaoNum - 1];
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean verificarEscolaridade(int codigo, String opcao) {
        try {
            int opcaoNum = Integer.parseInt(opcao);
            return codigo == opcaoNum;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean verificarEstadoCivil(int codigo, String opcao) {
        try {
            int opcaoNum = Integer.parseInt(opcao);
            int[] mapa = {0, 1, 3, 9, 5, 7};
            return codigo == mapa[opcaoNum];
        } catch (Exception e) {
            return false;
        }
    }

    private boolean verificarRacaCor(int codigo, String opcao) {
        try {
            int opcaoNum = Integer.parseInt(opcao);
            return codigo == opcaoNum;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
