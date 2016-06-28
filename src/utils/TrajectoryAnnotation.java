package utils;

import it.cnr.isti.kdd.lorenzo.dbmanager.ConnectionManager;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import viz.Movement;

import br.ufsc.core.TPoint;
import br.ufsc.core.Trajectory;

public class TrajectoryAnnotation {
	
	private Movement move;
	private int tid;
	private Double temperature;
	private int goal;
	private int place_to;
	private int place_from;
	private int transportation;
	private int weather;
	private int isStop;
	private int user;
	private int event;
	private String goal_name;
	private String place_to_name;
	private String place_from_name;
	private String transportation_name;
	private String weather_name;
	private String event_name;
	
	public TrajectoryAnnotation(int tid, Movement move, Double temperature, int goal,
			int place_to, int place_from, int transportation, int weather,
			int isStop, int user, int event) {
		//this.tid = tid;
		this.move = move;
		this.temperature = temperature;
		this.goal = goal;
		this.place_to = place_to;
		this.place_from = place_from;
		this.transportation = transportation;
		this.weather = weather;
		this.isStop = isStop;
		this.user = user;
		this.event = event;
	}
	
	public void storeAnnotation(){
		String sql = "";
		try{

			Connection conn = ConnectionManager.getConnection();
			Statement st = conn.createStatement();
			
			DateFormat dfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			
			sql = "INSERT INTO "+DataBaseTables.ANNOTATIONTABLE+" (tid, time_start, time_end, temperature," +
					"weather, goal_fk, transportation_fk, place_from_fk, place_to_fk, event_fk, id_user, is_stop) VALUES ";
			
			sql=sql+" ("+tid+",cast('"+dfTime.format(move.getStartMovement().getTime())+"' as timestamp),cast('"+dfTime.format(move.getEndMovement().getTime())+"' as timestamp),"+temperature
					+",'"+weather+"',"+goal+","+transportation+","+move.getStartLocation().getLid()
					+","+move.getStopLocation().getLid()+","+event+",current_user,"+isStop+") ";
			
			st.execute(sql);
			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Problem with query: "+sql);
		}
	}
	
	public void storeAnnotationFile(PrintWriter pw){
		String file = "";
		try{

			
			
			DateFormat dfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			file=move.getTrajectory().getTid()+";"+dfTime.format(move.getStartMovement().getTime())+";"+dfTime.format(move.getEndMovement().getTime())+";"+temperature
					+";'"+weather_name+"';"+goal_name+";"+transportation_name+";"+move.getStartLocation().getLid()
					+";"+move.getStopLocation().getLid()+";"+event_name+";"+move.getTrajectory().getUid()+";"+isStop+" ";
			
			pw.println(file);
			
			//st.execute(file);
			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Problem with query: "+file);
		}
	}
	
	public void storeTrajectory(Trajectory t){
		String sql = "";
		try{

			Connection conn = ConnectionManager.getConnection();
			Statement st = conn.createStatement();
			
			sql = "SELECT max(tid) from "+DataBaseTables.TRAJECTORYTABLE;
			
			ResultSet rs = st.executeQuery(sql);
			
			if(rs.next()){
				tid = rs.getInt(1)+1;
			}else{
				tid = 0;
			}
			
			sql = "INSERT INTO "+DataBaseTables.TRAJECTORYTABLE+" (tid, time, lat, lon, geom, id_user) values";
			DateFormat dfTime2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(int i =0; i<t.getLen();i++){
				TPoint p = t.getPoint(i); 
				sql = sql+ "("+tid+",cast('"+dfTime2.format(new Date(p.getTime()))+"' as timestamp)"+","+p.getY()+","+p.getX()+
						",ST_MakePoint("+p.getX()+","+p.getY()+")"+",current_user),";
			}
			sql = sql.substring(0,sql.length()-1);
			st.execute(sql);
			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Problem with query: "+sql);
		}
	}

	public Movement getMove() {
		return move;
	}

	public void setMove(Movement move) {
		this.move = move;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public int getGoal() {
		return goal;
	}

	public void setGoal(int goal) {
		this.goal = goal;
	}

	public int getPlace_to() {
		return place_to;
	}

	public void setPlace_to(int place_to) {
		this.place_to = place_to;
	}

	public int getPlace_from() {
		return place_from;
	}

	public void setPlace_from(int place_from) {
		this.place_from = place_from;
	}

	public int getTransportation() {
		return transportation;
	}

	public void setTransportation(int transportation) {
		this.transportation = transportation;
	}

	public int getWeather() {
		return weather;
	}

	public void setWeather(int weather) {
		this.weather = weather;
	}

	public int getIsStop() {
		return isStop;
	}

	public void setIsStop(int isStop) {
		this.isStop = isStop;
	}

	public int getUser() {
		return user;
	}

	public void setUser(int user) {
		this.user = user;
	}

	public  int getTid() {
		return tid;
	}

	public  void setTid(int tid) {
		tid = tid;
	}

	public int getEvent() {
		return event;
	}

	public void setEvent(int event) {
		this.event = event;
	}

	public String getGoal_name() {
		return goal_name;
	}

	public void setGoal_name(String goal_name) {
		this.goal_name = goal_name;
	}

	public String getPlace_to_name() {
		return place_to_name;
	}

	public void setPlace_to_name(String place_to_name) {
		this.place_to_name = place_to_name;
	}

	public String getPlace_from_name() {
		return place_from_name;
	}

	public void setPlace_from_name(String place_from_name) {
		this.place_from_name = place_from_name;
	}

	public String getTransportation_name() {
		return transportation_name;
	}

	public void setTransportation_name(String transportation_name) {
		this.transportation_name = transportation_name;
	}

	public String getWeather_name() {
		return weather_name;
	}

	public void setWeather_name(String weather_name) {
		this.weather_name = weather_name;
	}

	public String getEvent_name() {
		return event_name;
	}

	public void setEvent_name(String event_name) {
		this.event_name = event_name;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}
	
	
	
	  

}
