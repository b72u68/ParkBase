import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
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
		btnPr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					//gets user ID
					String userID = JOptionPane.showInputDialog(null, "Enter user ID");
					PreparedStatement pst = getConnection().prepareStatement("SELECT * FROM parking.member NATURAL JOIN parking.user WHERE parking.member.user_id = ?;",
							ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					pst.setString(1, userID);
					ResultSet profile = pst.executeQuery();
					
					//checks what tables the user is in
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
					
					//builds the table output
					JFrame f = new JFrame();
					f.setSize(400,200);
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
		btnRU.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//initialize new jFrame 
				JFrame f = new JFrame("Request Update");
				f.setSize(675, 270);
				f.setLayout(new GridLayout(0, 5));
				f.setLocationRelativeTo(null);
				
				//labels
				JLabel lblCU = new JLabel("New Value For:");
				JLabel lblCP = new JLabel("Enter New Value:");
				
				//radio buttons
				JRadioButton rbN = new JRadioButton("Name");
				rbN.setSelected(true); //is automatically selected to avoid NULL value
				JRadioButton rbP = new JRadioButton("Password");
				JRadioButton rbL = new JRadioButton("Lot ID");
				JRadioButton rbS = new JRadioButton("Spot ID");
				
				//Group the radio buttons.
			    ButtonGroup g = new ButtonGroup();
			    g.add(rbN);
			    g.add(rbP);
			    g.add(rbL);
			    g.add(rbS);
				
				//text boxes
				JTextField txtNV = new JTextField(30);
				
				//buttons
				JButton btnS = new JButton("Submit Request");
				JButton btnC = new JButton("Close");
				
				// constraints
				lblCU.setHorizontalAlignment(SwingConstants.CENTER);
				lblCP.setHorizontalAlignment(SwingConstants.CENTER);
				
				//adding objects to frame
				f.add(lblCU);
				f.add(rbN);
				f.add(rbP);
				f.add(rbL);
				f.add(rbS);
				f.add(lblCP);
				f.add(txtNV);
				f.add(btnS);
				f.add(btnC);
				
				//action listeners
				btnS.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							PreparedStatement pst = getConnection().prepareStatement("INSERT INTO parking.update_form (id, time_made, field_to_update, new_value) VALUES (?,?,?,?);",
									ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
							pst.setString(1, getUName());
							pst.setTimestamp(2, new Timestamp(new Date().getTime()));
							
							if (rbN.isSelected()) {
								pst.setString(3, "name");
							} else if (rbP.isSelected()){
								pst.setString(3, "password");
							} else if (rbL.isSelected()) {
								pst.setString(3, "lot_id");
							} else {
								pst.setString(3, "spot_id");
							}
							
							pst.setString(4, txtNV.getText());
							pst.executeUpdate();
							
							
							System.out.println("Change request successfully submitted!");
							pst.close();
							f.setVisible(false);
							f.dispose();
						} catch(SQLException ex) {
							System.out.println("Error: could not send name change request.");
							ex.printStackTrace();
						}
					}
				});
				btnC.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						f.setVisible(false);
						f.dispose();
					}
				});
				
				
				f.setVisible(true);
			}
		});
		btnMR.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UserMenu um = new UserMenu(getdbName(), getdbUsername(), getdbPassword(), getUName(), new Date());
				um.makeReservationScreen();
			}
			
		});
		btnAR.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//gets user ID
				String userID = JOptionPane.showInputDialog(null, "Enter user ID for whom you're making a reservation:");
				//initialize new jFrame 
				JFrame f = new JFrame("Create Reservation For User: " + userID);
				f.setSize(450, 540);
				f.setLayout(new GridLayout(10, 2));
				f.setLocationRelativeTo(null);
				
				//labels
				JLabel lblTC = new JLabel("Time Created:", SwingConstants.CENTER); //auto-completed
				JLabel lblCI = new JLabel("Check-In Time:", SwingConstants.CENTER); //auto-completed
				JLabel lblCO = new JLabel("Check-Out Time:", SwingConstants.CENTER);
				JLabel lblLi = new JLabel("Enter License Plate #:", SwingConstants.CENTER);
				JLabel lblAp = new JLabel("Application Type:", SwingConstants.CENTER); //auto-completed
				JLabel lblEm = new JLabel("Employee ID:", SwingConstants.CENTER); //auto-completed
				JLabel lblLo = new JLabel("Lot ID:", SwingConstants.CENTER);
				JLabel lblSp = new JLabel("Spot ID:", SwingConstants.CENTER);
				
				//time stamps
				Timestamp now = new Timestamp(new Date().getTime());
				
				//text boxes
				JTextField txtTC = new JTextField(20);
				txtTC.setText(now.toString());
				txtTC.setEditable(false);
				JTextField txtCI = new JTextField(20);
				txtCI.setText(now.toString());
				txtCI.setEditable(false);
				JTextField txtCO = new JTextField(20);
				JTextField txtLi = new JTextField(20);
				JTextField txtAp = new JTextField(20);
				txtAp.setText("Drive-In");
				txtAp.setEditable(false);
				JTextField txtEm = new JTextField(20);
				txtEm.setText(getUName());
				txtEm.setEditable(false);
				JTextField txtLo = new JTextField(10);
				JTextField txtSp = new JTextField(10);
				
				//buttons
				JButton btnS = new JButton("Submit");
				JButton btnC = new JButton("Cancel");
				
				//adding objects to frame
				f.add(lblTC);
				f.add(txtTC);
				f.add(lblCI);
				f.add(txtCI);
				f.add(lblCO);
				f.add(txtCO);
				f.add(lblLi);
				f.add(txtLi);
				f.add(lblAp);
				f.add(txtAp);
				f.add(lblEm);
				f.add(txtEm);
				f.add(lblLo);
				f.add(txtLo);
				f.add(lblSp);
				f.add(txtSp);
				f.add(btnS);
				f.add(btnC);
				
				//action listeners
				btnS.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//check if input date is valid
						boolean isDateValid = isTimeStampValid(txtCO.getText());
						if (!isDateValid) {
							System.out.println("Error: Date is not valid. Try Again");
							f.setVisible(false);
							f.dispose();
							return;
						} 
						
						//check if License is new and check if member is checking into their subscribed spot 
						boolean isLicenseNew = true;
						boolean isMemberSpot = false;
						try {
							PreparedStatement licS = getConnection().prepareStatement("SELECT * FROM parking.member WHERE parking.member.user_id = ?;");
							licS.setString(1, userID);
							ResultSet licR = licS.executeQuery();
							
							while(licR.next()) {
								if (txtLi.getText().equals(licR.getString("registered_license_plate"))) {
									isLicenseNew = false;
									if(txtLo.getText().equals(licR.getString("lot_id")) && licR.getInt("spot_id") == Integer.parseInt(txtSp.getText())) {
										isMemberSpot = true;
									}
								} 
							}
							licR.close();
							licS.close();
						} catch (SQLException ex) {
							isLicenseNew = true;
						}
						
						//check if Lot and Spot are valid
						boolean isValidLotSpot = false;
						try {
							PreparedStatement spS = getConnection().prepareStatement("SELECT * FROM parking.spot WHERE lot_id = ? AND spot_id = ?;");
							spS.setString(1, txtLo.getText());
							spS.setInt(2, Integer.parseInt(txtSp.getText()));
							ResultSet spR = spS.executeQuery();
							
							while(spR.next()) {
								isValidLotSpot = true;
							}
							
							spR.close();
							spS.close();
						} catch (SQLException ex) {
							isValidLotSpot = false;
						}
						if (!isValidLotSpot) {
							System.out.println("Error: Lot and Spot location is invalid. Try Again");
							f.setVisible(false);
							f.dispose();
							return;
						} 
						
						//check if Lot and Spot are open for the time
						boolean isOpenLotSpot = true;
						try {
							PreparedStatement reS = getConnection().prepareStatement("SELECT * FROM parking.reservation WHERE lot_id = ? AND spot_id = ? AND (reservation_time_in BETWEEN ? AND ? OR reservation_time_out BETWEEN ? AND ? OR ? BETWEEN reservation_time_in AND reservation_time_out);");
							reS.setString(1, txtLo.getText());
							reS.setInt(2, Integer.parseInt(txtSp.getText()));
							reS.setTimestamp(3, Timestamp.valueOf(txtCI.getText()));
							reS.setTimestamp(4, Timestamp.valueOf(txtCO.getText()));
							reS.setTimestamp(5, Timestamp.valueOf(txtCI.getText()));
							reS.setTimestamp(6, Timestamp.valueOf(txtCO.getText()));
							reS.setTimestamp(7, Timestamp.valueOf(txtCI.getText()));
							ResultSet reR = reS.executeQuery();
							
							while(reR.next()) {
								isOpenLotSpot = false;
							}
							
							reR.close();
							reS.close();
						} catch (SQLException ex) {
							isOpenLotSpot = false;
						}
						if (!isOpenLotSpot) {
							System.out.println("Error: Lot and Spot location are not avaible during request times. Try Again");
							f.setVisible(false);
							f.dispose();
							return;
						} 
						
						if(isDateValid && isValidLotSpot && isOpenLotSpot) {
							try {
								PreparedStatement regt = getConnection().prepareStatement("INSERT INTO parking.reservation (user_id, time_created, reservation_time_in, reservation_time_out, license_plate, application_type, employee_id, lot_id, spot_id) VALUES (?,?,?,?,?,?,?,?,?);",
										ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
								regt.setString(1, userID);
								regt.setTimestamp(2, Timestamp.valueOf(txtCI.getText()));
								regt.setTimestamp(3, Timestamp.valueOf(txtCI.getText()));
								regt.setTimestamp(4, Timestamp.valueOf(txtCO.getText()));
								regt.setString(5, txtLi.getText());
								regt.setString(6, "drive in");
								regt.setString(7, getUName());
								regt.setString(8, txtLo.getText());
								regt.setInt(9, Integer.parseInt(txtSp.getText()));
								regt.executeUpdate();
								System.out.println("Successfully submitted registration!");
								regt.close();
								
							} catch(SQLException ex) {
								System.out.println("Error: could not submit request.");
								ex.printStackTrace();
							}
						}
						
						f.setVisible(false);
						f.dispose();
					}
				});
				btnC.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						f.setVisible(false);
						f.dispose();
					}
				});

				f.setVisible(true);
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
	
	private void setConnection(String dbName, String dbUsername, String dbPassword, String UName) {
		
		//set the variables for getConnection to initialize the connection with
		this.dbName = dbName;
		this.dbUsername = dbUsername;
		this.dbPassword = dbPassword;
		this.UName = UName;
	}
	
	private Connection getConnection() {
		try {
			
			//initialize connection
			url = "jdbc:postgresql://localhost:5432/";
			url.concat(this.dbName);
			connection = DriverManager.getConnection(url, this.dbUsername, this.dbPassword);
		} catch (SQLException ex) {
			System.out.println("Error: could not set connection");
			ex.printStackTrace();
		}
		return connection;
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
	//helper functions for reservation
	public boolean isTimeStampValid(String timeOut){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            format.parse(timeOut);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}