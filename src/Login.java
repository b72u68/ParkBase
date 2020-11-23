import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class Login extends JFrame{
	
	public Login(Connection connection) {
    	
    	super("Login");
		
		setSize(450, 270);
		setLayout(new GridLayout(5, 2));
		setLocationRelativeTo(null);
		
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
				String query = "SELECT * FROM parking.user WHERE parking.user.user_id = ? AND parking.user.password = ?;";
				try (PreparedStatement stmt = connection.prepareStatement(query)) {
					stmt.setString(1, txtUname.getText());
					stmt.setString(2, txtPassword.getText());
					ResultSet rs = stmt.executeQuery();
					if (rs.next()) {
						setVisible(false); // HIDE THE FRAME
						dispose(); // CLOSE OUT THE WINDOW
						UserMenu userMenu = new UserMenu(connection, "7463462");
			            userMenu.requestUpdate();
					} else
						lblStatus.setText("User not found");
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
				query = "SELECT * FROM parking.employee WHERE parking.employee.employee_id = ? AND parking.employee.password = ?;";
				try (PreparedStatement stmt = connection.prepareStatement(query)) {
					stmt.setString(1, txtUname.getText());
					stmt.setString(2, txtPassword.getText());
					ResultSet rs = stmt.executeQuery();
					if (rs.next()) {
						setVisible(false); // HIDE THE FRAME
						dispose(); // CLOSE OUT THE WINDOW
						//Employee Menu
					} else
						lblStatus.setText("Employee not found");
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
				query = "SELECT * FROM parking.admin WHERE parking.admin.admin_id = ? AND parking.admin.password = ?;";
				try (PreparedStatement stmt = connection.prepareStatement(query)) {
					stmt.setString(1, txtUname.getText());
					stmt.setString(2, txtPassword.getText());
					ResultSet rs = stmt.executeQuery();
					if (rs.next()) {
						setVisible(false); // HIDE THE FRAME
						dispose(); // CLOSE OUT THE WINDOW
						//Admin Menu
					} else
						lblStatus.setText("Admin not found");
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}	
		});
		
		btnExit.addActionListener(e -> System.exit(0));

		setVisible(true); // SHOW THE FRAME
    }
}
