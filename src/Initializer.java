
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.ibatis.jdbc.ScriptRunner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.ibatis.jdbc.ScriptRunner;

@SuppressWarnings("serial")
public class Initializer extends JFrame {

	public Initializer() {
		
		super("PostgreSQL Connection Manager");
		
		
		/*Scanner sc = new Scanner(System.in);
		
		System.out.print("Identify your local database: ");
		dbName = sc.nextLine();
		url.concat(dbName);
		System.out.print("Username: ");
		dbUsername = sc.nextLine();
		System.out.print("Password: ");
		dbPassword = sc.nextLine();*/

		
		
		setSize(450, 270);
		setLayout(new GridLayout(5, 2));
		setLocationRelativeTo(null);
		
		// labels
		JLabel lblDatabase = new JLabel("Database", JLabel.LEFT);
		JLabel lblUsername = new JLabel("Username", JLabel.LEFT);
		JLabel lblPassword = new JLabel("Password", JLabel.LEFT);
		JLabel lblStatus = new JLabel(" ", JLabel.CENTER);
		
		JTextField txtDatabase = new JTextField();
		JTextField txtUname = new JTextField(10);
		JPasswordField txtPassword = new JPasswordField();
		JButton btnSubm = new JButton("Submit");
		JButton btnExit = new JButton("Exit");

		// constraints
		lblDatabase.setHorizontalAlignment(JLabel.CENTER);
		lblUsername.setHorizontalAlignment(JLabel.CENTER);
		lblPassword.setHorizontalAlignment(JLabel.CENTER);
		 
		// add objects to frame
		add(lblDatabase);	//1st row
		add(txtDatabase);
		add(lblUsername);  // 2nd row 
		add(txtUname);
		add(lblPassword); // 3rd row
		add(txtPassword);
		add(btnSubm);         // 4th row
		add(btnExit);
		add(lblStatus);   // 5th row

		btnSubm.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				String url = "jdbc:postgresql://localhost:5432/";
				String dbUsername = txtUname.getText();
				String dbPassword = txtPassword.getText();
				String dbName = txtDatabase.getText();
				url.concat(dbName);
				System.out.println("Initializing ParkBase Database...");
				
				try (Connection con = DriverManager.getConnection(url, dbUsername, dbPassword);) {
					System.out.println("Connection established...");
					ScriptRunner sr = new ScriptRunner(con);
					Reader bf = new BufferedReader(new FileReader("deliverables/deliverable_2.sql"));

					sr.runScript(bf);
		            insertMockData(con);

				} catch (SQLException ex) {
					Logger lgr = Logger.getLogger(Initializer.class.getName());
					lgr.log(Level.SEVERE, ex.getMessage(), ex);
				} catch (FileNotFoundException ex) {
					System.out.println("Error: could not find file.");
					ex.printStackTrace();
				}

				//sc.close();
			}
		});
		btnExit.addActionListener(e -> System.exit(0));

		setVisible(true);
	}
	
	public static void main(String[] args) {

		new Initializer();
	}

    public static void insertMockData(Connection connection) {
        MockData data = new MockData(connection);
        System.out.println("Inserting mock data...");
        data.insertAllData();
    }
}
