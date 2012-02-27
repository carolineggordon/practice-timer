package com.carolineggordon.android.practisetimer.test.dao;

import java.util.GregorianCalendar;
import java.util.List;

import com.carolineggordon.android.practisetimer.dao.CannotUpdateMeetingException;
import com.carolineggordon.android.practisetimer.dao.MeetingDAO;
import com.carolineggordon.android.practisetimer.model.Meeting;
import com.carolineggordon.android.practisetimer.model.Student;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.MediumTest;

public class MeetingDAOTest extends AndroidTestCase {

    private MeetingDAO dao = null;

    @Override
    protected void setUp() {
        dao = new MeetingDAO(new RenamingDelegatingContext(mContext, "test_"));
    }

    @Override
    protected void tearDown() {
        dao.deleteAll();
        dao.close();
    }

    @MediumTest
    public void test_create_a_meeting() {
        Meeting meeting = new Meeting(new Student("Test Student"), new GregorianCalendar(2010, 1, 5, 10, 15, 0).getTime(), 5, 240, 300, 30, 120);
        meeting = dao.save(meeting);
        
        assertNotNull(meeting.getId());
        meeting = dao.findById(meeting.getId());
        assertEquals("Test Student", meeting.getStudent().getName());
        assertEquals(new GregorianCalendar(2010, 1, 5, 10, 15, 0).getTime(), meeting.getDateTime());
        assertEquals(5f, meeting.getMeetingStats().getNumParticipants());
        assertEquals(240.f, meeting.getMeetingStats().getIndividualStatusLength());
        assertEquals(300f, meeting.getMeetingStats().getMeetingLength());
        assertEquals(30f, meeting.getMeetingStats().getQuickestStatus());
        assertEquals(120f, meeting.getMeetingStats().getLongestStatus());
    }

    @MediumTest
    public void test_cannot_update_a_meeting_that_has_already_been_created() {
        Meeting meeting = new Meeting(new Student("Test Student"), new GregorianCalendar(2010, 1, 5, 10, 15, 0).getTime(), 5, 240, 300, 30, 120);
        meeting = dao.save(meeting);

        try {
            dao.save(meeting);
            fail("Should have thrown an exception");
        } catch (CannotUpdateMeetingException e) {
            assertTrue(true);
        }
    }

    @MediumTest
    public void test_find_all_meetings_by_student_in_cronological_order() {
        Student student = new Student("Test Student");
        dao.save(new Meeting(student, new GregorianCalendar(2010, 1, 5, 10, 15, 0).getTime(), 5, 240, 300, 30, 120));
        dao.save(new Meeting(student, new GregorianCalendar(2010, 1, 4, 10, 15, 0).getTime(), 5, 240, 300, 30, 120));
        dao.save(new Meeting(student, new GregorianCalendar(2010, 1, 7, 10, 15, 0).getTime(), 5, 240, 300, 30, 120));
        dao.save(new Meeting(student, new GregorianCalendar(2010, 1, 1, 10, 15, 0).getTime(), 5, 240, 300, 30, 120));
        dao.save(new Meeting(student, new GregorianCalendar(2010, 1, 2, 10, 15, 0).getTime(), 5, 240, 300, 30, 120));

        List<Meeting> meetings = dao.findAllByStudent(student);
        assertEquals(5, meetings.size());
        assertEquals(new GregorianCalendar(2010, 1, 7, 10, 15, 0).getTime(), meetings.get(0).getDateTime());
        assertEquals(new GregorianCalendar(2010, 1, 5, 10, 15, 0).getTime(), meetings.get(1).getDateTime());
        assertEquals(new GregorianCalendar(2010, 1, 4, 10, 15, 0).getTime(), meetings.get(2).getDateTime());
        assertEquals(new GregorianCalendar(2010, 1, 2, 10, 15, 0).getTime(), meetings.get(3).getDateTime());
        assertEquals(new GregorianCalendar(2010, 1, 1, 10, 15, 0).getTime(), meetings.get(4).getDateTime());
    }

    @MediumTest
    public void test_delete_a_single_meeting() {
        Meeting meeting = new Meeting(new Student("Test Student"), new GregorianCalendar(2010, 1, 5, 10, 15, 0).getTime(), 5, 240, 300, 30, 120);
        meeting = dao.save(meeting);
        meeting = dao.findById(meeting.getId());
        assertNotNull(meeting.getId());

        dao.delete(meeting);
        meeting = dao.findById(meeting.getId());
        assertNull(meeting);
    }

    @MediumTest
    public void test_find_by_student_and_date() {
        Student student = new Student("Test Student");
        dao.save(new Meeting(student, new GregorianCalendar(2010, 1, 5, 10, 15, 0).getTime(), 5, 240, 300, 30, 120));
        dao.save(new Meeting(student, new GregorianCalendar(2010, 1, 4, 10, 15, 0).getTime(), 5, 240, 300, 30, 120));
        Meeting expected = dao.save(new Meeting(student, new GregorianCalendar(2010, 1, 7, 10, 15, 0).getTime(), 5, 240, 300, 30, 120));
        dao.save(new Meeting(student, new GregorianCalendar(2010, 1, 1, 10, 15, 0).getTime(), 5, 240, 300, 30, 120));
        dao.save(new Meeting(student, new GregorianCalendar(2010, 1, 2, 10, 15, 0).getTime(), 5, 240, 300, 30, 120));

        Meeting actual = dao.findByStudentAndDate(student, new GregorianCalendar(2010, 1, 7, 10, 15, 0).getTime());
        assertEquals(expected.getId(), actual.getId());
    }

    @MediumTest
    public void test_delete_all_by_student() {
        Student student = new Student("Test Student");
        dao.save(new Meeting(student, new GregorianCalendar(2010, 1, 5, 10, 15, 0).getTime(), 5, 240, 300, 30, 120));
        dao.save(new Meeting(student, new GregorianCalendar(2010, 1, 4, 10, 15, 0).getTime(), 5, 240, 300, 30, 120));

        assertFalse(dao.findAllByStudent(student).isEmpty());
        dao.deleteAllByStudent(student);
        assertTrue(dao.findAllByStudent(student).isEmpty());
    }
}
