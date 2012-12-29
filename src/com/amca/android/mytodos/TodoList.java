package com.amca.android.mytodos;

import java.util.ArrayList;

import com.amca.android.mytodos.customwindow.ListActivityWindow;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TodoList extends ListActivityWindow {
	private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int EDIT_ID = Menu.FIRST + 1;
    private static final int DELETE_ID = Menu.FIRST + 2;
    
    private CheckBox mTaskStatus;
    private TodoTable mDbHelper;
    private Cursor mNotesCursor;
    private Integer todoParentId = 0;
    MyAdapter mListAdapter;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_todos);
	    this.getListView().setDividerHeight(2);
	    registerForContextMenu(getListView());
	    
	    mDbHelper = new TodoTable(this);
        mDbHelper.open();
        
        checkExtras();
        fillData();
	}

	    
	private void checkExtras(){
		Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	Integer _todoParentId = extras.getInt(TodoTable.COLUMN_PARENT);
        	String _todoParentTask = extras.getString(TodoTable.COLUMN_TASK);
            if (_todoParentId != null && _todoParentTask != null) {
            	todoParentId = _todoParentId;
            	setTitle(_todoParentTask);
            	//Toast.makeText(TodoList.this, _todoParentId.toString(), Toast.LENGTH_SHORT).show();
            }
        }
	}
	
	private void fillData() {
		// Get all of the rows from the database and create the item list
        mNotesCursor = mDbHelper.fetchAllNotes(todoParentId);
        startManagingCursor(mNotesCursor);
        //mListAdapter = new MyAdapter(TodoList.this, mNotesCursor);
        //setListAdapter(mListAdapter);
        
        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{TodoTable.COLUMN_TASK};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.taskName};
        /*
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter todos = new SimpleCursorAdapter(this, R.layout.todo_row, mNotesCursor, from, to);
        setListAdapter(todos);
        //*
        this.mainListView = getListView();
        mainListView.setAdapter(todos);
      //*/
        SimpleCursorAdapter todos = new MyDataAdapter(this, R.layout.todo_row, mNotesCursor, from, to);
        setListAdapter(todos);
	}
	
	private class MyAdapter extends ResourceCursorAdapter {

        public MyAdapter(Context context, Cursor cur) {
            super(context, R.layout.todo_row, cur);
        }

        @Override
        public View newView(Context context, Cursor cur, ViewGroup parent) {
            LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return li.inflate(R.layout.todo_row, parent, false);
        }

		@Override
        public void bindView(View view, Context context, final Cursor cur) {
            CheckBox cbListCheck = (CheckBox)view.findViewById(R.id.taskStatus);
            TextView tvListText = (TextView)view.findViewById(R.id.taskName);
            
            tvListText.setText(cur.getString(cur.getColumnIndex(TodoTable.COLUMN_TASK)));
            cbListCheck.setOnCheckedChangeListener(null);
            cbListCheck.setChecked((cur.getInt(cur.getColumnIndex(TodoTable.COLUMN_STATUS))==0? false:true));
            cbListCheck.setOnCheckedChangeListener(new OnCheckedChangeListener(){
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                	String msg= "unchecked";;
                    if(isChecked) {
                    	msg = "checked";  
                    }
                    Toast.makeText(TodoList.this, msg + " " + cur.getInt(cur.getColumnIndexOrThrow(TodoTable.COLUMN_ID)), Toast.LENGTH_SHORT).show();
                }               
            });
        }
    }
	
	public class MyDataAdapter extends SimpleCursorAdapter {
		private Context context;
		private ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();

		// itemChecked will store the position of the checked items.

		@SuppressWarnings("deprecation")
		public MyDataAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
		    super(context, layout, c, from, to);
		    this.context = context;
		    
		    for (int i = 0; i < this.getCount(); i++) {
		        itemChecked.add(i, true); // initializes all items value with false
		    }
		}
		
		public View getView(final int pos, View inView, ViewGroup parent) {
		    if (inView == null) {
		        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        inView = inflater.inflate(R.layout.todo_row, null);
		    }

		    final Cursor c = mNotesCursor;
            c.moveToPosition(pos);
		    TextView tvListText = (TextView)inView.findViewById(R.id.taskName);
            CheckBox cbListCheck = (CheckBox)inView.findViewById(R.id.taskStatus);

            tvListText.setText(c.getString(c.getColumnIndex(TodoTable.COLUMN_TASK)));
            cbListCheck.setChecked((c.getInt(c.getColumnIndex(TodoTable.COLUMN_STATUS))==0? false:true));
            
		    final CheckBox cBox = (CheckBox) inView.findViewById(R.id.taskStatus); // your CheckBox
		    cBox.setOnClickListener(new OnClickListener() {
		        public void onClick(View v) {
		        	final Cursor cur = mNotesCursor;
		            cur.moveToPosition(pos);
		            CheckBox cb = (CheckBox) v.findViewById(R.id.taskStatus);
		            if (cb.isChecked()) {
		                itemChecked.set(pos, true);
		                Toast.makeText(TodoList.this, cur.getString(cur.getColumnIndexOrThrow(TodoTable.COLUMN_TASK)) + " done", Toast.LENGTH_SHORT).show();
		                mDbHelper.toggleNoteStatus(cur.getInt(cur.getColumnIndexOrThrow(TodoTable.COLUMN_ID)), 1);
		                // do some operations here
		            } else if (!cb.isChecked()) {
		                itemChecked.set(pos, false);
		                mDbHelper.toggleNoteStatus(cur.getInt(cur.getColumnIndexOrThrow(TodoTable.COLUMN_ID)), 0);
		                // do some operations here
		            }
		        }
		    });
		    //cBox.setChecked(itemChecked.get(pos)); // this will Check or Uncheck the
		    // CheckBox in ListView
		    // according to their original
		    // position and CheckBox never
		    // loss his State when you
		    // Scroll the List Items.
		    return inView;
		}}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID:
                createNote(todoParentId);
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, EDIT_ID, 0, R.string.menu_edit);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
        	case EDIT_ID :
        		return true;
            case DELETE_ID:
                mDbHelper.deleteNote(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createNote(int _parentTodo) {
        Intent i = new Intent(this, TodoForm.class);
        i.putExtra("activityMode", ACTIVITY_CREATE);
        i.putExtra(TodoTable.COLUMN_PARENT, _parentTodo);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor c = mNotesCursor;
        c.moveToPosition(position);

        Intent i = new Intent(this, TodoList.class);
        i.putExtra(TodoTable.COLUMN_TASK, c.getString(c.getColumnIndexOrThrow(TodoTable.COLUMN_TASK)));
        i.putExtra(TodoTable.COLUMN_PARENT, c.getInt(c.getColumnIndexOrThrow(TodoTable.COLUMN_ID)));
        startActivity(i);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Bundle extras = intent.getExtras();
        Integer parent = extras.getInt(TodoTable.COLUMN_PARENT);
        String task = extras.getString(TodoTable.COLUMN_TASK);
        Integer status = extras.getInt(TodoTable.COLUMN_STATUS);
        String deadline = extras.getString(TodoTable.COLUMN_DEADLINE);
        String timeCreated = extras.getString(TodoTable.COLUMN_TIME_CREATED);
        switch(requestCode) {
            case ACTIVITY_CREATE:
                mDbHelper.createNote(parent, task, status, deadline, timeCreated);
                break;
            case ACTIVITY_EDIT:
                Long rowId = extras.getLong(TodoTable.COLUMN_ID);
                if (rowId != null) {
                    mDbHelper.updateNote(rowId, parent, task, status, deadline, timeCreated);
                }
                break;
        }
        fillData();
    }
}
