import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
		JButton btnSiUp = new JButton("Sign Up");

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
		add(btnSiUp);


		btnSubm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String query = "SELECT * FROM parking.admin WHERE parking.admin.admin_id = ? AND parking.admin.password = ?;";
				try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
					String Uname = txtUname.getText();
					String Pword = txtPassword.getText();
					stmt.setString(1, Uname);
					stmt.setString(2, Pword);
					ResultSet rs = stmt.executeQuery();
					if (rs.next()) {
						setVisible(false); // HIDE THE FRAME
						dispose(); // CLOSE OUT THE WINDOW
						new AdminMenu(getdbName(), getdbUsername(), getdbPassword(), txtUname.getText());
					} else {

						query = "SELECT * FROM parking.employee WHERE parking.employee.employee_id = ? AND parking.employee.password = ?;";
						try (PreparedStatement stmt1 = getConnection().prepareStatement(query)) {
							stmt1.setString(1, Uname);
							stmt1.setString(2, Pword);
							ResultSet rs1 = stmt1.executeQuery();
							if (rs1.next()) {
								setVisible(false); // HIDE THE FRAME
								dispose(); // CLOSE OUT THE WINDOW
								//Employee Menu
								new StaffMenu(getdbName(), getdbUsername(), getdbPassword(), txtUname.getText());
							} else
								lblStatus.setText("Employee not found");
						} catch (SQLException ex) {
							ex.printStackTrace();
						}

						query = "SELECT * FROM parking.user WHERE parking.user.user_id = ? AND parking.user.password = ?;";
						try (PreparedStatement stmt2 = getConnection().prepareStatement(query)) {
							stmt2.setString(1, Uname);
							stmt2.setString(2, Pword);
							ResultSet rs2 = stmt2.executeQuery();
							if (rs2.next()) {
								setVisible(false); // HIDE THE FRAME
								dispose(); // CLOSE OUT THE WINDOW
								UserMenu userMenu = new UserMenu(getdbName(), getdbUsername(), getdbPassword(), txtUname.getText(), new Date());
					            userMenu.UserMenuScreen();
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
		btnSiUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(null, "Enter your name");
				String insert = "";
				if (JOptionPane.showConfirmDialog (null, "Be a member","Join our premium reservations?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					insert = "INSERT INTO parking.member (user_id, password, name) values (?, ?, ?);";
				} else {
					insert = "INSERT INTO parking.user (user_id, password, name) values (?, ?, ?);";
				}
				String Uname = txtUname.getText();
				String Pword = txtPassword.getText();
				//inserts user into database
				try (PreparedStatement stmt = getConnection().prepareStatement(insert)) {
					stmt.setString(1, Uname);
					stmt.setString(2, Pword);
					stmt.setString(3, name);
					stmt.executeUpdate();
					stmt.close();
				} catch (SQLException se) {
					se.printStackTrace();
				}
				try (Statement stmt = getConnection().createStatement()) { 
					String drop = "DROP USER IF EXISTS u_" + Uname;
					String create = "CREATE USER u_" + Uname;
					stmt.executeUpdate(drop);
					stmt.executeUpdate(create);
					stmt.close();
				} catch (SQLException se) {
					se.printStackTrace();
				}
				try (Statement stmt = getConnection().createStatement()) {
					String grant = "GRANT r_user TO u_" + Uname;
					stmt.executeUpdate(grant);
					stmt.close();
				} catch (SQLException se) {
					se.printStackTrace();
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
