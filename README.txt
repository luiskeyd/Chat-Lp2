Segunda atividade prática - Linguagem de programação II

Integrantes:
Edmar Miquéias Carvalho Vieira
Glezier Montalvane de Farias Ferreira
Luis Eduardo do Rosario Fonseca
Willy Kauã Diniz Teixeira

Funcionalidades:
O projeto é desenvolvido em Java e utiliza sockets e threads 
para criar um servidor virtual e gerenciar a entrada de usuários 
que podem ser do tipo comum ou administradores, além de permitir 
a utilização de salas virtuais.

Descrição das classes:
Usuario:
Refere-se ao cliente em si com seus atributos:
- Nome
- Admin (boolean)
- Sala atual
- Escritor para enviar mensagens

Cliente:
Tem a funcionalidade de conectar ao servidor e enviar comandos.
Também pode receber informações enviadas pelo servidor.

Servidor:
Cria um servidor socket ao qual os clientes se conectarão.
Possui uma thread específica para lidar com cada cliente paralelamente.

Sala:
Possui nome e uma coleção de usuários.
É responável por gerenciar os clientes que estão em cada sala,
isso inclui adicionar, remover e informar clientes.

ClienteConectado:
É responsável por lidar com cada cliente conectado ao servidor.
Implementa todos os comandos que são utilizados pelos clientes.
Envia as devidas mensagens para cada ação realizada ao destinatário adequado.

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
Em caso de conexões para pessoas em diferentes redes,
foi utilizado o Radmin para simular uma rede local.