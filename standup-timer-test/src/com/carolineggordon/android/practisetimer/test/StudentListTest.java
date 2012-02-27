package com.carolineggordon.android.practisetimer.test;

import com.carolineggordon.android.practisetimer.StudentList;
import com.carolineggordon.android.practisetimer.dao.DAOFactory;
import com.carolineggordon.android.practisetimer.dao.StudentDAO;

import com.carolineggordon.android.practisetimer.R;
import android.app.AlertDialog;
import android.test.ActivityInstrumentationTestCase2;
import android.test.RenamingDelegatingContext;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.KeyEvent;

public class StudentListTest extends ActivityInstrumentationTestCase2<StudentList> {
    private StudentList a = null;
    private StudentDAO dao = null;
    private DAOFactory daoFactory = DAOFactory.getInstance();

    public StudentListTest() {
        super("net.johnpwood.android.standuptimer", StudentList.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        a = getActivity();

        daoFactory.setCacheDAOInstances(true);
        daoFactory.setGlobalContext(new RenamingDelegatingContext(a, "test_"));
        dao = daoFactory.getStudentDAO(a);
    }

    @Override
    protected void tearDown() throws Exception {
        dao.deleteAll();
        super.tearDown();
    }

    @MediumTest
    public void test_can_add_a_new_student() {
        sendKeys(KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_A);
        sendKeys("T E S T SPACE S T U D E N T");
        TouchUtils.clickView(this, a.getCreateStudentDialog().getButton(AlertDialog.BUTTON_POSITIVE));
        assertNotNull(dao.findByName("test student"));
    }

    @MediumTest
    public void test_can_delete_an_existing_student() {
        sendKeys(KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_A);
        sendKeys("T E S T SPACE S T U D E N T");
        TouchUtils.clickView(this, a.getCreateStudentDialog().getButton(AlertDialog.BUTTON_POSITIVE));

        TouchUtils.longClickView(this, a.getListView().getChildAt(0));
        getInstrumentation().waitForIdleSync();
        getInstrumentation().invokeContextMenuAction(a, R.id.delete_student, 0);
        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, a.getConfirmDeleteStudentDialog().getButton(AlertDialog.BUTTON_POSITIVE));
        assertNull(dao.findByName("test student"));
    }

    @MediumTest
    public void test_deleting_a_student_can_be_aborted() {
        sendKeys(KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_A);
        sendKeys("T E S T SPACE S T U D E N T");
        TouchUtils.clickView(this, a.getCreateStudentDialog().getButton(AlertDialog.BUTTON_POSITIVE));

        TouchUtils.longClickView(this, a.getListView().getChildAt(0));
        getInstrumentation().waitForIdleSync();
        getInstrumentation().invokeContextMenuAction(a, R.id.delete_student, 0);
        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, a.getConfirmDeleteStudentDialog().getButton(AlertDialog.BUTTON_NEGATIVE));
        assertNotNull(dao.findByName("test student"));
    }
}
