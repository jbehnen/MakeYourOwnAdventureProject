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
 * Created by Julia on 11/15/2015.
 */
public class BookmarkedStoryDB {
    
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "BookmarkedStory.db";
    private static final String TABLE_NAME = "BookmarkedStory";
    
    private BookmarkedStoryDBHelper mBookmarkedStoryDBHelper;
    private SQLiteDatabase mSQLiteDatabase;
    
    public BookmarkedStoryDB(Context context) {
        mBookmarkedStoryDBHelper = new BookmarkedStoryDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mBookmarkedStoryDBHelper.getWritableDatabase();
    }
    
    public void closeDB() {
        mSQLiteDatabase.close();
    }
    
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

    public boolean deleteStory(String username, String author, String storyId) {
        long rowsAffected = mSQLiteDatabase.delete(
                TABLE_NAME,
                "username = ? AND author = ? AND storyId = ?",
                new String[]{username, author, storyId}
        );
        return rowsAffected == 1;
    }

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

        c.moveToFirst();

        String title = c.getString(0);
        String description = c.getString(1);
        StoryHeader storyHeader = new StoryHeader(author, storyId, title, description);

        c.close();
        return storyHeader;
    }

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
    
    private class BookmarkedStoryDBHelper extends SQLiteOpenHelper {

        private static final String CREATE_STORY_PROGRESS_SQL =
                "CREATE TABLE IF NOT EXISTS BookmarkedStory " +
                        "(username TEXT, author TEXT, storyId TEXT, storyTitle TEXT, " +
                        "storyDescription TEXT, PRIMARY KEY (username, author, storyId))";

        private static final String DROP_BOOKMARKED_STORY_SQL =
                "DROP TABLE IF EXISTS BookmarkedStory";
        
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
