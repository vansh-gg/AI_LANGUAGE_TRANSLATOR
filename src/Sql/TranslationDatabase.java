package Sql;

import java.sql.*;
import java.util.*;

public class TranslationDatabase {

    private static final String URL = "jdbc:mysql://localhost:3306/testdb";
    private static final String USER = "root";
    private static final String PASSWORD = "ditter";

    // Save a translation
    public static void saveTranslation(String input, String output) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "INSERT INTO translations (input_text, translated_text) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, input);
            ps.setString(2, output);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Fetch previous translations
    public static List<String> getTranslationHistory() {
        List<String> history = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT * FROM translations ORDER BY timestamp DESC LIMIT 10";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                history.add(
                        rs.getTimestamp("timestamp") + " | " +
                                rs.getString("input_text") + " → " +
                                rs.getString("translated_text"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return history;
    }
}
