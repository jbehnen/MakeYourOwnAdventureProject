package behnen.julia.makeyourownadventure.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import behnen.julia.makeyourownadventure.model.StoryHeader;

/**
 * A database for storing story headers that users have created locally.
 *
 * @author Julia Behnen
 * @version December 6, 2015
 */
public class CreatedStoryHeaderDB {

    /**
     * The version of the DB.
     */
    public static final int DB_VERSION = 1;
    /**
     * The name of the DB.
     */
    public static final String DB_NAME = "CreatedStoryHeader.db";
    /**
     * The name of the table being accessed.
     */
    private static final String TABLE_NAME = "CreatedStoryHeader";

    /**
     * The writable database that is accessed.
     */
    private SQLiteDatabase mSQLiteDatabase;

    /**
     * Constructs a new CreatedStoryHeaderDB.
     * @param context The context of the DB
     */
    public CreatedStoryHeaderDB(Context context) {
        CreatedStoryHeaderDBHelper mCreatedStoryHeaderDBHelper = new CreatedStoryHeaderDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mCreatedStoryHeaderDBHelper.getWritableDatabase();
    }

    /**
     * Closes the writable database.
     */
    public void closeDB() {
        mSQLiteDatabase.close();
    }

    /**
     * Inserts a StoryHeader into the database.
     * @param storyHeader The story header to be inserted.
     * @return True if the story header was successfully inserted, false otherwise.
     */
    public boolean insertStory(StoryHeader storyHeader) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("author", storyHeader.getAuthor());
        contentValues.put("storyId", storyHeader.getStoryId());
        contentValues.put("storyTitle", storyHeader.getTitle());
        contentValues.put("storyDescription", storyHeader.getDescription());
        contentValues.put("isFinal", false);

        long rowId = mSQLiteDatabase.insert(TABLE_NAME, null, contentValues);
        return rowId != -1;
    }

    /**
     * Uses the provided story header to replace the existing story element in the database
     * with the same author and storyId.
     * @param storyHeader The story header that replaces an existing one.
     * @return True if the story header was replaced, false otherwise.
     */
    public boolean updateStoryHeader(StoryHeader storyHeader) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("storyTitle", storyHeader.getTitle());
        contentValues.put("storyDescription", storyHeader.getDescription());

        long rowsAffected = mSQLiteDatabase.update(
                TABLE_NAME,
                contentValues,
                "author = ? AND storyId = ?",
                new String[]{storyHeader.getAuthor(), storyHeader.getStoryId()});
        return rowsAffected == 1;
    }

    /**
     * Deletes a story header from the database.
     * @param author The author of the story.
     * @param storyId The storyId of the story.
     * @return True if the story header was successfully deleted, false otherwise.
     */
    public boolean deleteStory(String author, String storyId) {
        long rowsAffected = mSQLiteDatabase.delete(
                TABLE_NAME,
                "author = ? AND storyId = ?",
                new String[]{author, storyId}
        );
        return rowsAffected == 1;
    }

    /**
     * Sets the story with the given author and storyId to final if isFinal is true
     * or not final if isFinal is false.
     * @param author The author of the story.
     * @param storyId The storyId of the story.
     * @param isFinal The "final" value of the story.
     * @return True if the update was successful, false otherwise.
     */
    public boolean setStoryFinal(String author, String storyId, boolean isFinal) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("isFinal", isFinal);

        long rowsAffected = mSQLiteDatabase.update(
                TABLE_NAME,
                contentValues,
                "author = ? AND storyId = ?",
                new String[]{author, storyId});
        return rowsAffected == 1;
    }

    /**
     * Returns true if the story is set in final in the database or the story cannot be found,
     * false otherwise.
     * @param author The author of the story.
     * @param storyId The storyId of the story.
     * @return True if the story is set in final in the database or the story cannot be found,
     * false otherwise.
     */
    public boolean isStoryFinal(String author, String storyId) {
        String[] columns = {
                "isFinal"
        };

        Cursor c = mSQLiteDatabase.query(
                TABLE_NAME,
                columns,
                "author = ? AND storyId = ?",
                new String[]{author, storyId},
                null,
                null,
                null
        );

        boolean isFinal = true;
        if (c.getCount() > 0) {
            c.moveToFirst();
            isFinal = (c.getInt(0) == 1);
        }

        c.close();
        return isFinal;
    }

    /**
     * Returns all stories that have been created by a given author in the database.
     * @param author The author whose stories will be returned.
     * @return All story headers that have been created by the given author.
     */
    public List<StoryHeader> getStoriesByAuthor(String author) {
        String[] columns = {
                "storyId", "storyTitle", "storyDescription"
        };

        Cursor c = mSQLiteDatabase.query(
                TABLE_NAME,
                columns,
                "author = ?",
                new String[]{author},
                null,
                null,
                null
        );

        c.moveToFirst();
        List<StoryHeader> list = new ArrayList<>();
        for (int i = 0; i < c.getCount(); i++) {
            String storyId = c.getString(0);
            String title = c.getString(1);
            String description = c.getString(2);
            StoryHeader storyHeader = new StoryHeader(author, storyId, title, description);
            list.add(storyHeader);
            c.moveToNext();
        }

        c.close();
        return list;
    }

    /**
     * Returns the StoryHeader in the database with the given author and storyId.
     * @param author The author of the story.
     * @param storyId The storyId of the story.
     * @return The story header if it exists, null otherwise.
     */
    public StoryHeader getStory(String author, String storyId) {
        String[] columns = {
                "storyTitle", "storyDescription"
        };

        Cursor c = mSQLiteDatabase.query(
                TABLE_NAME,
                columns,
                "author = ? AND storyId = ?",
                new String[]{author, storyId},
                null,
                null,
                null
        );

        StoryHeader storyHeader = null;
        if (c.getCount() > 0) {
            c.moveToFirst();

            String title = c.getString(0);
            String description = c.getString(1);
            storyHeader = new StoryHeader(author, storyId, title, description);
        }
        c.close();

        return storyHeader;
    }

    /**
     * The helper class for the writable database.
     */
    private class CreatedStoryHeaderDBHelper extends SQLiteOpenHelper {

        /**
         * The SQL used to create the table.
         */
        private static final String CREATE_CREATED_STORY_HEADER_SQL =
                "CREATE TABLE IF NOT EXISTS CreatedStoryHeader " +
                        "(author TEXT, storyId TEXT, storyTitle TEXT, storyDescription TEXT, " +
                        "isFinal BOOLEAN, PRIMARY KEY (author, storyId))";

        /**
         * The SQL used to delete the table.
         */
        private static final String DROP_CREATED_STORY_HEADER_SQL =
                "DROP TABLE IF EXISTS CreatedStoryHeader";

        /**
         * Constructs a new CreatedStoryHeaderDBHelper.
         * @param context The context of the DB Helper.
         * @param name The name of the database.
         * @param factory The cursor factory.
         * @param version The version of the database.
         */
        public CreatedStoryHeaderDBHelper(Context context, String name,
                                       SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_CREATED_STORY_HEADER_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int il) {
            sqLiteDatabase.execSQL(DROP_CREATED_STORY_HEADER_SQL);
            onCreate(sqLiteDatabase);
        }
    }

}
