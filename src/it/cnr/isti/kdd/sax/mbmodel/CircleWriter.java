package it.cnr.isti.kdd.sax.mbmodel;

import it.cnr.isti.kdd.sax.mbmodel.Circle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.io.WKBWriter;

public class CircleWriter {
	private static Logger log = Logger.getLogger(CircleWriter.class);
	
	Connection connection = null;
	PreparedStatement pst = null;
	PreparedStatement pst2 = null;
	int pkey = 0;

	public CircleWriter(Connection _connection) {
		connection = _connection;
	}

	public void initTable(String tableName) throws SQLException {
		String sql = "CREATE TABLE "
				+ tableName
				+ " (id numeric, the_point geometry, radius numeric, numPoints numeric)";

		if (connection != null) {
			try {
				Statement st = connection.createStatement();
				st.execute("DROP TABLE " + tableName);
				// connection.commit();
			} catch (Exception e) {
				// ignore this exception
				connection.rollback();
			}
			try {
				Statement st = connection.createStatement();
				st.execute("DROP TABLE " + tableName+"_p");
				// connection.commit();
			} catch (Exception e) {
				// ignore this exception
				connection.rollback();
			}
		}

		if (connection != null) {
			Statement st = connection.createStatement();
			
			log.info("Executing: " + sql);
			st.execute(sql);
			
			sql = "CREATE TABLE "
				+ tableName +"_p"
				+ " (cid numeric, uid numeric, tid numeric, isFirst boolean)";
			st.execute(sql);
			
			sql = "insert into " + tableName + " values(?,?,?,?)";
			pst = connection.prepareStatement(sql);
			
			sql = "insert into " + tableName+"_p" + " values(?,?,?,?)";
			pst2 = connection.prepareStatement(sql);
			// connection.commit();
		}
	}

	public void store(Circle c) throws SQLException {
		WKBWriter wkb = new WKBWriter();
	
		pst.setDouble(3, c.getRadius());
		pst.setInt(1, pkey);
		pst.setBytes(2, wkb.write(c.getCentroid()));
		pst.setInt(4, c.getPoints().size());
		// c.id = pkey;
		
		//System.out.println(pkey + " " + c.points); 
		pst.execute();
		for (PointOfTrajectory p : c.getPoints()) {
			pst2.setInt(1, pkey);
			pst2.setInt(2, p.uid);
			pst2.setInt(3, p.tid);
			pst2.setBoolean(4, p.isFirst);
			pst2.execute();
		}
		pkey++;
//		if (pkey % 1000 == 0) {
//			connection.commit();
//			System.out.println("Committing...." + pkey);
//		}
		// System.out.println(c.points);
	}

}
