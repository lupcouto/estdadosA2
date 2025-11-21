package unitins.br;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe para registro de operações e tempos de execução.
 */
public class Logger {

    private static final String LOG_DIRECTORY = "dados";
    private static final String ARQUIVO_LOG = LOG_DIRECTORY + "/operacao.log";
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        // Garante que o diretório de log exista
        File logDir = new File(LOG_DIRECTORY);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
    }

    /**
     * Registra uma operação com seu tempo de execução.
     * @param operacao Nome da operação realizada
     * @param tempoMs Tempo em milissegundos
     */
    public static void registrar(String operacao, long tempoMs) {
        String timestamp = LocalDateTime.now().format(FORMATO);
        String mensagem = String.format("[%s] %s - Tempo: %d ms (%.2f s)",
                                        timestamp, operacao, tempoMs, tempoMs / 1000.0);

        // Exibe no console
        System.out.println(mensagem);

        // Salva no arquivo
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_LOG, true))) {
            writer.println(mensagem);
        } catch (IOException e) {
            System.err.println("Erro ao salvar log: " + e.getMessage());
        }
    }

    /**
     * Registra uma mensagem informativa.
     * @param mensagem Mensagem a ser registrada
     */
    public static void info(String mensagem) {
        String timestamp = LocalDateTime.now().format(FORMATO);
        String log = String.format("[%s] INFO: %s", timestamp, mensagem);

        System.out.println(log);

        try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_LOG, true))) {
            writer.println(log);
        } catch (IOException e) {
            System.err.println("Erro ao salvar log: " + e.getMessage());
        }
    }

    /**
     * Registra um erro.
     * @param mensagem Mensagem de erro
     */
    public static void erro(String mensagem) {
        String timestamp = LocalDateTime.now().format(FORMATO);
        String log = String.format("[%s] ERRO: %s", timestamp, mensagem);

        System.err.println(log);

        try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_LOG, true))) {
            writer.println(log);
        } catch (IOException e) {
            System.err.println("Erro ao salvar log: " + e.getMessage());
        }
    }
}
