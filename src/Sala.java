import java.io.*;
import java.util.*;

public class Sala {
    private String nome;
    private Set<PrintWriter> membros;

    public Sala(String nome){
        setNome(nome);
        this.membros = new HashSet<>();
    }

    public String getNome(){
        return nome;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public synchronized void addMembro(PrintWriter usuario){
        membros.add(usuario);
    }

    public synchronized void removeMembro(PrintWriter usuario){
        membros.remove(usuario);
    }

    public synchronized void mensagemGrupo(String mensagem) {
        for (PrintWriter membro : membros) {
            membro.println(mensagem);
            membro.flush(); 
        }
    }
}
