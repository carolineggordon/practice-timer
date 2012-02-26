package net.johnpwood.android.standuptimer.dao;

public class DuplicateStudentException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DuplicateStudentException(String message) {
        super(message);
    }
}
