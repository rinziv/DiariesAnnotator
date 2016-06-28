package it.cnr.isti.kdd.sax.mbmodel;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.vividsolutions.jts.geom.Geometry;


public class SQLTrajectoryIterator extends SQLDataIterator<Trajectory> {

	protected int tidIdx = 2;
	protected int uidIdx = 1;
	protected int geomIdx = 3;
	public SQLTrajectoryIterator(Connection _connection, String _sql) {
		connection = _connection;
		if (connection != null) {
			try {
				Statement st = connection.createStatement();
				st.setFetchSize(1000);
				rs = st.executeQuery(_sql);
				rs.next();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
		}
	}

	@Override
	public Trajectory next() {
		try {
			Geometry geom = wkbReader.read(rs.getBytes(geomIdx));
			//Moving_point mp = (Moving_point) Moving_point.getInstance().buildUp(rs,geomIdx);
			//System.out.println("creating a new moving object " + geom.toString());
			int uid = rs.getInt(uidIdx);
			int tid = rs.getInt(tidIdx);
			Trajectory tr = new Trajectory(uid, tid, geom);
			tr.setStartTime(rs.getTimestamp("start_movement").getTime());
			tr.setEndTime(rs.getTimestamp("end_movement").getTime());

			moveCursor();
			return tr;
		} catch (SQLException e) {
//			e.printStackTrace();
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return null;
	}

}
