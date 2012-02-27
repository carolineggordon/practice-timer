package com.carolineggordon.android.practisetimer.dao;

public class InvalidStudentNameException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidStudentNameException(String message) {
        super(message);
    }
}
