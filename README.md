Nome do Aplicativo: Quiz de Conhecimentos Gerais

Integrantes: Matheus Henrique dos Santos Bragança & Isaque Spalenza Soares.
Objetivo do Aplicativo:

O aplicativo tem como objetivo ser um ambiente simples e intuitivo para testar os conhecimentos de uma pessoa.

O usuário pode:

	•	Criar perfis individuais para armazenar estatísticas;
	
	•	Escolher entre duas áreas de conhecimento (História ou Matemática);
	
	•	Ver a pontuação ao final do quiz;
	
	•	Acompanhar as estatísticas de desempenho;
	
	•	Há um menu “secreto” para adicionar, editar e excluir perguntas.
	
O foco do app é o aprendizado rápido, permitindo ao usuário utilizar o quiz tanto para estudo quanto para revisão de conteúdo.

Quando o app for aberto pela primeira vez, o usuário tem a opção de criar um perfil ou utilizar o perfil “Guest’.

Para poder editar as perguntas, é necessário que primeiro o usuário abra o Quiz ao menos uma vez para dar “trigger” e injetar as perguntas pré-programadas no banco de dados.

Para abrir o menu “secreto” basta clicar 5 vezes na toolbar da tela inicial.

Tecnologias Utilizadas:

Linguagem & IDE:

	•	Android Studio (Java)
	•	Gradle
	
Interface:

	•	Material Design Components
	•	RecyclerView
	•	ConstraintLayout
	
Persistência:

	•	SQLite (via SQLiteOpenHelper)
	•	Padrão Repository
	•	SharedPreferences para sessão do usuário

Estrutura do Banco de Dados:

O banco é criado e gerenciado por DatabaseHelper.
Ele contém as seguintes tabelas:

Tabela: users

Cadastra os usuários do Aplicativo

	Campo			Tipo					Descrição
	id			INTEGER PK	  		Identificador do usuário
	name		TEXT UNIQUE				Nome do usuário

Tabela: topics

Registra os temas disponíveis

	Campo			Tipo			Descrição
	id			INTEGER PK			Identificador do tema
	name		TEXT UNIQUE			Nome (História, Matemática, etc.)

Tabela: questions

Armazena todas as perguntas inseridas no app, seja via código (seed) ou pelo painel Admin

	Campo			Tipo			Descrição
	id			INTEGER PK	ID da pergunta
	topic_id	INTEGER FK	Tema ao qual pertence
	question_text	TEXT	Enunciado
	option1			TEXT	Alternativa A
	option2			TEXT	Alternativa B
	option3			TEXT	Alternativa C
	option4			TEXT	Alternativa D
	correct_index	INTEGER	Índice da alternativa correta (0–3)
	difficulty	TEXT	Normal ou Avançado


Tabela: scores

Armazena as tentativas de cada usuário

	Campo			Tipo			Descrição
	id			INTEGER PK			Identificador
	user_id		INTEGER FK			Usuário
	topic_id	INTEGER FK			Tema
	score		INTEGER			Pontuação obtida
	created_at		TEXT		Data e hora da tentativa

Funcionalidades Implementadas (CRUD Completo):

CRUD de Usuários:

	•	Criar usuário;
	•	Listar usuários;
	•	Atualizar nome do usuário (edição via diálogo);
	•	Excluir usuário;
	•	Selecionar usuário ativo (salvo via SharedPreferences).

CRUD de Perguntas (AdminActivity):

	•	Criar pergunta (tema, enunciado, alternativas, dificuldade, resposta correta);
	•	Listar perguntas com filtros;
	•	Editar pergunta existente;
	•	Excluir pergunta;
	•	Pré-população automática via seedIfEmpty() (com suas perguntas personalizadas).



Sistema de Quis:

	•	Escolha do tema;
	•	Exibição de perguntas e alternativas;
	•	Verificação automática de acertos;
	•	Cálculo de pontuação;
	•	Tela final com feedback;
	•	Registro automático do score.
Estatísticas:

	•	Histórico por usuário
	•	Exibição de tema, data e pontuação
	•	Listagem limpa via RecyclerView
	•	Cálculos feitos via ScoreRepository
Resumo Reflexivo (Diário de Bordo):

O desenvolvimento envolveu a integração entre múltiplos componentes do Android:

	•	Manipulação de banco de dados SQLite com múltiplas tabelas;
	•	Navegação entre Activities;
	•	Uso avançado de RecyclerViews e Adapters;
	•	Aplicação do padrão Repository para organizar o acesso ao Banco de Dados;
	•	Implementação de CRUD completo para perguntas e usuários;
	•	Construção de interfaces intuitivas com Material Design;
	•	Uso de SharedPreferences para armazenar sessão.
	
Os maiores desafios incluíram:

	•	Criar um fluxo consistente entre CRUD administrativo e o sistema de quiz;
	•	Garantir integridade do banco (foreign keys, cascatas e seeds);
	•	Organizar o app de forma extensível e clara;
	•	A quantidade de erros que o Java indica o tempo todo é amplificado no AndroidStudio;
	•	A ideia de tentar deixar o aplicativo “bonito” se provou complicada devido à falta de criatividade e passar tempo tentando arrumar coisas que não funcionavam.
No final de tudo, foi possível criar um app funcional e intuitivo.

Prints das Principais Telas do Aplicativo:

Tela Inicial:

<img width="632" height="1218" alt="image" src="https://github.com/user-attachments/assets/476a227f-dce4-4557-a89e-e8ac12580200" />


Tela de Gerenciamento de Usuários:

<img width="635" height="1222" alt="image" src="https://github.com/user-attachments/assets/094cd719-8285-490e-9a04-8adf19047665" />


Tela de Estatísticas:

<img width="632" height="1222" alt="image" src="https://github.com/user-attachments/assets/6de130eb-1737-47a6-9560-8d795dbc625f" />


Tela do Quiz:

<img width="626" height="1201" alt="image" src="https://github.com/user-attachments/assets/6604cb5d-084c-497c-be3d-54c42684ce18" />


Tela “secreta” de Gerenciamento de Perguntas:

<img width="571" height="1100" alt="image" src="https://github.com/user-attachments/assets/36a2fec2-4471-4449-a325-6336700ee5eb" /> 

<img width="571" height="1103" alt="image" src="https://github.com/user-attachments/assets/14507893-6aaf-4bcf-9d80-07826efa1d28" />







