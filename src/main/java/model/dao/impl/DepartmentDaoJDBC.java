package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação JDBC da interface DepartmentDao.
 *
 * Esta classe é responsável por realizar operações CRUD
 * na tabela "department" utilizando JDBC puro.
 *
 * Padrão aplicado:
 * - DAO (Data Access Object)
 */
public class DepartmentDaoJDBC implements DepartmentDao {

    // Conexão com o banco injetada via construtor
    private Connection connection;

    /**
     * Construtor recebe a conexão já aberta.
     * Isso permite reutilização da conexão e facilita testes.
     */
    public DepartmentDaoJDBC(Connection connection){
        this.connection = connection;
    }

    // ===== WRITE OPERATIONS =====

    /**
     * Insere um novo Department no banco de dados.
     *
     * Fluxo:
     * 1. Cria um PreparedStatement com SQL parametrizado.
     * 2. Define os valores dos parâmetros (?).
     * 3. Executa o INSERT.
     * 4. Recupera o ID gerado automaticamente (auto increment).
     * 5. Atualiza o objeto na memória com o ID gerado.
     */
    @Override
    public void insert(Department obj) {
        PreparedStatement st = null;
        try {
            // Statement.RETURN_GENERATED_KEYS permite recuperar o ID gerado
            st = connection.prepareStatement(
                   "INSERT INTO department "
                    + "(Name) "
                    + "VALUES "
                    + "(?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            // Substitui o ? pelo nome do departamento
            st.setString(1, obj.getName());

            // Executa o comando e retorna quantas linhas foram afetadas
            int rowAffected = st.executeUpdate();

            // Se inseriu pelo menos 1 linha, recupera o ID gerado
            if (rowAffected > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    obj.setId(id); // Atualiza o objeto com o ID do banco
                }
                DB.closeResultSet(rs);
            }
        } catch (SQLException e) {
            // Converte exceção SQL em exceção personalizada da aplicação
            throw new DbException(e.getMessage());
        } finally {
            // Sempre fecha o Statement para evitar vazamento de recurso
            DB.closeStatement(st);
        }
    }

    /**
     * Atualiza um Department existente no banco.
     *
     * Requisitos:
     * - O objeto deve ter um ID válido.
     *
     * Fluxo:
     * 1. Executa UPDATE com parâmetros.
     * 2. Atualiza o nome com base no ID.
     */
    @Override
    public void update(Department obj) {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(
                    "UPDATE department "
                            + "SET Name = ? "
                            + "WHERE Id = ? "
            );

            // Define os parâmetros na ordem dos ?
            st.setString(1, obj.getName());
            st.setInt(2, obj.getId());

            // Executa atualização
            st.executeUpdate();

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    /**
     * Remove um Department do banco pelo ID.
     *
     * Fluxo:
     * 1. Executa DELETE com WHERE Id = ?.
     * 2. Verifica se alguma linha foi afetada.
     * 3. Caso nenhuma linha seja removida, lança exceção.
     */
    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(
                    "DELETE FROM department "
                            + "WHERE Id = ? "
            );

            st.setInt(1, id);

            int rowAffected = st.executeUpdate();

            // Se nenhuma linha foi removida, o ID não existe
            if (rowAffected == 0) {
                throw new DbException("There is no department with that ID");
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    // ===== READ OPERATIONS =====

    /**
     * Busca um Department pelo ID.
     *
     * Retorno:
     * - Department se encontrado
     * - null se não existir registro
     *
     * Fluxo:
     * 1. Executa SELECT com WHERE Id = ?.
     * 2. Se houver resultado (rs.next()), instancia o objeto.
     */
    @Override
    public Department findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(
                    "SELECT department.* "
                            + "FROM department "
                            + " WHERE Id = ?"
            );

            st.setInt(1, id);

            rs = st.executeQuery();

            // Se encontrou registro
            if (rs.next()) {
                Department department = instantiateDepartment(rs);
                return department;
            }

            // Caso não encontre
            return null;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(st);
        }
    }

    /**
     * Retorna todos os Departments da tabela.
     *
     * Fluxo:
     * 1. Executa SELECT *.
     * 2. Percorre o ResultSet com while.
     * 3. Instancia cada objeto e adiciona à lista.
     * 4. Retorna lista final.
     */
    @Override
    public List<Department> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement("SELECT * FROM department");
            rs = st.executeQuery();

            List<Department> departmentList = new ArrayList<>();

            // Percorre cada linha do resultado
            while (rs.next()) {
                Department department = instantiateDepartment(rs);
                departmentList.add(department);
            }

            return departmentList;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(st);
        }
    }

    /**
     * Método auxiliar responsável por transformar
     * uma linha do ResultSet em um objeto Department.
     *
     * Esse padrão evita duplicação de código
     * (centraliza o mapeamento objeto-relacional).
     */
    private Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department department = new Department();

        // Mapeamento das colunas para atributos do objeto
        department.setId(rs.getInt("Id"));
        department.setName(rs.getString("Name"));

        return department;
    }
}
