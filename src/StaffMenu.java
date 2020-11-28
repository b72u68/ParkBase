import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class StaffMenu extends JFrame {
	protected String url;
	private String dbName;
	private String dbUsername;
	private String dbPassword;
	private String UName;
	protected Connection connection;
	
	public StaffMenu(String dbName, String dbUsername, String dbPassword, String UName) {
		
		//initialize jFrame 
		super("Staff Menu");
		setSize(450, 270);
		setLayout(new GridLayout(5, 2));
		setLocationRelativeTo(null);
		
		//establish database connection
		setConnection(dbName, dbUsername, dbPassword, UName);
		
		//buttons
		JButton btnPr = new JButton("View Profile");
		JButton btnRU = new JButton("Request Update");
		JButton btnMR = new JButton("Make Reservation");
		JButton btnAR = new JButton("Assist Reservation");
		JButton btnLo = new JButton("Logout");
		
		//adding buttons to frame
		add(btnPr);
		add(btnRU);
		add(btnMR);
		add(btnAR);
		add(btnLo);
		
		
		//action listeners 
		btnLo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
				new Login(getdbName(), getdbUsername(), getdbPassword());
			}
		});
		setVisible(true);
	}
	
	private void setConnection(String dbName, String dbUsername, String dbPassword, String UName) {
		
		//set the variables for getConnection to initialize the connection with
		this.dbName = dbName;
		this.dbUsername = dbUsername;
		this.dbPassword = dbPassword;
		this.UName = UName;
	}
	//getter methods in case you need any single private value. All private fields are set with "setConnection()"
	public String getdbName() {
		return this.dbName;
	}
	public String getdbUsername() {
		return this.dbUsername;
	}
	public String getdbPassword() {
		return this.dbPassword;
	}
	public String getUName() {
		return this.UName;
	}
}