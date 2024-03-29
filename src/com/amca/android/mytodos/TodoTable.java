package com.amca.android.mytodos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TodoTable {
	// Database table
	private static final String DATABASE_NAME = "mytodos";
	private static final String DATABASE_TABLE = "todo";
	private static final int DATABASE_VERSION = 1;
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_PARENT = "parent";
	public static final String COLUMN_TASK = "task";
	public static final String COLUMN_STATUS = "status";
	public static final String COLUMN_DEADLINE = "deadline";
	public static final String COLUMN_TIME_CREATED = "timeCreated";

	private static final String TAG = "NotesDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table " 
	    + DATABASE_TABLE
	    + "(" 
	    + COLUMN_ID + " integer primary key autoincrement, " 
	    + COLUMN_PARENT + " integer not null, " 
	    + COLUMN_TASK + " text not null," 
	    + COLUMN_STATUS + " integer not null,"
	    + COLUMN_DEADLINE + " datetime not null,"
	    + COLUMN_TIME_CREATED + " datetime not null"
	    + ");";
	private final Context mCtx;
	  
	private static class DatabaseHelper extends SQLiteOpenHelper {
	    DatabaseHelper(Context context) {
	          super(context, DATABASE_NAME, null, DATABASE_VERSION);
	      }
        @Override
        public void onCreate(SQLiteDatabase db) {
        	db.execSQL(DATABASE_CREATE);
        }

        @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        	Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
        			+ newVersion + ", which will destroy all old data");
        	db.execSQL("DROP TABLE IF EXISTS notes");
	        onCreate(db);
        	}
	    }

	    /**
	     * Constructor - takes the context to allow the database to be
	     * opened/created
	     * 
	     * @param ctx the Context within which to work
	     */
	    public TodoTable(Context ctx) {
	        this.mCtx = ctx;
	    }

	    /**
	     * Open the notes database. If it cannot be opened, try to create a new
	     * instance of the database. If it cannot be created, throw an exception to
	     * signal the failure
	     * 
	     * @return this (self reference, allowing this to be chained in an
	     *         initialization call)
	     * @throws SQLException if the database could be neither opened or created
	     */
	    public TodoTable open() throws SQLException {
	        mDbHelper = new DatabaseHelper(mCtx);
	        mDb = mDbHelper.getWritableDatabase();
	        return this;
	    }

	    public void close() {
	        mDbHelper.close();
	    }


	    /**
	     * Create a new note using the title and body provided. If the note is
	     * successfully created return the new rowId for that note, otherwise return
	     * a -1 to indicate failure.
	     * 
	     * @param title the title of the note
	     * @param body the body of the note
	     * @return rowId or -1 if failed
	     */
	    public long createNote(Integer parent, String task, Integer status, String deadline, String timeCreated) {
	        ContentValues initialValues = new ContentValues();
	        initialValues.put(COLUMN_PARENT, parent);
	        initialValues.put(COLUMN_TASK, task);
	        initialValues.put(COLUMN_STATUS, status);
	        initialValues.put(COLUMN_DEADLINE, deadline);
	        initialValues.put(COLUMN_TIME_CREATED, timeCreated);

	        return mDb.insert(DATABASE_TABLE, null, initialValues);
	    }

	    /**
	     * Delete the note with the given rowId
	     * 
	     * @param rowId id of note to delete
	     * @return true if deleted, false otherwise
	     */
	    public boolean deleteNote(long rowId) {

	        return mDb.delete(DATABASE_TABLE, COLUMN_ID + "=" + rowId, null) > 0;
	    }

	    /**
	     * Return a Cursor over the list of all notes in the database
	     * 
	     * @return Cursor over all notes
	     */
	    public Cursor fetchAllNotes(int parent) {
	        return mDb.query(DATABASE_TABLE, new String[] {COLUMN_ID, COLUMN_PARENT,
	        		COLUMN_TASK, COLUMN_STATUS, COLUMN_DEADLINE, COLUMN_TIME_CREATED}, COLUMN_PARENT + "=" + parent, null,
	        		null, null, null, null);
	    }

	    /**
	     * Return a Cursor positioned at the note that matches the given rowId
	     * 
	     * @param rowId id of note to retrieve
	     * @return Cursor positioned to matching note, if found
	     * @throws SQLException if note could not be found/retrieved
	     */
	    public Cursor fetchNote(long rowId) throws SQLException {
	        Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {COLUMN_ID, COLUMN_PARENT,
		        		COLUMN_TASK, COLUMN_STATUS, COLUMN_DEADLINE, COLUMN_TIME_CREATED}, COLUMN_ID + "=" + rowId, null,
	                    null, null, null, null);
	        if (mCursor != null) {
	            mCursor.moveToFirst();
	        }
	        return mCursor;

	    }

	    /**
	     * Update the note using the details provided. The note to be updated is
	     * specified using the rowId, and it is altered to use the title and body
	     * values passed in
	     * 
	     * @param rowId id of note to update
	     * @param title value to set note title to
	     * @param body value to set note body to
	     * @return true if the note was successfully updated, false otherwise
	     */
	    public boolean updateNote(long rowId, Integer parent, String task, Integer status, String deadline, String timeCreated) {
	        ContentValues args = new ContentValues();
	        args.put(COLUMN_PARENT, parent);
	        args.put(COLUMN_TASK, task);
	        args.put(COLUMN_STATUS, status);
	        args.put(COLUMN_DEADLINE, deadline);
	        args.put(COLUMN_TIME_CREATED, timeCreated);

	        return mDb.update(DATABASE_TABLE, args, COLUMN_ID + "=" + rowId, null) > 0;
	    }
	    
	    public boolean toggleNoteStatus(long rowId, Integer status) {
	        ContentValues args = new ContentValues();
	        args.put(COLUMN_STATUS, status);
	        return mDb.update(DATABASE_TABLE, args, COLUMN_ID + "=" + rowId, null) > 0;
	    }
} 