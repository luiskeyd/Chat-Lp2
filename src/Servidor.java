import java.net.*;
import java.util.*;

// Servidor
public class Servidor {

    // Atributos
    private static final int porta = 12345; // Porta usada para conexão dos usuários
    public static Map<String, Sala> salas = new HashMap<>(); // Dicionário contendo as salas criadas
    public static Set<Usuario> usuarios = new HashSet<>(); // Conjunto de de usuários conectados ao servidor


    // Main
    public static void main (String[] args){
        try (ServerSocket servidor = new ServerSocket(porta)) { // Criação do servidor
            System.out.println("[Servidor]: Iniciado na porta " + porta);
            while (true){ // Esperar uma conexão de algum usuário
                Socket cliente = servidor.accept(); // Aceita o cliente
                // Cria uma nova thread para lida com cada cliente conectado ao servidor paralelamente
                Thread t = new Thread(new ClienteConectado(cliente));
                t.start(); // Inicia a thread
            }
        } catch (Exception e) { // Se der algum problema
            System.err.println("[Erro]: " + e.getMessage());
        }
    }
}
