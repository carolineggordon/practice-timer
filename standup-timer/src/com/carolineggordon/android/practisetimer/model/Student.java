package com.carolineggordon.android.practisetimer.model;

import java.util.ArrayList;
import java.util.List;

import com.carolineggordon.android.practisetimer.dao.DAOFactory;
import com.carolineggordon.android.practisetimer.dao.StudentDAO;
import com.carolineggordon.android.practisetimer.utils.Logger;

import android.content.Context;

public class Student {
    private Long id = null;
    private String name = null;
    private static DAOFactory daoFactory = DAOFactory.getInstance();

    public Student(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Student name must not be null");
        }
        this.name = name.trim();
    }

    public Student(Long id, String name) {
        this(name);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void delete(Context context) {
        StudentDAO dao = null;
        try {
            Meeting.deleteAllByStudent(this, context);
            dao = daoFactory.getStudentDAO(context);
            dao.delete(this);
        } finally {
            if (dao != null) {
                dao.close();
            }
        }
    }

    public static Student create(String name, Context context) {
        StudentDAO dao = null;
        Student student = null;
        try {
            dao = daoFactory.getStudentDAO(context);
            student = dao.save(new Student(name));
        } catch (Exception e) {
            Logger.e(e.getMessage());
        } finally {
            dao.close();
        }

        return student;
    }

    public int getNumberOfMeetings(Context context) {
        return findAllMeetings(context).size();
    }

    public MeetingStats getAverageMeetingStats(Context context) {
        List<MeetingStats> meetingStats = new ArrayList<MeetingStats>();
        List<Meeting> meetings = findAllMeetings(context);
        for (Meeting meeting : meetings) {
            meetingStats.add(meeting.getMeetingStats());
        }
        return MeetingStats.getAverageStats(meetingStats);
    }

    public List<Meeting> findAllMeetings(Context context) {
        return Meeting.findAllByStudent(this, context);
    }

    public boolean hasMeetings(Context context) {
        return findAllMeetings(context).size() > 0;
    }

    public static Student findByName(String studentName, Context context) {
        StudentDAO dao = null;
        try {
            dao = daoFactory.getStudentDAO(context);
            return dao.findByName(studentName);
        } finally {
            if (dao != null) {
                dao.close();
            }
        }
    }

    public static List<String> findAllStudentNames(Context context) {
        StudentDAO dao = null;
        try {
            dao = daoFactory.getStudentDAO(context);
            return dao.findAllStudentNames();
        } finally {
            if (dao != null) {
                dao.close();
            }
        }
    }
}
