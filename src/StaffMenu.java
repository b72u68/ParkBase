import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
//				//initialize new jFrame 
//				JFrame f = new JFrame("Create Reservation");
//				f.setSize(450, 378);
//				f.setLayout(new GridLayout(7, 2));
//				f.setLocationRelativeTo(null);
//				
//				//labels
//				JLabel lblTy = new JLabel("Select Type of Reservation:"); //drop down
//				JLabel lblCI = new JLabel("Enter Check-In (yyyy-MM-dd hh:mm:ss):");
//				JLabel lblCO = new JLabel("Enter Check-Out (yyyy-MM-dd hh:mm:ss):");
//				JLabel lblLi = new JLabel("Choose License Plate:"); //drop down
//				JLabel lblLo = new JLabel("Choose Lot:"); //drop down
//				JLabel lblSp = new JLabel("Choose Spot Number:"); //drop down
//				
//				//text boxes
//				JTextField txtCI = new JTextField(20);
//				JTextField txtCO = new JTextField(20);
//				
//				//drop down menus
//				String[] reservationTypes = {"online","drive-in"};
//				JComboBox cbT = new JComboBox(reservationTypes);
//					/*TO-DO: need to make function that gets users license plates
//					  and stores it into string list below (licensePlates) */
//				String[] licensePlates = {"testing123","testing321"};
//				JComboBox cbLi = new JComboBox(licensePlates);
//					/*TO-DO: need to make function that gets lot letters
//				  	and stores it into string list below (lots) */
//				String[] lots = {"A","B", "C", "D"};
//				JComboBox cbLo = new JComboBox(lots);
//					/*TO-DO: need to make function that gets spot numbers
//				  	and stores it into string list below (spots) */
//				String[] spots = {"testing123","testing321"};
//				JComboBox cbSp = new JComboBox(spots);
//					
//				//adding objects to Frame
//				f.add(lblTy);
//				f.add(cbT);
//				
//				f.add(lblCI);
//				f.add(txtCI);
//				
//				f.add(lblCO);
//				f.add(txtCO);
//				
//				f.add(lblLi);
//				f.add(cbLi);
//				
//				f.add(lblLo);
//				f.add(cbLo);
//				
//				f.add(lblSp);
//				f.add(cbSp);
//				
//				
//				
//				
//				
//				
//				f.setVisible(true);
			}
			
		});
		btnAR.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//initialize new jFrame 
//				JFrame f = new JFrame("Create Reservation For Other User");
//				f.setSize(450, 270);
//				f.setLayout(new GridLayout(5, 2));
//				f.setLocationRelativeTo(null);
//				
//				//labels
//				JLabel lblU = new JLabel("Enter UserID:");
//				JLabel lblCI = new JLabel("Enter Check-In Time:");
//				JLabel lblCO = new JLabel("Enter Check-Out Time:");
//				JLabel lblLi = new JLabel("Choose License Plate:");
//				JLabel lblLo = new JLabel("Choose LotID:");
//				JLabel lbl
//				
//				
//				f.setVisible(true);
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
}