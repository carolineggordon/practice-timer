package com.carolineggordon.android.practisetimer.dao;

import static android.provider.BaseColumns._ID;

import java.util.ArrayList;
import java.util.List;

import com.carolineggordon.android.practisetimer.model.Student;
import com.carolineggordon.android.practisetimer.utils.Logger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class StudentDAO extends DAOHelper {

    public StudentDAO(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Student save(Student student) {
        SQLiteDatabase db = getWritableDatabase();
        if (student.getId() != null) {
            return updateExistingStudent(db, student);
        } else {
            return createNewStudent(db, student);
        }
    }

    public Student findById(Long id) {
        Cursor cursor = null;
        Student student = null;

        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.query(STUDENTS_TABLE_NAME, STUDENTS_ALL_COLUMS, _ID + " = ?", new String[]{id.toString()}, null, null, null);
            if (cursor.getCount() == 1) {
                if (cursor.moveToFirst()) {
                    String name = cursor.getString(1);
                    student = new Student(id, name);
                }
            }
        } finally {
            closeCursor(cursor);
        }

        return student;
    }

    public Student findByName(String name) {
        Cursor cursor = null;
        Student student = null;
        name = name.trim();

        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.query(STUDENTS_TABLE_NAME, STUDENTS_ALL_COLUMS, STUDENTS_NAME + " = ?", new String[]{name}, null, null, null);
            if (cursor.getCount() == 1) {
                if (cursor.moveToFirst()) {
                    long id = cursor.getLong(0);
                    name = cursor.getString(1);
                    student = new Student(id, name);
                }
            }
        } finally {
            closeCursor(cursor);
        }

        Logger.d((student == null ? "Unsuccessfully" : "Successfully") + " found student with a name of '" + name + "'");
        return student;
    }

    public List<String> findAllStudentNames() {
        List<String> studentNames = new ArrayList<String>();
        Cursor cursor = null;

        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.query(STUDENTS_TABLE_NAME, new String[]{STUDENTS_NAME}, null, null, null, null, STUDENTS_NAME);
            while (cursor.moveToNext()) {
                studentNames.add(cursor.getString(0));
            }
        } finally {
            closeCursor(cursor);
        }

        Logger.d("Found " + studentNames.size() + " students");
        return studentNames;
    }

    public void deleteAll() {
        Logger.d("Deleting all students");
        SQLiteDatabase db = getWritableDatabase();
        db.delete(STUDENTS_TABLE_NAME, null, null);
    }

    public void delete(Student student) {
        Logger.d("Deleting student with the name of '" + student.getName() + "'");
        if (student.getId() != null) {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(STUDENTS_TABLE_NAME, _ID + " = ?", new String[]{student.getId().toString()});
        }
    }

    private boolean attemptingToCreateDuplicateStudent(Student student) {
        return student.getId() == null && findByName(student.getName()) != null;
    }

    private Student createNewStudent(SQLiteDatabase db, Student student) {
        if (student.getName() == null || student.getName().trim().length() == 0) {
            String msg = "Attempting to create a student with an empty name";
            Logger.w(msg);
            throw new InvalidStudentNameException(msg);
        }

        if (attemptingToCreateDuplicateStudent(student)) {
            String msg = "Attempting to create duplicate student with the name " + student.getName();
            Logger.w(msg);
            throw new DuplicateStudentException(msg);
        }

        Logger.d("Creating new student with a name of '" + student.getName() + "'");
        ContentValues values = new ContentValues();
        values.put(STUDENTS_NAME, student.getName());
        long id = db.insertOrThrow(STUDENTS_TABLE_NAME, null, values);
        return new Student(id, student.getName());
    }

    private Student updateExistingStudent(SQLiteDatabase db, Student student) {
        Logger.d("Updating student with the name of '" + student.getName() + "'");
        ContentValues values = new ContentValues();
        values.put(STUDENTS_NAME, student.getName());
        long id = db.update(STUDENTS_TABLE_NAME, values, _ID + " = ?", new String[]{student.getId().toString()});
        return new Student(id, student.getName());
    }
}
