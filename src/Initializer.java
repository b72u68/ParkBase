import java.util.Date;

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
import org.apache.ibatis.jdbc.ScriptRunner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class Initializer extends JFrame {
	protected String url = "jdbc:postgresql://localhost:5432/";
	protected String dbName;
	protected String dbUsername;
	protected String dbPassword;
	
	private Connection connection;
	
	public Initializer() {
		
		super("PostgreSQL Connection Manager");
		
		setSize(450, 270);
		setLayout(new GridLayout(5, 2));
		setLocationRelativeTo(null);
		
		// labels
		JLabel lblDatabase = new JLabel("Database", SwingConstants.LEFT);
		JLabel lblUsername = new JLabel("Username", SwingConstants.LEFT);
		JLabel lblPassword = new JLabel("Password", SwingConstants.LEFT);
		JLabel lblStatus = new JLabel(" ", SwingConstants.CENTER);
		
		JTextField txtDatabase = new JTextField();
		JTextField txtUname = new JTextField(10);
		JPasswordField txtPassword = new JPasswordField();
		JButton btnSubm = new JButton("Submit");
		JButton btnExit = new JButton("Exit");

		// constraints
		lblDatabase.setHorizontalAlignment(SwingConstants.CENTER);
		lblUsername.setHorizontalAlignment(SwingConstants.CENTER);
		lblPassword.setHorizontalAlignment(SwingConstants.CENTER);
		 
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
			private String dbName;
			private String dbUsername;
			private String dbPassword;

			@Override
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				
				
				this.setConnection(txtDatabase.getText(), txtUname.getText(), txtPassword.getText());
				System.out.println("Initializing ParkBase Database...");
				try {
					System.out.println("Connection established...");
					ScriptRunner sr = new ScriptRunner(getConnection());
					Reader bf = new BufferedReader(new FileReader("deliverables/deliverable_2.sql"));

					sr.runScript(bf);
		            insertMockData(getConnection());
		            setVisible(false);
		            dispose();
		            
		            new Login(txtDatabase.getText(), txtUname.getText(), txtPassword.getText());
		            
		            //to test UserMenu, uncomment the following two statements and comment out "new Login...;" above
		            
//		            UserMenu userMenu = new UserMenu(getConnection(), "7463462", new Date());
//		            userMenu.UserMenuScreen();
		            
		            //new StaffMenu(txtDatabase.getText(), txtUname.getText(), txtPassword.getText());

				} catch (FileNotFoundException ex) {
					System.out.println("Error: could not find file.");
					ex.printStackTrace();
				}

			}

			private void setConnection(String dbName, String dbUsername, String dbPassword) {
				this.dbName = dbName;
				this.dbUsername = dbUsername;
				this.dbPassword = dbPassword;
			}
			
			private Connection getConnection() {
				try {
					url = "jdbc:postgresql://localhost:5432/";
					url.concat(this.dbName);
					connection = DriverManager.getConnection(url, this.dbUsername, this.dbPassword);
				} catch (SQLException ex) {
					System.out.println("Error: could not set connection");
					ex.printStackTrace();
				}
				return connection;
			}
		});
		btnExit.addActionListener(e -> System.exit(0));
		
		getRootPane().setDefaultButton(btnSubm);

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
