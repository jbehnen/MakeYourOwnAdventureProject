package behnen.julia.makeyourownadventure.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;

import behnen.julia.makeyourownadventure.model.Story;

/**
 * Created by Julia on 11/1/2015.
 *
 * PLEASE IGNORE: Not related to current app functionality.
 */
public class StoryDB {
//    private static final String DATABASE_NAME = "myoa.db";
//    private static final int DATABASE_VERSION = 1;
//    private static final String SAVED_STORIES_TABLE = "SAVED_STORIES";
//
//    private Context context;
//    private SQLiteDatabase db;
//
//    private SQLiteStatement insertStmt;
//    private static final String INSERT = "INSERT INTO " + SAVED_STORIES_TABLE
//            + "(username, author, story_id, title, description, serialized_story, progress) "
//            + "VALUES (?, ?, ?, ?, ?, ?, ?);";
//
//    public StoryDB(Context context) {
//        this.context = context;
//        OpenHelper openHelper = new OpenHelper(this.context);
//        this.db = openHelper.getWritableDatabase();
//        this.insertStmt = this.db.compileStatement(INSERT);
//    }
//
//    /**
//     * Inserts a story into the database.
//     * If successful, returns the rowid, otherwise -1.
//     */
//    public long insert(String username, String author, String story_id, String title,
//                       String description, String serialized_story, String progress)
//            throws Exception {
//        this.insertStmt.bindString(1, username);
//        this.insertStmt.bindString(2, author);
//        this.insertStmt.bindString(3, story_id);
//        this.insertStmt.bindString(4, title);
//        this.insertStmt.bindString(2, description);
//        this.insertStmt.bindString(3, serialized_story);
//        this.insertStmt.bindString(4, progress);
//
//        long rowID = this.insertStmt.executeInsert();
//        if (rowID == -1) {
//            throw new Exception("Unable to insert");
//        }
//        return rowID;
//    }
//
//    /**
//     * Delete everything from example
//     */
//    public void deleteAll() {
//        this.db.delete(SAVED_STORIES_TABLE, null, null);
//    }
//
//
//    /**
//     * Return an array list of edu.uw.tacoma.mmuppa.cssappwithwebservices.model.Course objects from the
//     * data returned from select query on Courses table.
//     *
//     * @return
//     */
//    public ArrayList<Story> selectAll() {
//        ArrayList<Story> list = new ArrayList<Story>();
//        Cursor cursor = this.db.query(SAVED_STORIES_TABLE, new String[]
//                {"username", "author", "story_id", "title", "description", "serialized_story",
//                        "progress"}, null, null, null, null, null);
//        if (cursor.moveToFirst()) {
//            do {
//                Story e = new Story(cursor.getString(6)); // just loading from serialized story
//                list.add(e);
//            } while (cursor.moveToNext());
//        }
//        if (cursor != null && !cursor.isClosed()) {
//            cursor.close();
//        }
//        return list;
//    }

//    /**
//     * Return the name when id is passed.
//     * null if no record found.
//     *
//     * @param id
//     * @return
//     */
//    public String selectByPrimaryKey(String username, String author, String story_id) {
//        Cursor cursor = this.db.query(SAVED_STORIES_TABLE, new String[]
//                        {"shortDesc"}, "id=?",
//                new String[]
//                        {Long.toString(id)}, null, null, null);
//        if (cursor.moveToFirst()) {
//            do {
//                return cursor.getString(0);
//            } while (cursor.moveToNext());
//        }
//        if (cursor != null && !cursor.isClosed()) {
//            cursor.close();
//        }
//        return null;
//    }
//
//    /**
//    * Close the connection
//    */
//    public void close() {
//        db.close();
//    }
//
//    private static class OpenHelper extends SQLiteOpenHelper {
//
//        OpenHelper(Context context) {
//            super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        }
//
//        @Override
//        public void onCreate(SQLiteDatabase db) {
//            db.execSQL("CREATE TABLE " + SAVED_STORIES_TABLE
//                    + " (username VARCHAR(255), author VARCHAR(255), story_id VARCHAR(255),"
//                    + " title TEXT, description TEXT, elements TEXT, serialized_story TEXT,"
//                    + " progress TEXT, PRIMARY KEY(username, author, story_id))");
//        }
//
//        @Override
//        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            Log.w("Example",
//                    "Upgrading database, this will drop tables and recreate.");
//            db.execSQL("DROP TABLE IF EXISTS " + SAVED_STORIES_TABLE);
//            onCreate(db);
//        }
//    }
}
