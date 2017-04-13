package com.loser.gotravel.greenDao;

import android.database.sqlite.SQLiteDatabase;

import com.loser.gotravel.objects.Attractions;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by DucAnhZ on 29/11/2015.
 */
public class DaoSession extends AbstractDaoSession {
    private DaoConfig attractionConfig;
    private AttractionsDao attractionsDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        attractionConfig = daoConfigMap.get(AttractionsDao.class).clone();
        attractionConfig.initIdentityScope(type);
        attractionsDao = new AttractionsDao(attractionConfig, this);
        registerDao(Attractions.class, attractionsDao);

    }

    public void clear() {
        attractionConfig.getIdentityScope().clear();
    }

    public AttractionsDao getAttractions() {
        return attractionsDao;
    }

}
