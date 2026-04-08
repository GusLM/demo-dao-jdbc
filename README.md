# 📦 Demo DAO JDBC

Este projeto é uma aplicação Java que demonstra a implementação do padrão de projeto **DAO (Data Access Object)** para persistência de dados utilizando **JDBC (Java Database Connectivity)**. A aplicação gerencia entidades de `Sellers` (Vendedores) e `Departments` (Departamentos) em um banco de dados MySQL.

---

### 🚀 Tecnologias Utilizadas

- **Java 21**: Linguagem de programação principal.
- **JDBC**: API do Java para conexão com bancos de dados relacionais.
- **MySQL Connector/J 9.6.0**: Driver JDBC oficial para o banco de dados MySQL.
- **Maven**: Gerenciador de dependências e automação de build.

---

### 🏛️ Padrões de Projeto Aplicados

O projeto utiliza padrões de arquitetura para garantir desacoplamento e facilidade de manutenção:
- **DAO (Data Access Object)**: Separação da lógica de acesso a dados da lógica de negócios.
- **Factory**: Utilizado para instanciar as implementações de DAO (`DaoFactory`), ocultando os detalhes de implementação das interfaces.
- **Composition**: Utilizado nas entidades (ex: `Seller` possui um `Department`).

---

### 📁 Estrutura do Projeto

```text
src/main/java
├── application
│   ├── Program.java       # Testes de operações com Seller
│   └── Program2.java      # Testes de operações com Department
├── db
│   ├── DB.java            # Gerenciamento da conexão e utilitários SQL
│   ├── DbException.java   # Exceção personalizada para erros de DB
│   └── DbIntegrityException.java # Exceção para erros de integridade referencial
└── model
    ├── dao
    │   ├── DaoFactory.java     # Fábrica de instâncias DAO
    │   ├── DepartmentDao.java  # Interface DAO para Department
    │   ├── SellerDao.java      # Interface DAO para Seller
    │   └── impl
    │       ├── DepartmentDaoJDBC.java # Implementação JDBC para Department
    │       └── SellerDaoJDBC.java     # Implementação JDBC para Seller
    └── entities
        ├── Department.java # Entidade de domínio Department
        └── Seller.java     # Entidade de domínio Seller
```

---

### ⚙️ Configuração do Banco de Dados

Para executar o projeto, você precisará de um banco de dados MySQL configurado.

1. Crie o arquivo `db.properties` na raiz do projeto com as suas credenciais:
   ```properties
   user=seu_usuario
   password=sua_senha
   dburl=jdbc:mysql://localhost:3306/nome_do_seu_banco
   useSSL=false
   ```

2. Certifique-se de que as tabelas `seller` e `department` existam no banco de dados.

---

### 🛠️ Como Executar

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/seu-usuario/demo-dao-jdbc.git
   ```

2. **Importe como um projeto Maven** na sua IDE favorita (IntelliJ, Eclipse, VS Code).

3. **Execute os programas principais:**
   - Execute `Program.java` para testar as operações de **Vendedores**.
   - Execute `Program2.java` para testar as operações de **Departamentos**.

---

### 🧪 Operações Suportadas

Ambas as implementações DAO suportam as operações de CRUD:
- `findById`: Busca por ID.
- `findAll`: Busca todos os registros.
- `insert`: Insere um novo registro.
- `update`: Atualiza um registro existente.
- `deleteById`: Remove um registro pelo ID.
- `findByDepartment` (específico para `Seller`): Busca vendedores por departamento.

---

### ✒️ Autor

Desenvolvido por **Gustavo Santos**.
