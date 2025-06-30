import java.io.PrintWriter;

// Usuário
public class Usuario {

    // Atributos
    private String nome;
    private boolean admin;
    private Sala salaAtual;
    private PrintWriter escritor;


    // Cria um usuário
    public Usuario(String nome, boolean admin, PrintWriter escritor) {
        this.nome = nome;
        this.admin = admin;
        this.escritor = escritor;
    }

    //  Encapsulamento...
    public String getNome() {
        return nome;
    }

    public boolean isAdmin() {
        return admin;
    }
    public PrintWriter getEscritor() {
        return escritor;
    }

    public Sala getSalaAtual() {
        return salaAtual;
    }
    public void setSalaAtual(Sala salaAtual) {
        this.salaAtual = salaAtual;
    }
}
