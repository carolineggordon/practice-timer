package com.carolineggordon.android.practisetimer.test.model;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.carolineggordon.android.practisetimer.dao.DAOFactory;
import com.carolineggordon.android.practisetimer.dao.DatabaseConstants;
import com.carolineggordon.android.practisetimer.dao.StudentDAO;
import com.carolineggordon.android.practisetimer.model.Meeting;
import com.carolineggordon.android.practisetimer.model.MeetingStats;
import com.carolineggordon.android.practisetimer.model.Student;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.MediumTest;

public class StudentTest extends AndroidTestCase implements DatabaseConstants {
    private StudentDAO dao = null;
    private DAOFactory daoFactory = DAOFactory.getInstance();

    @Override
    protected void setUp() {
        daoFactory.setGlobalContext(new RenamingDelegatingContext(mContext, "test_"));
        daoFactory.setCacheDAOInstances(true);
        dao = daoFactory.getStudentDAO(mContext);
    }

    @Override
    protected void tearDown() {
        dao.deleteAll();
        dao.close();
    }

    @MediumTest
    public void test_create_a_student() {
        Student student = Student.create("Test Student", mContext);
        assertNotNull(student.getId());
    }

    @MediumTest
    public void test_delete_a_student() {
        Student student = Student.create("Test Student", mContext);
        assertEquals(1, Student.findAllStudentNames(mContext).size());
        student.delete(mContext);
        assertEquals(0, Student.findAllStudentNames(mContext).size());        
    }

    @MediumTest
    public void test_deleting_a_student_deletes_its_meetings_as_well() {
        Student student = Student.create("Test Student", mContext);
        new Meeting(student, new GregorianCalendar(2010, 1, 5, 10, 15, 0).getTime(), 5, 240, 300, 30, 120).save(mContext);
        new Meeting(student, new GregorianCalendar(2010, 1, 4, 10, 15, 0).getTime(), 5, 240, 300, 30, 120).save(mContext);;

        assertFalse(Meeting.findAllByStudent(student, mContext).isEmpty());
        student.delete(mContext);
        assertEquals(0, Student.findAllStudentNames(mContext).size());
        assertTrue(Meeting.findAllByStudent(student, mContext).isEmpty());
    }

    @MediumTest
    public void test_find_a_student_by_name() {
        Student.create("Test Student", mContext);
        assertNotNull(Student.findByName("Test Student", mContext));
    }
    
    @MediumTest
    public void test_find_all_student_names() {
        Student.create("Test Student 1", mContext);
        Student.create("Test Student 2", mContext);
        Student.create("Test Student 3", mContext);
        List<String> studentNames = Student.findAllStudentNames(mContext);

        assertEquals(3, studentNames.size());
        assertTrue(studentNames.contains("Test Student 1"));
        assertTrue(studentNames.contains("Test Student 2"));
        assertTrue(studentNames.contains("Test Student 3"));
    }

    @MediumTest
    public void test_get_average_meeting_stats() {
        Student student = Student.create("Test Student", mContext);
        Date dateTime = new GregorianCalendar(2010, 1, 5, 10, 15, 0).getTime();
        new Meeting(student, dateTime, 5, 301, 343, 30, 65).save(mContext);
        new Meeting(student, dateTime, 8, 534, 550, 32, 120).save(mContext);
        new Meeting(student, dateTime, 2, 234, 300, 23, 122).save(mContext);
        new Meeting(student, dateTime, 3, 765, 765, 15, 78).save(mContext);
        new Meeting(student, dateTime, 9, 444, 445, 10, 93).save(mContext);

        MeetingStats averageStats = student.getAverageMeetingStats(mContext);
        assertEquals(5.4f, averageStats.getNumParticipants());
        assertEquals(455.6f, averageStats.getIndividualStatusLength());
        assertEquals(480.6f, averageStats.getMeetingLength());
        assertEquals(22f, averageStats.getQuickestStatus());
        assertEquals(95.6f, averageStats.getLongestStatus());
    }

    @MediumTest
    public void test_has_meetings() {
        Student student = Student.create("Test Student No Meetings", mContext);
        assertFalse(student.hasMeetings(mContext));
        new Meeting(student, new GregorianCalendar(2010, 1, 5, 10, 15, 0).getTime(), 5, 301, 343, 30, 65).save(mContext);
        assertTrue(student.hasMeetings(mContext));
    }
}
