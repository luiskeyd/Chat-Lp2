import java.io.*;
import java.net.*;

public class Cliente {
    public static void main(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket("localhost", 12345);

            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

            // Thread para escutar mensagens do servidor
            Thread escutador = new Thread(() -> {
                try {
                    String resposta;
                    while ((resposta = entrada.readLine()) != null) {
                        System.out.println(resposta);
                    }
                } catch (IOException e) {
                    System.out.println("[Cliente] Conexão encerrada pelo servidor.");
                }
            });
            escutador.start();

            // Envio de comandos
            String linha;
            while ((linha = teclado.readLine()) != null) {
                saida.println(linha);

                if (linha.equalsIgnoreCase("/sairServidor")) {
                    System.out.println("[Cliente] Encerrando conexão...");
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("[Erro] Falha na conexão: " + e.getMessage());
        } finally {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                    System.out.println("[Cliente] Socket fechado.");
                }
            } catch (IOException e) {
                System.out.println("[Erro] ao fechar o socket: " + e.getMessage());
            }
        }
    }
}
