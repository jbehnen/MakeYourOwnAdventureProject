package behnen.julia.makeyourownadventure.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Julia on 11/15/2015.
 */
public class CurrentElementDB {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "CurrentElement.db";
    private static final String TABLE_NAME = "CurrentElement";

    private CurrentElementDBHelper mCurrentElementDBHelper;
    private SQLiteDatabase mSQLiteDatabase;

    public CurrentElementDB(Context context) {
        mCurrentElementDBHelper = new CurrentElementDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mCurrentElementDBHelper.getWritableDatabase();
    }
    
    public void closeDB() {
        mSQLiteDatabase.close();
    }
    
    public boolean insertCurrentElement(String username) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("author", "");
        contentValues.put("storyId", "");
        contentValues.put("elementId", 0);

        try {
            long rowId = mSQLiteDatabase.insert(TABLE_NAME, null, contentValues);
            return rowId != -1;
        } catch (Exception e) {
            // already in database
            return true;
        }
    }

    public boolean updateCurrentElement(
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

    public boolean clearCurrentElement(String username) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("author", "");
        contentValues.put("storyId", "");
        contentValues.put("elementId", 0);

        long rowId = mSQLiteDatabase.update(
                TABLE_NAME,
                contentValues,
                "username = ?",
                new String[]{username});
        return rowId != -1;
    }

    public String[] getCurrentElement(String username) {
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
        String[] element = new String[3];
        element[0] = c.getString(0);
        element[1] = c.getString(1);
        element[2] = c.getString(2);
        c.close();
        return element;
    }

    private class CurrentElementDBHelper extends SQLiteOpenHelper {

        private static final String CREATE_CURRENT_ELEMENT_SQL =
                "CREATE TABLE IF NOT EXISTS CurrentElement " +
                        "(username TEXT PRIMARY KEY, author TEXT, storyId TEXT, elementId STRING)";

        private static final String DROP_CURRENT_ELEMENT_SQL =
                "DROP TABLE IF EXISTS CurrentElement";
        
        public CurrentElementDBHelper(Context context, String name,
                                     SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }
        
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_CURRENT_ELEMENT_SQL);
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int il) {
            sqLiteDatabase.execSQL(DROP_CURRENT_ELEMENT_SQL);
            onCreate(sqLiteDatabase);
        }
    }
}
