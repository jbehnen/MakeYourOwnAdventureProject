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
 * A database for storing story elements that users have created locally.
 *
 * @author Julia Behnen
 * @version December 6, 2015
 */
public class CreatedStoryElementDB {

    /**
     * The version of the DB.
     */
    public static final int DB_VERSION = 1;
    /**
     * The name of the DB.
     */
    public static final String DB_NAME = "CreatedStoryElement.db";
    /**
     * The name of the table being accessed.
     */
    private static final String TABLE_NAME = "CreatedStoryElement";

    /**
     * The writable database that is accessed.
     */
    private SQLiteDatabase mSQLiteDatabase;

    /**
     * Constructs a new CreatedStoryElementDB.
     * @param context The context of the DB
     */
    public CreatedStoryElementDB(Context context) {
        CreatedStoryElementDBHelper createdStoryElementDBHelper = new CreatedStoryElementDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = createdStoryElementDBHelper.getWritableDatabase();
    }

    /**
     * Closes the writable database.
     */
    public void closeDB() {
        mSQLiteDatabase.close();
    }

    /**
     * Inserts a StoryElement into the database.
     * @param storyElement The story header to be inserted.
     * @return True if the story element was successfully inserted, false otherwise.
     */
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

    /**
     * Deletes a story element from the database.
     * @param author The author of the story.
     * @param storyId The storyId of the story.
     * @param elementId The elementId of the story element.
     * @return True if the story element was successfully deleted, false otherwise.
     */
    public boolean deleteStoryElement(String author, String storyId, int elementId) {
        long rowsAffected = mSQLiteDatabase.delete(
                TABLE_NAME,
                "author = ? AND storyId = ? AND elementId = ?",
                new String[]{author, storyId, Integer.toString(elementId)}
        );
        return rowsAffected == 1;
    }

    /**
     * Deletes all story elements of a given story.
     * @param author The author of the story.
     * @param storyId The storyId of the story.
     * @return True if at least one story element is deleted, false otherwise.
     */
    public boolean deleteAllStoryElementsOfStory(String author, String storyId) {
        long rowsAffected = mSQLiteDatabase.delete(
                TABLE_NAME,
                "author = ? AND storyId = ?",
                new String[]{author, storyId}
        );
        return rowsAffected > 0;
    }

    /**
     * Returns the StoryElement in the database with the given author, storyId, and elementId.
     * @param author The author of the story.
     * @param storyId The storyId of the story.
     * @param elementId The elementId of the story element.
     * @return The story element if it exists, null otherwise.
     */
    public StoryElement getStoryElement(String author, String storyId, int elementId) {
        String[] columns = {
                "elementTitle", "imageUrl", "elementDescription", "isEnding",
                "choice1Id", "choice2Id", "choice1Text", "choice2Text"
        };

        Cursor c = mSQLiteDatabase.query(
                TABLE_NAME,
                columns,
                "author = ? AND storyId = ? AND elementId = ?",
                new String[]{author, storyId, Integer.toString(elementId)},
                null,
                null,
                null
        );

        StoryElement storyElement = null;
        if (c.getCount() > 0) {
            c.moveToFirst();

            String title = c.getString(0);
            String imageUrl = c.getString(1);
            String description = c.getString(2);
            boolean isEnding = (c.getInt(3) == 1);
            int choice1Id = c.getInt(4);
            int choice2Id = c.getInt(5);
            String choice1Text = c.getString(6);
            String choice2Text = c.getString(7);


            storyElement = new StoryElement(author, storyId, elementId, title, imageUrl,
                    description, isEnding, choice1Id, choice2Id, choice1Text, choice2Text);
        }

        c.close();
        return storyElement;
    }

    /**
     * Returns all stories that are part of a story.
     * @param author The author of the story.
     * @param storyId The storyId of the story.
     * @return All story elements that are part of the story.
     */
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
            storyElement = new StoryElement(author, storyId, elementId, title, imageUrl,
                    description, isEnding, choice1Id, choice2Id, choice1Text, choice2Text);
            list.add(storyElement);
            c.moveToNext();
        }
        c.close();
        return list;
    }

    /**
     * Uses the provided story element to replace the existing story element in the database
     * with the same author, storyId, and elementId.
     * @param storyElement The story element that replaces an existing one.
     * @return True if the story element was replaced, false otherwise.
     */
    public boolean updateStoryElement(StoryElement storyElement) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("elementTitle", storyElement.getTitle());
        contentValues.put("imageUrl", storyElement.getImageUrl());
        contentValues.put("elementDescription", storyElement.getDescription());
        contentValues.put("isEnding", storyElement.isEnding());
        contentValues.put("choice1Id", storyElement.getChoice1Id());
        contentValues.put("choice2Id", storyElement.getChoice2Id());
        contentValues.put("choice1Text", storyElement.getChoice1Text());
        contentValues.put("choice2Text", storyElement.getChoice2Text());

        long rowsAffected = mSQLiteDatabase.update(
                TABLE_NAME,
                contentValues,
                "author = ? AND storyId = ? AND elementId = ?",
                new String[]{storyElement.getAuthor(), storyElement.getStoryId(),
                        Integer.toString(storyElement.getElementId())});
        return rowsAffected > 0;
    }

    /**
     * Returns the next available element ID for the story with the given author and storyId.
     * @param author The author of the story.
     * @param storyId The storyId of the story.
     * @return The next available element ID for the story with the given author and storyId:
     * this is the maximum element ID plus one.
     */
    public int getNextElementId(String author, String storyId) {
        Cursor c = mSQLiteDatabase.rawQuery("SELECT MAX(elementId) FROM CreatedStoryElement " +
                "WHERE author = ? AND storyId = ?", new String[]{author, storyId});
        c.moveToFirst();
        int max = c.getInt(0);
        c.close();
        return max + 1;
    }

    /**
     * Returns true if the story specified by the given author and storyId has elements in
     * the database, false otherwise.
     * @param author The author of the story.
     * @param storyId The storyId of the story.
     * @return True if the story specified by the given author and storyId has elements in
     * the database, false otherwise.
     */
    public boolean hasStoryElements(String author, String storyId) {
        String[] columns = {
                "elementId",
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

        boolean areElements = c.getCount() > 0;
        c.close();
        return areElements;
    }

    /**
     * The helper class for the writable database.
     */
    private class CreatedStoryElementDBHelper extends SQLiteOpenHelper {

        /**
         * The SQL used to create the table.
         */
        private static final String CREATE_CREATED_STORY_ELEMENT_SQL =
                "CREATE TABLE IF NOT EXISTS CreatedStoryElement " +
                        "(author TEXT, storyId TEXT, elementId INT, elementTitle TEXT, " +
                        "imageUrl TEXT, elementDescription TEXT, isEnding BOOL, " +
                        "choice1Id INT, choice2Id INT, choice1Text TEXT, choice2Text TEXT, " +
                        "PRIMARY KEY (author, storyId, elementId))";

        /**
         * The SQL used to delete the table.
         */
        private static final String DROP_CREATED_STORY_ELEMENT_SQL =
                "DROP TABLE IF EXISTS CreatedStoryElement";

        /**
         * Constructs a new CreatedStoryElementDBHelper.
         * @param context The context of the DB Helper.
         * @param name The name of the database.
         * @param factory The cursor factory.
         * @param version The version of the database.
         */
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
