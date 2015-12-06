package behnen.julia.makeyourownadventure.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * A database for storing the currently-played story element for each user.
 *
 * @author Julia Behnen
 * @version December 6, 2015
 */
public class CurrentElementDB {

    /**
     * The version of the DB.
     */
    public static final int DB_VERSION = 1;
    /**
     * The name of the DB.
     */
    public static final String DB_NAME = "CurrentElement.db";
    /**
     * The name of the table being accessed.
     */
    private static final String TABLE_NAME = "CurrentElement";

    /**
     * The writable database that is accessed.
     */
    private SQLiteDatabase mSQLiteDatabase;

    /**
     * Constructs a new CurrentElementDB.
     * @param context The context of the DB
     */
    public CurrentElementDB(Context context) {
        CurrentElementDBHelper currentElementDBHelper = new CurrentElementDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = currentElementDBHelper.getWritableDatabase();
    }

    /**
     * Closes the writable database.
     */
    public void closeDB() {
        mSQLiteDatabase.close();
    }

    /**
     * Inserts a new user into the database, with empty strings for the author and story ID
     * to indicate that there is no current story element.
     * @param username The username to be inserted.
     * @return True if the username was successfully inserted, false otherwise.
     */
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

    /**
     * Updates the current story element stored for the given user.
     * @param username The user.
     * @param author The author of the current story element.
     * @param storyId The storyId of the current story element.
     * @param elementId The elementId of the current story element.
     * @return True if the update was successful, false otherwise.
     */
    public boolean updateCurrentElement(
            String username, String author, String storyId, int elementId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("author", author);
        contentValues.put("storyId", storyId);
        contentValues.put("elementId", Integer.toString(elementId));

        int rowsAffected = mSQLiteDatabase.update(
                TABLE_NAME,
                contentValues,
                "username = ?",
                new String[]{username});
        return rowsAffected == 1;
    }

    /**
     * Clears the user entry in the database, putting empty strings for the author and story ID
     * to indicate that there is no current story element.
     * @param username The username of the entry to be cleared.
     * @return True if the username was successfully cleared, false otherwise.
     */
    public boolean clearCurrentElement(String username) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("author", "");
        contentValues.put("storyId", "");
        contentValues.put("elementId", 0);

        int rowsAffected = mSQLiteDatabase.update(
                TABLE_NAME,
                contentValues,
                "username = ?",
                new String[]{username});
        return rowsAffected == 1;
    }

    /**
     * Returns the author, storyId, and elementId of the current story element for the given user.
     * @param username The user whose current story is being queried.
     * @return The author, storyId, and elementId of the current story element for the given user,
     * in that order, in a size 3 string array.
     */
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

        String[] element = new String[3];
        if (c.getCount() > 0) {
            c.moveToFirst();
            element[0] = c.getString(0);
            element[1] = c.getString(1);
            element[2] = c.getString(2);
        }
        c.close();
        return element;
    }

    /**
     * The helper class for the writable database.
     */
    private class CurrentElementDBHelper extends SQLiteOpenHelper {

        /**
         * The SQL used to create the table.
         */
        private static final String CREATE_CURRENT_ELEMENT_SQL =
                "CREATE TABLE IF NOT EXISTS CurrentElement " +
                        "(username TEXT PRIMARY KEY, author TEXT, storyId TEXT, elementId TEXT)";

        /**
         * The SQL used to delete the table.
         */
        private static final String DROP_CURRENT_ELEMENT_SQL =
                "DROP TABLE IF EXISTS CurrentElement";

        /**
         * Constructs a new CurrentElementDBHelper.
         * @param context The context of the DB Helper.
         * @param name The name of the database.
         * @param factory The cursor factory.
         * @param version The version of the database.
         */
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
