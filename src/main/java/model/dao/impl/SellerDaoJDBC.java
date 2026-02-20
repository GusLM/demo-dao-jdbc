package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementação JDBC do SellerDao.
 *
 * Responsável por realizar operações CRUD
 * na tabela "seller" utilizando JDBC.
 *
 * Observação:
 * Seller possui relacionamento com Department (FK),
 * então vários métodos fazem JOIN entre seller e department.
 */
public class SellerDaoJDBC implements SellerDao {

    // Conexão recebida via injeção pelo construtor
    private Connection connection;

    public SellerDaoJDBC(Connection connection) {
        this.connection = connection;
    }

    // ===== WRITE OPERATIONS =====

    /**
     * Insere um novo Seller no banco.
     *
     * Fluxo:
     * 1. Prepara o INSERT com parâmetros.
     * 2. Converte java.util.Date para java.sql.Date.
     * 3. Executa o comando.
     * 4. Recupera o ID gerado automaticamente.
     * 5. Atualiza o objeto em memória com o ID gerado.
     */
    @Override
    public void insert(Seller obj) {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(
                    "INSERT INTO seller "
                            + "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
                            + "VALUES "
                            + "(?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            // Define os valores nos placeholders (?)
            st.setString(1, obj.getName());
            st.setString(2, obj.getEmail());

            // Conversão obrigatória para tipo SQL Date
            st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));

            st.setDouble(4, obj.getBaseSalary());

            // FK para department
            st.setInt(5, obj.getDepartment().getId());

            int rowsAffected = st.executeUpdate();

            // Se inseriu com sucesso, recupera ID gerado
            if (rowsAffected > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    obj.setId(id);
                }
                DB.closeResultSet(rs);
            } else {
                throw new DbException("Unexpected error! No rows affected!");
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    /**
     * Atualiza um Seller existente.
     *
     * Requisitos:
     * - O objeto deve possuir ID válido.
     *
     * Atualiza todos os campos, incluindo o DepartmentId.
     */
    @Override
    public void update(Seller obj) {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(
                    "UPDATE seller "
                            + "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
                            + "WHERE Id = ?"
            );

            st.setString(1, obj.getName());
            st.setString(2, obj.getEmail());
            st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
            st.setDouble(4, obj.getBaseSalary());
            st.setInt(5, obj.getDepartment().getId());
            st.setInt(6, obj.getId());

            st.executeUpdate();

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    /**
     * Remove um Seller pelo ID.
     *
     * Se nenhuma linha for afetada,
     * significa que o ID não existe.
     */
    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(
                    "DELETE FROM seller "
                    + "WHERE Id = ? "
            );

            st.setInt(1, id);

            int row = st.executeUpdate();

            if (row == 0) {
                throw new DbException("There is no seller with that ID");
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    // ===== READ OPERATIONS =====

    /**
     * Busca um Seller pelo ID.
     *
     * Utiliza INNER JOIN para trazer também os dados do Department.
     *
     * Retorna:
     * - Seller completo (com Department)
     * - null caso não exista.
     */
    @Override
    public Seller findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = connection.prepareStatement(
                    "SELECT seller.*, department.Name as DepName "
                            + "FROM seller INNER JOIN department "
                            + "ON seller.DepartmentId = department.Id "
                            + "WHERE seller.Id = ?"
            );

            st.setInt(1, id);
            rs = st.executeQuery();

            if (rs.next()) {
                // Primeiro instancia o Department
                Department department = instantiateDepartment(rs);

                // Depois instancia o Seller associando o Department
                Seller obj = instantiateSeller(rs, department);
                return obj;
            }

            return null;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(st);
        }
    }

    /**
     * Retorna todos os Sellers.
     *
     * Estratégia importante:
     * Utiliza um Map<Integer, Department> para evitar
     * criar múltiplos objetos Department repetidos.
     *
     * Isso é uma forma manual de "cache de identidade".
     */
    @Override
    public List<Seller> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = connection.prepareStatement(
                    "SELECT seller.*, department.Name as DepName "
                            + "FROM seller INNER JOIN department "
                            + "ON seller.DepartmentId = department.Id "
                            + "ORDER BY Name"
            );

            rs = st.executeQuery();

            List<Seller> sellerList = new ArrayList<>();

            // Mapa para evitar duplicação de Department
            Map<Integer, Department> departmentMap = new HashMap<>();

            while (rs.next()) {

                // Verifica se já existe Department criado para esse ID
                Department department = departmentMap.get(rs.getInt("DepartmentId"));

                if (department == null) {
                    department = instantiateDepartment(rs);
                    departmentMap.put(rs.getInt("DepartmentId"), department);
                }

                Seller seller = instantiateSeller(rs, department);
                sellerList.add(seller);
            }

            return sellerList;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(st);
        }
    }

    /**
     * Busca Sellers pertencentes a um determinado Department.
     *
     * Usa WHERE DepartmentId = ?
     * Também utiliza Map para evitar recriar Department repetido.
     */
    @Override
    public List<Seller> findByDepartment(Department department) {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = connection.prepareStatement(
                    "SELECT seller.*, department.Name as DepName "
                            + "FROM seller INNER JOIN department "
                            + "ON seller.DepartmentId = department.Id "
                            + "WHERE DepartmentId = ? "
                            + "ORDER BY Name"
            );

            st.setInt(1, department.getId());
            rs = st.executeQuery();

            List<Seller> sellerList = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();

            while (rs.next()) {

                Department dept = map.get(rs.getInt("DepartmentId"));

                if (dept == null) {
                    dept = instantiateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), dept);
                }

                Seller seller = instantiateSeller(rs, dept);
                sellerList.add(seller);
            }

            return sellerList;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(st);
        }
    }

    /**
     * Método auxiliar que transforma uma linha do ResultSet
     * em um objeto Seller.
     *
     * Recebe o Department já instanciado
     * para evitar recriação desnecessária.
     */
    private Seller instantiateSeller(ResultSet rs, Department department) throws SQLException {
        Seller obj = new Seller();

        obj.setId(rs.getInt("Id"));
        obj.setName(rs.getString("Name"));
        obj.setEmail(rs.getString("Email"));
        obj.setBirthDate(rs.getDate("BirthDate"));
        obj.setBaseSalary(rs.getDouble("BaseSalary"));
        obj.setDepartment(department);

        return obj;
    }

    /**
     * Método auxiliar que instancia um Department
     * com base nos dados retornados pelo JOIN.
     *
     * Observação:
     * - Usa DepartmentId e o alias "DepName"
     *   definido no SELECT.
     */
    private Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department department = new Department();

        department.setId(rs.getInt("DepartmentId"));
        department.setName(rs.getString("DepName"));

        return department;
    }
}
