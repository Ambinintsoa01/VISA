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
import com.s5.framework.dev.models.Visa;

public class VisaRepository {

    public List<Visa> findAll() {
        String sql = "SELECT * FROM visa ORDER BY id_visa DESC";
        List<Visa> visas = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                visas.add(mapRow(rs));
            }
            return visas;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture des visas", e);
        }
    }

    public Optional<Visa> findById(Long id) {
        String sql = "SELECT * FROM visa WHERE id_visa = ?";

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
            throw new RuntimeException("Erreur lors de la recherche du visa id=" + id, e);
        }
    }

    public List<Visa> findByPersonneId(Long idPersonne) {
        String sql = "SELECT * FROM visa WHERE id_personne = ? ORDER BY created_at DESC, id_visa DESC";
        List<Visa> visas = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, idPersonne);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    visas.add(mapRow(rs));
                }
            }
            return visas;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'historique des visas de la personne id=" + idPersonne, e);
        }
    }

    public List<Visa> search(String numVisa, Long idPersonne, Integer idStatutVisa) {
        String sql = """
                SELECT *
                FROM visa
                WHERE (? IS NULL OR LOWER(num_visa) LIKE LOWER('%' || ? || '%'))
                  AND (? IS NULL OR id_personne = ?)
                  AND (? IS NULL OR id_statut_visa = ?)
                ORDER BY id_visa DESC
                """;

        List<Visa> visas = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, numVisa);
            statement.setString(2, numVisa);
            setNullableLong(statement, 3, idPersonne);
            setNullableLong(statement, 4, idPersonne);
            setNullableInteger(statement, 5, idStatutVisa);
            setNullableInteger(statement, 6, idStatutVisa);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    visas.add(mapRow(rs));
                }
            }
            return visas;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des visas", e);
        }
    }

    public Visa save(Visa visa) {
        if (visa.getIdVisa() == null) {
            return insert(visa);
        }
        return update(visa);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM visa WHERE id_visa = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du visa id=" + id, e);
        }
    }

    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM visa WHERE id_visa = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la verification existence visa id=" + id, e);
        }
    }

    public boolean existsByNumVisa(String numVisa) {
        String sql = "SELECT 1 FROM visa WHERE LOWER(num_visa) = LOWER(?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, numVisa);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la verification numero visa", e);
        }
    }

    public boolean existsByNumVisaAndIdVisaNot(String numVisa, Long idVisa) {
        String sql = "SELECT 1 FROM visa WHERE LOWER(num_visa) = LOWER(?) AND id_visa <> ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, numVisa);
            statement.setLong(2, idVisa);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la verification numero visa (update)", e);
        }
    }

    public Visa updateTransformable(Long idVisa, boolean value) {
        String sql = "UPDATE visa SET est_transformable = ?, updated_at = CURRENT_TIMESTAMP WHERE id_visa = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setBoolean(1, value);
            statement.setLong(2, idVisa);
            int rows = statement.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Visa introuvable avec id=" + idVisa);
            }
            return findById(idVisa).orElseThrow(() -> new RuntimeException("Visa introuvable apres mise a jour"));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise a jour transformable du visa", e);
        }
    }

    public Visa updateConnuInterieur(Long idVisa, boolean value) {
        String sql = "UPDATE visa SET est_connu_interieur = ?, updated_at = CURRENT_TIMESTAMP WHERE id_visa = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setBoolean(1, value);
            statement.setLong(2, idVisa);
            int rows = statement.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Visa introuvable avec id=" + idVisa);
            }
            return findById(idVisa).orElseThrow(() -> new RuntimeException("Visa introuvable apres mise a jour"));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise a jour connu interieur du visa", e);
        }
    }

    public Visa transferer(Long idVisaOriginal, Visa nouveauVisa, String motif) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            Long newVisaId = insertWithConnection(connection, nouveauVisa);
            insertTransfert(connection, idVisaOriginal, newVisaId, motif);
            updateOriginalStatusToTransfert(connection, idVisaOriginal);

            connection.commit();
            return findById(newVisaId)
                    .orElseThrow(() -> new RuntimeException("Nouveau visa introuvable apres transfert"));
        } catch (Exception e) {
            rollbackQuietly(connection);
            throw new RuntimeException("Erreur lors du transfert du visa", e);
        } finally {
            resetAutoCommitAndClose(connection);
        }
    }

    private Visa insert(Visa visa) {
        try (Connection connection = getConnection()) {
            Long id = insertWithConnection(connection, visa);
            return findById(id).orElseThrow(() -> new RuntimeException("Insertion effectuee mais visa introuvable"));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la creation du visa", e);
        }
    }

    private Long insertWithConnection(Connection connection, Visa visa) throws SQLException {
        String sql = """
                INSERT INTO visa (
                    id_personne, num_visa, date_entree, date_fin, id_statut_visa,
                    est_transformable, est_connu_interieur, created_at, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindUpsertFields(statement, visa);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            throw new RuntimeException("Creation visa sans cle generee");
        }
    }

    private Visa update(Visa visa) {
        String sql = """
                UPDATE visa
                SET id_personne = ?,
                    num_visa = ?,
                    date_entree = ?,
                    date_fin = ?,
                    id_statut_visa = ?,
                    est_transformable = ?,
                    est_connu_interieur = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id_visa = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            bindUpsertFields(statement, visa);
            statement.setLong(8, visa.getIdVisa());
            int rows = statement.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Visa introuvable avec id=" + visa.getIdVisa());
            }
            return findById(visa.getIdVisa())
                    .orElseThrow(() -> new RuntimeException("Mise a jour effectuee mais visa introuvable"));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise a jour du visa", e);
        }
    }

    private void bindUpsertFields(PreparedStatement statement, Visa visa) throws SQLException {
        setNullableLong(statement, 1, visa.getIdPersonne());
        statement.setString(2, visa.getNumVisa());

        LocalDate dateEntree = visa.getDateEntree();
        if (dateEntree != null) {
            statement.setDate(3, Date.valueOf(dateEntree));
        } else {
            statement.setNull(3, Types.DATE);
        }

        LocalDate dateFin = visa.getDateFin();
        if (dateFin != null) {
            statement.setDate(4, Date.valueOf(dateFin));
        } else {
            statement.setNull(4, Types.DATE);
        }

        setNullableInteger(statement, 5, visa.getIdStatutVisa());

        if (visa.getEstTransformable() == null) {
            statement.setBoolean(6, false);
        } else {
            statement.setBoolean(6, visa.getEstTransformable());
        }

        if (visa.getEstConnuInterieur() == null) {
            statement.setBoolean(7, false);
        } else {
            statement.setBoolean(7, visa.getEstConnuInterieur());
        }
    }

    private void insertTransfert(Connection connection, Long idVisaOriginal, Long idNouveauVisa, String motif)
            throws SQLException {
        String sql = """
                INSERT INTO transfert_visa (id_visa_original, id_nouveau_visa, date_transfert, motif, created_at)
                VALUES (?, ?, CURRENT_DATE, ?, CURRENT_TIMESTAMP)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, idVisaOriginal);
            statement.setLong(2, idNouveauVisa);
            statement.setString(3, motif);
            statement.executeUpdate();
        }
    }

    private void updateOriginalStatusToTransfert(Connection connection, Long idVisaOriginal) throws SQLException {
        Integer statusTransfert = findVisaStatusIdByCode(connection, "en_cours_transfert");
        if (statusTransfert == null) {
            return;
        }

        String sql = "UPDATE visa SET id_statut_visa = ?, updated_at = CURRENT_TIMESTAMP WHERE id_visa = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, statusTransfert);
            statement.setLong(2, idVisaOriginal);
            statement.executeUpdate();
        }
    }

    private Integer findVisaStatusIdByCode(Connection connection, String code) throws SQLException {
        String sql = "SELECT id_statut_visa FROM statut_visa_ref WHERE LOWER(code) = LOWER(?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, code);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return null;
            }
        }
    }

    private Visa mapRow(ResultSet rs) throws SQLException {
        Visa visa = new Visa();
        visa.setIdVisa(rs.getLong("id_visa"));
        visa.setIdPersonne(getNullableLong(rs, "id_personne"));
        visa.setNumVisa(rs.getString("num_visa"));

        Date dateEntree = rs.getDate("date_entree");
        if (dateEntree != null) {
            visa.setDateEntree(dateEntree.toLocalDate());
        }

        Date dateFin = rs.getDate("date_fin");
        if (dateFin != null) {
            visa.setDateFin(dateFin.toLocalDate());
        }

        visa.setIdStatutVisa(getNullableInt(rs, "id_statut_visa"));
        visa.setEstTransformable(getNullableBoolean(rs, "est_transformable"));
        visa.setEstConnuInterieur(getNullableBoolean(rs, "est_connu_interieur"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            visa.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            visa.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return visa;
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

    private void rollbackQuietly(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.rollback();
        } catch (SQLException ignored) {
        }
    }

    private void resetAutoCommitAndClose(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.setAutoCommit(true);
        } catch (SQLException ignored) {
        }
        try {
            connection.close();
        } catch (SQLException ignored) {
        }
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
