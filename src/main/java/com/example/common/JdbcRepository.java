package com.example.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class JdbcRepository<T, ID> implements CrudRepository<T, ID> {

    protected abstract String getTableName();
    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;
    protected abstract void setPreparedStatementParamsForCreate(PreparedStatement ps, T entity) throws SQLException;
    protected abstract void setPreparedStatementParamsForUpdate(PreparedStatement ps, T entity) throws SQLException;
    protected abstract String getPrimaryKeyColumn();

    @Override
    public T create(T entity) {
        String sql = getInsertSQL();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            setPreparedStatementParamsForCreate(ps, entity);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public T findById(ID id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + getPrimaryKeyColumn() + " = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<T> findAll() {
        String sql = "SELECT * FROM " + getTableName();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<T> results = new java.util.ArrayList<>();
            while (rs.next()) {
                results.add(mapResultSetToEntity(rs));
            }
            return results;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return java.util.Collections.emptyList();
    }

    @Override
    public T update(T entity) {
        String sql = getUpdateSQL();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setPreparedStatementParamsForUpdate(ps, entity);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public void delete(T entity) {
        String sql = "DELETE FROM " + getTableName() + " WHERE " + getPrimaryKeyColumn() + " = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, getEntityId(entity));
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected abstract String getInsertSQL();
    protected abstract String getUpdateSQL();
    protected abstract ID getEntityId(T entity);
}
