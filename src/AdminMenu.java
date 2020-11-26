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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class AdminMenu extends JFrame {
	protected String url;
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
								pst.close();
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
									pst.close();
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
									pst.close();
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
					
					for (int hour=0; hour<24; hour++) {
						data.add(new Vector<ArrayList<String>>());
						
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
					JFrame report = new JFrame();
					report.setSize(500,500);
					JTable spot_calendar = new JTable(data, columnNames);
					//spot_calendar.setBounds(30, 40, 200, 400);
					spot_calendar.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					JScrollPane sp = new JScrollPane(spot_calendar, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					report.add(sp);
					report.setVisible(true);
					
					
					//gets profit within month interval listed
					ResultSet member_pay = stm.executeQuery("SELECT * FROM parking.member_pay;");
					ResultSet guest_pay = stm.executeQuery("SELECT * FROM parking.guest_pay;");
					ResultSet lot_ratios = stm.executeQuery("SELECT * FROM parking.lot_ratios;");
					ResultSet times = stm.executeQuery("SELECT * FROM parking.times;");
					
					JFrame reportPay = new JFrame();
					reportPay.setSize(500,500);
					JPanel mainPanel = new JPanel();
					mainPanel.setLayout(new GridLayout(6, 1));
					reportPay.add(mainPanel);
					

					ResultSetMetaData metaData = member_pay.getMetaData();
					int numCol = metaData.getColumnCount();
					Map<String,double[]> months = new Hashtable<String,double[]>();
					while(member_pay.next()) {
						months.putIfAbsent(member_pay.getString(1), new double[12]);
						for (int i=(int)member_pay.getDouble(2); i<=(int)member_pay.getDouble(3); i++) {
							months.get(member_pay.getString(1))[i-1] += member_pay.getDouble(4);
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
						JTable memTable = new JTable(monthsArray, new String[]{"MEMBER", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"});
						jp.add(new JScrollPane(memTable));
						mainPanel.add(jp);
					}
					metaData = guest_pay.getMetaData();
					numCol = metaData.getColumnCount();
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
					
	
					metaData = lot_ratios.getMetaData();
					numCol = metaData.getColumnCount();
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
