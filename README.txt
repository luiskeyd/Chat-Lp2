Segunda atividade prática - Linguagem de programação II

Funcionalidades:
O projeto é desenvolvido em Java e utiliza sockets e threads 
para criar um servidor virtual e gerenciar a entrada de usuários 
que podem ser do tipo comum ou administradores.

Cliente:



Comandos:
Obs: Todos os comandos são feitos via terminal.

Usuário comum e adm:
/help -> Listar comandos
/listarSalas -> Listar salas disponíveis
/entrar nome_da_sala -> Entrar na sala desejada
/sairSala -> Sair da sala atual
/msg" -> Enviar mensagem
/sairServidor -> Sair do servidor

Somente adm:
/criar nome_da_sala -> Criar nova sala
/expulsar nome_do_cliente -> Expulsar usuário determinado
/encerrarSala nome_da_sala -> Encerrar sala


O trabalho foi desenvolvido e testado via VSCode e IntelliJ. 
Em caso de conexões para pessoas conectadas em diferentes redes,
foi utilizado o Radmin para simular uma rede local.