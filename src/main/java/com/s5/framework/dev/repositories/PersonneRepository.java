package com.s5.framework.dev.repositories;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import com.maharavo.flame.config.PropertiesLoader;
import com.s5.framework.dev.models.Personne;

public class PersonneRepository {

        public List<Personne> findAll() {
                String sql = "SELECT * FROM personne ORDER BY id_personne DESC";
                List<Personne> personnes = new ArrayList<>();

                try (Connection connection = getConnection();
                         PreparedStatement statement = connection.prepareStatement(sql);
                         ResultSet rs = statement.executeQuery()) {

                        while (rs.next()) {
                                personnes.add(mapRow(rs));
                        }
                        return personnes;
                } catch (SQLException e) {
                        throw new RuntimeException("Erreur lors de la lecture des personnes", e);
                }
        }

        public Optional<Personne> findById(Long id) {
                String sql = "SELECT * FROM personne WHERE id_personne = ?";

                try (Connection connection = getConnection();
                         PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setLong(1, id);
                        try (ResultSet rs = statement.executeQuery()) {
                                if (rs.next()) {
                                        return Optional.of(mapRow(rs));
                                }
                                return Optional.empty();
                        }
                } catch (SQLException e) {
                        throw new RuntimeException("Erreur lors de la recherche de la personne id=" + id, e);
                }
        }

        public Personne save(Personne personne) {
                if (personne.getIdPersonne() == null) {
                        return insert(personne);
                }
                return update(personne);
        }

        public void deleteById(Long id) {
                String sql = "DELETE FROM personne WHERE id_personne = ?";

                try (Connection connection = getConnection();
                         PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setLong(1, id);
                        statement.executeUpdate();
                } catch (SQLException e) {
                        throw new RuntimeException("Erreur lors de la suppression de la personne id=" + id, e);
                }
        }

        public boolean existsById(Long id) {
                String sql = "SELECT 1 FROM personne WHERE id_personne = ?";

                try (Connection connection = getConnection();
                         PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setLong(1, id);
                        try (ResultSet rs = statement.executeQuery()) {
                                return rs.next();
                        }
                } catch (SQLException e) {
                        throw new RuntimeException("Erreur lors de la verification existence personne id=" + id, e);
                }
        }

        public boolean existsByEmail(String email) {
                String sql = "SELECT 1 FROM personne WHERE LOWER(email) = LOWER(?)";

                try (Connection connection = getConnection();
                         PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setString(1, email);
                        try (ResultSet rs = statement.executeQuery()) {
                                return rs.next();
                        }
                } catch (SQLException e) {
                        throw new RuntimeException("Erreur lors de la verification email", e);
                }
        }

        public boolean existsByEmailAndIdPersonneNot(String email, Long idPersonne) {
                String sql = "SELECT 1 FROM personne WHERE LOWER(email) = LOWER(?) AND id_personne <> ?";

                try (Connection connection = getConnection();
                         PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setString(1, email);
                        statement.setLong(2, idPersonne);
                        try (ResultSet rs = statement.executeQuery()) {
                                return rs.next();
                        }
                } catch (SQLException e) {
                        throw new RuntimeException("Erreur lors de la verification email (update)", e);
                }
        }

        public List<Personne> search(String nom, String email, String numVisa, String numCarte) {
                String sql = """
                                SELECT DISTINCT p.*
                                FROM personne p
                                LEFT JOIN visa v ON v.id_personne = p.id_personne
                                LEFT JOIN carte_resident c ON c.id_personne = p.id_personne
                                WHERE (? IS NULL OR LOWER(p.nom) LIKE LOWER('%' || ? || '%'))
                                  AND (? IS NULL OR LOWER(p.email) LIKE LOWER('%' || ? || '%'))
                                  AND (? IS NULL OR LOWER(v.num_visa) LIKE LOWER('%' || ? || '%'))
                                  AND (? IS NULL OR LOWER(c.num_carte) LIKE LOWER('%' || ? || '%'))
                                ORDER BY p.id_personne DESC
                                """;

                List<Personne> personnes = new ArrayList<>();
                try (Connection connection = getConnection();
                         PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setString(1, nom);
                        statement.setString(2, nom);
                        statement.setString(3, email);
                        statement.setString(4, email);
                        statement.setString(5, numVisa);
                        statement.setString(6, numVisa);
                        statement.setString(7, numCarte);
                        statement.setString(8, numCarte);

                        try (ResultSet rs = statement.executeQuery()) {
                                while (rs.next()) {
                                        personnes.add(mapRow(rs));
                                }
                        }
                        return personnes;
                } catch (SQLException e) {
                        throw new RuntimeException("Erreur lors de la recherche multi-criteres", e);
                }
        }

        private Personne insert(Personne personne) {
                String sql = """
                                INSERT INTO personne (
                                        nom, nom_jeune_fille, date_naissance, lieu_naissance,
                                        id_situation_familiale, id_nationalite, id_profession,
                                        adresse, contact, email, photo_url, created_at, updated_at
                                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                                """;

                try (Connection connection = getConnection();
                         PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                        bindUpsertFields(statement, personne);
                        statement.executeUpdate();

                        try (ResultSet keys = statement.getGeneratedKeys()) {
                                if (keys.next()) {
                                        personne.setIdPersonne(keys.getLong(1));
                                }
                        }
                        return findById(personne.getIdPersonne())
                                        .orElseThrow(() -> new RuntimeException("Insertion effectuee mais personne introuvable"));
                } catch (SQLException e) {
                        throw new RuntimeException("Erreur lors de la creation de la personne", e);
                }
        }

        private Personne update(Personne personne) {
                String sql = """
                                UPDATE personne
                                SET nom = ?,
                                        nom_jeune_fille = ?,
                                        date_naissance = ?,
                                        lieu_naissance = ?,
                                        id_situation_familiale = ?,
                                        id_nationalite = ?,
                                        id_profession = ?,
                                        adresse = ?,
                                        contact = ?,
                                        email = ?,
                                        photo_url = ?,
                                        updated_at = CURRENT_TIMESTAMP
                                WHERE id_personne = ?
                                """;

                try (Connection connection = getConnection();
                         PreparedStatement statement = connection.prepareStatement(sql)) {

                        bindUpsertFields(statement, personne);
                        statement.setLong(12, personne.getIdPersonne());
                        int rows = statement.executeUpdate();
                        if (rows == 0) {
                                throw new RuntimeException("Personne introuvable avec id=" + personne.getIdPersonne());
                        }
                        return findById(personne.getIdPersonne())
                                        .orElseThrow(() -> new RuntimeException("Mise a jour effectuee mais personne introuvable"));
                } catch (SQLException e) {
                        throw new RuntimeException("Erreur lors de la mise a jour de la personne", e);
                }
        }

        private void bindUpsertFields(PreparedStatement statement, Personne personne) throws SQLException {
                statement.setString(1, personne.getNom());
                statement.setString(2, personne.getNomJeuneFille());

                LocalDate dateNaissance = personne.getDateNaissance();
                if (dateNaissance != null) {
                        statement.setDate(3, Date.valueOf(dateNaissance));
                } else {
                        statement.setNull(3, Types.DATE);
                }

                statement.setString(4, personne.getLieuNaissance());
                setNullableInteger(statement, 5, personne.getIdSituationFamiliale());
                setNullableInteger(statement, 6, personne.getIdNationalite());
                setNullableInteger(statement, 7, personne.getIdProfession());
                statement.setString(8, personne.getAdresse());
                statement.setString(9, personne.getContact());
                statement.setString(10, personne.getEmail());
                statement.setString(11, personne.getPhotoUrl());
        }

        private void setNullableInteger(PreparedStatement statement, int index, Integer value) throws SQLException {
                if (value == null) {
                        statement.setNull(index, Types.INTEGER);
                        return;
                }
                statement.setInt(index, value);
        }

        private Personne mapRow(ResultSet rs) throws SQLException {
                Personne personne = new Personne();
                personne.setIdPersonne(rs.getLong("id_personne"));
                personne.setNom(rs.getString("nom"));
                personne.setNomJeuneFille(rs.getString("nom_jeune_fille"));

                Date sqlDate = rs.getDate("date_naissance");
                if (sqlDate != null) {
                        personne.setDateNaissance(sqlDate.toLocalDate());
                }

                personne.setLieuNaissance(rs.getString("lieu_naissance"));
                personne.setIdSituationFamiliale(getNullableInt(rs, "id_situation_familiale"));
                personne.setIdNationalite(getNullableInt(rs, "id_nationalite"));
                personne.setIdProfession(getNullableInt(rs, "id_profession"));
                personne.setAdresse(rs.getString("adresse"));
                personne.setContact(rs.getString("contact"));
                personne.setEmail(rs.getString("email"));
                personne.setPhotoUrl(rs.getString("photo_url"));

                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                        personne.setCreatedAt(createdAt.toLocalDateTime());
                }

                Timestamp updatedAt = rs.getTimestamp("updated_at");
                if (updatedAt != null) {
                        personne.setUpdatedAt(updatedAt.toLocalDateTime());
                }

                return personne;
        }

        private Integer getNullableInt(ResultSet rs, String column) throws SQLException {
                int value = rs.getInt(column);
                return rs.wasNull() ? null : value;
        }

        private Connection getConnection() {
                try {
                        Properties properties = PropertiesLoader.loadProperties();

                        String url = resolveProperty(properties, "spring.datasource.url");
                        String username = resolveProperty(properties, "spring.datasource.username");
                        String password = resolveProperty(properties, "spring.datasource.password");
                        String driver = resolveProperty(properties, "spring.datasource.driver-class-name");

                        if (url == null) {
                                throw new RuntimeException("Configuration datasource manquante (spring.datasource.url)");
                        }

                        if (driver == null || driver.isBlank()) {
                                driver = "org.postgresql.Driver";
                        }
                        Class.forName(driver);

                        return DriverManager.getConnection(url, username, password);
                } catch (Exception e) {
                        throw new RuntimeException("Impossible d'etablir la connexion PostgreSQL", e);
                }
        }

        private String resolveProperty(Properties properties, String key) {
                String value = properties.getProperty(key);
                if (value == null || value.isBlank()) {
                        return null;
                }
                value = value.trim();

                if (isPlaceholder(value)) {
                        String inner = value.substring(2, value.length() - 1);
                        int sep = inner.indexOf(':');

                        String envKey = sep >= 0 ? inner.substring(0, sep) : inner;
                        String defaultValue = sep >= 0 ? inner.substring(sep + 1) : null;

                        String envValue = System.getenv(envKey);
                        if (envValue != null && !envValue.isBlank()) {
                                return envValue;
                        }

                        if (defaultValue != null && !defaultValue.isBlank()) {
                                return defaultValue;
                        }

                        return null;
                }

                return value;
        }

        private boolean isPlaceholder(String value) {
                return value.startsWith("${") && value.endsWith("}");
        }

        private String firstNonBlank(String first, String second) {
                if (first != null && !first.isBlank()) {
                        return first;
                }
                if (second != null && !second.isBlank()) {
                        return second;
                }
                return null;
        }
}
