package net.johnpwood.android.standuptimer.test.model;

import java.util.GregorianCalendar;
import java.util.List;

import net.johnpwood.android.standuptimer.dao.DAOFactory;
import net.johnpwood.android.standuptimer.dao.DatabaseConstants;
import net.johnpwood.android.standuptimer.dao.MeetingDAO;
import net.johnpwood.android.standuptimer.model.Meeting;
import net.johnpwood.android.standuptimer.model.Student;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.MediumTest;

public class MeetingTest extends AndroidTestCase implements DatabaseConstants {
    private MeetingDAO dao = null;
    private DAOFactory daoFactory = DAOFactory.getInstance();

    @Override
    protected void setUp() {
        daoFactory.setGlobalContext(new RenamingDelegatingContext(mContext, "test_"));
        daoFactory.setCacheDAOInstances(true);
        dao = daoFactory.getMeetingDAO(mContext);
    }

    @Override
    protected void tearDown() {
        dao.deleteAll();
        dao.close();
    }

    @MediumTest
    public void test_save_a_meeting() {
        Meeting meeting = new Meeting(new Student("Test Student"), new GregorianCalendar(2010, 1, 5, 10, 15, 0).getTime(), 5, 240, 300, 30, 120);
        meeting = meeting.save(mContext);
        assertNotNull(meeting.getId());
    }

    @MediumTest
    public void test_delete_a_meeting() {
        Student student = new Student("Test Student");

        Meeting meeting = new Meeting(student, new GregorianCalendar(2010, 1, 5, 10, 15, 0).getTime(), 5, 240, 300, 30, 120);
        meeting = meeting.save(mContext);
        assertEquals(1, Meeting.findAllByStudent(student, mContext).size());

        meeting.delete(mContext);
        assertEquals(0, Meeting.findAllByStudent(student, mContext).size());
    }

    @MediumTest
    public void test_find_all_meetings_by_student() {
        Student student = new Student("Test Student");
        new Meeting(student, new GregorianCalendar(2010, 1, 5, 10, 15, 0).getTime(), 5, 240, 300, 30, 120).save(mContext);
        new Meeting(student, new GregorianCalendar(2010, 1, 4, 10, 15, 0).getTime(), 5, 240, 300, 30, 120).save(mContext);;
        new Meeting(student, new GregorianCalendar(2010, 1, 7, 10, 15, 0).getTime(), 5, 240, 300, 30, 120).save(mContext);;
        new Meeting(student, new GregorianCalendar(2010, 1, 1, 10, 15, 0).getTime(), 5, 240, 300, 30, 120).save(mContext);;
        new Meeting(student, new GregorianCalendar(2010, 1, 2, 10, 15, 0).getTime(), 5, 240, 300, 30, 120).save(mContext);;

        List<Meeting> meetings = Meeting.findAllByStudent(student, mContext);
        assertEquals(5, meetings.size());
        assertEquals(new GregorianCalendar(2010, 1, 7, 10, 15, 0).getTime(), meetings.get(0).getDateTime());
        assertEquals(new GregorianCalendar(2010, 1, 5, 10, 15, 0).getTime(), meetings.get(1).getDateTime());
        assertEquals(new GregorianCalendar(2010, 1, 4, 10, 15, 0).getTime(), meetings.get(2).getDateTime());
        assertEquals(new GregorianCalendar(2010, 1, 2, 10, 15, 0).getTime(), meetings.get(3).getDateTime());
        assertEquals(new GregorianCalendar(2010, 1, 1, 10, 15, 0).getTime(), meetings.get(4).getDateTime());
    }

    @MediumTest
    public void test_find_by_student_and_date() {
        Student student = new Student("Test Student");
        new Meeting(student, new GregorianCalendar(2010, 1, 5, 10, 15, 0).getTime(), 5, 240, 300, 30, 120).save(mContext);
        new Meeting(student, new GregorianCalendar(2010, 1, 4, 10, 15, 0).getTime(), 5, 240, 300, 30, 120).save(mContext);;
        Meeting expected = new Meeting(student, new GregorianCalendar(2010, 1, 7, 10, 15, 0).getTime(), 5, 240, 300, 30, 120).save(mContext);;
        new Meeting(student, new GregorianCalendar(2010, 1, 1, 10, 15, 0).getTime(), 5, 240, 300, 30, 120).save(mContext);;
        new Meeting(student, new GregorianCalendar(2010, 1, 2, 10, 15, 0).getTime(), 5, 240, 300, 30, 120).save(mContext);;

        Meeting actual = Meeting.findByStudentAndDate(student, new GregorianCalendar(2010, 1, 7, 10, 15, 0).getTime(), mContext);
        assertEquals(expected.getId(), actual.getId());
    }

    @MediumTest
    public void test_delete_all_by_student() {
        Student student = new Student("Test Student");
        new Meeting(student, new GregorianCalendar(2010, 1, 5, 10, 15, 0).getTime(), 5, 240, 300, 30, 120).save(mContext);
        new Meeting(student, new GregorianCalendar(2010, 1, 4, 10, 15, 0).getTime(), 5, 240, 300, 30, 120).save(mContext);;

        assertFalse(Meeting.findAllByStudent(student, mContext).isEmpty());
        Meeting.deleteAllByStudent(student, mContext);
        assertTrue(Meeting.findAllByStudent(student, mContext).isEmpty());
    }
}
