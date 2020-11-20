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


public class Login extends JFrame{
	
	public Login(Connection connection) {
    	
    	super("Login");
		
		setSize(450, 270);
		setLayout(new GridLayout(5, 2));
		setLocationRelativeTo(null);
		
		// labels
		JLabel lblUsername = new JLabel("Username", JLabel.LEFT);
		JLabel lblPassword = new JLabel("Password", JLabel.LEFT);
		JLabel lblStatus = new JLabel(" ", JLabel.CENTER);

		JTextField txtUname = new JTextField();
		JPasswordField txtPassword = new JPasswordField();
		JButton btnSubm = new JButton("Submit");
		JButton btnExit = new JButton("Exit");

		// constraints
		lblUsername.setHorizontalAlignment(JLabel.CENTER);
		lblPassword.setHorizontalAlignment(JLabel.CENTER);
		 
		// add objects to frame
		add(lblUsername);  
		add(txtUname);
		add(lblPassword); 
		add(txtPassword);
		add(btnSubm);         
		add(btnExit);
		add(lblStatus);   


		btnSubm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String query = "SELECT * FROM parking.user WHERE parking.user.user_id = ? AND parking.user.password = ?;";
				try (PreparedStatement stmt = connection.prepareStatement(query)) {
					stmt.setString(1, txtUname.getText());
					stmt.setString(2, txtPassword.getText());
					ResultSet rs = stmt.executeQuery();
					if (rs.next()) {
						UserMenu userMenu = new UserMenu(connection, "7463462");
			            userMenu.requestUpdate();
						setVisible(false); // HIDE THE FRAME
						dispose(); // CLOSE OUT THE WINDOW
					} else
						lblStatus.setText("User not found");
					while (rs.next()) {
						System.out.println(rs.getString("user_id"));
						System.out.println(rs.getString("password"));
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});
		
		btnExit.addActionListener(e -> System.exit(0));

		setVisible(true); // SHOW THE FRAME
    }
}
