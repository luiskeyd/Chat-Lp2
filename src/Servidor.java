import java.net.*;
import java.util.*;
import java.io.*;

public class Servidor{
    
    private static final int porta = 12345;
    private static Set<PrintWriter> clientes = new HashSet<>();
    private static Map<String, Sala> salas = new HashMap<>();
    public static void main (String [] args){        

        try (ServerSocket servidor = new ServerSocket(porta)){
            while (true){
                Socket cliente = servidor.accept(); 
                System.out.println("Cliente " + cliente.getInetAddress().getHostAddress() + "conectado");
                ClienteConectado novoCliente = new ClienteConectado(cliente, clientes);
                Thread t1 = new Thread(novoCliente);
                t1.start();
            }
        }
        catch(Exception e){
            System.out.println("Err " + e.getMessage());
        }

    } 
}
