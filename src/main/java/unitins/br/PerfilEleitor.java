package unitins.br;

/**
 * Record que representa o perfil do eleitor por seção eleitoral.
 * Estrutura baseada no arquivo do TSE (31 campos).
 *
 * IMPORTANTE: Cada instância representa um GRUPO de eleitores com o mesmo perfil
 * demográfico, não um eleitor individual. O campo qtEleitoresPerfil() indica
 * quantos eleitores têm esse perfil específico dentro da seção.
 *
 * Exemplo: Se uma seção tem 5 homens solteiros de 25-29 anos com ensino médio,
 * haverá UM registro com qtEleitoresPerfil() = 5, não 5 registros separados.
 */
public record PerfilEleitor(
    String dtGeracao,
    String hhGeracao,
    int anoEleicao,
    String estado,
    int codCidade,
    String nomeCidade,
    int nrZona,
    int nrSecao,
    int nrLocalVotacao,
    String nmLocalVotacao,
    int cdGenero,
    String dsGenero,
    int cdEstadoCivil,
    String dsEstadoCivil,
    int cdFaixaEtaria,
    String dsFaixaEtaria,
    int cdGrauEscolaridade,
    String dsGrauEscolaridade,
    int cdRacaCor,
    String dsRacaCor,
    int cdIdentidadeGenero,
    String dsIdentidadeGenero,
    int cdQuilombola,
    String dsQuilombola,
    int cdInterpreteLibras,
    String dsInterpreteLibras,
    String tpObrigatoriedadeVoto,
    int qtEleitoresPerfil,
    int qtEleitoresBiometria,
    int qtEleitoresDeficiencia,
    int qtEleitoresIncNmSocial
) {

    /**
     * Cria um PerfilEleitor a partir de uma linha CSV.
     * Trata campos #NULO (-1 para numéricos) e #NE (-3 para numéricos).
     * @param campos Array de strings com os campos separados
     * @return PerfilEleitor preenchido
     */
    public static PerfilEleitor fromCsv(String[] campos) {
        return new PerfilEleitor(
            limparString(campos[0]),                    // DT_GERACAO
            limparString(campos[1]),                    // HH_GERACAO
            parseIntSeguro(campos[2]),                  // ANO_ELEICAO
            limparString(campos[3]),                    // SG_UF
            parseIntSeguro(campos[4]),                  // CD_MUNICIPIO
            limparString(campos[5]),                    // NM_MUNICIPIO
            parseIntSeguro(campos[6]),                  // NR_ZONA
            parseIntSeguro(campos[7]),                  // NR_SECAO
            parseIntSeguro(campos[8]),                  // NR_LOCAL_VOTACAO
            limparString(campos[9]),                    // NM_LOCAL_VOTACAO
            parseIntSeguro(campos[10]),                 // CD_GENERO
            limparString(campos[11]),                   // DS_GENERO
            parseIntSeguro(campos[12]),                 // CD_ESTADO_CIVIL
            limparString(campos[13]),                   // DS_ESTADO_CIVIL
            parseIntSeguro(campos[14]),                 // CD_FAIXA_ETARIA
            limparString(campos[15]),                   // DS_FAIXA_ETARIA
            parseIntSeguro(campos[16]),                 // CD_GRAU_ESCOLARIDADE
            limparString(campos[17]),                   // DS_GRAU_ESCOLARIDADE
            parseIntSeguro(campos[18]),                 // CD_RACA_COR
            limparString(campos[19]),                   // DS_RACA_COR
            parseIntSeguro(campos[20]),                 // CD_IDENTIDADE_GENERO
            limparString(campos[21]),                   // DS_IDENTIDADE_GENERO
            parseIntSeguro(campos[22]),                 // CD_QUILOMBOLA
            limparString(campos[23]),                   // DS_QUILOMBOLA
            parseIntSeguro(campos[24]),                 // CD_INTERPRETE_LIBRAS
            limparString(campos[25]),                   // DS_INTERPRETE_LIBRAS
            limparString(campos[26]),                   // TP_OBRIGATORIEDADE_VOTO
            parseIntSeguro(campos[27]),                 // QT_ELEITORES_PERFIL
            parseIntSeguro(campos[28]),                 // QT_ELEITORES_BIOMETRIA
            parseIntSeguro(campos[29]),                 // QT_ELEITORES_DEFICIENCIA
            parseIntSeguro(campos[30])                  // QT_ELEITORES_INC_NM_SOCIAL
        );
    }

    /**
     * Remove aspas e espaços de uma string.
     */
    private static String limparString(String valor) {
        if (valor == null) return "";
        return valor.replace("\"", "").trim();
    }

    /**
     * Converte string para int, tratando #NULO como -1 e #NE como -3.
     */
    private static int parseIntSeguro(String valor) {
        if (valor == null) return -1;
        String limpo = valor.replace("\"", "").trim();
        if (limpo.isEmpty() || limpo.equals("#NULO")) return -1;
        if (limpo.equals("#NE")) return -3;
        try {
            return Integer.parseInt(limpo);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
