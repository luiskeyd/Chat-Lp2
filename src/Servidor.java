import java.net.*;
import java.util.*;

public class Servidor {
    private static final int porta = 12345;
    public static Map<String, Sala> salas = new HashMap<>();
    public static Set<Usuario> usuarios = new HashSet<>();

    public static void main (String[] args) {
        try (ServerSocket servidor = new ServerSocket(porta)) {
            System.out.println("[Servidor] Iniciado na porta " + porta);
            while (true){
                Socket cliente = servidor.accept();
                Thread t = new Thread(new ClienteConectado(cliente));
                t.start();
            }
        } catch (Exception e) {
            System.out.println("[Erro] " + e.getMessage());
        }
    }
}
