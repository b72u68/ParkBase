import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class Login extends JFrame{
	protected  String url;
	private String dbName;
	private String dbUsername;
	private String dbPassword;
	protected Connection connection;
	
	public Login(String dbName, String dbUsername, String dbPassword) {
	//public Login(Connection connection) {
    	
    	super("Login");
		
		setSize(450, 270);
		setLayout(new GridLayout(5, 2));
		setLocationRelativeTo(null);
		
		setConnection(dbName, dbUsername, dbPassword);
		// labels
		JLabel lblUsername = new JLabel("Username", SwingConstants.LEFT);
		JLabel lblPassword = new JLabel("Password", SwingConstants.LEFT);
		JLabel lblStatus = new JLabel(" ", SwingConstants.CENTER);

		JTextField txtUname = new JTextField();
		JPasswordField txtPassword = new JPasswordField();
		JButton btnSubm = new JButton("Submit");
		JButton btnExit = new JButton("Exit");

		// constraints
		lblUsername.setHorizontalAlignment(SwingConstants.CENTER);
		lblPassword.setHorizontalAlignment(SwingConstants.CENTER);
		 
		// add objects to frame
		add(lblUsername);  
		add(txtUname);
		add(lblPassword); 
		add(txtPassword);
		add(btnSubm);         
		add(btnExit);
		add(lblStatus);   


		btnSubm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String query = "SELECT * FROM parking.admin WHERE parking.admin.admin_id = ? AND parking.admin.password = ?;";
				try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
					stmt.setString(1, txtUname.getText());
					stmt.setString(2, txtPassword.getText());
					ResultSet rs = stmt.executeQuery();
					if (rs.next()) {
						setVisible(false); // HIDE THE FRAME
						dispose(); // CLOSE OUT THE WINDOW
						AdminMenu aMenu = new AdminMenu(getdbName(), getdbUsername(), getdbPassword(), txtUname.getText());
					} else {

						query = "SELECT * FROM parking.employee WHERE parking.employee.employee_id = ? AND parking.employee.password = ?;";
						try (PreparedStatement stmt1 = getConnection().prepareStatement(query)) {
							stmt1.setString(1, txtUname.getText());
							stmt1.setString(2, txtPassword.getText());
							ResultSet rs1 = stmt.executeQuery();
							if (rs1.next()) {
								setVisible(false); // HIDE THE FRAME
								dispose(); // CLOSE OUT THE WINDOW
								//Employee Menu
							} else
								lblStatus.setText("Employee not found");
						} catch (SQLException ex) {
							ex.printStackTrace();
						}

						query = "SELECT * FROM parking.user WHERE parking.user.user_id = ? AND parking.user.password = ?;";
						try (PreparedStatement stmt2 = getConnection().prepareStatement(query)) {
							stmt2.setString(1, txtUname.getText());
							stmt2.setString(2, txtPassword.getText());
							ResultSet rs2 = stmt.executeQuery();
							if (rs2.next()) {
								setVisible(false); // HIDE THE FRAME
								dispose(); // CLOSE OUT THE WINDOW
								UserMenu userMenu = new UserMenu(getConnection(), "7463462", new Date());
					            //userMenu.requestUpdate();
							} else
								lblStatus.setText("User not found");
						} catch (SQLException ex) {
							ex.printStackTrace();
						}
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
				
			}	
		});
		
		btnExit.addActionListener(e -> System.exit(0));

		getRootPane().setDefaultButton(btnSubm);
		
		setVisible(true); // SHOW THE FRAME
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
	
	public String getdbName() {
		return this.dbName;
	}
	public String getdbUsername() {
		return this.dbUsername;
	}
	public String getdbPassword() {
		return this.dbPassword;
	}
}
