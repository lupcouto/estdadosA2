package unitins.br;

import java.util.Scanner;

/**
 * Interface de linha de comando (CLI) para análise de dados do eleitorado.
 * Trabalho A2 - Estrutura de Dados - UNITINS
 *
 * IMPORTANTE: Esta classe contém apenas a interface do usuário (menus e I/O).
 * Os alunos NÃO devem modificar esta classe.
 * Toda a lógica está na classe AppService.
 */
public class App {

    private static Scanner scanner;
    private static AppService service;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        service = new AppService();

        System.out.println("===========================================");
        System.out.println("  ANÁLISE DO ELEITORADO - TSE");
        System.out.println("  Estrutura de Dados - UNITINS");
        System.out.println("===========================================");
        System.out.println();

        // Criar diretório de dados
        Arquivo.criarDiretorio("dados");

        boolean continuar = true;

        while (continuar) {
            System.out.println("\n============ MENU PRINCIPAL ============");
            System.out.println("1 - Carregar dados de um estado");
            System.out.println("2 - Consultar quantidade de eleitores");
            System.out.println("3 - Exibir estatísticas gerais");
            System.out.println("4 - Listar registros");
            System.out.println("0 - Sair");
            System.out.print("\nEscolha uma opção: ");

            String opcao = scanner.nextLine().trim();

            switch (opcao) {
                case "1":
                    menuCarregarDados();
                    break;
                case "2":
                    menuConsultarEleitores();
                    break;
                case "3":
                    menuEstatisticas();
                    break;
                case "4":
                    menuListarRegistros();
                    break;
                case "0":
                    continuar = false;
                    System.out.println("\nEncerrando aplicação...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }

        scanner.close();
    }

    /**
     * Menu para carregar dados de um estado.
     */
    private static void menuCarregarDados() {
        System.out.println("\nEstados disponíveis:");
        System.out.println("AC, AL, AM, AP, BA, CE, DF, ES, GO, MA, MG, MS, MT,");
        System.out.println("PA, PB, PE, PI, PR, RJ, RN, RO, RR, RS, SC, SE, SP, TO");
        System.out.println("ZZ = Exterior");
        System.out.print("\nDigite a sigla do estado: ");

        String estado = scanner.nextLine().trim().toUpperCase();

        if (!service.estadoValido(estado)) {
            System.out.println("Estado inválido!");
            return;
        }

        service.carregarDados(estado);
    }

    /**
     * Menu para consultar quantidade de eleitores.
     */
    private static void menuConsultarEleitores() {
        if (!service.temDados()) {
            System.out.println("\nNenhum dado carregado. Carregue primeiro os dados de um estado (opção 1).");
            return;
        }

        // ========== PASSO 1: Selecionar abrangência ==========
        System.out.println("\n======= CONSULTA DE ELEITORES =======");
        System.out.println("\nSelecione a ABRANGÊNCIA da consulta:");
        System.out.println("1 - Estado (todo o estado carregado)");
        System.out.println("2 - Cidade (município específico)");
        System.out.println("3 - Local de votação (escola/prédio)");
        System.out.println("4 - Seção eleitoral");
        System.out.print("\nOpção: ");

        String opcaoAbrangencia = scanner.nextLine().trim();

        String filtroAbrangencia = "";
        int codigoCidade = -1;
        int numeroZona = -1;
        int numeroSecao = -1;
        int numeroLocal = -1;

        switch (opcaoAbrangencia) {
            case "1":
                filtroAbrangencia = "ESTADO";
                System.out.println("\nConsultando todo o estado: " + service.getEstadoCarregado());
                break;

            case "2":
                filtroAbrangencia = "CIDADE";
                System.out.println("\nCidades disponíveis no estado " + service.getEstadoCarregado() + ":");
                String[][] cidades = service.getCidadesDisponiveis();
                for (String[] cidade : cidades) {
                    System.out.printf("  %s - %s%n", cidade[0], cidade[1]);
                }
                System.out.print("\nDigite o código da cidade: ");
                try {
                    codigoCidade = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Código inválido!");
                    return;
                }
                break;

            case "3":
                filtroAbrangencia = "LOCAL";
                System.out.print("\nDigite o código da cidade: ");
                try {
                    codigoCidade = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Código inválido!");
                    return;
                }
                System.out.print("Digite o número da zona eleitoral: ");
                try {
                    numeroZona = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Número inválido!");
                    return;
                }
                System.out.print("Digite o número do local de votação: ");
                try {
                    numeroLocal = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Número inválido!");
                    return;
                }
                break;

            case "4":
                filtroAbrangencia = "SECAO";
                System.out.print("\nDigite o código da cidade: ");
                try {
                    codigoCidade = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Código inválido!");
                    return;
                }
                System.out.print("Digite o número da zona eleitoral: ");
                try {
                    numeroZona = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Número inválido!");
                    return;
                }
                System.out.print("Digite o número da seção: ");
                try {
                    numeroSecao = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Número inválido!");
                    return;
                }
                break;

            default:
                System.out.println("Opção inválida!");
                return;
        }

        // ========== PASSO 2: Selecionar perfil ==========
        System.out.println("\nSelecione o PERFIL dos eleitores:");
        System.out.println("1 - Todos os eleitores");
        System.out.println("2 - Por obrigatoriedade (obrigatório/facultativo)");
        System.out.println("3 - Por gênero (masculino/feminino)");
        System.out.println("4 - Por faixa etária");
        System.out.println("5 - Por grau de escolaridade");
        System.out.println("6 - Por estado civil");
        System.out.println("7 - Por raça/cor");
        System.out.println("8 - Eleitores com deficiência");
        System.out.println("9 - Eleitores com biometria");
        System.out.print("\nOpção: ");

        String opcaoPerfil = scanner.nextLine().trim();

        String filtroPerfil = "";
        String valorPerfil = "";

        switch (opcaoPerfil) {
            case "1":
                filtroPerfil = "TODOS";
                break;

            case "2":
                filtroPerfil = "OBRIGATORIEDADE";
                System.out.println("\nTipo de voto:");
                System.out.println("1 - Obrigatório");
                System.out.println("2 - Facultativo");
                System.out.print("Opção: ");
                String opcaoObrig = scanner.nextLine().trim();
                valorPerfil = opcaoObrig.equals("1") ? "Obrigatório" : "Facultativo";
                break;

            case "3":
                filtroPerfil = "GENERO";
                System.out.println("\nGênero:");
                System.out.println("1 - Masculino");
                System.out.println("2 - Feminino");
                System.out.print("Opção: ");
                String opcaoGenero = scanner.nextLine().trim();
                valorPerfil = opcaoGenero.equals("1") ? "MASCULINO" : "FEMININO";
                break;

            case "4":
                filtroPerfil = "FAIXA_ETARIA";
                System.out.println("\nFaixas etárias disponíveis:");
                System.out.println("1 - 16 anos");
                System.out.println("2 - 17 anos");
                System.out.println("3 - 18 a 20 anos");
                System.out.println("4 - 21 a 24 anos");
                System.out.println("5 - 25 a 29 anos");
                System.out.println("6 - 30 a 34 anos");
                System.out.println("7 - 35 a 39 anos");
                System.out.println("8 - 40 a 44 anos");
                System.out.println("9 - 45 a 49 anos");
                System.out.println("10 - 50 a 54 anos");
                System.out.println("11 - 55 a 59 anos");
                System.out.println("12 - 60 a 64 anos");
                System.out.println("13 - 65 a 69 anos");
                System.out.println("14 - 70 a 74 anos");
                System.out.println("15 - 75 a 79 anos");
                System.out.println("16 - 80 a 84 anos");
                System.out.println("17 - 85 a 89 anos");
                System.out.println("18 - 90 a 94 anos");
                System.out.println("19 - 95 a 99 anos");
                System.out.println("20 - 100 anos ou mais");
                System.out.print("Opção: ");
                valorPerfil = scanner.nextLine().trim();
                break;

            case "5":
                filtroPerfil = "ESCOLARIDADE";
                System.out.println("\nGrau de escolaridade:");
                System.out.println("1 - Analfabeto");
                System.out.println("2 - Lê e escreve");
                System.out.println("3 - Ensino fundamental incompleto");
                System.out.println("4 - Ensino fundamental completo");
                System.out.println("5 - Ensino médio incompleto");
                System.out.println("6 - Ensino médio completo");
                System.out.println("7 - Superior incompleto");
                System.out.println("8 - Superior completo");
                System.out.print("Opção: ");
                valorPerfil = scanner.nextLine().trim();
                break;

            case "6":
                filtroPerfil = "ESTADO_CIVIL";
                System.out.println("\nEstado civil:");
                System.out.println("1 - Solteiro");
                System.out.println("2 - Casado");
                System.out.println("3 - Divorciado");
                System.out.println("4 - Viúvo");
                System.out.println("5 - Separado judicialmente");
                System.out.print("Opção: ");
                valorPerfil = scanner.nextLine().trim();
                break;

            case "7":
                filtroPerfil = "RACA_COR";
                System.out.println("\nRaça/Cor:");
                System.out.println("1 - Branca");
                System.out.println("2 - Preta");
                System.out.println("3 - Parda");
                System.out.println("4 - Amarela");
                System.out.println("5 - Indígena");
                System.out.print("Opção: ");
                valorPerfil = scanner.nextLine().trim();
                break;

            case "8":
                filtroPerfil = "DEFICIENCIA";
                break;

            case "9":
                filtroPerfil = "BIOMETRIA";
                break;

            default:
                System.out.println("Opção inválida!");
                return;
        }

        // ========== PASSO 3: Calcular e exibir resultado ==========
        long totalEleitores = service.calcularEleitores(
            filtroAbrangencia, codigoCidade, numeroZona, numeroSecao, numeroLocal,
            filtroPerfil, valorPerfil
        );

        System.out.println("\n======= RESULTADO DA CONSULTA =======");
        System.out.println("Abrangência: " + filtroAbrangencia);
        System.out.println("Perfil: " + filtroPerfil + (valorPerfil.isEmpty() ? "" : " - " + valorPerfil));
        System.out.printf("Total de eleitores: %,d%n", totalEleitores);
    }

    /**
     * Menu para exibir estatísticas gerais.
     */
    private static void menuEstatisticas() {
        if (!service.temDados()) {
            System.out.println("\nNenhum dado carregado. Carregue primeiro os dados de um estado.");
            return;
        }

        long[] stats = service.calcularEstatisticas();

        System.out.println("\n======= ESTATÍSTICAS GERAIS =======");
        System.out.printf("Total de registros: %,d%n", service.getTotalRegistros());
        System.out.printf("Total de eleitores: %,d%n", stats[0]);
        System.out.printf("Eleitores com biometria: %,d (%.1f%%)%n",
                         stats[1], (stats[1] * 100.0 / stats[0]));
        System.out.printf("Eleitores com deficiência: %,d%n", stats[2]);
        System.out.printf("Eleitores com nome social: %,d%n", stats[3]);
        System.out.println("Estado: " + service.getEstadoCarregado());
    }

    /**
     * Menu para listar registros.
     */
    private static void menuListarRegistros() {
        if (!service.temDados()) {
            System.out.println("\nNenhum dado carregado. Carregue primeiro os dados de um estado.");
            return;
        }

        System.out.print("\nQuantos registros deseja listar? ");
        int quantidade;
        try {
            quantidade = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            quantidade = 10;
        }

        PerfilEleitor[] registros = service.listarRegistros(quantidade);

        System.out.println("\n=== PRIMEIROS " + registros.length + " REGISTROS ===\n");

        for (int i = 0; i < registros.length; i++) {
            PerfilEleitor e = registros[i];
            System.out.printf("%d. %s - %s - Zona %d - Seção %d - %s - %s - %d eleitores%n",
                    i + 1,
                    e.estado(),
                    e.nomeCidade(),
                    e.nrZona(),
                    e.nrSecao(),
                    e.dsGenero(),
                    e.dsFaixaEtaria(),
                    e.qtEleitoresPerfil());
        }
    }
}
