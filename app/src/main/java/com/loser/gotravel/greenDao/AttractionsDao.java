package com.loser.gotravel.greenDao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.loser.gotravel.objects.Attractions;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by DucAnhZ on 29/11/2015.
 */
public class AttractionsDao extends AbstractDao<Attractions, String> {

    public static final String TABLENAME = "Attractions";

    public static class Properties {
        public final static Property _id = new Property(0, Long.class, "_id", true, "_id");
        public final static Property attractionsId = new Property(1, Long.class, "attractionsId", false, "attractionsId");
        public final static Property name = new Property(2, String.class, "name", false, "name");
        public final static Property address = new Property(3, String.class, "address", false, "address");
        public final static Property classify = new Property(4, String.class, "classify", false, "classify");
    }

    public AttractionsDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public AttractionsDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'Attractions' ("
                + "'_id' INTEGER PRIMARY KEY,"
                + "'attractionsId' INTEGER,"
                + "'name' TEXT,"
                + "'address' TEXT,"
                + "'classify' TEXT);");
    }

    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'NOTE'";
        db.execSQL(sql);
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, Attractions entity) {
        stmt.clearBindings();

        Long id = entity._id;
        if (id != null) {
            stmt.bindLong(1, id);
        }
        if (entity.attractionsId != null) {
            stmt.bindLong(2, entity.attractionsId);
        }
        if (entity.name != null) {
            stmt.bindString(3, entity.name);
        }
        if (entity.address != null) {
            stmt.bindString(4, entity.address);
        }
        if (entity.classify != null) {
            stmt.bindString(5, entity.classify);
        }

    }

    @Override
    protected String getKey(Attractions arg0) {
        return null;
    }

    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

    @Override
    protected Attractions readEntity(Cursor cursor, int offset) {
        Attractions entity = new Attractions(
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0),
                cursor.getLong(offset + 1),
                cursor.getString(offset + 2),
                cursor.getString(offset + 3),
                cursor.getString(offset + 4));
        return entity;
    }

    @Override
    protected void readEntity(Cursor cursor, Attractions entity, int offset) {
        entity._id = cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
        entity.attractionsId = cursor.getLong(offset + 1);
        entity.name = cursor.getString(offset + 2);
        entity.address = cursor.getString(offset + 3);
        entity.classify = cursor.getString(offset + 4);
    }

    @Override
    protected String readKey(Cursor arg0, int arg1) {
        return null;
    }

    @Override
    protected String updateKeyAfterInsert(Attractions arg0, long arg1) {
        return null;
    }
}
