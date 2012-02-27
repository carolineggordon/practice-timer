package com.carolineggordon.android.practisetimer.model;

import java.util.Date;
import java.util.List;

import com.carolineggordon.android.practisetimer.dao.DAOFactory;
import com.carolineggordon.android.practisetimer.dao.MeetingDAO;
import com.carolineggordon.android.practisetimer.utils.Logger;

import android.content.Context;
import android.text.format.DateFormat;

public class Meeting {
    public static String DESCRIPTION_FORMAT = "MM/dd/yyyy h:mm:ssaa";

    private Long id = null;
    private Student student = null;
    private Date dateTime = null;
    private MeetingStats meetingStats = null;

    private static DAOFactory daoFactory = DAOFactory.getInstance();

    public Meeting(Student student, Date dateTime, int numParticipants, int individualStatusLength,
            int meetingLength, int quickestStatus, int longestStatus) {

        if (student == null) {
            throw new IllegalArgumentException("Meeting student must not be null");
        } else {
            this.student = new Student(student.getName());
        }

        if (dateTime == null) {
            throw new IllegalArgumentException("Meeting date/time must not be null");
        } else {
            this.dateTime = dateTime;
        }

        if (numParticipants < 1) {
            throw new IllegalArgumentException("Meeting must have at least 1 participant");
        }

        meetingStats = new MeetingStats(numParticipants, individualStatusLength, meetingLength, quickestStatus, longestStatus);
    }

    public Meeting(Long id, Student student, Date dateTime, int numParticipants, int individualStatusLength,
            int meetingLength, int quickestStatus, int longestStatus) {
        this(student, dateTime, numParticipants, individualStatusLength, meetingLength, quickestStatus, longestStatus);
        this.id = id;
    }

    public Meeting(Long id, Meeting meeting) {
        this.id = id;
        this.student = new Student(meeting.getStudent().getName());
        this.dateTime = meeting.dateTime;
        this.meetingStats = meeting.meetingStats;
    }

    public void delete(Context context) {
        MeetingDAO dao = null;
        try {
            dao = daoFactory.getMeetingDAO(context);
            dao.delete(this);
        } finally {
            if (dao != null) {
                dao.close();
            }
        }
    }

    public static void deleteAllByStudent(Student student, Context context) {
        MeetingDAO dao = null;
        try {
            dao = daoFactory.getMeetingDAO(context);
            dao.deleteAllByStudent(student);
        } finally {
            if (dao != null) {
                dao.close();
            }
        }
    }

    public Meeting save(Context context) {
        MeetingDAO dao = null;
        Meeting meeting = null;
        try {
            dao = daoFactory.getMeetingDAO(context);
            meeting = dao.save(this);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        } finally {
            dao.close();
        }

        return meeting;
    }

    public static List<Meeting> findAllByStudent(Student student, Context context) {
        MeetingDAO dao = null;
        try {
            dao = daoFactory.getMeetingDAO(context);
            return dao.findAllByStudent(student);
        } finally {
            if (dao != null) {
                dao.close();
            }
        }
    }

    public static Meeting findByStudentAndDate(Student student, Date date, Context context) {
        MeetingDAO dao = null;
        try {
            dao = daoFactory.getMeetingDAO(context);
            return dao.findByStudentAndDate(student, date);
        } finally {
            if (dao != null) {
                dao.close();
            }
        }
    }

    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public MeetingStats getMeetingStats() {
        return meetingStats;
    }

    public String getDescription() {
        return DateFormat.format(DESCRIPTION_FORMAT, dateTime).toString();
    }
}
