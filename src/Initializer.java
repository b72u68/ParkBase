
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Initializer {
	
	public static void main(String[] args) {

		String url = "jdbc:postgresql://localhost:5432/";
		String user = "";
		String password = "";
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Initializing ParkBase Database...");
		System.out.print("Identify your local database: ");
		String db = sc.nextLine();
		url.concat(db);
		System.out.print("Username: ");
		user = sc.nextLine();
		System.out.print("Password: ");
		password = sc.nextLine();
		
		try (Connection con = DriverManager.getConnection(url, user, password);
				PreparedStatement pst = con.prepareStatement("DROP TABLE IF EXISTS Author;\r\n" + 
					"\r\n" + 
					"CREATE TABLE IF NOT EXISTS Author (\r\n" + 
					"	id serial PRIMARY KEY,\r\n" + 
					"	name VARCHAR(25)\r\n" + 
					");\r\n" + 
					"\r\n" + 
					"INSERT INTO Author(id, name) VALUES(1, 'Jack London');\r\n" + 
					"INSERT INTO Author(id, name) VALUES(2, 'Honore de Balzac');\r\n" + 
					"INSERT INTO Author(id, name) VALUES(3, 'Patrick Crowe');"
					+ "SELECT * FROM Author")) {
				
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Initializer.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		}
		
		System.out.print("Enter 1 if you'd like to make an entry\nEnter 2 if you'd like to quit: ");
		int num = sc.nextInt();
		
		switch (num) {
		case 1:
			prepareSql(url, user, password);
			break;
		case 2:
			
		default:
			
		}
		sc.close();
		return;
	}
	
	public static void prepareSql(String url, String user, String password) {
		
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter id of author: ");
		int id;
		String input = sc.nextLine();
		try {
			id = Integer.parseInt(input);
		} catch (Exception e) {
			sc.close();
			System.out.println("Not a valid value.");
			return;
		}
		
		System.out.print("Enter name of author: ");
		input = sc.nextLine();
		String author;
		try {
			author = input;
		} catch (Exception e) {
			sc.close();
			System.out.println("Not a valid value.");
			return;
		}
		sc.close();
		
		String query = "INSERT INTO Author(id, name) VALUES(?, ?)";
		
		try (Connection con = DriverManager.getConnection(url, user, password);
				PreparedStatement pst = con.prepareStatement(query)) {
			
			pst.setInt(1, id);
			pst.setString(2, author);
			pst.executeUpdate();
			
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM Author");
			while (rs.next()) {
				System.out.println(rs.getString(2));
			} 
			
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Initializer.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

}
