package behnen.julia.makeyourownadventure.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import behnen.julia.makeyourownadventure.model.StoryElement;

/**
 * Created by Julia on 11/22/2015.
 */
public class CreatedStoryElementDB {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "CreatedStoryElement.db";
    private static final String TABLE_NAME = "CreatedStoryElement";

    private CreatedStoryElementDBHelper mCreatedStoryElementDBHelper;
    private SQLiteDatabase mSQLiteDatabase;

    public CreatedStoryElementDB(Context context) {
        mCreatedStoryElementDBHelper = new CreatedStoryElementDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mCreatedStoryElementDBHelper.getWritableDatabase();
    }

    public void closeDB() {
        mSQLiteDatabase.close();
    }

    public boolean insertStoryElement(StoryElement storyElement) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("author", storyElement.getAuthor());
        contentValues.put("storyId", storyElement.getStoryId());
        contentValues.put("elementId", storyElement.getElementId());
        contentValues.put("elementTitle", storyElement.getTitle());
        contentValues.put("imageUrl", storyElement.getImageUrl());
        contentValues.put("elementDescription", storyElement.getDescription());
        contentValues.put("isEnding", storyElement.isEnding());
        contentValues.put("choice1Id", storyElement.getChoice1Id());
        contentValues.put("choice2Id", storyElement.getChoice2Id());
        contentValues.put("choice1Text", storyElement.getChoice1Text());
        contentValues.put("choice2Text", storyElement.getChoice2Text());

        long rowId = mSQLiteDatabase.insert(TABLE_NAME, null, contentValues);
        return rowId != -1;
    }

    public boolean deleteStoryElement(String author, String storyId, int elementId) {
        long rowsAffected = mSQLiteDatabase.delete(
                TABLE_NAME,
                "author = ? AND storyId = ? AND elementId = ?",
                new String[]{author, storyId, Integer.toString(elementId)}
        );
        return rowsAffected == 1;
    }

    public StoryElement getStoryElement(String author, String storyId, int elementId) {
        String[] columns = {
                "elementTitle", "imageUrl", "elementDescription", "isEnding",
                "choice1Id", "choice2Id", "choice1Text", "choice2Text"
        };

        Cursor c = mSQLiteDatabase.query(
                TABLE_NAME,
                columns,
                "author = ? AND storyId = ? AND elementId = ?",
                new String[]{author, storyId, Integer.toBinaryString(elementId)},
                null,
                null,
                null
        );

        c.moveToFirst();

        String title = c.getString(0);
        String imageUrl = c.getString(1);
        String description = c.getString(2);
        boolean isEnding = (c.getInt(3) == 1);
        int choice1Id = c.getInt(4);
        int choice2Id = c.getInt(5);
        String choice1Text = c.getString(6);
        String choice2Text = c.getString(7);

        StoryElement storyElement;
        if (isEnding) {
            storyElement = new StoryElement(author, storyId, elementId, title,
                    imageUrl, description, choice1Id, choice2Id, choice1Text, choice2Text);
        } else {
            storyElement = new StoryElement(author, storyId, elementId, title,
                    imageUrl, description);
        }

        return storyElement;
    }

    public List<StoryElement> getStoryElementsByStory(String author, String storyId) {
        String[] columns = {
                "elementId", "elementTitle", "imageUrl", "elementDescription", "isEnding",
                "choice1Id", "choice2Id", "choice1Text", "choice2Text"
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
        List<StoryElement> list = new ArrayList<>();
        for (int i = 0; i < c.getCount(); i++) {
            int elementId = c.getInt(0);
            String title = c.getString(1);
            String imageUrl = c.getString(2);
            String description = c.getString(3);
            boolean isEnding = (c.getInt(4) == 1);
            int choice1Id = c.getInt(5);
            int choice2Id = c.getInt(6);
            String choice1Text = c.getString(7);
            String choice2Text = c.getString(8);

            StoryElement storyElement;
            if (isEnding) {
                storyElement = new StoryElement(author, storyId, elementId, title,
                        imageUrl, description, choice1Id, choice2Id, choice1Text, choice2Text);
            } else {
                storyElement = new StoryElement(author, storyId, elementId, title,
                        imageUrl, description);
            }
            list.add(storyElement);
            c.moveToNext();
        }
        return list;
    }

    private class CreatedStoryElementDBHelper extends SQLiteOpenHelper {

        private static final String CREATE_CREATED_STORY_ELEMENT_SQL =
                "CREATE TABLE IF NOT EXISTS CreatedStoryElement " +
                        "(author TEXT, storyId TEXT, elementId INT, elementTitle TEXT, " +
                        "imageUrl TEXT, elementDescription TEXT, isEnding BOOL, " +
                        "choice1Id INT, choice2Id INT, choice1Text TEXT, choice2Text TEXT, " +
                        "PRIMARY KEY (author, storyId, elementId))";

        private static final String DROP_CREATED_STORY_ELEMENT_SQL =
                "DROP TABLE IF EXISTS CreatedStoryElement";

        public CreatedStoryElementDBHelper(Context context, String name,
                                       SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_CREATED_STORY_ELEMENT_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int il) {
            sqLiteDatabase.execSQL(DROP_CREATED_STORY_ELEMENT_SQL);
            onCreate(sqLiteDatabase);
        }
    }

}
