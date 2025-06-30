import java.io.*;
import java.net.*;

// Classe que lida com cada cliente que entra no servidor
public class ClienteConectado implements Runnable {

    // Atributos
    private Socket socket; // Conexão do cliente
    private BufferedReader entrada; // Lê a entrada do cliente
    private PrintWriter saida; // Envia mensagem ao cliente
    private Usuario usuario; // O cliente de fato, que é um objeto do tipo usuário

    // Construtor da conexão do cliente
    public ClienteConectado(Socket socket) {
        this.socket = socket;
    }

    // Método que aplica a thread ao cliente conectado
    @Override
    public void run() {
        try {
            // Inicializa os atributos de entrada e saída
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            saida = new PrintWriter(socket.getOutputStream(), true);

            // Solicita o login
            saida.println("-> Digite:\n/login nome (usuários comuns)\n/login nome admin (administrador");

            // Esse ciclo representa a interface do cliente para efetuar o login
            while (true) {
                String linha = entrada.readLine(); // Recebe a instrução do cliente
                if (linha == null) break; // Se a instrução for válida

                // Se o usuário esta tentando fazer um login
                if (linha.startsWith("/login")) { // Verifica se o prefixo da string é "/login"
                    // Cria uma array de String com as instruções separadas por espaço
                    String[] partes = linha.split(" ");
                    if (partes.length >= 2) { // Se tiver mais que duas instruções (/login nome)
                        String nome = partes[1]; // Armazena o nome do usuário, que equivale a segunda instrução
                        // Verifica se a terceira instrução é "admin" e armazena seu valor verdade em um booleano
                        boolean admin = partes.length == 3 && partes[2].equalsIgnoreCase("admin");

                        // Cria um objeto do tipo usuário
                        usuario = new Usuario(nome, admin, saida);
                        synchronized (Servidor.usuarios) {
                            Servidor.usuarios.add(usuario); // Adiciona o usuário ao conjunto de usuários do servidor
                        }
                        saida.println("[Servidor]: Logado como " + nome + (admin ? " (admin)" : ""));
                        System.out.println("[Servidor]: " + nome + (admin ? " (admin)" : "") + " entrou");
                        break; // Encerra o loop se caso o login tenha sido feito com sucesso
                    } else {
                        saida.println("[Erro]: Use /login nome [admin]"); // Caso o usuário não digite o nome
                    }
                } else {
                    // Caso o usuário tente executar outra instrução antes de logar
                    saida.println("[Erro]: Faça login primeiro.");
                }
            }

            // Loop de comandos
            saida.println("[Servidor]: Digite /help para listar as instruções");
            String comando;
            while ((comando = entrada.readLine()) != null) { // Espera comandos do usuário

                // Listar salas
                if (comando.startsWith("/listarSalas")) {
                    if (Servidor.salas.isEmpty()) { // Verifica se a sala ta vazia
                        saida.println("[Servidor]: Nenhuma sala disponível."); // Se estiver
                    } 
                    else {
                        // Exibe as salas
                        saida.println("[Servidor]: Salas:");
                        for (String nomeSala : Servidor.salas.keySet()) {
                            saida.println(" - " + nomeSala);
                        }
                    }
                }

                //  Entrar em uma sala 
                else if (comando.startsWith("/entrar ")) { //
                    String nomeSala = comando.split(" ", 2)[1]; // Armazena o nome da sala
                    Sala sala = Servidor.salas.get(nomeSala); // Ponteiro para a sala identificado pelo nome
                    if (sala == null) { // Se a sala não existe
                        saida.println("[Erro]: Sala não existe.");
                    } else {
                        // Se existe
                        if (usuario.getSalaAtual() != null) { // Se o cliente já está em uma sala
                            usuario.getSalaAtual().removeMembro(usuario); // Remove ele da sala
                        }
                        // Coloca o cliente na sala especificada
                        usuario.setSalaAtual(sala);
                        sala.addMembro(usuario);
                        saida.println("[Servidor]: Entrou na sala " + nomeSala);
                    }
                } 

                // Sair do servidor
                else if (comando.startsWith("/sairServidor")) {
                    saida.println("[Servidor]: Saindo...");
                    System.out.println("[Servidor]: O usuario " + usuario.getNome() +" se desconectou");
                    desconectar();
                    break;
                } 
                
                // Sair da sala
                else if (comando.startsWith("/sairSala")) {
                    Sala sala = usuario.getSalaAtual();
                    if (sala != null) { // Se o usuário está em uma sala
                        sala.removeMembro(usuario); // Remove o usuário da sala
                        usuario.setSalaAtual(null); // Atualiza o status de sala atual
                        saida.println("[Servidor]: Saiu da sala.");
                    } else {
                        saida.println("[Erro]: Não está em nenhuma sala.");
                    }
                }

                // Enviar mensagem na sala
                else if (comando.startsWith("/msg ")) {
                    // Armazena a string depois do primeiro espaço da instrução
                    String msg = comando.split(" ", 2)[1];
                    Sala sala = usuario.getSalaAtual(); // Aponta para sala atual do usuário
                    if (sala != null) { // Se ele estiver em uma sala
                        sala.mensagemGrupo("[" + usuario.getSalaAtual().getNome() + "] " + usuario.getNome() +":" + msg);
                    } else {
                        saida.println("[Erro]: Você não está em nenhuma sala.");
                    }
                } 
                
                // Criar uma sala
                else if (comando.startsWith("/criar ")) {
                    if (!usuario.isAdmin()) { // Se o usuário não é um admin
                        saida.println("[Erro]: Somente admins podem criar salas.");
                        continue;
                    }
                    String nome = comando.split(" ", 2)[1]; // Armazena o nome da sala
                    if (Servidor.salas.containsKey(nome)) { // Se tentar criar uma sala que já existe
                        saida.println("[Erro]: Sala já existe.");
                    } else {
                        // Caso contrário, cria uma nova sala
                        Servidor.salas.put(nome, new Sala(nome)); //
                        saida.println("[Servidor]: Sala criada: " + nome);
                        System.out.println("[Servidor]: A sala " + nome + " foi criada pelo admin " + usuario.getNome());
                    }
                } 
                
                // Encerrar uma sala
                else if (comando.startsWith("/encerrarSala ")) {
                    if (!usuario.isAdmin()) { // Se o usuário não é um admin
                        saida.println("[Erro]: Somente admins podem encerrar salas.");
                        continue;
                    }
                    String nome = comando.split(" ", 2)[1]; // Armazena o nome da sala
                    Sala sala = Servidor.salas.get(nome); // Aponta para a sala do usuário
                    if (sala != null) { // Se a sala existe
                        for (Usuario u : sala.getMembros()) {
                            // Remove todos os usuários e os informa
                            u.getEscritor().println("[Servidor]: Sala encerrada pelo admin.");
                            u.setSalaAtual(null);
                        }
                        System.out.println("[Servidor]: A sala " + " foi encerrada pelo adm " + usuario.getNome());
                        Servidor.salas.remove(nome); // Remove a sala do dicionário de salas
                        saida.println("[Servidor]: Sala encerrada.");
                    } else {
                        saida.println("[Erro]: Sala não encontrada.");
                    }
                } 
                
                // Expulsar alguém da sala
                else if (comando.startsWith("/expulsar ")) {
                    if (!usuario.isAdmin()) { // Se não for um admin
                        saida.println("[Erro] Somente admins podem expulsar.");
                        continue;
                    }
                    Sala sala = usuario.getSalaAtual(); // Aponta para a sala
                    if (sala == null) {
                        saida.println("[Erro]: Você não está em uma sala.");
                        continue;
                    }
                    String nome = comando.split(" ", 2)[1]; // Armazena o nome especificado
                    Usuario alvo = sala.buscarUsuario(nome); // Busca o nome especificado na sala
                    if (alvo != null) { // Se o especificado está na sala, remove
                        sala.removeMembro(alvo);
                        alvo.setSalaAtual(null);
                        alvo.getEscritor().println("[Servidor]: Você foi expulso por um admin."); // Informa ao usuário a expulsão
                        saida.println("[Servidor]: Usuário expulso.");
                    } else {
                        saida.println("[Erro]: Usuário não encontrado na sala.");
                    }
                } 
                
                // Exibe a lista de comandos
                else if (comando.startsWith("/help")){
                    saida.println("-> Comandos comuns:\n");
                    saida.println("/listarSalas");
                    saida.println("/entrar nome_da_sala");
                    saida.println("/sairSala");
                    saida.println("/msg");
                    saida.println("/sairServidor\n");
                    saida.println("-> Somente Adms:\n");
                    saida.println("/criar nome_da_sala");
                    saida.println("/expulsar nome_do_cliente");
                    saida.println("/encerrarSala nome_da_sala\n");
                }
                else {
                    saida.println("[Erro]: Comando inválido.");
                }
            }

        } catch (IOException e) { // Se der algo errado
            System.out.println("[Erro IO]: " + e.getMessage());
        } finally {
            desconectar();
        }
    }

    // Método para desconectar do servidor
    private void desconectar() {
        try {
            if (usuario != null) { // Se o usuário logou ao servidor
                if (usuario.getSalaAtual() != null) { // Se ele tiver em uma sala
                    usuario.getSalaAtual().removeMembro(usuario); // tira ele da sala
                }
                // Remove o usuário do conjunto de usuários do servidor
                synchronized (Servidor.usuarios) {
                    Servidor.usuarios.remove(usuario);
                }
            }
            socket.close(); // Encerra a conexão
        } catch (IOException e) {
            System.out.println("[Erro ao desconectar]: " + e.getMessage());
        }
    }
}
