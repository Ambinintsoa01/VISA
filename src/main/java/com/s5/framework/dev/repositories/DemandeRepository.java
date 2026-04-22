package com.s5.framework.dev.repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import com.maharavo.flame.config.PropertiesLoader;
import com.s5.framework.dev.models.Demande;

public class DemandeRepository {

    public List<Demande> findAll() {
        String sql = "SELECT * FROM demande ORDER BY id_demande DESC";
        List<Demande> demandes = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                demandes.add(mapRow(rs));
            }
            return demandes;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture des demandes", e);
        }
    }

    public Optional<Demande> findById(Long id) {
        String sql = "SELECT * FROM demande WHERE id_demande = ?";

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
            throw new RuntimeException("Erreur lors de la recherche de la demande id=" + id, e);
        }
    }

    public List<Demande> findByPersonneId(Long idPersonne) {
        String sql = "SELECT * FROM demande WHERE id_personne = ? ORDER BY date_demande DESC, id_demande DESC";
        List<Demande> demandes = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, idPersonne);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    demandes.add(mapRow(rs));
                }
            }
            return demandes;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'historique des demandes de la personne id=" + idPersonne, e);
        }
    }

    public List<Demande> findEnAttente() {
        String sql = """
                SELECT d.*
                FROM demande d
                JOIN statut_demande_ref s ON s.id_statut_demande = d.id_statut_demande
                WHERE LOWER(s.code) = LOWER('en_attente')
                ORDER BY d.date_demande ASC
                """;

        List<Demande> demandes = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                demandes.add(mapRow(rs));
            }
            return demandes;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture des demandes en attente", e);
        }
    }

    public List<Demande> search(Long idPersonne, Integer idStatutDemande, Integer idTypeDemande, Boolean sansDonneInterieur) {
        String sql = """
                SELECT *
                FROM demande
                WHERE (? IS NULL OR id_personne = ?)
                  AND (? IS NULL OR id_statut_demande = ?)
                  AND (? IS NULL OR id_type_demande = ?)
                  AND (? IS NULL OR sans_donne_interieur = ?)
                ORDER BY id_demande DESC
                """;

        List<Demande> demandes = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setNullableLong(statement, 1, idPersonne);
            setNullableLong(statement, 2, idPersonne);
            setNullableInteger(statement, 3, idStatutDemande);
            setNullableInteger(statement, 4, idStatutDemande);
            setNullableInteger(statement, 5, idTypeDemande);
            setNullableInteger(statement, 6, idTypeDemande);
            setNullableBoolean(statement, 7, sansDonneInterieur);
            setNullableBoolean(statement, 8, sansDonneInterieur);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    demandes.add(mapRow(rs));
                }
            }
            return demandes;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des demandes", e);
        }
    }

    public Demande save(Demande demande) {
        if (demande.getIdDemande() == null) {
            return insert(demande);
        }
        return update(demande);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM demande WHERE id_demande = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la demande id=" + id, e);
        }
    }

    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM demande WHERE id_demande = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la verification existence demande id=" + id, e);
        }
    }

    public Integer findTypeDemandeIdByCode(String code) {
        String sql = "SELECT id_type_demande FROM type_demande_ref WHERE LOWER(code) = LOWER(?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, code);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche type demande code=" + code, e);
        }
    }

    public Integer findStatutDemandeIdByCode(String code) {
        String sql = "SELECT id_statut_demande FROM statut_demande_ref WHERE LOWER(code) = LOWER(?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, code);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche statut demande code=" + code, e);
        }
    }

    public Demande updateStatut(Long idDemande, Integer idStatutDemande, String traitePar) {
        String sql = """
                UPDATE demande
                SET id_statut_demande = ?,
                    traite_par = ?,
                    date_traitement = CURRENT_TIMESTAMP
                WHERE id_demande = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setNullableInteger(statement, 1, idStatutDemande);
            statement.setString(2, traitePar);
            statement.setLong(3, idDemande);
            int rows = statement.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Demande introuvable avec id=" + idDemande);
            }
            return findById(idDemande).orElseThrow(() -> new RuntimeException("Demande introuvable apres changement statut"));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du changement de statut de la demande", e);
        }
    }

    private Demande insert(Demande demande) {
        String sql = """
                INSERT INTO demande (
                    id_personne, id_visa_original, id_type_demande, id_type_visa_voulu,
                    id_statut_demande, sans_donne_interieur, date_demande, motif, traite_par, date_traitement
                ) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?)
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            bindUpsertFields(statement, demande);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    demande.setIdDemande(keys.getLong(1));
                }
            }
            return findById(demande.getIdDemande())
                    .orElseThrow(() -> new RuntimeException("Insertion effectuee mais demande introuvable"));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la creation de la demande", e);
        }
    }

    private Demande update(Demande demande) {
        String sql = """
                UPDATE demande
                SET id_personne = ?,
                    id_visa_original = ?,
                    id_type_demande = ?,
                    id_type_visa_voulu = ?,
                    id_statut_demande = ?,
                    sans_donne_interieur = ?,
                    motif = ?,
                    traite_par = ?,
                    date_traitement = ?
                WHERE id_demande = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            bindUpsertFields(statement, demande);
            statement.setLong(10, demande.getIdDemande());
            int rows = statement.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Demande introuvable avec id=" + demande.getIdDemande());
            }
            return findById(demande.getIdDemande())
                    .orElseThrow(() -> new RuntimeException("Mise a jour effectuee mais demande introuvable"));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise a jour de la demande", e);
        }
    }

    private void bindUpsertFields(PreparedStatement statement, Demande demande) throws SQLException {
        setNullableLong(statement, 1, demande.getIdPersonne());
        setNullableLong(statement, 2, demande.getIdVisaOriginal());
        setNullableInteger(statement, 3, demande.getIdTypeDemande());
        setNullableInteger(statement, 4, demande.getIdTypeVisaVoulu());
        setNullableInteger(statement, 5, demande.getIdStatutDemande());
        setNullableBoolean(statement, 6, defaultBoolean(demande.getSansDonneInterieur()));
        statement.setString(7, demande.getMotif());
        statement.setString(8, demande.getTraitePar());

        if (demande.getDateTraitement() == null) {
            statement.setNull(9, Types.TIMESTAMP);
        } else {
            statement.setTimestamp(9, Timestamp.valueOf(demande.getDateTraitement()));
        }
    }

    private Boolean defaultBoolean(Boolean value) {
        return value != null && value;
    }

    private Demande mapRow(ResultSet rs) throws SQLException {
        Demande demande = new Demande();
        demande.setIdDemande(rs.getLong("id_demande"));
        demande.setIdPersonne(getNullableLong(rs, "id_personne"));
        demande.setIdVisaOriginal(getNullableLong(rs, "id_visa_original"));
        demande.setIdTypeDemande(getNullableInt(rs, "id_type_demande"));
        demande.setIdTypeVisaVoulu(getNullableInt(rs, "id_type_visa_voulu"));
        demande.setIdStatutDemande(getNullableInt(rs, "id_statut_demande"));
        demande.setSansDonneInterieur(getNullableBoolean(rs, "sans_donne_interieur"));
        demande.setMotif(rs.getString("motif"));
        demande.setTraitePar(rs.getString("traite_par"));

        Timestamp dateDemande = rs.getTimestamp("date_demande");
        if (dateDemande != null) {
            demande.setDateDemande(dateDemande.toLocalDateTime());
        }

        Timestamp dateTraitement = rs.getTimestamp("date_traitement");
        if (dateTraitement != null) {
            demande.setDateTraitement(dateTraitement.toLocalDateTime());
        }

        return demande;
    }

    private Integer getNullableInt(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }

    private Long getNullableLong(ResultSet rs, String column) throws SQLException {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }

    private Boolean getNullableBoolean(ResultSet rs, String column) throws SQLException {
        boolean value = rs.getBoolean(column);
        return rs.wasNull() ? null : value;
    }

    private void setNullableInteger(PreparedStatement statement, int index, Integer value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.INTEGER);
            return;
        }
        statement.setInt(index, value);
    }

    private void setNullableLong(PreparedStatement statement, int index, Long value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.BIGINT);
            return;
        }
        statement.setLong(index, value);
    }

    private void setNullableBoolean(PreparedStatement statement, int index, Boolean value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.BOOLEAN);
            return;
        }
        statement.setBoolean(index, value);
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
}
