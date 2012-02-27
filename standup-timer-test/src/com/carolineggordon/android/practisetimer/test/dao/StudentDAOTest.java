package com.carolineggordon.android.practisetimer.test.dao;

import java.util.List;

import com.carolineggordon.android.practisetimer.dao.DuplicateStudentException;
import com.carolineggordon.android.practisetimer.dao.InvalidStudentNameException;
import com.carolineggordon.android.practisetimer.dao.StudentDAO;
import com.carolineggordon.android.practisetimer.model.Student;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.MediumTest;

public class StudentDAOTest extends AndroidTestCase {

    private StudentDAO dao = null;

    @Override
    protected void setUp() {
        dao = new StudentDAO(new RenamingDelegatingContext(mContext, "test_"));
    }

    @Override
    protected void tearDown() {
        dao.deleteAll();
        dao.close();
    }

    @MediumTest
    public void test_create_a_student() {
        Student student = new Student("Test Student");
        student = dao.save(student);
        assertNotNull(student.getId());
        assertEquals("Test Student", student.getName());
    }

    @MediumTest
    public void test_create_a_student_with_an_apostrophy_in_the_name() {
        Student student = new Student("John's Student");
        student = dao.save(student);
        assertNotNull(student.getId());
        assertEquals("John's Student", student.getName());
    }

    @MediumTest
    public void test_find_a_student_by_id() {
        Student student = new Student("Another Test Student");
        student = dao.save(student);

        Student foundStudent = dao.findById(student.getId());
        assertEquals(student.getId(), foundStudent.getId());
        assertEquals(student.getName(), foundStudent.getName());
    }

    @MediumTest
    public void test_find_all_can_retrieve_all_students() {
        dao.save(new Student("Test Student 1"));
        dao.save(new Student("Test Student 2"));
        dao.save(new Student("Test Student 3"));
        dao.save(new Student("Test Student 4"));

        List<String> students = dao.findAllStudentNames();
        assertEquals(4, students.size());
    }

    @MediumTest
    public void test_can_find_a_student_by_name() {
        dao.save(new Student("Test Student 1"));

        Student student = dao.findByName("Test Student 1");
        assertEquals("Test Student 1", student.getName());
        assertNotNull(student.getId());
    }

    @MediumTest
    public void test_find_by_student_returns_null_if_student_cannot_be_found() {
        Student student = dao.findByName("Blah Blah Blah");
        assertNull(student);
    }

    @MediumTest
    public void test_cannot_create_a_student_with_a_name_that_already_exists() {
        dao.save(new Student("Test Student 1"));
        try {
            dao.save(new Student("Test Student 1"));
            assertTrue("Should have thrown an exception", false);
        } catch (DuplicateStudentException e) {
            assertTrue(true);
        }
    }

    @MediumTest
    public void test_cannot_create_a_student_with_an_empty_name() {
        try {
            dao.save(new Student(""));
            assertTrue("Should have thrown an exception", false);
        } catch (InvalidStudentNameException e) {
            assertTrue(true);
        }

        try {
            dao.save(new Student("    "));
            assertTrue("Should have thrown an exception", false);
        } catch (InvalidStudentNameException e) {
            assertTrue(true);
        }
    }

    @MediumTest
    public void test_can_update_an_existing_student() {
        Student student1 = dao.save(new Student("Test Student 1"));
        dao.save(new Student(student1.getId(), "Test Student 2"));

        Student student = dao.findById(student1.getId());
        assertEquals("Test Student 2", student.getName());
    }

    @MediumTest
    public void test_can_delete_a_student() {
        Student student = dao.save(new Student("Test Student 1"));
        assertEquals(1, dao.findAllStudentNames().size());

        dao.delete(student);
        assertEquals(0, dao.findAllStudentNames().size());
    }
}
