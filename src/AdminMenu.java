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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


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
					JFrame f = new JFrame();
					f.setSize(400,200);
					String userID = JOptionPane.showInputDialog(null, "Enter user ID");
					PreparedStatement pst = getConnection().prepareStatement("SELECT * FROM parking.member NATURAL JOIN parking.user WHERE parking.member.user_id = ?;",
							ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					pst.setString(1, userID);
					ResultSet profile = pst.executeQuery();
					if (!profile.next()) {
						pst = getConnection().prepareStatement("SELECT * FROM parking.user WHERE parking.user.user_id = ?;",
								ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
						pst.setString(1, userID);
						profile = pst.executeQuery();
					}
					profile.previous();
					if (!profile.next()) {
						pst = getConnection().prepareStatement("SELECT * FROM parking.employee WHERE parking.employee.employee_id = ?;",
								ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
						pst.setString(1, userID);
						profile = pst.executeQuery();
					}
					profile.previous();
					if (!profile.next()) {
						pst = getConnection().prepareStatement("SELECT * FROM parking.admin WHERE parking.admin.admin_id = ?;",
								ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
						pst.setString(1, userID);
						profile = pst.executeQuery();
					}
					profile.previous();
					
					JTable jt = new JTable(profileJTable.buildTableModel(profile));
					jt.setBounds(30, 40, 200, 400);
					JScrollPane sp = new JScrollPane(jt);
					f.add(sp);
					f.setVisible(true);
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
				try {
					Statement stm = getConnection().createStatement();
					ResultSet updates = stm.executeQuery("SELECT * FROM parking.update_form");

					JFrame subf = new JFrame();
					Container contentPane = subf.getContentPane();
					contentPane.setLayout(new BorderLayout());
					JTable jt = new JTable(pUpdateJTable.buildTableModel(updates));
					JScrollPane sp = new JScrollPane(jt);
					contentPane.add(sp, BorderLayout.CENTER);
					subf.setSize(500, 400);
					
					JButton btnU = new JButton("Update");		
					JButton btnC = new JButton("Close Update Request");
					contentPane.add(btnU, BorderLayout.NORTH);
					contentPane.add(btnC, BorderLayout.SOUTH);
					
					btnU.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							try {
								JFrame UpFrame = new JFrame();
								UpFrame.setSize(400,200);
								String userID = JOptionPane.showInputDialog(null, "Enter user ID");
								PreparedStatement pst = getConnection().prepareStatement("SELECT * FROM parking.user WHERE parking.user.user_id = ?;",
									ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
								pst.setString(1, userID);
								ResultSet profile = pst.executeQuery();
								
								if (profile.next()) {
									pst = getConnection().prepareStatement("SELECT * FROM parking.member WHERE parking.member.user_id = ?;",
											ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
									pst.setString(1, userID);
									profile = pst.executeQuery();
									if (profile.next()) {
										update_confirmation_form(UpFrame, "member", userID);		//current user is a member
									} else {
										update_confirmation_form(UpFrame, "user", userID);			//current user is just a user
									}
								} else {
									pst = getConnection().prepareStatement("SELECT * FROM parking.employee WHERE parking.employee.employee_id = ?;",
											ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
									pst.setString(1, userID);
									profile = pst.executeQuery();
									if (profile.next()) {
										profile.previous();
										update_confirmation_form(UpFrame, "user", userID);
									} else {
										pst = getConnection().prepareStatement("SELECT * FROM parking.admin WHERE parking.admin.admin_id = ?;",
												ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
										pst.setString(1, userID);
										profile = pst.executeQuery();
										if (profile.next()) {
											profile.previous();
											update_confirmation_form(UpFrame, "user", userID);
										}
									}
								}
								UpFrame.setVisible(true);
							} catch (SQLException ex) {
								System.out.println("Error: could not update profile.");
								ex.printStackTrace();
							}			
						} 
					}); 
					btnC.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							try {
								String userID = JOptionPane.showInputDialog(null, "Delete all update requests from this user ID");
								PreparedStatement pst = getConnection().prepareStatement("SELECT * FROM parking.update_form WHERE parking.update_form.user_id = ?;",
									ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
								pst.setString(1, userID);
								ResultSet profile = pst.executeQuery();
								
								if (profile.next()) {
									pst = getConnection().prepareStatement("DELETE FROM parking.update_form WHERE parking.update_form.user_id = ?;");
									pst.setString(1, userID);
									pst.executeUpdate();
									System.out.println("All update requests from user \"" + userID + "\" closed.");
								}
							} catch (SQLException ex) {
								System.out.println("Error: Could not close form");
								ex.printStackTrace();
							}
						}
					});
					subf.setVisible(true);
				} catch (SQLException ex) {
					System.out.println("Could not retrieve update_forms");
					ex.printStackTrace();
				}
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
	
	public void update_confirmation_form(JFrame frame, String table, String userID) {
		
		String sqlTable = "parking." + table;
		if (table.compareTo("member") == 0) {
			table = "user";
		}
		String sqlCol = sqlTable + "." + table + "_id";
		
		frame.setSize(450, 270);
		frame.setLayout(new GridLayout(5, 2));
		
		JLabel lblField = new JLabel("Enter field to change", SwingConstants.LEFT);
		JLabel lblValue = new JLabel("Enter value for field", SwingConstants.LEFT);
		JLabel lblStatus = new JLabel(" ", SwingConstants.CENTER);

		JTextField txtField = new JTextField();
		JTextField txtValue = new JTextField();
		JButton btnSubm = new JButton("Submit");
		JButton btnExit = new JButton("Exit");

		// constraints
		lblField.setHorizontalAlignment(SwingConstants.CENTER);
		lblValue.setHorizontalAlignment(SwingConstants.CENTER);
		 
		// add objects to frame
		frame.add(lblField);  
		frame.add(txtField);
		frame.add(lblValue);
		frame.add(txtValue);
		frame.add(btnSubm);
		frame.add(btnExit);
		
		btnSubm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					PreparedStatement pst = getConnection().prepareStatement("UPDATE " + sqlTable + " SET " + txtField.getText() + " = ? WHERE " + sqlCol + " = ?;",
							ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					if (txtField.getText().compareTo("spot_id") == 0) {
						pst.setInt(1, Integer.parseInt(txtValue.getText()));
					} else {
						pst.setString(1, txtValue.getText());
					}
					pst.setString(2, userID);
					pst.executeUpdate();
					pst.close();
					System.out.println("Update Successful!");
					frame.setVisible(false);
					frame.dispose();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				frame.dispose();
			}
		});
	}

}
