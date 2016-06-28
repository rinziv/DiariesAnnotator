package utils;

public class DataBaseTables {
	
	
	
	public static String ANNOTATIONTABLE = "semantic_annotation";
	public static String TRAJECTORYTABLE = "trajectory_points";

	public DataBaseTables(){
		
	}
}


//-- Table: semantic_annotation
//
//-- DROP TABLE semantic_annotation;
//
//CREATE TABLE semantic_annotation
//(
//  id serial NOT NULL,
//  tid integer NOT NULL,
//  time_start timestamp without time zone,
//  time_end timestamp without time zone,
//  temperature numeric,
//  weather character varying(255),
//  goal_fk integer,
//  subgoal_fk integer,
//  transportation_fk integer,
//  place_from_fk integer,
//  event_fk integer,
//  id_user character varying(100),
//  is_stop integer DEFAULT 0,
//  place_to_fk integer
//)
//WITH (
//  OIDS=FALSE
//);
//ALTER TABLE semantic_annotation OWNER TO postgres;
//
//-- Table: trajectory_points
//
//-- DROP TABLE trajectory_points;
//
//CREATE TABLE trajectory_points
//(
//  gid serial NOT NULL,
//  tid integer,
//  "time" timestamp without time zone,
//  lat numeric,
//  lon numeric,
//  geom geometry,
//  id_user character varying(100)
//)
//WITH (
//  OIDS=FALSE
//);
//ALTER TABLE trajectory_points OWNER TO postgres;

