package com.carolineggordon.android.practisetimer.dao;

import android.content.Context;

public class DAOFactory {
    private static DAOFactory instance = null;

    private Context globalContext = null;
    private boolean cacheDAOInstances = false;
    private StudentDAO cachedStudentDAO = null;
    private MeetingDAO cachedMeetingDAO = null;

    public static DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }

    private DAOFactory() {
    }

    public StudentDAO getStudentDAO(Context context) {
        if (cacheDAOInstances) {
            if (cachedStudentDAO == null) {
                cachedStudentDAO = new StudentDAO(getProperDAOContext(context));
            }
            return cachedStudentDAO;
        } else {
            return new StudentDAO(getProperDAOContext(context));
        }
    }

    public MeetingDAO getMeetingDAO(Context context) {
        if (cacheDAOInstances) {
            if (cachedMeetingDAO == null) {
                cachedMeetingDAO = new MeetingDAO(getProperDAOContext(context));
            }
            return cachedMeetingDAO;
        } else {
            return new MeetingDAO(getProperDAOContext(context));
        }
    }

    public void setGlobalContext(Context context) {
        globalContext = context;
    }

    public void setCacheDAOInstances(boolean cacheDAOInstances) {
        this.cacheDAOInstances = cacheDAOInstances;
    }

    private Context getProperDAOContext(Context context) {
        if (globalContext != null) {
            return globalContext;
        } else {
            return context;
        }
    }
}
