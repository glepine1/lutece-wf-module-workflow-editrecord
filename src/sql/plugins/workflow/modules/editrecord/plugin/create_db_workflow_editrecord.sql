DROP TABLE IF EXISTS task_edit_record_cf;
DROP TABLE IF EXISTS task_edit_record_value;
DROP TABLE IF EXISTS task_edit_record;

--
-- Table structure for table task_edit_record_cf
--
CREATE TABLE task_edit_record_cf(
  id_task INT DEFAULT 0 NOT NULL,
  id_state_after_edition INT DEFAULT 0 NOT NULL,
  default_message LONG VARCHAR,
  PRIMARY KEY (id_task)
);

--
-- Table structure for table task_edit_record
--
CREATE TABLE task_edit_record(
  id_history INT DEFAULT 0 NOT NULL,
  id_task INT DEFAULT 0 NOT NULL,
  message LONG VARCHAR,
  is_complete SMALLINT DEFAULT 0 NOT NULL,
  PRIMARY KEY (id_history, id_task)
);

--
-- Table structure for task_edit_record_value
--
CREATE TABLE task_edit_record_value(
  id_history INT DEFAULT 0 NOT NULL,
  id_entry INT DEFAULT 0 NOT NULL,
  PRIMARY KEY (id_history, id_entry)
);
