import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;


public class AdminMenu extends JFrame {
	
	public AdminMenu(Connection con) {
		super("Admin Menu");
		
		setSize(450, 270);
		setLayout(new GridLayout(5, 2));
		setLocationRelativeTo(null);
		
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
			public void actionPerformed(ActionEvent e) {
				try {
					String userID = JOptionPane.showInputDialog(null, "Enter user ID");
					PreparedStatement pst = con.prepareStatement("SELECT * FROM (parking.user LEFT JOIN parking.member) WHERE user_id = ?;");
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
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		btnAR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		btnUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//new ProfileUpdateMenu();
			}
		});
		btnRp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Statement stm = con.createStatement();
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
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				//call login jframe
			}
		});
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		
		//new AdminMenu();
	}

}
