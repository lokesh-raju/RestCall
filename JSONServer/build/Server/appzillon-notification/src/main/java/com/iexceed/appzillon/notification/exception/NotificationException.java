package com.iexceed.appzillon.notification.exception;

/**
 *
 * @author Vinod Rawat
 */
import java.util.HashMap;
import java.util.Map;

import com.iexceed.appzillon.exception.AppzillonException;

@SuppressWarnings("serial")
public class NotificationException extends AppzillonException {

    private static final Map<Code, String> NOTIFICATION_EXCEPTIONS = new HashMap<Code, String>();
    String code;
    String message;

    private NotificationException() {

    }
    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    static {
        NOTIFICATION_EXCEPTIONS.put(Code.APZ_NT_001,
                "Notification's InterfaceId mismatched");
    }

    public enum Code {

        APZ_NT_001;

        public String toString() {
            return this.name().replace('_', '-');
        }
    }

    public String getNotificationExceptionMessage(Object key) {
        return NOTIFICATION_EXCEPTIONS.get(key);
    }
    public static NotificationException getNotificationExceptionInstance() {
        return new NotificationException();
    }

}
