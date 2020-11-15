
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.ibatis.jdbc.ScriptRunner;

public class Initializer {

	public static void main(String[] args) {

		String url = "jdbc:postgresql://localhost:5432/";
		String dbUsername = "";
		String dbPassword = "";
		String dbName = "";

		Scanner sc = new Scanner(System.in);
		System.out.println("Initializing ParkBase Database...");
		System.out.print("Identify your local database: ");
		dbName = sc.nextLine();
		url.concat(dbName);
		System.out.print("Username: ");
		dbUsername = sc.nextLine();
		System.out.print("Password: ");
		dbPassword = sc.nextLine();

		try (Connection con = DriverManager.getConnection(url, dbUsername, dbPassword);) {
			System.out.println("Connection established...");
			ScriptRunner sr = new ScriptRunner(con);
			Reader bf = new BufferedReader(new FileReader("deliverables/deliverable_2.sql"));

			sr.runScript(bf);

            MockData data = new MockData(con);
            System.out.println("Insert mock data...");
            data.insertAllData();

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Initializer.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} catch (FileNotFoundException ex) {
			System.out.println("Error: could not find file.");
			ex.printStackTrace();
		}

		sc.close();
	}
}
