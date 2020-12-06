import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
public class AdminMenu extends JFrame {
	protected String url;
	private String dbName;
	private String dbUsername;
	private String dbPassword;
	private String UName;
	protected Connection connection;
	
	public AdminMenu(String dbName, String dbUsername, String dbPassword, String UName) {
		
		super("Admin Menu");
		setSize(450, 270);
		setLayout(new GridLayout(5, 2));
		setLocationRelativeTo(null);
		
		setConnection(dbName, dbUsername, dbPassword, UName);
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
		btnMR.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//makes an instance of the user menu to activate the reservation screen
				UserMenu um = new UserMenu(getdbName(), getdbUsername(), getdbPassword(), getUName(), new Date());
				um.makeReservationScreen();
			}
			
		});
		btnAR.addActionListener(new ActionListener() {
			
			//Coded by Danny Arvizu
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

		btnUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					
					//Pulls all the profile update requests
					Statement stm = getConnection().createStatement();
					ResultSet updates = stm.executeQuery("SELECT * FROM parking.update_form");

					//Builds the window, with the profile update requests in the middle, the button to confirm an update on top, and the button to close a user's requests on the bottom
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
					
					//update button
					btnU.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							try {
								
								//gets User id
								String userID = JOptionPane.showInputDialog(null, "Enter user ID");
								
								//searches the user table for the user
								PreparedStatement pst = getConnection().prepareStatement("SELECT * FROM parking.user WHERE parking.user.user_id = ?;",
									ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
								pst.setString(1, userID);
								ResultSet profile = pst.executeQuery();
								pst.close();
								
								//Initializes result window
								JFrame UpFrame = new JFrame();
								UpFrame.setSize(400,200);
								
								if (profile.next()) {
									update_confirmation_form(UpFrame, "user", userID);		//current user is in user table
								}
								//search the member table to see if they are a member
								pst = getConnection().prepareStatement("SELECT * FROM parking.member WHERE parking.member.user_id = ?;",
											ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
								pst.setString(1, userID);
								profile = pst.executeQuery();
								pst.close();
									
								if (profile.next()) {
									update_confirmation_form(UpFrame, "member", userID);		//current user is a member
								} 
								//search the admin table to see if they are an admin
								pst = getConnection().prepareStatement("SELECT * FROM parking.admin WHERE parking.admin.admin_id = ?;",
												ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
								pst.setString(1, userID);
								profile = pst.executeQuery();
								pst.close();
								if (profile.next()) {
									update_confirmation_form(UpFrame, "admin", userID);
								}
								//search the employee table to see if they are an employee
								pst = getConnection().prepareStatement("SELECT * FROM parking.employee WHERE parking.employee.employee_id = ?;",
												ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
								pst.setString(1, userID);
								profile = pst.executeQuery();
								pst.close();
								if (profile.next()) {
									update_confirmation_form(UpFrame, "employee", userID);
								}
								
								//display window
								UpFrame.setVisible(true);
							} catch (SQLException ex) {
								System.out.println("Error: could not update profile.");
								ex.printStackTrace();
							}			
						} 
					}); 
					
					//close (drop) button
					btnC.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							try {
								
								//create confirmation popup
								String userID = JOptionPane.showInputDialog(null, "Delete all update requests from this user ID");
								PreparedStatement pst = getConnection().prepareStatement("SELECT * FROM parking.update_form WHERE parking.update_form.id = ?;",
									ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
								pst.setString(1, userID);
								ResultSet profile = pst.executeQuery();
								
								//if the profile is found, delete all update_form entries associated with that id
								if (profile.next()) {
									pst = getConnection().prepareStatement("DELETE FROM parking.update_form WHERE parking.update_form.id = ?;");
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
					System.out.println("Running report, please wait...");
					Statement stm = getConnection().createStatement();
					
					//gathers info about available spots
					ResultSet days = stm.executeQuery("SELECT reservation_time_in, reservation_time_out FROM parking.reservation;");
					ArrayList<Timestamp[]> timeDaysArray = new ArrayList<Timestamp[]>();
					while (days.next()) {
						timeDaysArray.add(new Timestamp[] {days.getTimestamp(1), days.getTimestamp(2)});
					}
					System.out.println("Retrieved all reservation start and end dates.");
					ArrayList<Timestamp> ctDayArray = new ArrayList<Timestamp>();
					
					//gets a list of all days between start and end dates
					for (int i=0; i<timeDaysArray.size(); i++) {
						Timestamp ts = timeDaysArray.get(i)[0];
						
						while (ts.compareTo(timeDaysArray.get(i)[1]) < 0) {
							Calendar c = Calendar.getInstance();
	
							c.setTime(ts);
							ctDayArray.add((Timestamp)ts.clone());
							c.add(Calendar.DAY_OF_MONTH, 1);  
							c.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
							c.set(Calendar.MINUTE, 0);                 // set minute in hour
							c.set(Calendar.SECOND, 0);                 // set second in minute
							c.set(Calendar.MILLISECOND, 0);            // set millis in second

							ts.setTime(c.getTime().getTime());
							

						}
					}
					
					//get a table of all spots available at each hour
					System.out.println("Retrieved all intermediate dates.");
					Vector<String> columnNames = new Vector<String>();
					int columnCount = ctDayArray.size();
					columnNames.add(" ");
					for (int column = 0; column < columnCount; column++) {
						columnNames.add(ctDayArray.get(column).toString());
					}
					Vector<Vector<ArrayList<String>>> data = new Vector<Vector<ArrayList<String>>>();
					
					//rows are hour timeslots
					for (int hour=0; hour<24; hour++) {
						data.add(new Vector<ArrayList<String>>());
						
						//columns are days with reservations on them
						for (int i=0; i<ctDayArray.size()+1; i++) {
							
							data.get(hour).add(new ArrayList<String>());
							if (i==0) {
								data.get(hour).get(i).add(hour + ":00-" + (hour+1) + ":00");
							} else {
								try {
									Statement statement = getConnection().createStatement();
									
									//select all the spots that are not in the set of spots with in times before the iterative day and with out times after the iterative day
									ResultSet freeSpots = statement.executeQuery("SELECT lot_id, spot_id FROM parking.spot"
										+ " WHERE NOT EXISTS ("
											+ "SELECT C.lot_id, C.spot_id FROM parking.reservation as C"
											+ " WHERE reservation_time_in <= '" + ctDayArray.get(i-1)
											+ "' AND reservation_time_out >= '" + ctDayArray.get(i-1)
											+ "' AND date_part('hour', reservation_time_in) <= " + hour
											+ " AND date_part('hour', reservation_time_out) >= " + hour 
											+ " AND C.lot_id = parking.spot.lot_id"
											+ " AND C.spot_id = parking.spot.spot_id" 
										+ ");"
									);
								
									while (freeSpots.next()) {
										data.get(hour).get(i).add(freeSpots.getString(1)+freeSpots.getString(2));
									}
									statement.close();
									freeSpots.close();
									connection.close();
								} catch (SQLException ex) {
									System.out.println("Error: Could not select spot based on day and hour");
									ex.printStackTrace();
								}
							} 
						}
					}
					JFrame report = new JFrame("Spot Calendar Report");
					
					report.setSize(500,500);
					JTable spot_calendar = new JTable(data, columnNames);
					spot_calendar.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					JScrollPane sp = new JScrollPane(spot_calendar, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					report.add(sp);
					report.setVisible(true);
					
					
					//gets profit within month interval listed
					ResultSet member_pay = stm.executeQuery("SELECT * FROM parking.member_pay;");
					ResultSet guest_pay = stm.executeQuery("SELECT * FROM parking.guest_pay;");
					ResultSet lot_ratios = stm.executeQuery("SELECT * FROM parking.lot_ratios;");
					ResultSet times = stm.executeQuery("SELECT * FROM parking.times;");
					
					JFrame reportPay = new JFrame("Report Cont.");
					reportPay.setSize(500,500);
					JPanel mainPanel = new JPanel();
					mainPanel.setLayout(new GridLayout(6, 1));
					reportPay.add(mainPanel);
					
					Map<String,double[]> months = new Hashtable<String,double[]>();
					while(member_pay.next()) {
						months.putIfAbsent(member_pay.getString(1), new double[12]);
						for (int i=(int)member_pay.getDouble(2); i<=(int)member_pay.getDouble(3); i++) {
							months.get(member_pay.getString(1))[i-1] += member_pay.getDouble(4);
						}
					}
					//for every key in months, make a frame element that shows every month and the revenue corresponding with that month
					for (String key : months.keySet()) {
						
						String[][] monthsArray = new String[1][13];
						monthsArray[0][0] = key;
						for (int i=1; i<13; i++) {
							monthsArray[0][i] = String.valueOf(months.get(key)[i-1]);
						}
						JPanel jp = new JPanel();
						jp.setSize(100, 20);
						JTable memTable = new JTable(monthsArray, new String[]{"MEMBER", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"});
						jp.add(new JScrollPane(memTable));
						mainPanel.add(jp);
					}
					months.clear();
					
					
					while(guest_pay.next()) {
						months.putIfAbsent(guest_pay.getString(1), new double[12]);
						for (int i=(int)guest_pay.getDouble(2); i<=(int)guest_pay.getDouble(3); i++) {
							months.get(guest_pay.getString(1))[i-1] += guest_pay.getDouble(4);
						}
					}
					
					//for every key in months, make a frame element that shows every month and the revenue corresponding with that month
					for (String key : months.keySet()) {
						JPanel jp = new JPanel();
						String[][] monthsArray = new String[1][13];
						monthsArray[0][0] = key;
						for (int i=1; i<13; i++) {
							monthsArray[0][i] = String.valueOf(months.get(key)[i-1]);
						}
						JTable guestTable = new JTable(monthsArray, new String[]{"GUEST", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"});
						jp.add(new JScrollPane(guestTable));
						mainPanel.add(jp);
					}
					
	
					String[][] ratioArray = new String[months.size()][4];
					int rowCount = 0;
					while(lot_ratios.next()) {
						ratioArray[rowCount][0] = lot_ratios.getString(1);
						ratioArray[rowCount][1] = String.valueOf(100*(double)lot_ratios.getInt(2)/(double)lot_ratios.getInt(5)) + "%";
						ratioArray[rowCount][2] = String.valueOf(100*(double)lot_ratios.getInt(3)/(double)lot_ratios.getInt(5)) + "%";
						ratioArray[rowCount][3] = String.valueOf(100*(double)lot_ratios.getInt(4)/(double)lot_ratios.getInt(5)) + "%";
						rowCount++;
					}
					JTable ratTable = new JTable(ratioArray, new String[]{" ", "% Members", "% Online", "% Drive In"});
					JPanel ratPanel = new JPanel();
					ratPanel.add(new JScrollPane(ratTable));
					mainPanel.add(ratPanel);
					

					JTable tiTable = new JTable(pUpdateJTable.buildTableModel(times));
					JPanel tiPanel = new JPanel();
					tiPanel.add(new JScrollPane(tiTable));
					mainPanel.add(tiPanel);
					
					//adding the main panel
					JScrollPane mp = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					reportPay.add(mp);
					reportPay.setVisible(true);
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
	
	public void update_confirmation_form(JFrame frame, String table, String userID) {
		
		//set up sql formatting
		String sqlTable = "parking." + table;
		if (table.compareTo("member") == 0) {
			table = "user";
		}
		String sqlCol = sqlTable + "." + table + "_id";
		
		//build window
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
					
					//uses the identified field to update that column in the SQL table with the specified value
					PreparedStatement pst = getConnection().prepareStatement("UPDATE " + sqlTable + " SET " + txtField.getText() + " = ? WHERE " + sqlCol + " = ?;",
							ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					
					//anything other than the spot_id can be inserted as a string
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
				
				//destroy window
				frame.setVisible(false);
				frame.dispose();
			}
		});
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
	
	//Coded by Danny Arvizu
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
