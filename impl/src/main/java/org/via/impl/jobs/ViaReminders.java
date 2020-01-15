package org.via.impl.jobs;

import lombok.*;
import org.apache.log4j.*;
import org.quartz.*;
import org.sakaiproject.db.api.*;
import org.sakaiproject.user.api.*;
import org.via.*;
import org.via.enums.*;

import java.sql.*;
import java.text.*;
import java.util.Date;

public class ViaReminders implements Job {

    @Setter
    @Getter
    protected IapiTool apiTool = null;

    private static final Logger log = Logger.getLogger(ViaReminders.class);

    private Date lastExportSync;
    private static boolean isRunning = false;
    private Date sessionDate;
    private int reminder;
    private String query;
    private Object[] fields;

    @Getter
    @Setter
    private static SqlService sqlService;

    private int getReminder(int reminder) {
	switch (reminder) {
	// 1h
	case 1:
	    return 1000 * 60 * 60;
	    // 2h
	case 2:
	    return 1000 * 60 * 60 * 2;
	    // 1j
	case 3:
	    return 1000 * 60 * 60 * 24;
	    // 2j
	case 4:
	    return 1000 * 60 * 60 * 24 * 2;
	    // 1week
	case 5:
	    return 1000 * 60 * 60 * 24 * 7;
	default:
	    return 1000 * 60 * 60;
	}
    }

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
	log.info("ViaReminders job Start");

	if (isRunning) {
	    log.warn("Stopping job since it's already running");
	    return;
	}
	isRunning = true;

	try {
	    Connection connection = sqlService.borrowConnection();
	    ;
	    boolean wasCommit = connection.getAutoCommit();
	    connection.setAutoCommit(false);

	    // SYNC GROUPS

	    // Retrieve activities
	    query =
		    "SELECT * FROM via_activity WHERE SessionState = ? AND Reminder > ? AND SessionDate > ? AND ReminderSent IS NULL";
	    fields = new Object[] { eActivityState.Activer.getValue(), 0 , new Date()};
        
	    sqlService.dbRead(connection, query, fields, new SqlReader<Object>() {

		public Object readSqlResultRecord(ResultSet activityResult) throws SqlReaderFinishedException {
		    try {
		        
			// For each activity to check Reminder
			do {
			    sessionDate = new Date (activityResult.getTimestamp("SessionDate", sqlService.getCal()).getTime());
			    reminder = getReminder(activityResult.getInt("Reminder"));

			    // Verify if reminder to send..
			    if ((sessionDate.getTime() - reminder) <= new Date().getTime()) {
				query = "SELECT * FROM via_activityusers WHERE ActivityID = ? AND ParticipantType != ?";
				fields =
					new Object[] { activityResult.getString("ActivityID"),
						eParticipantType.Presentateur.getValue() };
				sqlService.dbRead(connection, query, fields, new SqlReader<Object>() {

				    @Override
				    public Object readSqlResultRecord(ResultSet usersResult)
					    throws SqlReaderFinishedException {
					User sakaiUser;
					try {

					    do {
						sakaiUser =
							apiTool.getSakaiUserInfo(usersResult.getString("SakaiUserID"));
						if (sakaiUser != null) {
						    apiTool.sendActivityEmailReminder(
							    activityResult.getString("Title"), sessionDate,
							    sakaiUser.getEmail(),
							    activityResult.getString("SakaiSiteID"));
						} else {
						    log.warn("ViaReminders - The email for "
							    + usersResult.getString("SakaiUserID")
							    + " could not be found");
						}
					    } while (usersResult.next());
					} catch (SQLException e) {
					    log.error("ViaReminders, users retrieval: " + e.getMessage());
					}

					return null;
				    }
				});

				// Update reminderSent value
				query = "UPDATE via_activity SET ReminderSent = 1 WHERE ActivityID = ?";
				fields = new Object[] { activityResult.getString("ActivityID"), };
				sqlService.dbWrite(connection, query, fields);
				connection.commit();

			    }
			} while (activityResult.next());

		    } catch (SQLException e) {
			log.error("ViaReminders, activities retrieval: " + e.getMessage());
		    }

		    return null;
		}
	    });

	    // Export Reminders.
	    try {
		if (lastExportSync != null) {

		    for (IExport export : apiTool.getLatestExports(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
			    .format(lastExportSync))) {
			query = "SELECT SakaiUserID FROM via_users WHERE UserID = ?";
			fields = new Object[] { export.getUserID() };
			sqlService.dbRead(connection, query, fields, new SqlReader<Object>() {

			    @Override
			    public Object readSqlResultRecord(ResultSet result) throws SqlReaderFinishedException {
					query = "SELECT SakaiSiteID FROM via_activity WHERE ActivityID = ?";
					fields = new Object[] { export.getActivityID() };
					sqlService.dbRead(connection, query, fields, new SqlReader<Object>() {

					    @Override
					    public Object readSqlResultRecord(ResultSet activityResult)
						    throws SqlReaderFinishedException {
						try {
								apiTool.sendExportNotification(
								export.getPlaybackTitle(),
								export.getRecordingType(),
								apiTool.getSakaiUserInfo(
									result.getString("SakaiUserID")).getEmail(),
								activityResult.getString("SakaiSiteID"));
						} catch (SQLException e) {
						    log.error("ViaReminders, users retrieval: " + e.getMessage());
						}

						return null;
					    }
					});

				return null;
			    }
			});

		    }
		}

		connection.setAutoCommit(wasCommit);
		sqlService.returnConnection(connection);
	    } catch (Exception e) {
		log.error("ViaReminders(Export) : " + e.getMessage());
		e.printStackTrace();
	    } finally {
	    }

	    lastExportSync = new Date();
	} catch (Exception e) {
	    log.error("ViaReminders(Export) : " + e.getMessage());
	    e.printStackTrace();
	} finally {
	    isRunning = false;
	}

	log.info("ViaReminders job finished");
    }

}
