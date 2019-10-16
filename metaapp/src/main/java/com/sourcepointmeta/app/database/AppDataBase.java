package com.sourcepointmeta.app.database;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.sourcepointmeta.app.AppExecutors;
import com.sourcepointmeta.app.database.dao.TargetingParamDao;
import com.sourcepointmeta.app.database.dao.WebsiteListDao;
import com.sourcepointmeta.app.database.entity.TargetingParam;
import com.sourcepointmeta.app.database.entity.Website;

@Database(entities = {Website.class, TargetingParam.class}, version = 3, exportSchema = false)
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

    public abstract WebsiteListDao websiteListDao();

    public abstract TargetingParamDao targetingParamDao();

}
