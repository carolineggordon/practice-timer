package com.carolineggordon.android.practisetimer.dao;

import static android.provider.BaseColumns._ID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.carolineggordon.android.practisetimer.model.Meeting;
import com.carolineggordon.android.practisetimer.model.Student;
import com.carolineggordon.android.practisetimer.utils.Logger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MeetingDAO extends DAOHelper {

    public MeetingDAO(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Meeting save(Meeting meeting) {
        if (meeting.getId() == null) {
            SQLiteDatabase db = getWritableDatabase();
            return createNewMeeting(db, meeting);
        } else {
            String msg = "Attempting to update an existing meeting.  Meeting entries cannot be updated.";
            Logger.w(msg);
            throw new CannotUpdateMeetingException(msg);
        }
    }

    public Meeting findById(Long id) {
        Cursor cursor = null;
        Meeting meeting = null;

        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.query(MEETINGS_TABLE_NAME, MEETINGS_ALL_COLUMS, _ID + " = ?", new String[]{id.toString()}, null, null, null);
            if (cursor.getCount() == 1) {
                if (cursor.moveToFirst()) {
                    meeting = createMeetingFromCursorData(cursor);
                }
            }
        } finally {
            closeCursor(cursor);
        }

        return meeting;
    }

    public List<Meeting> findAllByStudent(Student student) {
        List<Meeting> meetings = new ArrayList<Meeting>();
        Cursor cursor = null;

        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.query(MEETINGS_TABLE_NAME, MEETINGS_ALL_COLUMS, MEETINGS_STUDENT_NAME + " = ?", new String[]{student.getName()}, null, null, MEETINGS_MEETING_TIME);
            while (cursor.moveToNext()) {
                meetings.add(createMeetingFromCursorData(cursor));
            }
        } finally {
            closeCursor(cursor);
        }

        Logger.d("Found " + meetings.size() + " meetings");
        Collections.reverse(meetings);
        return meetings;
    }

    public Meeting findByStudentAndDate(Student student, Date date) {
        Cursor cursor = null;
        Meeting meeting = null;

        try {
            long startTime = date.getTime();
            Date endDate = new Date(date.getTime());
            endDate.setSeconds(endDate.getSeconds() + 1);
            long endTime = endDate.getTime();

            SQLiteDatabase db = getReadableDatabase();
            cursor = db.query(MEETINGS_TABLE_NAME, MEETINGS_ALL_COLUMS,
                    MEETINGS_STUDENT_NAME + " = ? and " + MEETINGS_MEETING_TIME + " >= ? and " + MEETINGS_MEETING_TIME + " < ?",
                    new String[] {student.getName(), Long.toString(startTime), Long.toString(endTime)},
                    null, null, null);
            if (cursor.getCount() == 1) {
                if (cursor.moveToFirst()) {
                    meeting = createMeetingFromCursorData(cursor);
                }
            }
        } finally {
            closeCursor(cursor);
        }

        return meeting;
    }

    public void deleteAll() {
        Logger.d("Deleting all meetings");
        SQLiteDatabase db = getWritableDatabase();
        db.delete(MEETINGS_TABLE_NAME, null, null);
    }

    public void deleteAllByStudent(Student student) {
        Logger.d("Deleting all meetings for student " + student.getName());
        SQLiteDatabase db = getWritableDatabase();
        db.delete(MEETINGS_TABLE_NAME, MEETINGS_STUDENT_NAME + " = ?", new String[]{student.getName()});
    }

    public void delete(Meeting meeting) {
        Logger.d("Deleting meeting for " + meeting.getStudent().getName() + " with a date/time of '" + meeting.getDateTime() + "'");
        if (meeting.getId() != null) {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(MEETINGS_TABLE_NAME, _ID + " = ?", new String[]{meeting.getId().toString()});
        }
    }

    private Meeting createNewMeeting(SQLiteDatabase db, Meeting meeting) {
        Logger.d("Creating new meeting for " + meeting.getStudent().getName() + " with a date/time of '" + meeting.getDateTime() + "'");
        ContentValues values = createContentValues(meeting);
        long id = db.insertOrThrow(MEETINGS_TABLE_NAME, null, values);
        return new Meeting(id, meeting);
    }

    private ContentValues createContentValues(Meeting meeting) {
        ContentValues values = new ContentValues();
        values.put(MEETINGS_STUDENT_NAME, meeting.getStudent().getName());
        values.put(MEETINGS_MEETING_TIME, meeting.getDateTime().getTime());
        values.put(MEETINGS_NUM_PARTICIPANTS, meeting.getMeetingStats().getNumParticipants());
        values.put(MEETINGS_INDIVIDUAL_STATUS_LENGTH, meeting.getMeetingStats().getIndividualStatusLength());
        values.put(MEETINGS_MEETING_LENGTH, meeting.getMeetingStats().getMeetingLength());
        values.put(MEETINGS_QUICKEST_STATUS, meeting.getMeetingStats().getQuickestStatus());
        values.put(MEETINGS_LONGEST_STATUS, meeting.getMeetingStats().getLongestStatus());
        return values;
    }

    private Meeting createMeetingFromCursorData(Cursor cursor) {
        long id = cursor.getLong(0);
        String studentName = cursor.getString(1);
        Date meetingTime = new Date(cursor.getLong(2));
        int numParticipants = cursor.getInt(3);
        int individualStatusLength = cursor.getInt(4);
        int meetingLength = cursor.getInt(5);
        int quickestStatus = cursor.getInt(6);
        int longestStatus = cursor.getInt(7);
        return new Meeting(id, new Student(studentName), meetingTime, numParticipants, 
                individualStatusLength, meetingLength, quickestStatus, longestStatus);
    }
}
