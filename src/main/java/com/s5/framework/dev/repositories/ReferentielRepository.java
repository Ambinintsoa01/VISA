package com.s5.framework.dev.repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import com.maharavo.flame.config.PropertiesLoader;
import com.s5.framework.dev.models.ReferentielItem;

public class ReferentielRepository {

    private static class RefMeta {
        private final String table;
        private final String idColumn;
        private final boolean hasDescription;
        private final boolean hasOrdre;

        private RefMeta(String table, String idColumn, boolean hasDescription, boolean hasOrdre) {
            this.table = table;
            this.idColumn = idColumn;
            this.hasDescription = hasDescription;
            this.hasOrdre = hasOrdre;
        }
    }

    private static final Map<String, RefMeta> REF_META = new HashMap<>();

    static {
        REF_META.put("statut-visa", new RefMeta("statut_visa_ref", "id_statut_visa", true, true));
        REF_META.put("statut-carte", new RefMeta("statut_carte_ref", "id_statut_carte", true, true));
        REF_META.put("statut-demande", new RefMeta("statut_demande_ref", "id_statut_demande", true, true));

        REF_META.put("type-demande", new RefMeta("type_demande_ref", "id_type_demande", true, false));
        REF_META.put("type-visa-voulu", new RefMeta("type_visa_voulu_ref", "id_type_visa_voulu", true, false));
        REF_META.put("type-carte", new RefMeta("type_carte_ref", "id_type_carte", true, false));

        REF_META.put("situation-familiale", new RefMeta("situation_familiale_ref", "id_situation", false, false));
        REF_META.put("nationalite", new RefMeta("nationalite_ref", "id_nationalite", false, false));
        REF_META.put("profession", new RefMeta("profession_ref", "id_profession", false, false));
    }

    public Map<String, List<ReferentielItem>> listAllActive() {
        Map<String, List<ReferentielItem>> all = new HashMap<>();
        for (String type : REF_META.keySet()) {
            all.put(type, listByType(type, true));
        }
        return all;
    }

    public List<ReferentielItem> listByType(String type, boolean actifOnly) {
        RefMeta meta = getMeta(type);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
                .append(meta.idColumn).append(" AS id, code, libelle");

        if (meta.hasDescription) {
            sql.append(", description");
        } else {
            sql.append(", NULL AS description");
        }

        if (meta.hasOrdre) {
            sql.append(", ordre_affichage");
        } else {
            sql.append(", NULL AS ordre_affichage");
        }

        sql.append(", actif FROM ").append(meta.table);

        if (actifOnly) {
            sql.append(" WHERE actif = TRUE");
        }

        if (meta.hasOrdre) {
            sql.append(" ORDER BY ordre_affichage ASC, libelle ASC");
        } else {
            sql.append(" ORDER BY libelle ASC");
        }

        List<ReferentielItem> items = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString());
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                ReferentielItem item = new ReferentielItem();
                item.setId(rs.getInt("id"));
                item.setCode(rs.getString("code"));
                item.setLibelle(rs.getString("libelle"));
                item.setDescription(rs.getString("description"));

                Integer ordre = getNullableInt(rs, "ordre_affichage");
                item.setOrdreAffichage(ordre);

                boolean actif = rs.getBoolean("actif");
                item.setActif(rs.wasNull() ? null : actif);
                items.add(item);
            }
            return items;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture du referentiel: " + type, e);
        }
    }

    public ReferentielItem create(String type, ReferentielItem item) {
        RefMeta meta = getMeta(type);

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(meta.table)
                .append(" (code, libelle");

        if (meta.hasDescription) {
            sql.append(", description");
        }
        if (meta.hasOrdre) {
            sql.append(", ordre_affichage");
        }
        sql.append(", actif) VALUES (?, ?");

        if (meta.hasDescription) {
            sql.append(", ?");
        }
        if (meta.hasOrdre) {
            sql.append(", ?");
        }
        sql.append(", ?)");

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {

            int idx = 1;
            statement.setString(idx++, item.getCode());
            statement.setString(idx++, item.getLibelle());

            if (meta.hasDescription) {
                statement.setString(idx++, item.getDescription());
            }

            if (meta.hasOrdre) {
                if (item.getOrdreAffichage() == null) {
                    statement.setNull(idx++, Types.INTEGER);
                } else {
                    statement.setInt(idx++, item.getOrdreAffichage());
                }
            }

            statement.setBoolean(idx, item.getActif() != null && item.getActif());
            statement.executeUpdate();

            Integer newId = null;
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    newId = keys.getInt(1);
                }
            }

            if (newId == null) {
                throw new RuntimeException("Creation referentiel reussie sans ID genere");
            }

            return findById(type, newId)
                    .orElseThrow(() -> new RuntimeException("Element referentiel introuvable apres creation"));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la creation d'un element referentiel: " + type, e);
        }
    }

    public boolean existsByCode(String type, String code) {
        RefMeta meta = getMeta(type);
        String sql = "SELECT 1 FROM " + meta.table + " WHERE LOWER(code) = LOWER(?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, code);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la verification code referentiel", e);
        }
    }

    private java.util.Optional<ReferentielItem> findById(String type, Integer id) {
        RefMeta meta = getMeta(type);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
                .append(meta.idColumn).append(" AS id, code, libelle");

        if (meta.hasDescription) {
            sql.append(", description");
        } else {
            sql.append(", NULL AS description");
        }

        if (meta.hasOrdre) {
            sql.append(", ordre_affichage");
        } else {
            sql.append(", NULL AS ordre_affichage");
        }

        sql.append(", actif FROM ").append(meta.table)
                .append(" WHERE ").append(meta.idColumn).append(" = ?");

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return java.util.Optional.empty();
                }

                ReferentielItem item = new ReferentielItem();
                item.setId(rs.getInt("id"));
                item.setCode(rs.getString("code"));
                item.setLibelle(rs.getString("libelle"));
                item.setDescription(rs.getString("description"));
                item.setOrdreAffichage(getNullableInt(rs, "ordre_affichage"));

                boolean actif = rs.getBoolean("actif");
                item.setActif(rs.wasNull() ? null : actif);
                return java.util.Optional.of(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture d'un element referentiel", e);
        }
    }

    public boolean supportsType(String type) {
        if (type == null) {
            return false;
        }
        return REF_META.containsKey(type.trim().toLowerCase(Locale.ROOT));
    }

    private RefMeta getMeta(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Le type referentiel est obligatoire");
        }

        RefMeta meta = REF_META.get(type.trim().toLowerCase(Locale.ROOT));
        if (meta == null) {
            throw new IllegalArgumentException("Type referentiel non supporte: " + type);
        }
        return meta;
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
}
