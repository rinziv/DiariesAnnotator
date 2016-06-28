package it.cnr.isti.kdd.sax.mbmodel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequenceFactory;
import com.vividsolutions.jts.io.WKBReader;

abstract public class SQLDataIterator<T> implements Iterator<T>{

	protected Connection connection = null;
	protected ResultSet rs = null;
	protected boolean hasNext = true;
	protected WKBReader wkbReader = new WKBReader(new GeometryFactory(new PackedCoordinateSequenceFactory(PackedCoordinateSequenceFactory.DOUBLE, 3)));

	public SQLDataIterator() {
		super();
	}

	protected void moveCursor() throws SQLException {
		hasNext = rs.next();
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("This method is not valid for this iterator");
	}

	@Override
	abstract public T next();

}