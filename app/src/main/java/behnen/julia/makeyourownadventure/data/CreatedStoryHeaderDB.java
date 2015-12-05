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
 * Created by Julia on 11/22/2015.
 */
public class CreatedStoryHeaderDB {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "CreatedStoryHeader.db";
    private static final String TABLE_NAME = "CreatedStoryHeader";

    private CreatedStoryHeaderDBHelper mCreatedStoryHeaderDBHelper;
    private SQLiteDatabase mSQLiteDatabase;

    public CreatedStoryHeaderDB(Context context) {
        mCreatedStoryHeaderDBHelper = new CreatedStoryHeaderDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mCreatedStoryHeaderDBHelper.getWritableDatabase();
    }

    public void closeDB() {
        mSQLiteDatabase.close();
    }

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

    public boolean updateStoryHeader(StoryHeader storyHeader) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("storyTitle", storyHeader.getTitle());
        contentValues.put("storyDescription", storyHeader.getDescription());

        long rowId = mSQLiteDatabase.update(
                TABLE_NAME,
                contentValues,
                "author = ? AND storyId = ?",
                new String[]{storyHeader.getAuthor(), storyHeader.getStoryId()});
        return rowId != -1;
    }

    public boolean deleteStory(String author, String storyId) {
        long rowsAffected = mSQLiteDatabase.delete(
                TABLE_NAME,
                "author = ? AND storyId = ?",
                new String[]{author, storyId}
        );
        return rowsAffected == 1;
    }

    public boolean setStoryFinal(String author, String storyId, boolean isFinal) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("isFinal", isFinal);

        long rowId = mSQLiteDatabase.update(
                TABLE_NAME,
                contentValues,
                "author = ? AND storyId = ?",
                new String[]{author, storyId});
        return rowId != -1;
    }

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

        c.moveToFirst();
        boolean isFinal = (c.getInt(0) == 1);

        c.close();
        return isFinal;
    }

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

    public boolean storyExists(String author, String storyId) {
        String[] columns = {
                "storyId"
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
        boolean exists = c.getCount() > 0;
        c.close();
        return exists;
    }

    private class CreatedStoryHeaderDBHelper extends SQLiteOpenHelper {

        private static final String CREATE_CREATED_STORY_HEADER_SQL =
                "CREATE TABLE IF NOT EXISTS CreatedStoryHeader " +
                        "(author TEXT, storyId TEXT, storyTitle TEXT, storyDescription TEXT, " +
                        "isFinal BOOLEAN, PRIMARY KEY (author, storyId))";

        private static final String DROP_CREATED_STORY_HEADER_SQL =
                "DROP TABLE IF EXISTS CreatedStoryHeader";

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
