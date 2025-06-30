import java.io.*;
import java.net.*;

// Cliente
public class Cliente {

    // Main
    public static void main(String[] args) {

        Socket socket = null; // Inicializando a conexão
        try {
            // Criando a conexão ("localhost" equivale ao IP do computador)
            socket = new Socket("localhost", 12345);

            // Inicializando os atributos de entrada e saída
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

            // Thread para escutar mensagens do servidor
            Thread escutador = new Thread(() -> {
                try {
                    String resposta;
                    while ((resposta = entrada.readLine()) != null) { // Aguardando mensagens do servidor
                        System.out.println(resposta); // Exibe a mensagem
                    }
                } catch (IOException e) {
                    System.err.println("[Cliente]: Conexão encerrada pelo servidor.");
                }
            });
            escutador.start(); // Inicia a Thread de escuta

            // Envio de comandos
            String linha;
            while ((linha = teclado.readLine()) != null) {  // Aguardando comandos do cliente
                saida.println(linha); // Envia a mensagem para o servidor

                if (linha.equalsIgnoreCase("/sairServidor")) { // Se for o comando de sair do servidor
                    System.out.println("[Cliente]: Encerrando conexão...");
                    break;
                }
            }
        } 

        catch (IOException e) {
            System.out.println("[Erro]: Falha na conexão: " + e.getMessage());
        } 

        finally {
            //  Encerra a conexão caso não tenha sido encerrada ainda
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                    System.out.println("[Cliente]: Socket fechado.");
                }
            } catch (IOException e) {
                System.err.println("[Erro]: ao fechar o socket: " + e.getMessage());
            }
        }
    }
}
