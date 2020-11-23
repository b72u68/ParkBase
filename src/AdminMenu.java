import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;


public class AdminMenu extends JFrame {
	protected  String url;
	private String dbName;
	private String dbUsername;
	private String dbPassword;
	protected Connection connection;
	
	public AdminMenu(String dbName, String dbUsername, String dbPassword) {
	//public AdminMenu(Connection con) {
		
		super("Admin Menu");
		
		setSize(450, 270);
		setLayout(new GridLayout(5, 2));
		setLocationRelativeTo(null);
		
		setConnection(dbName, dbUsername, dbPassword);
		//buttons
		JButton btnPr = new JButton("View Profile");
		JButton btnMR = new JButton("Make Reservation");
		JButton btnAR = new JButton("Assist Reservation");
		JButton btnUp = new JButton("Update Profile");
		JButton btnRp = new JButton("Run Report");
		JButton btnLo = new JButton("Logout");
		 
		// add objects to frame
		add(btnPr);
		add(btnMR);
		add(btnAR);
		add(btnUp);
		add(btnRp);
		add(btnLo);


		btnPr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String userID = JOptionPane.showInputDialog(null, "Enter user ID");
					PreparedStatement pst = getConnection().prepareStatement("SELECT * FROM (parking.user LEFT JOIN parking.member) WHERE parking.user_id = ?;");
					pst.setString(1, userID);
					ResultSet profile = pst.executeQuery();
					
					JTable jt = new JTable(profileJTable.buildTableModel(profile));
					jt.setBounds(30, 40, 200, 400);
					JScrollPane sp = new JScrollPane(jt);
					add(sp);
					setVisible(true);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		btnMR.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		btnAR.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		btnUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//new ProfileUpdateMenu();
			}
		});
		btnRp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Statement stm = getConnection().createStatement();
					ResultSet spots = stm.executeQuery("SELECT * FROM parking.spot WHERE distinct (spot_id, lot_id, reservation_time_in, reservation_time_out) NOT IN (SELECT distinct (spot_id, lot_id, reservation_time_in, reservation_time_out) FROM parking.reservation;");
					ResultSet member_pay = stm.executeQuery("SELECT * FROM parking.member_pay;");
					ResultSet guest_pay = stm.executeQuery("SELECT * FROM parking.guest_pay;");
					ResultSet lot_ratios = stm.executeQuery("SELECT * FROM parking.lot_ratios;");
					ResultSet times = stm.executeQuery("SELECT * FROM parking.times;");
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
				
			}
		});
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
	
	public static void main(String[] args) {
		
		//new AdminMenu();
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
