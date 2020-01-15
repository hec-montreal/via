ALTER TABLE via_activity ADD (Reminder smallint DEFAULT NULL);

ALTER TABLE via_activity ADD (ReminderSent NUMBER(1,0) DEFAULT NULL);

ALTER TABLE via_activity ADD (IsNewVia NUMBER(1,0) DEFAULT NULL);

ALTER TABLE via_activity ADD (EnrollmentType smallint DEFAULT NULL);

UPDATE via_activity SET EnrollmentType = 0 ;
UPDATE via_activity SET EnrollmentType = 2 WHERE ActivityID IN(SELECT DISTINCT ActivityID FROM via_activitygroups);