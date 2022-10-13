package ru.detmir.hybris.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sap.engine.interfaces.messaging.api.MessageDirection;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

/**
 * Класс для записи данных в аудитлог.
 * Взят as is из статьи "Creating custom Log Entries in Audit Log from Java Proxy"
 * @author Ivan Alekseev
 * @see <a href= "https://blogs.sap.com/2017/10/13/creating-custom-log-entries-in-audit-log-from-java-proxy/">Creating custom Log Entries in Audit Log from Java Proxy</a>
 */
public class MessageMonitor {
    private Method mtdAddAuditLogEntry;
    private Object objAuditAccess;
    private Class clsMessageKey;
    private Class clsLogStatus;
    private Object objMessageKey;

    public MessageMonitor(String msgId, MessageDirection msgDirection) throws IllegalAccessException, InstantiationException, NamingException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
        Context context = new InitialContext();

        Class clsAPIPublick = context.lookup("com.sap.engine.interfaces.messaging.api.public").getClass();
        Object objAPIPublick = clsAPIPublick.newInstance();
        Method mtdGetAuditAccess = clsAPIPublick.getDeclaredMethod("getAuditAccess");

        objAuditAccess = mtdGetAuditAccess.invoke(objAPIPublick);
        Class clsAuditAccess = objAuditAccess.getClass();

        for (Method mtd : clsAuditAccess.getMethods()) {
            for (Class cls : mtd.getParameterTypes()) {
                if (cls.getCanonicalName().equals("com.sap.engine.interfaces.messaging.api.MessageKey")) {
                    clsMessageKey = cls;
                } else if (cls.getCanonicalName().equals("com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus")) {
                    clsLogStatus = cls;
                }
            }
            if (clsMessageKey != null && clsAuditAccess != null) break;
        }

        mtdAddAuditLogEntry = clsAuditAccess.getMethod("addAuditLogEntry", new Class[]{clsMessageKey, clsLogStatus, String.class});

        Class clsMessageDirection = null;
        outer: for (Constructor cnstr : clsMessageKey.getConstructors()) {
            for (Class cls : cnstr.getParameterTypes()) {
                if (cls.getCanonicalName().equals("com.sap.engine.interfaces.messaging.api.MessageDirection")) {
                    clsMessageDirection = cls;
                    break outer;
                }
            }
        }

        Constructor cnstrMessageDirection = clsMessageKey.getConstructor(new Class[]{String.class, clsMessageDirection});
        Field fldMessageDirection = clsMessageDirection.getField(msgDirection.toString());
        objMessageKey = cnstrMessageDirection.newInstance(new Object[]{new String(msgId), fldMessageDirection.get(clsMessageDirection)});
    }

    public void addLogEntry(AuditLogStatus logStatus, String logMsg) throws SecurityException, NoSuchMethodException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Field fldLogStatus;
        if (logStatus.toString().equals("S")) {
            fldLogStatus = clsLogStatus.getField("SUCCESS");
        } else if (logStatus.toString().equals("W")) {
            fldLogStatus = clsLogStatus.getField("WARNING");
        } else {
            fldLogStatus = clsLogStatus.getField("ERROR");
        }

        mtdAddAuditLogEntry.invoke(objAuditAccess, new Object[]{objMessageKey, fldLogStatus.get(clsLogStatus), new String(logMsg)});
    }
}

