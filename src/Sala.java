import java.util.*;

// Sala
public class Sala {

    // Atributos
    private String nome; // Nome da sala
    private Set<Usuario> membros; // Conjunto de membros da sala

    // Cria a sala
    public Sala(String nome){
        this.nome = nome;
        this.membros = new HashSet<>();
    }

    // Retorna o nome da sala
    public String getNome(){
        return nome;
    }

    // Retorna o conjunto de membros
    public Set<Usuario> getMembros() {
        return membros;
    }

    // Retorna um usuário se ele estiver no conjunto, casoi contrário retorna null
    public synchronized Usuario buscarUsuario(String nome) {
        for (Usuario usuario : membros) {
            if (usuario.getNome().equals(nome)) {
                return usuario; // Retorna assim que encontrar
            }
        }
        return null; // Se não encontrar, retorna null
    }
    // Adiciona um usuário a sala
    public synchronized void addMembro(Usuario usuario){
        membros.add(usuario);
        mensagemGrupo("[Servidor] " + usuario.getNome() + " entrou na sala.");
    }
    
    // Remove usuário da sala
    public synchronized void removeMembro(Usuario usuario){
        membros.remove(usuario);
        mensagemGrupo("[Servidor] " + usuario.getNome() + " saiu da sala.");
    }

    // Envia uma mensagem para todos os membros da sala
    public synchronized void mensagemGrupo(String mensagem) {
        for (Usuario membro : membros) {
            membro.getEscritor().println(mensagem);
        }
    }

    // Verifica se um usuário específico esta na sala (vamo nem usar isso porvavelmente ó)
    public synchronized boolean contemUsuario(String nome) {
        for (Usuario usuario : membros) {
            if (usuario.getNome().equals(nome)) {
                return true; // Encontrou o usuário
            }
        }
        return false; // Não encontrou
    }
}
