package unitins.br;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Classe utilitária para operações com arquivos.
 * Responsável por download e extração de arquivos do TSE.
 */
public class Arquivo {

    // Tamanho do buffer para operações de I/O (64KB)
    private static final int TAMANHO_BUFFER = 65536;

    /**
     * Baixa um arquivo de uma URL e salva no destino especificado.
     *
     * @param url URL do arquivo a ser baixado
     * @param destino Caminho local onde o arquivo será salvo
     * @return true se o download foi bem sucedido, false caso contrário
     */
    public static boolean baixarArquivo(String url, String destino) {
        System.out.println("\nBaixando arquivo do TSE...");
        System.out.println("URL: " + url);

        long inicio = System.currentTimeMillis();

        try {
            // Criar cliente HTTP com redirecionamento automático
            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build();

            // Criar requisição GET
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            // Enviar requisição e obter resposta como stream
            HttpResponse<InputStream> response = client.send(request,
                    HttpResponse.BodyHandlers.ofInputStream());

            // Verificar código de resposta
            if (response.statusCode() != 200) {
                Logger.erro("Falha no download. Código HTTP: " + response.statusCode());
                return false;
            }

            // Criar diretório de destino se não existir
            Path caminhoDestino = Paths.get(destino);
            if (caminhoDestino.getParent() != null) {
                Files.createDirectories(caminhoDestino.getParent());
            }

            // Salvar arquivo com buffer grande para melhor performance
            try (InputStream in = response.body();
                 BufferedOutputStream out = new BufferedOutputStream(
                         new FileOutputStream(destino), TAMANHO_BUFFER)) {

                byte[] buffer = new byte[TAMANHO_BUFFER];
                int bytesLidos;
                long totalBytes = 0;
                long ultimoProgresso = 0;

                while ((bytesLidos = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesLidos);
                    totalBytes += bytesLidos;

                    // Mostrar progresso a cada 10MB
                    if (totalBytes - ultimoProgresso >= 10485760) {
                        System.out.printf("  Baixado: %.1f MB%n", totalBytes / 1048576.0);
                        ultimoProgresso = totalBytes;
                    }
                }

                long tempo = System.currentTimeMillis() - inicio;
                Logger.registrar(String.format("Download concluído (%.2f MB)", totalBytes / 1048576.0), tempo);
                return true;
            }

        } catch (Exception e) {
            Logger.erro("Erro no download: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extrai um arquivo ZIP para o diretório de destino.
     *
     * @param arquivoZip Caminho do arquivo ZIP a ser extraído
     * @param destino Diretório onde os arquivos serão extraídos
     * @return true se a extração foi bem sucedida, false caso contrário
     */
    public static boolean extrairZip(String arquivoZip, String destino) {
        System.out.println("\nExtraindo arquivo ZIP...");

        long inicio = System.currentTimeMillis();
        
        // Adicionado para correção de segurança (Zip Slip)
        Path diretorioDestino = Paths.get(destino).toAbsolutePath();

        try {
            // Criar diretório de destino se não existir
            Files.createDirectories(diretorioDestino);

            // Abrir arquivo ZIP com buffer grande
            try (ZipInputStream zis = new ZipInputStream(
                    new BufferedInputStream(new FileInputStream(arquivoZip), TAMANHO_BUFFER))) {

                ZipEntry entry;

                while ((entry = zis.getNextEntry()) != null) {
                    Path caminhoDestino = diretorioDestino.resolve(entry.getName()).normalize();

                    // Prevenção contra Path Traversal (Zip Slip)
                    if (!caminhoDestino.startsWith(diretorioDestino)) {
                        throw new IOException("Zip Slip detectado: a entrada " + entry.getName() + " tenta extrair arquivos para fora do diretório de destino.");
                    }

                    // Verificar se é diretório ou arquivo
                    if (entry.isDirectory()) {
                        Files.createDirectories(caminhoDestino);
                    } else {
                        // Criar diretórios pais se necessário (já coberto pelo resolve)
                        if (caminhoDestino.getParent() != null) {
                            Files.createDirectories(caminhoDestino.getParent());
                        }

                        // Extrair arquivo com buffer grande
                        try (BufferedOutputStream fos = new BufferedOutputStream(
                                new FileOutputStream(caminhoDestino.toFile()), TAMANHO_BUFFER)) {

                            byte[] buffer = new byte[TAMANHO_BUFFER];
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                        }
                        System.out.println("  Extraído: " + caminhoDestino);
                    }
                    zis.closeEntry();
                }

                long tempo = System.currentTimeMillis() - inicio;
                Logger.registrar("Extração do ZIP concluída", tempo);
                return true;
            }

        } catch (IOException e) {
            Logger.erro("Erro ao extrair ZIP: " + e.getMessage());
            e.printStackTrace(); // Didático: mostrar o stack trace para o aluno ver o erro completo
            return false;
        }
    }

    /**
     * Verifica se um arquivo existe.
     *
     * @param caminho Caminho do arquivo
     * @return true se o arquivo existe, false caso contrário
     */
    public static boolean existe(String caminho) {
        return Files.exists(Paths.get(caminho));
    }

    /**
     * Retorna o tamanho de um arquivo em bytes.
     *
     * @param caminho Caminho do arquivo
     * @return Tamanho em bytes, ou -1 se o arquivo não existe
     */
    public static long tamanho(String caminho) {
        try {
            return Files.size(Paths.get(caminho));
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * Cria um diretório se não existir.
     *
     * @param caminho Caminho do diretório
     * @return true se o diretório foi criado ou já existe, false em caso de erro
     */
    public static boolean criarDiretorio(String caminho) {
        try {
            Files.createDirectories(Paths.get(caminho));
            return true;
        } catch (IOException e) {
            Logger.erro("Não foi possível criar diretório: " + e.getMessage());
            return false;
        }
    }
}
