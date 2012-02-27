package com.carolineggordon.android.practisetimer;

import com.carolineggordon.android.practisetimer.model.Student;
import com.carolineggordon.android.practisetimer.utils.Logger;

import com.carolineggordon.android.practisetimer.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class StudentList extends ListActivity {
    private static final int CREATE_STUDENT_DIALOG = 1;
    private static final int CONFIRM_DELETE_DIALOG = 2;

    private View textEntryView = null;
    private Dialog createStudentDialog = null;
    private Dialog confirmDeleteStudentDialog = null;
    private Integer positionOfStudentToDelete = null;
    private ArrayAdapter<String> studentListAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.students);
        registerForContextMenu(getListView());
    }

    @Override
    protected void onResume() {
        super.onResume();
        studentListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Student.findAllStudentNames(this));
        setListAdapter(studentListAdapter);
        getListView().setTextFilterEnabled(true);
        getTextEntryView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.students_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.add_student:
            Logger.d("Displaying the add student dialog box");
            displayAddStudentDialog();
            return true;
        default:
            Logger.e("Unknown menu item selected");
            return false;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.students_context_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
        case R.id.delete_student:
            positionOfStudentToDelete = info.position;
            showDialog(CONFIRM_DELETE_DIALOG);
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        String studentName = studentListAdapter.getItem(position);

        Intent intent = new Intent(this, StudentDetails.class);
        intent.putExtra("studentName", studentName);
        startActivity(intent);
    }

    private void deleteStudent(String studentName) {
        Student student = Student.findByName(studentName, this);
        student.delete(this);
    }

    protected void displayAddStudentDialog() {
        showDialog(CREATE_STUDENT_DIALOG);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case CREATE_STUDENT_DIALOG:
            if (createStudentDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.add_student);
                builder.setView(getTextEntryView());
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.ok, addStudentButtonListener());
                builder.setNegativeButton(R.string.revert, cancelListener());
                createStudentDialog = builder.create();
            }
            return createStudentDialog;

        case CONFIRM_DELETE_DIALOG:
            if (confirmDeleteStudentDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to delete this student?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", deleteStudentConfirmationListener());
                builder.setNegativeButton("No", cancelListener());
                confirmDeleteStudentDialog = builder.create();
            }
            return confirmDeleteStudentDialog;

        default:
            Logger.e("Attempting to create an unkonwn dialog with an id of " + id);
            return null;
        }
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        EditText collectedTextView = (EditText) getTextEntryView().findViewById(R.id.collected_text);
        collectedTextView.setText("");
    }

    synchronized protected View getTextEntryView() {
        if (textEntryView == null) {
            LayoutInflater factory = LayoutInflater.from(this);
            textEntryView = factory.inflate(R.layout.collect_text, null);
        }
        return textEntryView;
    }

    protected DialogInterface.OnClickListener addStudentButtonListener() {
        return new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText collectedTextView = (EditText) getTextEntryView().findViewById(R.id.collected_text);
                String name = collectedTextView.getText().toString();
                Student.create(name, StudentList.this);
                studentListAdapter.add(name);
            }
        };
    }

    protected DialogInterface.OnClickListener deleteStudentConfirmationListener() {
        return new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String studentName = studentListAdapter.getItem(positionOfStudentToDelete);
                deleteStudent(studentName);
                studentListAdapter.remove(studentName);
            }
        };
    }

    protected DialogInterface.OnClickListener cancelListener() {
        return new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        };
    }

    public AlertDialog getCreateStudentDialog() {
        return (AlertDialog) createStudentDialog;
    }

    public AlertDialog getConfirmDeleteStudentDialog() {
        return (AlertDialog) confirmDeleteStudentDialog;
    }
}
