import java.io.PrintWriter;

public class Usuario {
    private String nome;
    private boolean admin;
    private Sala salaAtual;
    private PrintWriter escritor;

    public Usuario(String nome, boolean admin, PrintWriter escritor) {
        this.nome = nome;
        this.admin = admin;
        this.escritor = escritor;
    }

    public String getNome() { return nome; }
    public boolean isAdmin() { return admin; }
    public PrintWriter getEscritor() { return escritor; }

    public Sala getSalaAtual() { return salaAtual; }
    public void setSalaAtual(Sala salaAtual) { this.salaAtual = salaAtual; }
}
