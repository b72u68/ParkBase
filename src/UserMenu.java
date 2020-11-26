import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class UserMenu extends JFrame {
	private Connection connection;
    private String userID;
    private Date loginTime;
    private Date logoutTime;
    private HashMap<String, ArrayList<Integer>> lotAndSpot = new HashMap<String, ArrayList<Integer>>();

    public String getUserID() {
        return userID;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public Date getLogoutTime() {
        return logoutTime;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    /*
    private void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    private void setLogoutTime(Date logoutTime) {
        this.logoutTime = logoutTime;
    }
    */

    public void getLotAndSpot() {
        try {
            Statement st = connection.createStatement();
            ResultSet rset = st.executeQuery("SELECT * FROM parking.spot;");

            while (rset.next()) {
                int spotId = rset.getInt(1);
                String lotId = rset.getString(2);

                if (lotAndSpot.get(lotId) == null) {
                    lotAndSpot.put(lotId, new ArrayList<Integer>());
                } else {
                    ArrayList<Integer> tempSpotIds = lotAndSpot.get(lotId);
                    tempSpotIds.add(spotId);
                    lotAndSpot.put(lotId, tempSpotIds);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UserMenu(Connection connection, String userID) {
        super("User Menu");

        setConnection(connection);
        setUserID(userID);
        getLotAndSpot();
        
        setSize(450, 270);
        setLayout(new GridLayout(5, 2));
        setLocationRelativeTo(null);

        // buttons
        JButton btnPr = new JButton("View Profile");
        JButton btnUp = new JButton("Request Update Profile");
        JButton btnMR = new JButton("Make Reservation");
        JButton btnLo = new JButton("Logout");

        // add objects to frame
        add(btnPr);
        add(btnUp);
        add(btnMR);
        add(btnLo);

		btnPr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                viewProfileMenu();
            }
        });
        btnUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestUpdateMenu();
            }
        });
        btnMR.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public boolean isNumeric(String str) {
        if (str == null || str.strip() == "") {
            return false;
        } else {
            try {
                Integer.parseInt(str);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    public void viewProfileMenu() {
        try {
            boolean foundProfile = false;

            JFrame f = new JFrame();
            f.setSize(400,200);

            PreparedStatement pst = connection.prepareStatement("SELECT * FROM parking.member NATURAL JOIN parking.user WHERE parking.member.user_id = ?;",
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            pst.setString(1, userID);
            ResultSet profile = pst.executeQuery();

            while (profile.next()) {
                foundProfile = true;
            }

            if (!foundProfile) {
                pst = connection.prepareStatement("SELECT * FROM parking.user WHERE parking.user.user_id = ?;",
                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                pst.setString(1, userID);
                profile = pst.executeQuery();
            }
            
            JTable jt = new JTable(profileJTable.buildTableModel(profile));
            jt.setBounds(30, 40, 200, 400);
            JScrollPane sp = new JScrollPane(jt);
            f.add(sp);
            f.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestUpdateMenu() {
        String[] updateFields;
        String[] userUpdateFields = {"name", "password"};
        String[] memberUpdateFields = {"name", "password", "lot_id", "spot_id"};
        String updateValueTemp = "";

        try {
            boolean isMember = false;
            boolean isValid = false;

            PreparedStatement pst = connection.prepareStatement("SELECT * FROM parking.member WHERE parking.member.user_id = ?;",
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            pst.setString(1, userID);
            ResultSet profile = pst.executeQuery();

            while (!profile.next()) {
                isMember = true;
            }

            if (isMember) {
                updateFields = memberUpdateFields;
            } else {
                updateFields = userUpdateFields;
            }
            JFrame f = new JFrame();
            f.setSize(400,200);

            final JComboBox<String> fieldCB = new JComboBox<String>(updateFields);

            fieldCB.setVisible(true);
            f.add(fieldCB);

            String updateField = fieldCB.getSelectedItem().toString();
            
            if (updateField == "name" || updateField == "password") {
                updateValueTemp = JOptionPane.showInputDialog(null, "Enter new value");

            } else if (updateField == "lot_id") {
                final JComboBox<String> lotCB = new JComboBox<String>(lotAndSpot.keySet().stream().toArray(String[] ::new));

                lotCB.setVisible(updateField == "lot_id");
                f.add(fieldCB);

                updateValueTemp = lotCB.getSelectedItem().toString();

            } else if (updateField == "spot_id") {
                Statement st = connection.createStatement();
                ResultSet member = st.executeQuery(String.format("SELECT * FROM parking.member WHERE parking.member.id = \"%s\"", userID));
                ArrayList<Integer> spotIds = lotAndSpot.get(member.getString(3));

                final JComboBox<Integer> spotCB = new JComboBox<Integer>(spotIds.toArray(Integer[] ::new));

                spotCB.setVisible(updateField == "spot_id");
                f.add(fieldCB);

                updateValueTemp = spotCB.getSelectedItem().toString();
            }

            switch (updateField) {
                case "name":
                    if (updateValueTemp.length() <= 30) {
                        isValid = true;
                    }
                    break;
                case "password":
                    if (updateValueTemp.length() <= 20) {
                        isValid = true;
                    }
                    break;
                case "lot_id":
                    if (updateValueTemp != null) {
                        isValid = true;
                    }
                    break;
                case "spot_id":
                    if (updateValueTemp != null) {
                        isValid = true;
                    }
                    break;
            }

            String updateValue = updateValueTemp;

            JButton confirm = new JButton("OK");
            f.add(confirm);

            if (isValid) {
                confirm.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            PreparedStatement pst = connection.prepareStatement("INSERT INTO parking.update_form VALUES (?,?,?,?);");

                            pst.setString(1, userID);
                            pst.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
                            pst.setString(3, updateField);
                            if (updateField == "spot_id") {
                                pst.setInt(4, Integer.parseInt(updateValue));
                            } else {
                                pst.setString(4, updateValue);
                            }

                            pst.executeUpdate();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: Finish this screen
    public void makeReservationScreen() {
        try {
            JFrame f = new JFrame();
            f.setSize(400,200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
