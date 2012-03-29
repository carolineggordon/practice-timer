package com.carolineggordon.android.practisetimer.mock;

import com.carolineggordon.android.practisetimer.ConfigurePractiseTimer;

import android.content.Intent;
import android.widget.EditText;
import android.widget.Spinner;

public class ConfigurePractiseTimerMock extends ConfigurePractiseTimer {
    private boolean showInvalidNumberOfParticipantsDialogCalled = false;
    private boolean startTimerCalled = false;
    private boolean displaySettingsCalled = false;
    private boolean displayAboutBoxCalled = false;
    private boolean displayHelpDialogCalled = false;
    private boolean displayStudentConfigurationCalled = false;
    private Intent intent = null;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void showInvalidNumberOfParticipantsDialog() {
        showInvalidNumberOfParticipantsDialogCalled = true;
    }

    public boolean showInvalidNumberOfParticipantsDialogCalled() {
        return showInvalidNumberOfParticipantsDialogCalled;
    }

    @Override
    protected void startTimer(Intent i) {
        startTimerCalled = true;
        intent = i;
    }

    public boolean startTimerCalled() {
        return startTimerCalled;
    }

    public Intent getIntent() {
        return intent;
    }

    @Override
    protected void displaySettings() {
        displaySettingsCalled = true;
    }

    public boolean displaySettingsCalled() {
        return displaySettingsCalled;
    }

    @Override
    protected void displayAboutBox() {
        displayAboutBoxCalled = true;
    }

    public boolean displayAboutBoxCalled() {
        return displayAboutBoxCalled;
    }

    @Override
    protected void displayHelpDialog() {
        displayHelpDialogCalled = true;
    }

    public boolean displayHelpDialogCalled() {
        return displayHelpDialogCalled;
    }

    @Override
    protected void displayStudentConfiguration() {
        displayStudentConfigurationCalled = true;
    }

    public boolean displayStudentConfigurationCalled() {
        return displayStudentConfigurationCalled;
    }

    @Override
    public void loadState() {
        super.loadState();
    }

    @Override
    public int getMeetingLength() {
        return super.getMeetingLength();
    }

    @Override
    public int getNumParticipants() {
        return super.getNumParticipants();
    }

    @Override
    public int getStudentNamesPos() {
        return super.getStudentNamesPos();
    }

    @Override
    public Spinner getMeetingLengthSpinner() {
        return super.getMeetingLengthSpinner();
    }

    @Override
    public EditText getMeetingLengthEditText() {
        return super.getMeetingLengthEditText();
    }
}
