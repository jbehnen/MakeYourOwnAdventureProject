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
 * A database for storing story headers that users have downloaded from the online database.
 *
 * @author Julia Behnen
 * @version December 6, 2015
 */
public class BookmarkedStoryDB {

    /**
     * The version of the DB.
     */
    public static final int DB_VERSION = 1;
    /**
     * The name of the DB.
     */
    public static final String DB_NAME = "BookmarkedStory.db";
    /**
     * The name of the table being accessed.
     */
    private static final String TABLE_NAME = "BookmarkedStory";

    /**
     * The writable database that is accessed.
     */
    private SQLiteDatabase mSQLiteDatabase;

    /**
     * Constructs a new BookmarkedStoryDB.
     * @param context The context of the DB
     */
    public BookmarkedStoryDB(Context context) {
        BookmarkedStoryDBHelper bookmarkedStoryDBHelper = new BookmarkedStoryDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = bookmarkedStoryDBHelper.getWritableDatabase();
    }

    /**
     * Closes the writable database.
     */
    public void closeDB() {
        mSQLiteDatabase.close();
    }

    /**
     * Inserts a StoryHeader into the database along with the user who downloaded it.
     * @param username The user who downloaded the story header.
     * @param storyHeader The story header to be inserted.
     * @return True if the story header was successfully inserted, false otherwise.
     */
    public boolean insertStory(String username, StoryHeader storyHeader) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("author", storyHeader.getAuthor());
        contentValues.put("storyId", storyHeader.getStoryId());
        contentValues.put("storyTitle", storyHeader.getTitle());
        contentValues.put("storyDescription", storyHeader.getDescription());

        long rowId = mSQLiteDatabase.insert(TABLE_NAME, null, contentValues);
        return rowId != -1;
    }

    /**
     * Deletes a story header from the database.
     * @param username The user who originally downloaded the story.
     * @param author The author of the story.
     * @param storyId The storyId of the story.
     * @return True if the story header was successfully deleted, false otherwise.
     */
    public boolean deleteStory(String username, String author, String storyId) {
        long rowsAffected = mSQLiteDatabase.delete(
                TABLE_NAME,
                "username = ? AND author = ? AND storyId = ?",
                new String[]{username, author, storyId}
        );
        return rowsAffected == 1;
    }

    /**
     * Returns the StoryHeader in the database downloaded by the given user with the given author
     * and storyId.
     * @param username The user who downloaded the story.
     * @param author The author of the story.
     * @param storyId The storyId of the story.
     * @return The story header if it exists, null otherwise.
     */
    public StoryHeader getStoryHeader(String username, String author, String storyId) {
        String[] columns = {
                "storyTitle", "storyDescription"
        };

        Cursor c = mSQLiteDatabase.query(
                TABLE_NAME,
                columns,
                "username = ? AND author = ? and storyId = ?",
                new String[]{username, author, storyId},
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
     * Returns all stories that have been downloaded by the given user.
     * @param username The username whose stories will be returned.
     * @return All story headers that have been downloaded by the given user.
     */
    public List<StoryHeader> getStoriesByUsername(String username) {
        String[] columns = {
          "author", "storyId", "storyTitle", "storyDescription"
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
        List<StoryHeader> list = new ArrayList<>();
        for (int i = 0; i < c.getCount(); i++) {
            String author = c.getString(0);
            String storyId = c.getString(1);
            String title = c.getString(2);
            String description = c.getString(3);
            StoryHeader storyHeader = new StoryHeader(author, storyId, title, description);
            list.add(storyHeader);
            c.moveToNext();
        }
        c.close();
        return list;
    }

    /**
     * The helper class for the writable database.
     */
    private class BookmarkedStoryDBHelper extends SQLiteOpenHelper {

        /**
         * The SQL used to create the table.
         */
        private static final String CREATE_STORY_PROGRESS_SQL =
                "CREATE TABLE IF NOT EXISTS BookmarkedStory " +
                        "(username TEXT, author TEXT, storyId TEXT, storyTitle TEXT, " +
                        "storyDescription TEXT, PRIMARY KEY (username, author, storyId))";

        /**
         * The SQL used to delete the table.
         */
        private static final String DROP_BOOKMARKED_STORY_SQL =
                "DROP TABLE IF EXISTS BookmarkedStory";

        /**
         * Constructs a new BookmarkedStoryDBHelper.
         * @param context The context of the DB Helper.
         * @param name The name of the database.
         * @param factory The cursor factory.
         * @param version The version of the database.
         */
        public BookmarkedStoryDBHelper(Context context, String name, 
                                     SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }
        
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_STORY_PROGRESS_SQL);
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int il) {
            sqLiteDatabase.execSQL(DROP_BOOKMARKED_STORY_SQL);
            onCreate(sqLiteDatabase);
        }
    }
}
