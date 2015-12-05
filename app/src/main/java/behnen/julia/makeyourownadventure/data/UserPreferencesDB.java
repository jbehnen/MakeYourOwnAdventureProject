package behnen.julia.makeyourownadventure.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Julia on 11/15/2015.
 */
public class UserPreferencesDB {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "UserPreferences.db";
    private static final String TABLE_NAME = "UserPreferences";

    private UserPreferencesDBHelper mUserPreferencesDBHelper;
    private SQLiteDatabase mSQLiteDatabase;

    public UserPreferencesDB(Context context) {
        mUserPreferencesDBHelper = new UserPreferencesDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mUserPreferencesDBHelper.getWritableDatabase();
    }
    
    public void closeDB() {
        mSQLiteDatabase.close();
    }
    
    public boolean insertUserPreferences(String username) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.putNull("author");
        contentValues.putNull("storyId");
        contentValues.putNull("elementId");

        long rowId = mSQLiteDatabase.insert(TABLE_NAME, null, contentValues);
        return rowId != -1;

//        try {
//            long rowId = mSQLiteDatabase.insert(TABLE_NAME, null, contentValues);
//            return rowId != -1;
//        } catch (SQLDataException e) {
//            // already in database
//            return true;
//        }
    }

    public boolean updateUserPreferences(
            String username, String author, String storyId, int elementId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("author", author);
        contentValues.put("storyId", storyId);
        contentValues.put("elementId", Integer.toString(elementId));

        long rowId = mSQLiteDatabase.update(
                TABLE_NAME,
                contentValues,
                "username = ?",
                new String[]{username});
        return rowId != -1;
    }

    public boolean clearUserPreferences(String username) {
        ContentValues contentValues = new ContentValues();
        contentValues.putNull("author");
        contentValues.putNull("storyId");
        contentValues.putNull("elementId");

        long rowId = mSQLiteDatabase.update(
                TABLE_NAME,
                contentValues,
                "username = ?",
                new String[]{username});
        return rowId != -1;
    }

    public String[] getUserPreferences(String username) {
        String[] columns = {
          "author", "storyId", "elementId",
        };

        Cursor c = mSQLiteDatabase.query(
                TABLE_NAME,
                columns,
                "username = ?",
                new String[]{username},
                null,
                null,
                null
        );

        c.moveToFirst();
        String[] preferences = new String[3];
        preferences[0] = c.getString(0);
        preferences[1] = c.getString(1);
        preferences[2] = c.getString(2);
        c.close();
        return preferences;
    }

    private class UserPreferencesDBHelper extends SQLiteOpenHelper {

        private static final String CREATE_USER_PREFERENCES_SQL =
                "CREATE TABLE IF NOT EXISTS UserPreferences " +
                        "(username TEXT PRIMARY KEY, author TEXT, storyId TEXT, elementId STRING)";

        private static final String DROP_USER_PREFERENCES_SQL =
                "DROP TABLE IF EXISTS UserPreferences";
        
        public UserPreferencesDBHelper(Context context, String name,
                                     SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }
        
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_USER_PREFERENCES_SQL);
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int il) {
            sqLiteDatabase.execSQL(DROP_USER_PREFERENCES_SQL);
            onCreate(sqLiteDatabase);
        }
    }
}
