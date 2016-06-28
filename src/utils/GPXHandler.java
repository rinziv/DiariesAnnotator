package utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import br.ufsc.core.TPoint;


public class GPXHandler extends DefaultHandler{
	
	private static Logger log =Logger.getLogger(GPXHandler.class);
	
	private String text = null;
	private TPoint point = new TPoint(0,0,null,null);
	private ArrayList<TPoint> track = null;
	
	public GPXHandler(){
		super();
	}
	
	public void startDocument(){
		log.debug("Start Document");
		track = new ArrayList<TPoint>();
	}
	
	public void endDocument(){
		log.debug("End Document");
	}
	
	public void startElement(String uri, String name, String qName, Attributes atts){
		if(qName.equals("trkpt")){
			point = new TPoint(new Double(atts.getValue("lon")), new Double(atts.getValue("lat")));
			point.setTransformedGeom(null);
		}	
	}
	
	public void endElement(String uri, String name, String qName){
		
//		if(qName.equals("ele")){
//			point.setEle(text);
//		}
		
		if(qName.equals("time")){
			
			//point.setTime(text);
			text=text.replace("T", " ");
			text=text.replace("Z", " ");
			
			DateFormat dfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Timestamp ts = new Timestamp(dfTime.parse(text).getTime());
				point.setTimestamp(ts);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}

		if(qName.equals("trkpt")){
			track.add(point);
		}
	}
	
	public void characters (char ch[], int start, int length){
		text = new String(ch, start, length); 
	}
	
	public ArrayList<TPoint> getTrack(){
		return track;
	}

}
