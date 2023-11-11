import java.sql.*;
import java.sql.Connection;
import java.sql.*;
import javax.sql.*;
import java.io.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ManipulacaoDadosSQLite {

    public static void main(String[] args) {
        Connection connection = null;

        try {
            // Carregar o driver JDBC
            Class.forName("org.sqlite.JDBC");

            // Estabelecer a conexão com o banco de dados
            String url = "jdbc:sqlite:TrabalhoBanco.db";
            connection = DriverManager.getConnection(url);

            // Criar tabelas
            criarTabelas(connection);

            // Exemplo de array de objetos Cliente
            Cliente[] clientes = {
                    new Cliente("Alice", "alice@email.com"),
                    new Cliente("Bob", "bob@email.com"),
                    new Cliente("Charlie", "charlie@email.com"),
                    new Cliente("David", "david@email.com"),
                    new Cliente("Eva", "eva@email.com")
            };

            // Adicionar clientes ao banco de dados
            adicionarClientes(connection, clientes);

            // Buscar clientes por substring no nome
            String substring = "o";
            buscarClientesPorSubstring(connection, substring);

            // Atualizar o nome de um cliente
            atualizarCliente(connection, 1, "Alice Silva");

            // Exibir clientes após a atualização
            System.out.println("\nClientes após a atualização:");
            consultarClientes(connection);

            // Remover um pedido
            removerPedido(connection, 1);

            // Exibir pedidos após a remoção
            System.out.println("\nPedidos após a remoção:");
            consultarPedidos(connection);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            // Fechar a conexão ao finalizar
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Método para criar tabelas
    private static void criarTabelas(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();

        // Executar os comandos SQL para criar tabelas
        statement.execute("CREATE TABLE IF NOT EXISTS Clientes (id_cliente INTEGER PRIMARY KEY, nome TEXT, email TEXT);");
        statement.execute("CREATE TABLE IF NOT EXISTS Pedidos (id_pedido INTEGER PRIMARY KEY, descricao TEXT, valor REAL, id_cliente INTEGER, FOREIGN KEY (id_cliente) REFERENCES Clientes(id_cliente));");

        statement.close();
    }

    // Método para adicionar um array de clientes
    private static void adicionarClientes(Connection connection, Cliente[] clientes) throws SQLException {
        String sql = "INSERT INTO Clientes (nome, email) VALUES (?, ?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (Cliente cliente : clientes) {
                preparedStatement.setString(1, cliente.getNome());
                preparedStatement.setString(2, cliente.getEmail());
                preparedStatement.executeUpdate();
            }
        }
    }

    // Método para buscar registros por substring no nome dos clientes
    private static void buscarClientesPorSubstring(Connection connection, String substring) throws SQLException {
        String sql = "SELECT * FROM Clientes WHERE nome LIKE ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + substring + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("\nClientes que contêm '" + substring + "' no nome:");
                while (resultSet.next()) {
                    System.out.println("ID: " + resultSet.getInt("id_cliente") +
                            ", Nome: " + resultSet.getString("nome") +
                            ", Email: " + resultSet.getString("email"));
                }
            }
        }
    }

    // Método para consultar clientes
    private static void consultarClientes(Connection connection) throws SQLException {
        String sql = "SELECT * FROM Clientes;";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("id_cliente") +
                        ", Nome: " + resultSet.getString("nome") +
                        ", Email: " + resultSet.getString("email"));
            }
        }
    }

    // Método para consultar pedidos
    private static void consultarPedidos(Connection connection) throws SQLException {
        String sql = "SELECT * FROM Pedidos;";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                System.out.println("ID Pedido: " + resultSet.getInt("id_pedido") +
                        ", Descrição: " + resultSet.getString("descricao") +
                        ", Valor: " + resultSet.getDouble("valor") +
                        ", ID Cliente: " + resultSet.getInt("id_cliente"));
            }
        }
    }

    // Método para atualizar um cliente
    private static void atualizarCliente(Connection connection, int idCliente, String novoNome) throws SQLException {
        String sql = "UPDATE Clientes SET nome = ? WHERE id_cliente = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, novoNome);
            preparedStatement.setInt(2, idCliente);
            preparedStatement.executeUpdate();
        }
    }

    // Método para remover um pedido
    private static void removerPedido(Connection connection, int idPedido) throws SQLException {
        String sql = "DELETE FROM Pedidos WHERE id_pedido = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, idPedido);
            preparedStatement.executeUpdate();
        }
    }

    // Classe Cliente para representar os dados do cliente
    static class Cliente {
        private String nome;
        private String email;

        public Cliente(String nome, String email) {
            this.nome = nome;
            this.email = email;
        }

        public String getNome() {
            return nome;
        }

        public String getEmail() {
            return email;
        }
    }
}
