/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amca.android.mytodos;

import java.util.Calendar;
import java.util.Date;
import com.amca.android.mytodos.customwindow.ActivityWindow;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class TodoForm extends ActivityWindow {
	private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int DATE_DIALOG_ID = 999;
    
    private EditText mTaskText;
    private EditText mDeadlineText;
    private DatePicker mDeadline;
    private Button btnDeadline;
    private Long mRowId;
    private Integer todoParent = 0;
	private int year;
	private int month;
	private int day;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_form);
        setTitle(R.string.menu_edit);

        mTaskText = (EditText) findViewById(R.id.form_task);
        mDeadlineText = (EditText) findViewById(R.id.text_form_deadline);
        mDeadline = (DatePicker) findViewById(R.id.form_deadline);
        Button confirmButton = (Button) findViewById(R.id.form_confirm);

        mRowId = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	int activityMode = extras.getInt("activityMode");
        	todoParent = extras.getInt(TodoTable.COLUMN_PARENT);
        	switch(activityMode){
        		case ACTIVITY_EDIT :
                    String task = extras.getString(TodoTable.COLUMN_TASK);
                    Integer status = extras.getInt(TodoTable.COLUMN_STATUS);
                    String deadline = extras.getString(TodoTable.COLUMN_DEADLINE);
                    String timeCreated = extras.getString(TodoTable.COLUMN_TIME_CREATED);
                    mRowId = extras.getLong(TodoTable.COLUMN_ID);
                    
                    if (task != null) {
                        mTaskText.setText(task);
                    }
                    if (deadline != null) {
                        mDeadline.setTag(deadline);
                    }
        			break;
        	}
        	//Toast.makeText(TodoForm.this, todoParent.toString(), Toast.LENGTH_SHORT).show();
        }
        addListenerOnButton();
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Date now = new Date();
                Bundle bundle = new Bundle();
                bundle.putInt(TodoTable.COLUMN_PARENT, todoParent);
                bundle.putString(TodoTable.COLUMN_TASK, mTaskText.getText().toString());
                bundle.putString(TodoTable.COLUMN_DEADLINE, mDeadlineText.getText().toString());
                bundle.putInt(TodoTable.COLUMN_STATUS, 0);
                bundle.putString(TodoTable.COLUMN_TIME_CREATED, now.toString());
                if (mRowId != null) {
                    bundle.putLong(TodoTable.COLUMN_ID, mRowId);
                }

                Intent mIntent = new Intent();
                mIntent.putExtras(bundle);
                setResult(RESULT_OK, mIntent);
                finish();
            }

        });
    }
    
    public void addListenerOnButton() {    	 
		btnDeadline = (Button) findViewById(R.id.button_form_deadline);
		btnDeadline.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
	}
    
    @Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
		   // set date picker as current date
			final Calendar c = Calendar.getInstance();
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
		   return new DatePickerDialog(this, datePickerListener, year, month, day);
		}
		return null;
	}
 
	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
 
		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;
 
			// set selected date into textview
			mDeadlineText.setText(new StringBuilder().append(month + 1)
			   .append("-").append(day).append("-").append(year)
			   .append(" "));
 
		}
	};
}
