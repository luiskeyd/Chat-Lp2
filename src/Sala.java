import java.util.*;

public class Sala {
    private String nome;
    private Set<Usuario> membros;

    public Sala(String nome){
        this.nome = nome;
        this.membros = new HashSet<>();
    }

    public String getNome(){ return nome; }

    public synchronized void addMembro(Usuario usuario){
        membros.add(usuario);
        mensagemGrupo("[Servidor] " + usuario.getNome() + " entrou na sala.");
    }

    public synchronized void removeMembro(Usuario usuario){
        membros.remove(usuario);
        mensagemGrupo("[Servidor] " + usuario.getNome() + " saiu da sala.");
    }

    public synchronized void mensagemGrupo(String mensagem) {
        for (Usuario membro : membros) {
            membro.getEscritor().println(mensagem);
        }
    }

    public synchronized boolean contemUsuario(String nome) {
        return membros.stream().anyMatch(u -> u.getNome().equals(nome));
    }

    public synchronized Usuario buscarUsuario(String nome) {
        return membros.stream().filter(u -> u.getNome().equals(nome)).findFirst().orElse(null);
    }

    public Set<Usuario> getMembros() {
        return membros;
    }
}
