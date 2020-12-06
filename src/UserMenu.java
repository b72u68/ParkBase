import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;

import java.util.Arrays;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMenu {
    private String dbName;
    private String dbUserName;
    private String dbPassword;

    private Connection connection;
    private Scanner sc = new Scanner(System.in);
    private String userID;
    private String type;
    private Date loginTime;
    private HashMap<String, ArrayList<Integer>> lotAndSpot = new HashMap<String, ArrayList<Integer>>();
    private HashMap<String, ArrayList<Integer>> memberLotAndSpot = new HashMap<String, ArrayList<Integer>>();

    public UserMenu(String dbName, String dbUserName, String dbPassword, String userID, Date loginTime) {
        setDbName(dbName);
        setDbUserName(dbUserName);
        setDbPassword(dbPassword);
        connection = getConnection();

        setUserID(userID);
        setLoginTime(loginTime);
        getUserType();
        getLotAndSpot();
        getMemberLotAndSpot();
    }

    private Connection getConnection() {
		try {
			String url = "jdbc:postgresql://localhost:5432/";
			url.concat(this.dbName);
			connection = DriverManager.getConnection(url, dbUserName, dbPassword);
		} catch (SQLException ex) {
			System.out.println("Error: could not set connection");
			ex.printStackTrace();
		}
		return connection;
	}

    public String getUserID() {
        return userID;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    private void setDbName(String dbName) {
        this.dbName = dbName;
    }

    private void setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
    }

    private void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    private void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public String convertTime(Long millisecond) {
        int seconds = (int) ((millisecond / 1000) % 60);
        int minutes = (int) ((millisecond / (1000*60)) % 60);
        int hours = (int) ((millisecond / (1000*60*60)) % 24);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public boolean isNumeric(String str) {
        if (str == null || str.strip().equals("")) {
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

    public boolean isDate(String date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            format.parse(date);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean isValidLot(String lotId) {
        if (lotId.length() == 1 && lotAndSpot.keySet().contains(lotId)) {
             return true;
        }
        return false;
    }

    public boolean isValidSpot(String lotId, String spotId) {
        if (lotAndSpot.keySet().contains(lotId)) {
            if (isNumeric(spotId)) {
                if (lotAndSpot.get(lotId).contains(Integer.parseInt(spotId))) {
                    if (type.equals("user")) {
                        if (memberLotAndSpot.keySet().contains(lotId) && memberLotAndSpot.get(lotId).contains(Integer.parseInt(spotId))) {
                            return false;
                        }
                        return true;
                    } else {
                        try {
                            PreparedStatement pst = connection.prepareStatement("SELECT * FROM parking.member WHERE parking.member.user_id = ?");
                            pst.setString(1, userID);
                            ResultSet rset = pst.executeQuery();

                            if (rset.next()) {
                                if (memberLotAndSpot.keySet().contains(lotId) && memberLotAndSpot.get(lotId).contains(Integer.parseInt(spotId))) {
                                    if (lotId.equals(rset.getString("lot_id")) && spotId.equals(Integer.toString(rset.getInt("spot_id")))) {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                                return true;
                            }

                            rset.close();
                            pst.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } 
            } 
        }
        return false;
    }

    public void getLotAndSpot() {
        try {
            Statement st = connection.createStatement();
            ResultSet rset = st.executeQuery("SELECT * FROM parking.spot;");

            while (rset.next()) {
                int spotId = rset.getInt(1);
                String lotId = rset.getString(2);

                if (lotAndSpot.get(lotId) == null) {
                    ArrayList<Integer> temp = new ArrayList<Integer>();
                    temp.add(spotId);
                    lotAndSpot.put(lotId, temp);
                } else {
                    ArrayList<Integer> temp = lotAndSpot.get(lotId);
                    temp.add(spotId);
                    lotAndSpot.put(lotId, temp);
                }
            }

            rset.close();
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMemberLotAndSpot() {
        try {
            Statement st = connection.createStatement();
            ResultSet rset = st.executeQuery("SELECT * FROM parking.member;");

            while (rset.next()) {
                int spotId = rset.getInt("spot_id");
                String lotId = rset.getString("lot_id");

                if (memberLotAndSpot.get(lotId) == null) {
                    ArrayList<Integer> temp = new ArrayList<Integer>();
                    temp.add(spotId);
                    memberLotAndSpot.put(lotId, temp);
                } else {
                    ArrayList<Integer> temp = memberLotAndSpot.get(lotId);
                    temp.add(spotId);
                    memberLotAndSpot.put(lotId, temp);
                }
            }

            rset.close();
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getUserType() {
        try {
            PreparedStatement pst = connection.prepareStatement("SELECT * FROM parking.member NATURAL JOIN parking.user WHERE parking.member.user_id = ?;");
            pst.setString(1, userID);
            ResultSet rset = pst.executeQuery();

            if (rset.next()) {
                type = "member";
            } else {
                PreparedStatement pstUser = connection.prepareStatement("SELECT * FROM parking.user WHERE parking.user.user_id = ?;");
                pstUser.setString(1, userID);
                ResultSet userResult = pstUser.executeQuery();

                if (userResult.next()) {
                    type = "user";
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
                    makeProfileUpdateRequestScreen();
                    break;
                case "3":
                    makeReservationScreen();
                    break;
                case "4":
                    logout();
                    exit = true;
                    break;
                default:
                    System.out.println("\nInvalid option. Try again.");
            }
        }
    }

    public void viewProfileScreen() {
        try {
            if (type.equals("member")) {
                PreparedStatement pstMember = connection.prepareStatement("SELECT * FROM parking.user NATURAL JOIN parking.member, parking.parking_lot WHERE parking.member.user_id = ? AND parking.member.lot_id = parking.parking_lot.lot_id");
                pstMember.setString(1, userID);
                ResultSet memberResult = pstMember.executeQuery();
                
                if (memberResult.next()) {
                    printUserProfile(memberResult, type);
                }

                memberResult.close();
                pstMember.close();
            } else if (type.equals("user")) {
                    PreparedStatement pstUser = connection.prepareStatement("SELECT * FROM parking.user WHERE parking.user.user_id = ?");
                    pstUser.setString(1, userID);
                    ResultSet userResult = pstUser.executeQuery();
                    
                    if (userResult.next()) {
                        printUserProfile(userResult, type);
                    }

                    userResult.close();
                    pstUser.close();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printUserProfile(ResultSet rset, String type) {
        try {
            System.out.println("\nUser Profile");
            System.out.println(String.format("User ID: %s", rset.getString("user_id")));
            System.out.println(String.format("Name: %s", rset.getString("name")));
            System.out.println(String.format("Password: %s", rset.getString("password")));

            if (type.equals("member")) {
                PreparedStatement pst = connection.prepareStatement("DELETE FROM parking.temporary_license_plate WHERE parking.temporary_license_plate.time_created <= NOW() - INTERVAL \'1 DAY\'");
                pst.executeUpdate();

                pst = connection.prepareStatement("SELECT * FROM parking.temporary_license_plate WHERE parking.temporary_license_plate.user_id = ?");
                pst.setString(1, userID);

                ResultSet rsetTemp = pst.executeQuery();

                System.out.println(String.format("Registered license plate: %s", rset.getString("registered_license_plate")));
                if (rsetTemp.next()) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(rsetTemp.getTimestamp("time_created"));
                    c.add(Calendar.HOUR, 24);
                    Long timeRemain = c.getTime().getTime() - new Date().getTime();
                    System.out.println(String.format("Temporary license plate: %s (remain time: %s)", rsetTemp.getString("plate_number"), convertTime(timeRemain)));
                }
                System.out.println(String.format("Registered lot: %s", rset.getString("lot_id")));
                System.out.println(String.format("Registered spot: %d", rset.getInt("spot_id")));
                System.out.println(String.format("Membership fee: $%.2f", rset.getDouble("membership_fee")));

                rsetTemp.close();
                pst.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String profileUpdateRequestOptions() {
        System.out.println("\nUpdate Profile");
        System.out.println("1. Name\n2. Password");

        if (type.equals("member")) {
            System.out.println("3. Registered license plate\n4. Registered lot\n5. Registered spot\n6. Temporary license plate\n7. Exit");
        } else {
            System.out.println("3. Exit");
        }

        System.out.print("Enter option here: ");
        String option = sc.nextLine();
        return option;
    }

    public String getNewValue() {
        System.out.print("\nEnter new value here: ");
        String newValue = sc.nextLine();
        return newValue;
    }

    public void makeProfileUpdateRequestScreen() {
        boolean exit = false;

        while (!exit) {
            String option = profileUpdateRequestOptions();

            switch (option) {
                case "1":
                    String name = getNewValue();
                    if (name.length() <= 30) {
                        makeProfileUpdateRequest("name", name);
                    } else {
                        System.out.println("\nInvalid input (name has to have less than 30 characters). Try again.");
                    }
                    break;
                case "2":
                    String password = getNewValue();
                    if (password.length() <= 20) {
                        makeProfileUpdateRequest("password", password);
                    } else {
                        System.out.println("\nInvalid input (password has to have less than 20 characters). Try again.");
                        }
                    break;
                case "3":
                    if (type.equals("member")) {
                        String value = getNewValue();
                        if (value.length() == 7) {
                            makeProfileUpdateRequest("reg_license_plate", value);
                        } else {
                            System.out.println("\nInvalid input (license plate has 7 characters). Try again.");
                        }
                    } else {
                        System.out.println("\nGoing back to user menu...");
                        exit = true;
                    }
                    break;
                case "4":
                    if (type.equals("member")) {
                        String value = getNewValue();
                        boolean isValidLot = isValidLot(value);
                        if (isValidLot) {
                            makeProfileUpdateRequest("lot_id", value);
                        } else {
                            System.out.println("\nInvalid input (unavailable or invalid lot). Try again.");
                        }
                    } else {
                        System.out.println("\nInvalid option. Try again.");
                    }
                    break;
                case "5":
                    if (type.equals("member")) {
                        String value = getNewValue();
                        String lotId = "";
                        try {
                            PreparedStatement pst = connection.prepareStatement("SELECT * FROM parking.member WHERE parking.member.user_id = ?");
                            pst.setString(1, userID);
                            ResultSet rset = pst.executeQuery();

                            if (rset.next()) {
                                lotId = rset.getString("lot_id");
                            }
                            
                            rset.close();
                            pst.close();

                            boolean isValidSpot = isValidSpot(lotId, value);
                            if (isValidSpot) {
                                makeProfileUpdateRequest("spot_id", value);
                            } else {
                                System.out.println("\nInvalid input (unavailable or invalid spot). Try again.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("\nInvalid input (unavailable or invalid spot). Try again.");
                        }
                    } else {
                        System.out.println("\nInvalid option. Try again.");
                    }
                    break;
                case "6":
                    if (type.equals("member")) {
                        try {
                            PreparedStatement pst = connection.prepareStatement("DELETE FROM parking.temporary_license_plate WHERE parking.temporary_license_plate.time_created <= NOW() - INTERVAL \'1 Day\'");
                            pst.executeUpdate();

                            pst = connection.prepareStatement("SELECT * FROM parking.temporary_license_plate WHERE parking.temporary_license_plate.user_id = ?");
                            pst.setString(1, userID);

                            ResultSet rset = pst.executeQuery();

                            String value = getNewValue();
                            
                            if (value.length() == 7) {
                                if (rset.next()) {
                                    PreparedStatement pstUpdate = connection.prepareStatement("UPDATE parking.temporary_license_plate SET plate_number = ?, time_created = ? WHERE user_id = ?");
                                    pstUpdate.setString(1, value);
                                    pstUpdate.setTimestamp(2, new Timestamp(new Date().getTime()));
                                    pstUpdate.setString(3, userID);

                                    pstUpdate.executeUpdate();
                                    pstUpdate.close();

                                    makeProfileUpdateRequest("temp_license_plate", value);
                                } else {
                                    PreparedStatement pstCreate = connection.prepareStatement("INSERT INTO parking.temporary_license_plate (user_id, plate_number, time_created) VALUES (?,?,?)");
                                    pstCreate.setString(1, userID);
                                    pstCreate.setString(2, value);
                                    pstCreate.setTimestamp(3, new Timestamp(new Date().getTime()));

                                    pstCreate.executeUpdate();
                                    pstCreate.close();

                                    makeProfileUpdateRequest("temp_license_plate", value);
                                }
                            } else {
                                System.out.println("\nInvalid input (license plate has 7 characters). Try again.");
                            }

                            pst.close();
                            rset.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("\nInvalid input (unavailable or invalid spot). Try again.");
                    }
                    break;
                case "7":
                    if (type.equals("member")) {
                        System.out.println("\nGoing back to user menu...");
                        exit = true;
                    } else {
                        System.out.println("\nInvalid input (unavailable or invalid spot). Try again.");
                    }
                    break;
                default:
                    System.out.println("\nInvalid option. Try again.");
            }
        }
    }
    
    public void makeProfileUpdateRequest(String updateField, String newValue) {
        try {
            Date timeMade = new Date();
            PreparedStatement pst = connection.prepareStatement(String.format("INSERT INTO parking.update_form (id, time_made, field_to_update, new_value) VALUES (?,?,?,?)", updateField));
            pst.setString(1, userID);
            pst.setTimestamp(2, new Timestamp(timeMade.getTime()));
            pst.setString(3, updateField);
            pst.setString(4, newValue);
            pst.executeUpdate();

            System.out.println("\nMake profile update request successfully.");

            printProfileUpdateRequest(timeMade);

            pst.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printProfileUpdateRequest(Date timeMade) {
        try {
            PreparedStatement pst = connection.prepareStatement("SELECT * FROM parking.update_form WHERE parking.update_form.id = ? AND parking.update_form.time_made = ?");
            pst.setString(1, userID);
            pst.setTimestamp(2, new Timestamp(timeMade.getTime()));
            ResultSet rset = pst.executeQuery();

            if (rset.next()) {
                System.out.println("\nProfile Update Form");
                String updateField = "";

                switch (rset.getString("field_to_update")) {
                    case "name":
                        updateField = "Name";
                        break;
                    case "password":
                        updateField = "Password";
                        break;
                    case "reg_license_plate":
                        updateField = "Registered license plate";
                        break;
                    case "lot_id":
                        updateField = "Registered lot";
                        break;
                    case "spot_id":
                        updateField = "Registered spot";
                        break;
                    case "temp_license_plate":
                        updateField = "Temporary license plate";
                        break;
                    default:
                        break;
                }

                System.out.println(String.format("Time made: %s", rset.getTimestamp("time_made").toString()));
                System.out.println(String.format("Update field: %s", updateField));
                System.out.println(String.format("New value: %s", rset.getString("new_value")));
            }

            rset.close();
            pst.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
         _________________________________________
        /  Life is wonderful until you have       \
        \  to code in Java.                       /
         -----------------------------------------
                \   ^__^
                \   (oo)\_______
                    (__)\       )\/\
                        ||----w |
                        ||     ||
    */
    public HashMap<String, String> reservationMenu() {
        HashMap<String, String> reservationInfo = new HashMap<String, String>();
        ArrayList<String> validApplicationTypes = new ArrayList<String>(Arrays.asList("online", "member"));

        System.out.println("\nReservation");

        System.out.print("Enter type of reservation (online, member): ");
        String applicationType = sc.nextLine();

        try {
            PreparedStatement pst = connection.prepareStatement("SELECT * FROM parking.member WHERE parking.member.user_id = ?");
            pst.setString(1, userID);
            ResultSet rset = pst.executeQuery();

            if (validApplicationTypes.contains(applicationType)) {
                reservationInfo.put("application_type", applicationType);
                if (rset.next() && applicationType.equals("member")) {
                    System.out.print("Enter check-in (format yyyy-MM-dd hh:mm:ss): ");
                    String timeIn = sc.nextLine();
                    reservationInfo.put("reservation_time_in", timeIn);

                    System.out.print("Enter check-out (format yyyy-MM-dd hh:mm:ss): ");
                    String timeOut = sc.nextLine();
                    reservationInfo.put("reservation_time_out", timeOut);

                    reservationInfo.put("license_plate", rset.getString("registered_license_plate"));
                    reservationInfo.put("lot_id", rset.getString("lot_id"));
                    reservationInfo.put("spot_id", Integer.toString(rset.getInt("spot_id")));

                } else if (!applicationType.equals("member")) {
                    System.out.print("Enter time in (format yyyy-MM-dd hh:mm:ss): ");
                    String timeIn = sc.nextLine();
                    reservationInfo.put("reservation_time_in", timeIn);

                    System.out.print("Enter time out (format yyyy-MM-dd hh:mm:ss): ");
                    String timeOut = sc.nextLine();
                    reservationInfo.put("reservation_time_out", timeOut);

                    System.out.print("Enter your license plate: ");
                    String licensePlate = sc.nextLine();
                    reservationInfo.put("license_plate", licensePlate);

                    System.out.print(String.format("Enter lot %s: ", lotAndSpot.keySet().toString()));
                    String lotId = sc.nextLine();
                    reservationInfo.put("lot_id", lotId);

                    System.out.print("Enter spot: ");
                    String spotId = sc.nextLine();
                    reservationInfo.put("spot_id", spotId);

                } else if (!type.equals("member") && applicationType.equals("member")) {
                    System.out.println("\nInvalid application type. Try again.");
                    return null;
                }
            } else {
                System.out.println("\nInvalid application type. Try again.");
                return null;
            }

            rset.close();
            pst.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reservationInfo;
    }

    public void makeReservationScreen() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        HashMap<String, String> reservationInfo = reservationMenu();

        if (reservationInfo != null) {
            boolean isValidTimeIn = isDate(reservationInfo.get("reservation_time_in"));
            boolean isValidTimeOut = isDate(reservationInfo.get("reservation_time_out"));
            boolean isValidLicensePlate = false;
            if (reservationInfo.get("license_plate").length() == 7) {
                isValidLicensePlate = true;
            }
            boolean isValidLot = isValidLot(reservationInfo.get("lot_id"));
            boolean isValidSpot = isValidSpot(reservationInfo.get("lot_id"), reservationInfo.get("spot_id"));

            if (isValidTimeIn && isValidTimeOut && isValidLicensePlate && isValidLot && isValidSpot) {
                try {
                    Date timeIn = format.parse(reservationInfo.get("reservation_time_in"));
                    Date timeOut = format.parse(reservationInfo.get("reservation_time_out"));

                    if (!timeIn.equals(timeOut) || timeIn.before(timeOut)) {
                        makeReservation(reservationInfo.get("application_type"), timeIn, timeOut, reservationInfo.get("license_plate"), reservationInfo.get("lot_id"), reservationInfo.get("spot_id"));
                    } else {
                        System.out.println("\nInvalid time in and time out. Try again.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("\nInvalid inputs. Try again.");
            }
        }
    }

    public void makeReservation(String applicationType, Date timeIn, Date timeOut, String licensePlate, String lotId, String spotId) {
        boolean isValid = true;
        Date timeCreated = new Date();
        try {
            PreparedStatement pst = connection.prepareStatement("SELECT * FROM parking.reservation WHERE parking.reservation.lot_id = ? AND parking.reservation.spot_id = ?");
            pst.setString(1, lotId);
            pst.setInt(2, Integer.parseInt(spotId));
            ResultSet rset = pst.executeQuery();

            while (rset.next()) {
                Date tempTimeIn = new Date(rset.getTimestamp("reservation_time_in").getTime());
                Date tempTimeOut = new Date(rset.getTimestamp("reservation_time_out").getTime());

                if (tempTimeIn.equals(timeIn) || tempTimeOut.equals(timeOut) || (tempTimeIn.after(timeIn) && tempTimeIn.before(timeOut)) || (tempTimeOut.before(timeOut) && tempTimeOut.after(timeIn))) {
                    isValid = false;
                    System.out.println("\nTime slot was taken. Try again.");
                    break;
                }
            }

            if (isValid || applicationType.equals("member")) {
                PreparedStatement pstReservation = connection.prepareStatement("INSERT INTO parking.reservation (user_id, time_created, reservation_time_in, reservation_time_out, license_plate, application_type, employee_id, lot_id, spot_id) VALUES (?,?,?,?,?,?,?,?,?)");
                pstReservation.setString(1, userID);
                pstReservation.setTimestamp(2, new Timestamp(timeCreated.getTime()));
                pstReservation.setTimestamp(3, new Timestamp(timeIn.getTime()));
                pstReservation.setTimestamp(4, new Timestamp(timeOut.getTime()));
                pstReservation.setString(5, licensePlate);
                pstReservation.setString(6, applicationType);
                pstReservation.setString(7, null);
                pstReservation.setString(8, lotId);
                pstReservation.setInt(9, Integer.parseInt(spotId));

                pstReservation.executeUpdate();

                pstReservation.close();

                System.out.println("\nMake reservation successfully.");

                printReservationInfo(timeCreated);
            }

            rset.close();
            pst.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printReservationInfo(Date timeCreated) {
        try {
            PreparedStatement pst = connection.prepareStatement("SELECT * FROM parking.reservation, parking.parking_lot WHERE parking.parking_lot.lot_id = parking.reservation.lot_id AND parking.reservation.user_id = ? AND parking.reservation.time_created = ?");
            pst.setString(1, userID);
            pst.setTimestamp(2, new Timestamp(timeCreated.getTime()));

            ResultSet rset = pst.executeQuery();

            while (rset.next()) {
                System.out.println("\nReservation Information");
                System.out.println(String.format("Time made: %s", rset.getTimestamp("time_created").toString()));
                System.out.println(String.format("User: %s", userID));
                System.out.println(String.format("License plate: %s", rset.getString("license_plate")));
                System.out.println(String.format("Check in time: %s", rset.getTimestamp("reservation_time_in").toString()));
                System.out.println(String.format("Check out time: %s", rset.getTimestamp("reservation_time_out").toString()));
                System.out.println(String.format("Type: %s", rset.getString("application_type")));
                System.out.println(String.format("Lot: %s", rset.getString("lot_id")));
                System.out.println(String.format("Spot: %d", rset.getInt("spot_id")));


                if (rset.getString("application_type").equals("member")) {
                    System.out.println(String.format("Fee: $0.00 (membership fee: $%.2f)", rset.getDouble("membership_fee")));
                } else {
                    System.out.println(String.format("Fee: $%.2f", rset.getDouble("guest_fee")));
                }
            }

            rset.close();
            pst.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        try {
            PreparedStatement pst = connection.prepareStatement("UPDATE parking.user SET login_time = ?, logout_time = ? WHERE user_id = ?");
            pst.setTimestamp(1, new Timestamp(loginTime.getTime()));
            pst.setTimestamp(2, new Timestamp(new Date().getTime()));
            pst.setString(3, userID);
            pst.executeUpdate();

            System.out.println("\nLogging out...");

            pst.close();

            new Login(dbName, dbUserName, dbPassword);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
