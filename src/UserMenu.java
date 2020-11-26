import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMenu {
    private Connection connection;
    private Scanner sc = new Scanner(System.in);
    private String userID;
    private String type;
    private Date loginTime;
    private Date logoutTime;
    private HashMap<String, ArrayList<Integer>> lotAndSpot = new HashMap<String, ArrayList<Integer>>();

    public UserMenu(Connection connection, String userID, Date loginTime) {
        setConnection(connection);
        setUserID(userID);
        setLoginTime(loginTime);
        getUserType();
    }

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

    private void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    /*
    private void setLogoutTime(Date logoutTime) {
        this.logoutTime = logoutTime;
    }
    */

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

    public void getUserType() {
        try {
            PreparedStatement pst = connection.prepareStatement("SELECT * FROM parking.member NATURAL JOIN parking.user WHERE parking.member.user_id = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            pst.setString(1, userID);
            ResultSet rset = pst.executeQuery();

            if (rset.next()) {
                type = "member";
            } else {
                PreparedStatement pstUser = connection.prepareStatement("SELECT * FROM parking.user WHERE parking.user.user_id = ?");
                pstUser.setString(1, userID);
                ResultSet userResult = pstUser.executeQuery();

                if (userResult.next()) {
                    type = "user";
                } else {
                    type = "employee";
                }

                userResult.close();
                pstUser.close();
            }
            rset.close();
            pst.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String UserMenuOptions() {
        System.out.println("\nUser Menu");
        System.out.println("1. View Profile\n2. Update Profile\n3. Make Reservation\n4. Logout");
        System.out.print("Enter your option here: ");
        String option = sc.nextLine();
        return option;
    }

    public void UserMenuScreen() {
        boolean exit = false;
        while (!exit) {
            String option = UserMenuOptions();

            switch (option) {
                case "1":
                    viewProfileScreen();
                    break;
                case "2":
                    updateProfileScreen();
                    break;
                case "3":
                    makeReservationScreen();
                    break;
                case "4":
                    logout();
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    public void viewProfileScreen() {
        try {
            if (type == "member") {
                PreparedStatement pstMember = connection.prepareStatement("SELECT * FROM parking.user NATURAL JOIN parking.member, parking.parking_lot WHERE parking.member.user_id = ? AND parking.member.lot_id = parking.parking_lot.lot_id");
                pstMember.setString(1, userID);
                ResultSet memberResult = pstMember.executeQuery();
                
                if (memberResult.next()) {
                    printUserProfile(memberResult, type);
                }

                memberResult.close();
                pstMember.close();
            } else if (type == "user") {
                    PreparedStatement pstUser = connection.prepareStatement("SELECT * FROM parking.user WHERE parking.user.user_id = ?");
                    pstUser.setString(1, userID);
                    ResultSet userResult = pstUser.executeQuery();
                    
                    if (userResult.next()) {
                        printUserProfile(userResult, type);
                    }

                    userResult.close();
                    pstUser.close();
                } else if (type == "employee") {
                    PreparedStatement pstEmployee = connection.prepareStatement("SELECT * FROM parking.employee WHERE parking.employee.employee_id = ?");
                    pstEmployee.setString(1, userID);
                    ResultSet employeeResult = pstEmployee.executeQuery();

                    if (employeeResult.next()) {
                        printUserProfile(employeeResult, type);
                    }

                    pstEmployee.close();
                    employeeResult.close();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printUserProfile(ResultSet rset, String type) {
        try {
            System.out.println("\nUser Profile");
            System.out.println(String.format("Name: %s", rset.getString("name")));
            System.out.println(String.format("Password: %s", rset.getString("password")));

            if (type == "member") {
                System.out.println(String.format("Registered license plate: %s", rset.getString("registered_license_plate")));
                System.out.println(String.format("Registered lot: %s", rset.getString("lot_id")));
                System.out.println(String.format("Registered spot: %d", rset.getInt("spot_id")));
                System.out.println(String.format("Membership fee: %f", rset.getDouble("membership_fee")));
            } else if (type == "employee") {
                System.out.println(String.format("Type: %s", rset.getString("type")));
                System.out.println(String.format("Salary: %f", rset.getDouble("salary")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateProfileScreen() {}
    public void makeReservationScreen() {}
    public void logout() {}
}
