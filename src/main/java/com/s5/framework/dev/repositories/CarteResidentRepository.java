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
import com.s5.framework.dev.models.CarteResident;

public class CarteResidentRepository {

    public static class DemandeInfo {
        private Long idPersonne;
        private String typeDemandeCode;
        private String statutDemandeCode;
        private String typeVisaVouluCode;

        public Long getIdPersonne() {
            return idPersonne;
        }

        public void setIdPersonne(Long idPersonne) {
            this.idPersonne = idPersonne;
        }

        public String getTypeDemandeCode() {
            return typeDemandeCode;
        }

        public void setTypeDemandeCode(String typeDemandeCode) {
            this.typeDemandeCode = typeDemandeCode;
        }

        public String getStatutDemandeCode() {
            return statutDemandeCode;
        }

        public void setStatutDemandeCode(String statutDemandeCode) {
            this.statutDemandeCode = statutDemandeCode;
        }

        public String getTypeVisaVouluCode() {
            return typeVisaVouluCode;
        }

        public void setTypeVisaVouluCode(String typeVisaVouluCode) {
            this.typeVisaVouluCode = typeVisaVouluCode;
        }
    }

    public List<CarteResident> findAll() {
        String sql = "SELECT * FROM carte_resident ORDER BY id_carte DESC";
        List<CarteResident> cartes = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                cartes.add(mapRow(rs));
            }
            return cartes;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture des cartes resident", e);
        }
    }

    public Optional<CarteResident> findById(Long id) {
        String sql = "SELECT * FROM carte_resident WHERE id_carte = ?";

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
            throw new RuntimeException("Erreur lors de la recherche de la carte id=" + id, e);
        }
    }

    public List<CarteResident> findByPersonneId(Long idPersonne) {
        String sql = "SELECT * FROM carte_resident WHERE id_personne = ? ORDER BY date_emission DESC, id_carte DESC";
        List<CarteResident> cartes = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, idPersonne);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    cartes.add(mapRow(rs));
                }
            }
            return cartes;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'historique des cartes de la personne id=" + idPersonne, e);
        }
    }

    public List<CarteResident> search(String numCarte, Long idPersonne, Integer idStatutCarte) {
        String sql = """
                SELECT *
                FROM carte_resident
                WHERE (? IS NULL OR LOWER(num_carte) LIKE LOWER('%' || ? || '%'))
                  AND (? IS NULL OR id_personne = ?)
                  AND (? IS NULL OR id_statut_carte = ?)
                ORDER BY id_carte DESC
                """;

        List<CarteResident> cartes = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, numCarte);
            statement.setString(2, numCarte);
            setNullableLong(statement, 3, idPersonne);
            setNullableLong(statement, 4, idPersonne);
            setNullableInteger(statement, 5, idStatutCarte);
            setNullableInteger(statement, 6, idStatutCarte);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    cartes.add(mapRow(rs));
                }
            }
            return cartes;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des cartes resident", e);
        }
    }

    public List<CarteResident> findExpiringWithinDays(int days) {
        String sql = """
                SELECT c.*
                FROM carte_resident c
                JOIN statut_carte_ref s ON s.id_statut_carte = c.id_statut_carte
                WHERE c.date_expiration BETWEEN CURRENT_DATE AND CURRENT_DATE + (? * INTERVAL '1 day')
                  AND LOWER(s.code) = LOWER('actif')
                ORDER BY c.date_expiration ASC
                """;

        List<CarteResident> cartes = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, days);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    cartes.add(mapRow(rs));
                }
            }
            return cartes;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture des cartes expirant bientot", e);
        }
    }

    public CarteResident save(CarteResident carte) {
        if (carte.getIdCarte() == null) {
            return insert(carte);
        }
        return update(carte);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM carte_resident WHERE id_carte = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la carte id=" + id, e);
        }
    }

    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM carte_resident WHERE id_carte = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la verification existence carte id=" + id, e);
        }
    }

    public boolean existsByNumCarte(String numCarte) {
        String sql = "SELECT 1 FROM carte_resident WHERE LOWER(num_carte) = LOWER(?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, numCarte);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la verification numero carte", e);
        }
    }

    public boolean existsByNumCarteAndIdCarteNot(String numCarte, Long idCarte) {
        String sql = "SELECT 1 FROM carte_resident WHERE LOWER(num_carte) = LOWER(?) AND id_carte <> ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, numCarte);
            statement.setLong(2, idCarte);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la verification numero carte (update)", e);
        }
    }

    public Integer findStatutCarteIdByCode(String code) {
        String sql = "SELECT id_statut_carte FROM statut_carte_ref WHERE LOWER(code) = LOWER(?)";

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
            throw new RuntimeException("Erreur lors de la recherche statut carte code=" + code, e);
        }
    }

    public String findStatutCarteCodeById(Integer idStatutCarte) {
        if (idStatutCarte == null) {
            return null;
        }
        String sql = "SELECT code FROM statut_carte_ref WHERE id_statut_carte = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idStatutCarte);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche code statut carte id=" + idStatutCarte, e);
        }
    }

    public Integer findTypeCarteIdByCode(String code) {
        String sql = "SELECT id_type_carte FROM type_carte_ref WHERE LOWER(code) = LOWER(?)";

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
            throw new RuntimeException("Erreur lors de la recherche type carte code=" + code, e);
        }
    }

    public Optional<DemandeInfo> findDemandeInfo(Long idDemande) {
        String sql = """
                SELECT d.id_personne,
                       td.code AS type_demande_code,
                       sd.code AS statut_demande_code,
                       tv.code AS type_visa_voulu_code
                FROM demande d
                LEFT JOIN type_demande_ref td ON td.id_type_demande = d.id_type_demande
                LEFT JOIN statut_demande_ref sd ON sd.id_statut_demande = d.id_statut_demande
                LEFT JOIN type_visa_voulu_ref tv ON tv.id_type_visa_voulu = d.id_type_visa_voulu
                WHERE d.id_demande = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, idDemande);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                DemandeInfo info = new DemandeInfo();
                info.setIdPersonne(getNullableLong(rs, "id_personne"));
                info.setTypeDemandeCode(rs.getString("type_demande_code"));
                info.setStatutDemandeCode(rs.getString("statut_demande_code"));
                info.setTypeVisaVouluCode(rs.getString("type_visa_voulu_code"));
                return Optional.of(info);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche infos demande id=" + idDemande, e);
        }
    }

    public CarteResident updateStatut(Long idCarte, Integer idStatutCarte) {
        String sql = "UPDATE carte_resident SET id_statut_carte = ?, updated_at = CURRENT_TIMESTAMP WHERE id_carte = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setNullableInteger(statement, 1, idStatutCarte);
            statement.setLong(2, idCarte);
            int rows = statement.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Carte introuvable avec id=" + idCarte);
            }
            return findById(idCarte).orElseThrow(() -> new RuntimeException("Carte introuvable apres changement statut"));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du changement de statut de la carte", e);
        }
    }

    public CarteResident createDuplicata(Long idCarteOriginal, Long idDemande, String numNouvelleCarte,
                                         LocalDate dateDelivrance, Integer idStatutCarteActif, String motif)
            throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            CarteResident original = findByIdWithConnection(connection, idCarteOriginal)
                    .orElseThrow(() -> new RuntimeException("Carte originale introuvable avec id=" + idCarteOriginal));

            CarteResident nouvelleCarte = new CarteResident();
            nouvelleCarte.setIdPersonne(original.getIdPersonne());
            nouvelleCarte.setIdDemande(idDemande);
            nouvelleCarte.setNumCarte(numNouvelleCarte);
            nouvelleCarte.setDateEmission(dateDelivrance);
            nouvelleCarte.setDateExpiration(dateDelivrance.plusYears(2));
            nouvelleCarte.setIdStatutCarte(idStatutCarteActif);
            nouvelleCarte.setIdTypeCarte(original.getIdTypeCarte());

            Long idNouvelleCarte = insertWithConnection(connection, nouvelleCarte);
            insertDuplicata(connection, idCarteOriginal, idDemande, numNouvelleCarte, motif, dateDelivrance);

            connection.commit();
            return findById(idNouvelleCarte)
                    .orElseThrow(() -> new RuntimeException("Nouvelle carte introuvable apres duplicata"));
        } catch (Exception e) {
            rollbackQuietly(connection);
            throw new RuntimeException("Erreur lors de la creation du duplicata", e);
        } finally {
            resetAutoCommitAndClose(connection);
        }
    }

    public CarteResident transferer(Long idCarteOriginal, CarteResident nouvelleCarte, Long idDemande,
                                    String motif, Integer idStatutOriginalTransfert)
            throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            Long idNouvelleCarte = insertWithConnection(connection, nouvelleCarte);
            insertTransfert(connection, idCarteOriginal, idNouvelleCarte, idDemande, motif);
            updateOriginalStatus(connection, idCarteOriginal, idStatutOriginalTransfert);

            connection.commit();
            return findById(idNouvelleCarte)
                    .orElseThrow(() -> new RuntimeException("Nouvelle carte introuvable apres transfert"));
        } catch (Exception e) {
            rollbackQuietly(connection);
            throw new RuntimeException("Erreur lors du transfert de la carte", e);
        } finally {
            resetAutoCommitAndClose(connection);
        }
    }

    private CarteResident insert(CarteResident carte) {
        try (Connection connection = getConnection()) {
            Long id = insertWithConnection(connection, carte);
            return findById(id).orElseThrow(() -> new RuntimeException("Insertion effectuee mais carte introuvable"));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la creation de la carte", e);
        }
    }

    private Long insertWithConnection(Connection connection, CarteResident carte) throws SQLException {
        String sql = """
                INSERT INTO carte_resident (
                    id_personne, id_demande, num_carte, date_emission, date_expiration,
                    id_statut_carte, id_type_carte, created_at, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindUpsertFields(statement, carte);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            throw new RuntimeException("Creation carte sans cle generee");
        }
    }

    private CarteResident update(CarteResident carte) {
        String sql = """
                UPDATE carte_resident
                SET id_personne = ?,
                    id_demande = ?,
                    num_carte = ?,
                    date_emission = ?,
                    date_expiration = ?,
                    id_statut_carte = ?,
                    id_type_carte = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id_carte = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            bindUpsertFields(statement, carte);
            statement.setLong(8, carte.getIdCarte());
            int rows = statement.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Carte introuvable avec id=" + carte.getIdCarte());
            }
            return findById(carte.getIdCarte())
                    .orElseThrow(() -> new RuntimeException("Mise a jour effectuee mais carte introuvable"));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise a jour de la carte", e);
        }
    }

    private void bindUpsertFields(PreparedStatement statement, CarteResident carte) throws SQLException {
        setNullableLong(statement, 1, carte.getIdPersonne());
        setNullableLong(statement, 2, carte.getIdDemande());
        statement.setString(3, carte.getNumCarte());

        if (carte.getDateEmission() == null) {
            statement.setNull(4, Types.DATE);
        } else {
            statement.setDate(4, Date.valueOf(carte.getDateEmission()));
        }

        if (carte.getDateExpiration() == null) {
            statement.setNull(5, Types.DATE);
        } else {
            statement.setDate(5, Date.valueOf(carte.getDateExpiration()));
        }

        setNullableInteger(statement, 6, carte.getIdStatutCarte());
        setNullableInteger(statement, 7, carte.getIdTypeCarte());
    }

    private void insertDuplicata(Connection connection, Long idCarteOriginal, Long idDemande,
                                 String numNouvelleCarte, String motif, LocalDate dateDelivrance)
            throws SQLException {
        String sql = """
                INSERT INTO duplicata (
                    id_carte_original, id_demande, num_nouvelle_carte, motif, date_delivrance, created_at
                ) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, idCarteOriginal);
            setNullableLong(statement, 2, idDemande);
            statement.setString(3, numNouvelleCarte);
            statement.setString(4, motif);
            statement.setDate(5, Date.valueOf(dateDelivrance));
            statement.executeUpdate();
        }
    }

    private void insertTransfert(Connection connection, Long idCarteOriginal, Long idNouvelleCarte,
                                 Long idDemande, String motif)
            throws SQLException {
        String sql = """
                INSERT INTO transfert_carte (
                    id_carte_original, id_nouvelle_carte, id_demande, date_transfert, motif, created_at
                ) VALUES (?, ?, ?, CURRENT_DATE, ?, CURRENT_TIMESTAMP)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, idCarteOriginal);
            statement.setLong(2, idNouvelleCarte);
            setNullableLong(statement, 3, idDemande);
            statement.setString(4, motif);
            statement.executeUpdate();
        }
    }

    private void updateOriginalStatus(Connection connection, Long idCarteOriginal, Integer idStatutTransfert)
            throws SQLException {
        String sql = "UPDATE carte_resident SET id_statut_carte = ?, updated_at = CURRENT_TIMESTAMP WHERE id_carte = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setNullableInteger(statement, 1, idStatutTransfert);
            statement.setLong(2, idCarteOriginal);
            statement.executeUpdate();
        }
    }

    private Optional<CarteResident> findByIdWithConnection(Connection connection, Long id) throws SQLException {
        String sql = "SELECT * FROM carte_resident WHERE id_carte = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        }
    }

    private CarteResident mapRow(ResultSet rs) throws SQLException {
        CarteResident carte = new CarteResident();
        carte.setIdCarte(rs.getLong("id_carte"));
        carte.setIdPersonne(getNullableLong(rs, "id_personne"));
        carte.setIdDemande(getNullableLong(rs, "id_demande"));
        carte.setNumCarte(rs.getString("num_carte"));

        Date dateEmission = rs.getDate("date_emission");
        if (dateEmission != null) {
            carte.setDateEmission(dateEmission.toLocalDate());
        }

        Date dateExpiration = rs.getDate("date_expiration");
        if (dateExpiration != null) {
            carte.setDateExpiration(dateExpiration.toLocalDate());
        }

        carte.setIdStatutCarte(getNullableInt(rs, "id_statut_carte"));
        carte.setIdTypeCarte(getNullableInt(rs, "id_type_carte"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            carte.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            carte.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return carte;
    }

    private Integer getNullableInt(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }

    private Long getNullableLong(ResultSet rs, String column) throws SQLException {
        long value = rs.getLong(column);
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
