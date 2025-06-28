import java.io.*;
import java.net.*;
import java.util.*;

public class ClienteConectado implements Runnable{
    private Socket servidor;
    //private BufferedReader buffer;
    private PrintWriter cliente;
    private Set<PrintWriter> lista;

    public ClienteConectado(Socket servidor, Set<PrintWriter> lista){
        this.servidor = servidor;
        this.lista = lista;
    }

    @Override
    public void run(){
        try{
            //buffer = new BufferedReader(new InputStreamReader(servidor.getInputStream()));
            cliente = new PrintWriter(servidor.getOutputStream(), true);

            synchronized (lista) {
                lista.add(cliente);
            }

        }
        catch (Exception e){
            System.err.println("Erro: " + e.getMessage());
        }
    }

}