import java.io.*;
import java.net.*;

public class ClienteConectado implements Runnable {
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter saida;
    private Usuario usuario;

    public ClienteConectado(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            saida = new PrintWriter(socket.getOutputStream(), true);

            saida.println("Digite /login nome (ou /login nome admin para admin):");

            while (true) {
                String linha = entrada.readLine();
                if (linha == null) break;

                if (linha.startsWith("/login")) {
                    String[] partes = linha.split(" ");
                    if (partes.length >= 2) {
                        String nome = partes[1];
                        boolean admin = partes.length == 3 && partes[2].equalsIgnoreCase("admin");

                        usuario = new Usuario(nome, admin, saida);
                        synchronized (Servidor.usuarios) {
                            Servidor.usuarios.add(usuario);
                        }
                        saida.println("[Servidor] Logado como " + nome + (admin ? " (admin)" : ""));
                        break;
                    } else {
                        saida.println("[Erro] Use /login nome [admin]");
                    }
                } else {
                    saida.println("[Erro] Faça login primeiro.");
                }
            }

            // Loop de comandos
            String comando;
            while ((comando = entrada.readLine()) != null) {
                if (comando.startsWith("/salas")) {
                    if (Servidor.salas.isEmpty()) {
                        saida.println("Nenhuma sala disponível.");
                    } else {
                        saida.println("Salas:");
                        for (String nomeSala : Servidor.salas.keySet()) {
                            saida.println(" - " + nomeSala);
                        }
                    }

                } else if (comando.startsWith("/entrar ")) {
                    String nomeSala = comando.split(" ", 2)[1];
                    Sala sala = Servidor.salas.get(nomeSala);
                    if (sala == null) {
                        saida.println("[Erro] Sala não existe.");
                    } else {
                        if (usuario.getSalaAtual() != null) {
                            usuario.getSalaAtual().removeMembro(usuario);
                        }
                        usuario.setSalaAtual(sala);
                        sala.addMembro(usuario);
                        saida.println("[Servidor] Entrou na sala " + nomeSala);
                    }

                } else if (comando.startsWith("/sairServidor")) {
                    saida.println("[Servidor] Saindo...");
                    desconectar();
                    break;

                } else if (comando.startsWith("/sair")) {
                    Sala sala = usuario.getSalaAtual();
                    if (sala != null) {
                        sala.removeMembro(usuario);
                        usuario.setSalaAtual(null);
                        saida.println("[Servidor] Saiu da sala.");
                    } else {
                        saida.println("[Erro] Não está em nenhuma sala.");
                    }

                } else if (comando.startsWith("/msg ")) {
                    String msg = comando.split(" ", 2)[1];
                    Sala sala = usuario.getSalaAtual();
                    if (sala != null) {
                        sala.mensagemGrupo("[" + usuario.getNome() + "]: " + msg);
                    } else {
                        saida.println("[Erro] Você não está em nenhuma sala.");
                    }

                } else if (comando.startsWith("/criar ")) {
                    if (!usuario.isAdmin()) {
                        saida.println("[Erro] Somente admins podem criar salas.");
                        continue;
                    }
                    String nome = comando.split(" ", 2)[1];
                    if (Servidor.salas.containsKey(nome)) {
                        saida.println("[Erro] Sala já existe.");
                    } else {
                        Servidor.salas.put(nome, new Sala(nome));
                        saida.println("[Servidor] Sala criada: " + nome);
                    }

                } else if (comando.startsWith("/encerrar ")) {
                    if (!usuario.isAdmin()) {
                        saida.println("[Erro] Somente admins podem encerrar salas.");
                        continue;
                    }
                    String nome = comando.split(" ", 2)[1];
                    Sala sala = Servidor.salas.get(nome);
                    if (sala != null) {
                        for (Usuario u : sala.getMembros()) {
                            u.getEscritor().println("[Servidor] Sala encerrada pelo admin.");
                            u.setSalaAtual(null);
                        }
                        Servidor.salas.remove(nome);
                        saida.println("[Servidor] Sala encerrada.");
                    } else {
                        saida.println("[Erro] Sala não encontrada.");
                    }

                } else if (comando.startsWith("/expulsar ")) {
                    if (!usuario.isAdmin()) {
                        saida.println("[Erro] Somente admins podem expulsar.");
                        continue;
                    }
                    Sala sala = usuario.getSalaAtual();
                    if (sala == null) {
                        saida.println("[Erro] Você não está em uma sala.");
                        continue;
                    }
                    String nome = comando.split(" ", 2)[1];
                    Usuario alvo = sala.buscarUsuario(nome);
                    if (alvo != null) {
                        sala.removeMembro(alvo);
                        alvo.setSalaAtual(null);
                        alvo.getEscritor().println("[Servidor] Você foi expulso por um admin.");
                        saida.println("[Servidor] Usuário expulso.");
                    } else {
                        saida.println("[Erro] Usuário não encontrado na sala.");
                    }

                } else {
                    saida.println("[Erro] Comando inválido.");
                }
            }

        } catch (IOException e) {
            System.out.println("[Erro IO] " + e.getMessage());
        } finally {
            desconectar();
        }
    }

    private void desconectar() {
        try {
            if (usuario != null) {
                if (usuario.getSalaAtual() != null) {
                    usuario.getSalaAtual().removeMembro(usuario);
                }
                synchronized (Servidor.usuarios) {
                    Servidor.usuarios.remove(usuario);
                }
            }
            socket.close();
        } catch (IOException e) {
            System.out.println("[Erro ao desconectar] " + e.getMessage());
        }
    }
}
