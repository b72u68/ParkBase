import java.util.Date;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

class UserMenu {
    private DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSSSS");
    private Scanner scanner = new Scanner(System.in);
    private Connection connection;
    private String userID;
    private Date loginTime;
    private Date logoutTime;

    public UserMenu(Connection connection, String userID) {
        this.connection = connection;
        this.userID = userID;
    }

    public UserMenu(Connection connection) {
        this.connection = connection;
    }

    public UserMenu(String userID) {
        this.userID = userID;
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

    private void setLogoutTime(Date logoutTime) {
        this.logoutTime = logoutTime;
    }

    public String userUpdateMenu() {
        System.out.println("\nChoose Update Field");
        System.out.println("1. Name\n2.Password\n3.Exit");
        System.out.print("Enter your option: ");

        String option = scanner.nextLine();

        return option;
    }

    public String memberUpdateMenu() {
        System.out.println("\nChoose Update Field");
        System.out.println("1. Name\n2. Password\n3. Parking Lot\n4. Parking Spot\n5. Exit");
        System.out.print("Enter your option: ");
        String option = scanner.nextLine();

        return option;
    }

    public void requestUpdate() {
        boolean exit = false;
        boolean isMember = false;
        String[] updateFieldOptions = new String[] {"name", "password", "lot_id", "spot_id"};
        String updateField = "";

        if (userID != null && userID.strip() != "") {
            try {
                ArrayList<String> members = new ArrayList<String>();

                String checkMembership = String.format("select * from parking.member", userID);
                Statement stmt = connection.createStatement();
                ResultSet rset = stmt.executeQuery(checkMembership);

                while (rset.next()) {
                    members.add(rset.getString("user_id"));
                }

                isMember = members.contains(userID);

            } catch (SQLException e) {
                Logger lgr = Logger.getLogger(UserMenu.class.getName());
                lgr.log(Level.SEVERE, e.getMessage(), e);

                exit = true;
            }

            while (!exit) {
                String option = "";
                if (isMember) {
                    option = memberUpdateMenu();
                } else {
                    option = userUpdateMenu();
                }

                switch (option) {
                    case "1":
                        updateField = updateFieldOptions[Integer.parseInt(option) - 1];
                        // get name and check its value
                        break;
                    case "2":
                        updateField = updateFieldOptions[Integer.parseInt(option) - 1];
                        // get password and check its value
                        break;
                    case "3":
                        if (isMember) {
                            updateField = updateFieldOptions[Integer.parseInt(option) - 1];
                            // get lot id and check its value
                        } else {
                            exit = true;
                        }
                        break;
                    case "4":
                        if (isMember) {
                            updateField = updateFieldOptions[Integer.parseInt(option) - 1];
                            // get spot id and check its value
                        } else {
                            System.out.println("Invalid option.");
                        }
                        break;
                    case "5":
                        if (isMember) {
                            exit = true;
                        } else {
                            System.out.println("Invalid option.");
                        }
                        break;
                    default:
                        System.out.println("Invalid option.");
                        break;
                }
            }
        }
    }
}
