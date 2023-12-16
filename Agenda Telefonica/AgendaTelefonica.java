package Aula13;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AgendaTelefonica {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");

            createTableIfNotExists(conn);

            Scanner teclado = new Scanner(System.in);

            int escolha = 0;
            while (escolha != 10) {
                try {
                    System.out.println("Menu:");
                    System.out.println("1 - Inserir Registro");
                    System.out.println("2 - Listar todos (ordem alfabética de nome)");
                    System.out.println("3 - Listar todos (ordem crescente de e-mail)");
                    System.out.println("4 - Listar todos (ordem crescente de sobrenome)");
                    System.out.println("5 - Remover Registro");
                    System.out.println("6 - Procurar por nome");
                    System.out.println("7 - Procurar por sobrenome");
                    System.out.println("8 - Editar registro");
                    System.out.println("9 - Listar todos os sobrenomes e total de aparições");
                    System.out.println("10 - Sair");
                    System.out.print("Escolha uma opção: ");
                    escolha = teclado.nextInt();
                    teclado.nextLine();

                    switch (escolha) {
                        case 1:
                            inserirRegistro(conn, teclado);
                            break;
                        case 2:
                            listarRegistrosPorNomeEmOrdemAlfabetica(conn);
                            break;
                        case 3:
                            listarRegistrosPorEmail(conn);
                            break;
                        case 4:
                            listarRegistrosPorSobrenome(conn);
                            break;
                        case 5:
                            deletarRegistro(conn, teclado);
                            break;
                        case 6:
                            procurarPorNome(conn, teclado);
                            break;
                        case 7:
                            procurarPorSobrenome(conn, teclado);
                            break;
                        case 8:
                            atualizarRegistro(conn, teclado);
                            break;
                        case 9:
                            listarSobrenomesEQuantidade(conn);
                            break;
                        case 10:
                            System.out.println("Saindo do programa.");
                            break;
                        default:
                            System.out.println("Opção inválida. Tente novamente.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Entrada inválida. Certifique-se de inserir o tipo correto de dados.");
                    teclado.nextLine();
                } catch (SQLException e) {
                    System.out.println("Ocorreu um erro no banco de dados. Verifique sua conexão ou a consulta SQL.");
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTableIfNotExists(Connection conn) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS AGENDA_TELEFONICA (id INTEGER not NULL, nome VARCHAR(255),\n" +
                "\n" +
                "sobrenome VARCHAR(255), telefone VARCHAR(120),  email VARCHAR(150),\n" +
                "\n" +
                "idade INTEGER, PRIMARY KEY ( id ))";
        PreparedStatement stmt = conn.prepareStatement(createTableSQL);
        stmt.executeUpdate();
    }

    private static void inserirRegistro(Connection conn, Scanner teclado) throws SQLException {

        try {
            System.out.print("Digite o ID: ");
            int id = teclado.nextInt();
            teclado.next();
            System.out.print("Digite o nome: ");
            String nome = teclado.next();
            System.out.print("Digite o sobrenome: ");
            String sobrenome = teclado.next();
            System.out.println("Digite seu telefone");
            String telefone = teclado.next();
            System.out.println("Digite seu e-mail");
            String email = teclado.next();
            System.out.print("Digite a idade: ");
            int idade = teclado.nextInt();


            if (nomeJaExiste(conn, nome, sobrenome)) {
                System.out.println("Erro: Já existe um registro com o mesmo nome e sobrenome.");
            } else {

                String insertSQL = "INSERT INTO AGENDA_TELEFONICA (id, nome, sobrenome, telefone, email, idade) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(insertSQL);
                stmt.setInt(1, id);
                stmt.setString(2, nome);
                stmt.setString(3, sobrenome);
                stmt.setString(4, telefone);
                stmt.setString(5, email);
                stmt.setInt(6, idade);
                stmt.executeUpdate();
            }
            System.out.println("Registro inserido com sucesso.");
        } catch (org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException e) {
            System.out.println("Erro: Já existe um registro com o mesmo e-mail ou ID.");
        } catch (SQLException e) {
            System.out.println("Ocorreu um erro no banco de dados. Verifique sua conexão ou a consulta SQL.");
            e.printStackTrace();
        }
    }


    private static void procurarPorNome(Connection conn, Scanner teclado) throws SQLException {
        System.out.print("Digite um nome para pesquisar: ");
        String nome = lerApenasLetras(teclado);

        String searchSQL = "SELECT * FROM AGENDA_TELEFONICA WHERE nome LIKE ?";
        PreparedStatement stmt = conn.prepareStatement(searchSQL);
        stmt.setString(1, "%" + nome + "%");
        ResultSet rs = stmt.executeQuery();

        boolean registrosEncontrados = false;

        while (rs.next()) {
            int id = rs.getInt("id");
            String resultNome = rs.getString("nome");
            String sobrenome = rs.getString("sobrenome");
            String telefone = rs.getString("telefone");
            String email = rs.getString("email");
            int idade = rs.getInt("idade");
            System.out.println("ID: " + id + ", Nome: " + resultNome + ", Sobrenome: " + sobrenome + ", Telefone: " + telefone + ", E-mail: " + email + ", Idade: " + idade);
            registrosEncontrados = true;
        }

        if (!registrosEncontrados) {
            System.out.println("Nenhum registro encontrado com o nome fornecido.");
        }
    }

    private static void listarRegistrosPorNomeEmOrdemAlfabetica(Connection conn) throws SQLException {
        String selectSQL = "SELECT * FROM AGENDA_TELEFONICA ORDER BY nome";
        PreparedStatement stmt = conn.prepareStatement(selectSQL);
        ResultSet rs = stmt.executeQuery();

        boolean registrosEncontrados = false;

        while (rs.next()) {
            int id = rs.getInt("id");
            String resultNome = rs.getString("nome");
            String sobrenome = rs.getString("sobrenome");
            String telefone = rs.getString("telefone");
            String email = rs.getString("email");
            int idade = rs.getInt("idade");
            System.out.println("ID: " + id + ", Nome: " + resultNome + ", Sobrenome: " + sobrenome + ", Telefone: " + telefone + ", E-mail: " + email + ", Idade: " + idade);
            registrosEncontrados = true;
        }

        if (!registrosEncontrados) {
            System.out.println("Nenhum registro encontrado na agenda.");
        }
    }

    private static void listarRegistrosPorEmail(Connection conn) throws SQLException {
        String selectSQL = "SELECT * FROM AGENDA_TELEFONICA ORDER BY email";
        PreparedStatement stmt = conn.prepareStatement(selectSQL);
        ResultSet rs = stmt.executeQuery();

        boolean registrosEncontrados = false;

        while (rs.next()) {
            int id = rs.getInt("id");
            String resultNome = rs.getString("nome");
            String sobrenome = rs.getString("sobrenome");
            String telefone = rs.getString("telefone");
            String email = rs.getString("email");
            int idade = rs.getInt("idade");
            System.out.println("ID: " + id + ", Nome: " + resultNome + ", Sobrenome: " + sobrenome + ", Telefone: " + telefone + ", E-mail: " + email + ", Idade: " + idade);
            registrosEncontrados = true;
        }

        if (!registrosEncontrados) {
            System.out.println("Nenhum registro encontrado na agenda.");
        }
    }

    private static void listarRegistrosPorSobrenome(Connection conn) throws SQLException {
        String selectSQL = "SELECT * FROM AGENDA_TELEFONICA ORDER BY sobrenome";
        PreparedStatement stmt = conn.prepareStatement(selectSQL);
        ResultSet rs = stmt.executeQuery();

        boolean registrosEncontrados = false;

        while (rs.next()) {
            int id = rs.getInt("id");
            String resultNome = rs.getString("nome");
            String sobrenome = rs.getString("sobrenome");
            String telefone = rs.getString("telefone");
            String email = rs.getString("email");
            int idade = rs.getInt("idade");
            System.out.println("ID: " + id + ", Nome: " + resultNome + ", Sobrenome: " + sobrenome + ", Telefone: " + telefone + ", E-mail: " + email + ", Idade: " + idade);
            registrosEncontrados = true;
        }

        if (!registrosEncontrados) {
            System.out.println("Nenhum registro encontrado na agenda.");
        }
    }


    private static void procurarPorSobrenome(Connection conn, Scanner teclado) throws SQLException {
        System.out.print("Digite um sobrenome para pesquisar: ");
        String sobrenome = lerApenasLetras(teclado);

        String searchSQL = "SELECT * FROM AGENDA_TELEFONICA WHERE sobrenome LIKE ?";
        PreparedStatement stmt = conn.prepareStatement(searchSQL);
        stmt.setString(1, "%" + sobrenome + "%");
        ResultSet rs = stmt.executeQuery();

        boolean registrosEncontrados = false;

        while (rs.next()) {
            int id = rs.getInt("id");
            String nome = rs.getString("nome");
            String resultSobrenome = rs.getString("sobrenome");
            String telefone = rs.getString("telefone");
            String email = rs.getString("email");
            int idade = rs.getInt("idade");
            System.out.println("ID: " + id + ", Nome: " + nome + ", Sobrenome: " + resultSobrenome + ", Telefone: " + telefone + ", E-mail: " + email + ", Idade: " + idade);
            registrosEncontrados = true;
        }

        if (!registrosEncontrados) {
            System.out.println("Nenhum registro encontrado com o sobrenome fornecido.");
        }
    }


    private static void deletarRegistro(Connection conn, Scanner teclado) throws SQLException {
        System.out.print("Digite o ID do registro a ser removido: ");
        int id = teclado.nextInt();
        String deleteSQL = "DELETE FROM AGENDA_TELEFONICA WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(deleteSQL);
        stmt.setInt(1, id);
        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Registro removido com sucesso.");
        } else {
            System.out.println("Nenhum registro com o ID fornecido encontrado.");
        }
    }

    private static void atualizarRegistro(Connection conn, Scanner teclado) throws SQLException {
        System.out.print("Digite o ID do registro a ser atualizado: ");
        int id = teclado.nextInt();
        teclado.nextLine();

        if (registroExiste(conn, id)) {
            System.out.print("Digite o novo nome: ");
            String nome = teclado.nextLine();
            System.out.print("Digite o novo sobrenome: ");
            String sobrenome = teclado.nextLine();
            System.out.println("Digite seu telefone");
            String telefone = teclado.nextLine();
            System.out.println("Digite seu e-mail");
            String email = teclado.nextLine();
            System.out.print("Digite a idade: ");
            int idade = teclado.nextInt();

            String updateSQL = "UPDATE AGENDA_TELEFONICA SET nome = ?, sobrenome = ?, telefone = ?, email = ?, idade = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(updateSQL);
            stmt.setString(1, nome);
            stmt.setString(2, sobrenome);
            stmt.setString(3, telefone);
            stmt.setString(4, email);
            stmt.setInt(5, idade);
            stmt.setInt(6, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Registro atualizado com sucesso.");
            } else {
                System.out.println("Nenhum registro com o ID fornecido encontrado. Nenhum registro foi atualizado.");
            }
        } else {
            System.out.println("Registro com o ID fornecido não encontrado. Não é possível atualizar.");
        }
    }

    private static void listarSobrenomesEQuantidade(Connection conn) throws SQLException {
        String selectSQL = "SELECT sobrenome, COUNT(*) as total FROM AGENDA_TELEFONICA GROUP BY sobrenome";
        PreparedStatement stmt = conn.prepareStatement(selectSQL);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String sobrenome = rs.getString("sobrenome");
            int total = rs.getInt("total");
            System.out.println(sobrenome + " " + total);
        }
    }

    private static String lerApenasLetras(Scanner teclado) {
        String input;
        do {
            input = teclado.nextLine();
            if (!input.matches("^[a-zA-Z]*$")) {
                System.out.println("Somente letras são permitidas. Tente novamente.");
            }
        } while (!input.matches("^[a-zA-Z]*$"));
        return input;
    }

    private static boolean registroExiste(Connection conn, int id) throws SQLException {
        String selectSQL = "SELECT id FROM AGENDA_TELEFONICA WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(selectSQL);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        return rs.next();

    }

    private static boolean nomeJaExiste(Connection conn, String nome, String sobrenome) throws SQLException {
        String selectSQL = "SELECT nome FROM AGENDA_TELEFONICA WHERE nome = ? AND sobrenome = ?";
        PreparedStatement stmt = conn.prepareStatement(selectSQL);
        stmt.setString(1, nome);
        stmt.setString(2, sobrenome);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }
}


