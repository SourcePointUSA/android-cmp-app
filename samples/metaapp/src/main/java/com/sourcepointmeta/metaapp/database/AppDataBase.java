package com.sourcepointmeta.metaapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.sourcepointmeta.metaapp.AppExecutors;
import com.sourcepointmeta.metaapp.database.dao.PropertyListDao;
import com.sourcepointmeta.metaapp.database.dao.TargetingParamDao;
import com.sourcepointmeta.metaapp.database.entity.Property;
import com.sourcepointmeta.metaapp.database.entity.TargetingParam;

@Database(entities = {Property.class, TargetingParam.class}, version = 7, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {

    private static AppDataBase sInstance;

    private static final String DATABASE_NAME = "SourcePointDB.db";

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public static synchronized AppDataBase getInstance(final Context context, final AppExecutors executors) {

        if (sInstance == null) {
            sInstance = buildDatabase(context.getApplicationContext(), executors);
            sInstance.updateDatabaseCreated(context.getApplicationContext());
        }

        return sInstance;
    }

    /**
     * Build the database. {@link Builder#build()} only sets up the database configuration and
     * creates a new instance of the database.
     * The SQLite database is only created when it's accessed for the first time.
     */
    private static AppDataBase buildDatabase(final Context appContext,
                                             final AppExecutors executors) {
        return Room.databaseBuilder(appContext, AppDataBase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        executors.diskIO().execute(() -> {
                            AppDataBase database = AppDataBase.getInstance(appContext, executors);

                            //// add primary insert here

                            database.setDatabaseCreated();
                        });
                    }
                }).addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_3)
                .addMigrations(MIGRATION_3_4)
                .addMigrations(MIGRATION_4_5)
                .addMigrations(MIGRATION_5_6)
                .addMigrations(MIGRATION_6_7)
                .build();
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create the new table
            database.execSQL(
                    "CREATE TABLE websites_new (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `accountId` INTEGER NOT NULL, `name` TEXT, `staging` INTEGER NOT NULL)");
            // Copy the data
            database.execSQL(
                    "INSERT INTO websites_new (id, accountId, name ,staging) SELECT id, accountId, name,staging  FROM websites");
            // Remove the old table
            database.execSQL("DROP TABLE websites");
            // Change the table name to the correct one
            database.execSQL("ALTER TABLE websites_new RENAME TO websites");
        }
    };

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create the new table
            database.execSQL(
                    "CREATE TABLE websites_new (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `accountId` INTEGER NOT NULL, `name` TEXT, `staging` INTEGER NOT NULL, `authId` TEXT)");
            // Copy the data
            database.execSQL(
                    "INSERT INTO websites_new (id, accountId, name ,staging) SELECT id, accountId, name,staging  FROM websites");
            // Remove the old table
            database.execSQL("DROP TABLE websites");
            // Change the table name to the correct one
            database.execSQL("ALTER TABLE websites_new RENAME TO websites");
        }
    };

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create the new table
            database.execSQL(
                    "CREATE TABLE websites_new (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `accountId` INTEGER NOT NULL, `siteId` INTEGER NOT NULL, `name` TEXT, `pmId` TEXT, `staging` INTEGER NOT NULL, `showPM` INTEGER NOT NULL,`authId` TEXT)");

            // Remove the old table
            database.execSQL("DROP TABLE websites");
            // Change the table name to the correct one
            database.execSQL("ALTER TABLE websites_new RENAME TO websites");
            // delete old targetting param details from databse
            database.execSQL("DELETE  FROM targeting_param");
        }
    };

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create the new table
            database.execSQL(
                    "CREATE TABLE property (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `accountId` INTEGER NOT NULL, `propertyId` INTEGER NOT NULL, `property` TEXT, `pmId` TEXT, `staging` INTEGER NOT NULL, `showPM` INTEGER NOT NULL,`authId` TEXT)");
            //copy the data
            database.execSQL(
                    "INSERT INTO property (id, accountId, propertyId, property, pmId, staging, showPM, authId) SELECT id, accountId, siteId, name, pmId, staging, showPM, authId  FROM websites");

            // Remove the old table
            database.execSQL("DROP TABLE websites");
            // craete targeting params duplicate
            database.execSQL(
                    "CREATE TABLE targeting_param_new (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `mKey` TEXT , `mValue` TEXT , `refID` INTEGER NOT NULL  ,FOREIGN KEY (refID)  REFERENCES property (id) ON DELETE CASCADE ON UPDATE CASCADE ) " );
            database.execSQL("CREATE UNIQUE INDEX `index_targeting_param_mKey_refID_new` ON `targeting_param_new` (mKey, refID)");
            //copy data from targeting param
            database.execSQL(
                    "INSERT INTO targeting_param_new (id, mKey, mValue, refID) SELECT id, mKey ,mValue, refID  FROM targeting_param  ");
            // drop table targetting param
            database.execSQL("DROP TABLE targeting_param");
            // Change the table name to the targetting param
            database.execSQL("ALTER TABLE targeting_param_new RENAME TO targeting_param");

        }
    };

    private static final Migration MIGRATION_5_6 = new Migration(5,6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL(
                    "CREATE TABLE property_new (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `accountId` INTEGER NOT NULL, `propertyId` INTEGER NOT NULL, `property` TEXT, `pmId` TEXT, `staging` INTEGER NOT NULL, `isNative` INTEGER NOT NULL,`authId` TEXT)");
            //copy the data
            database.execSQL(
                    "INSERT INTO property_new (id, accountId, propertyId, property, pmId, staging, isNative, authId) SELECT id, accountId, propertyId, property, pmId, staging, showPM, authId  FROM property");


            database.execSQL("DROP TABLE property");

            database.execSQL("ALTER TABLE property_new RENAME TO property");
        }
    };

    private static final Migration MIGRATION_6_7 = new Migration(6,7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL(
                    "CREATE TABLE property_new (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `accountId` INTEGER NOT NULL, `propertyId` INTEGER NOT NULL, `property` TEXT, `pmId` TEXT, `staging` INTEGER NOT NULL, `isNative` INTEGER NOT NULL,`authId` TEXT, `message_language` TEXT)");
            //copy the data
            database.execSQL(
                    "INSERT INTO property_new (id, accountId, propertyId, property, pmId, staging, isNative, authId) SELECT id, accountId, propertyId, property, pmId, staging, isNative, authId  FROM property");


            database.execSQL("DROP TABLE property");

            database.execSQL("ALTER TABLE property_new RENAME TO property");
        }
    };


    /**
     * Check whether the database already exists and expose it via {@link #getDatabaseCreated()}
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true);
    }

    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }

    public abstract PropertyListDao propertyListDao();

    public abstract TargetingParamDao targetingParamDao();

}
