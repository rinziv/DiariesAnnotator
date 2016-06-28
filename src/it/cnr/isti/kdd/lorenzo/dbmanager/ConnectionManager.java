package it.cnr.isti.kdd.lorenzo.dbmanager;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.log4j.Logger;
import java.sql.Connection;

//(c) 2012 Lorenzo Gabrielli, Mirco Nanni  KDD Lab. - ISTI - CNR, Pisa, Italy
/**
  * Questa Classe si occupa di creare la connessione con il database utilizzando i parametri inseriti nel file database.properties
 */
public class ConnectionManager {

	/**
	 * @param args
	 * @throws IOException 
	 */
	static Logger log = Logger.getLogger(ConnectionManager.class);
	public static Connection getConnection(){

		// Start loading connection parameters
		Properties props = new Properties();
		FileInputStream in = null;
		try {
			in = new FileInputStream("database.properties");
			props.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.error("Errore nel caricamento del file database.properties");
			return null;
		} catch (IOException e) {
			log.error("Errore nel caricamento del file database.properties");
		}
		
		String drivers = props.getProperty("jdbc.drivers");
		if (drivers != null)
			System.setProperty("jdbc.drivers", drivers);
		else {
			log.error("Errore nel caricamento del jdbc.drivers");
			System.exit(1);
		}
		String url = props.getProperty("jdbc.url");
		String username = props.getProperty("jdbc.username");
		String password = props.getProperty("jdbc.password");
		// End loading connection parameters

		try {
			Class.forName(drivers);
		} catch (ClassNotFoundException e) {
			log.error("Errore durante Class.forName(Drivers) " +", drivers:"+drivers  );
			
		}
		
		Connection con = null;
		try {
			con = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			log.error("Url:"+url + ", username:"+username + ", password:"+password);
			
		}
		return con;

	}
	public Connection getConnection(String jdbcDriver, String jdbcUrl,	String jdbcUsername, String jdbcPassword) {
		// TODO Auto-generated method stub
		// Start loading connection parameters
				Properties props = new Properties();
				FileInputStream in = null;
				try {
					in = new FileInputStream("database.properties");
					props.load(in);
					in.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					log.error("Errore nel caricamento del file database.properties");
					return null;
				} catch (IOException e) {
					log.error("Errore nel caricamento del file database.properties");
				}
				
				String drivers = props.getProperty(jdbcDriver);
				if (drivers != null)
					System.setProperty("jdbc.drivers", drivers);
				else {
					log.error("Errore nel caricamento del jdbc.drivers");
					System.exit(1);
				}
				String url = props.getProperty(jdbcUrl);
				String username = props.getProperty(jdbcUsername);
				String password = props.getProperty(jdbcPassword);
				// End loading connection parameters

				try {
					Class.forName(drivers);
				} catch (ClassNotFoundException e) {
					log.error("Errore durante Class.forName(Drivers) " +", drivers:"+drivers  );
					
				}
				
				Connection con = null;
				try {
					con = DriverManager.getConnection(url, username, password);
				} catch (SQLException e) {
					log.error("Url:"+url + ", username:"+username + ", password:"+password);
					
				}
				return con;
	}

}
