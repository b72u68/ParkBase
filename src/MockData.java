import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MockData {
    private Connection connection;

    public MockData(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

	public void insertUserData() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        File userFile = new File("data/user.csv");
		try {
			Scanner userData = new Scanner(userFile);
            userData.nextLine();

            while (userData.hasNextLine()) {
                PreparedStatement pStmt = connection.prepareStatement("insert into parking.user (user_id, name, password, login_time, logout_time) values (?,?,?,?,?)");

                String data = userData.nextLine();
                String[] dataList = data.split(",");

                String userID = dataList[0];
                String name = dataList[1];
                String password = dataList[2];
                Timestamp loginTime = new Timestamp(format.parse(dataList[3]).getTime());
                Timestamp logoutTime = new Timestamp(format.parse(dataList[4]).getTime());

                pStmt.setString(1, userID);
                pStmt.setString(2, name);
                pStmt.setString(3, password);
                pStmt.setTimestamp(4, loginTime);
                pStmt.setTimestamp(5, logoutTime);
                pStmt.executeUpdate();

                Statement st = connection.createStatement();
                String drop = "DROP USER IF EXISTS u_" + userID + ";";
                String create = "CREATE USER u_" + userID;

                String grant = "GRANT r_user TO u_" + userID;
                st.executeUpdate(drop);
                st.executeUpdate(create);
                st.executeUpdate(grant);

                st.close();
                pStmt.close();
            }

            System.out.println("Insert user data successfully!");
            userData.close();

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Initializer.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (ParseException e) {
            System.out.println("Error: could not parse timestamp data.");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
			System.out.println("Error: could not find file.");
			e.printStackTrace();
		}
	}

	public void insertMemberData () {
        File memberFile = new File("data/member.csv");
		try {
			Scanner memberData = new Scanner(memberFile);
            memberData.nextLine();

            while (memberData.hasNextLine()) {
                PreparedStatement pStmt = connection.prepareStatement("insert into parking.member (user_id, registered_license_plate, lot_id, spot_id) values (?,?,?,?)");

                String data = memberData.nextLine();
                String[] dataList = data.split(",");

                String userID = dataList[0];
                String plate = dataList[1];
                String lotID = dataList[2];
                int spotID = Integer.parseInt(dataList[3]);

                pStmt.setString(1, userID);
                pStmt.setString(2, plate);
                pStmt.setString(3, lotID);
                pStmt.setInt(4, spotID);
                pStmt.executeUpdate();

                Statement st = connection.createStatement();
                String grant = "GRANT member TO u_" + userID;
                st.executeUpdate(grant);

                st.close();
                pStmt.close();
            }

            System.out.println("Insert member data successfully!");
            memberData.close();

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Initializer.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (FileNotFoundException e) {
			System.out.println("Error: could not find file.");
			e.printStackTrace();
		}
	}

	public void insertLotData () {
        File lotFile = new File("data/parking_lot.csv");
		try {
			Scanner lotData = new Scanner(lotFile);
            lotData.nextLine();

            while (lotData.hasNextLine()) {
                PreparedStatement pStmt = connection.prepareStatement("insert into parking.parking_lot (lot_id, guest_fee, membership_fee, upkeep_cost) values (?,?,?,?)");

                String data = lotData.nextLine();
                String[] dataList = data.split(",");

                String lotID = dataList[0];
                double guestFee = Double.parseDouble(dataList[1]);
                double membershipFee = Double.parseDouble(dataList[2]);
                double upkeepCost = Double.parseDouble(dataList[3]);

                pStmt.setString(1, lotID);
                pStmt.setDouble(2, guestFee);
                pStmt.setDouble(3, membershipFee);
                pStmt.setDouble(4, upkeepCost);
                pStmt.executeUpdate();

                pStmt.close();
            }

            System.out.println("Insert lot data successfully!");
            lotData.close();

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Initializer.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (FileNotFoundException e) {
			System.out.println("Error: could not find file.");
			e.printStackTrace();
		}
	}

	public void insertSpotData () {
        File spotFile = new File("data/spot.csv");
		try {
			Scanner spotData = new Scanner(spotFile);
            spotData.nextLine();

            while (spotData.hasNextLine()) {
                PreparedStatement pStmt = connection.prepareStatement("insert into parking.spot (spot_id, lot_id) values (?,?)");

                String data = spotData.nextLine();
                String[] dataList = data.split(",");

                int spotID = Integer.parseInt(dataList[0]);
                String lotID = dataList[1];

                pStmt.setInt(1, spotID);
                pStmt.setString(2, lotID);

                pStmt.executeUpdate();
                pStmt.close();
            }

            System.out.println("Insert spot data successfully!");
            spotData.close();

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Initializer.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (FileNotFoundException e) {
			System.out.println("Error: could not find file.");
			e.printStackTrace();
		}
	}

	public void insertEmployeeData () {
        File employeeFile = new File("data/employee.csv");
		try {
			Scanner employeeData = new Scanner(employeeFile);
            employeeData.nextLine();

            while (employeeData.hasNextLine()) {
                PreparedStatement pStmt = connection.prepareStatement("insert into parking.employee (employee_id, name, password, salary, type) values (?,?,?,?,?)");

                String data = employeeData.nextLine();
                String[] dataList = data.split(",");

                String employeeID = dataList[0];
                String name = dataList[1];
                String password = dataList[2];
                double salary = Double.parseDouble(dataList[3]);
                String type = dataList[4];

                pStmt.setString(1, employeeID);
                pStmt.setString(2, name);
                pStmt.setString(3, password);
                pStmt.setDouble(4, salary);
                pStmt.setString(5, type);
                pStmt.executeUpdate();

                Statement st = connection.createStatement();
                String drop = "DROP USER IF EXISTS u_" + employeeID + ";";
                String create = "CREATE USER u_" + employeeID;

                String grant = "GRANT staff TO u_" + employeeID;
                st.executeUpdate(drop);
                st.executeUpdate(create);
                st.executeUpdate(grant);

                st.close();
                pStmt.close();
            }

            System.out.println("Insert employee data successfully!");
            employeeData.close();

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Initializer.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (FileNotFoundException e) {
			System.out.println("Error: could not find file.");
			e.printStackTrace();
		}
	}
	
	public void insertAdminData () {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        File adminFile = new File("data/admin.csv");
		try {
			Scanner adminData = new Scanner(adminFile);
            adminData.nextLine();

            while (adminData.hasNextLine()) {
                PreparedStatement pStmt = connection.prepareStatement("insert into parking.admin (admin_id, name, password, login_time, logout_time) values (?,?,?,?,?)");

                String data = adminData.nextLine();
                String[] dataList = data.split(",");

                String adminID = dataList[0];
                String name = dataList[1];
                String password = dataList[2];
                Timestamp loginTime = null;
                Timestamp logoutTime = null;
				try {
					loginTime = new Timestamp(format.parse(dataList[3]).getTime());
					logoutTime = new Timestamp(format.parse(dataList[4]).getTime());
				} catch (ParseException e) {
					System.out.println("Error: could not read time");
					e.printStackTrace();
				}

                pStmt.setString(1, adminID);
                pStmt.setString(2, name);
                pStmt.setString(3, password);
                pStmt.setTimestamp(4, loginTime);
                pStmt.setTimestamp(5, logoutTime);
                pStmt.executeUpdate();
                
                Statement stmt = connection.createStatement();
                String drop = "DROP USER IF EXISTS u_" + adminID + ";";
                String create = "CREATE USER u_" + adminID + ";";
                String grant = "GRANT admin TO u_" + adminID + ";";
                stmt.executeUpdate(drop);
                stmt.executeUpdate(create);
                stmt.executeUpdate(grant);
                stmt.close();
                pStmt.close();
            }

            System.out.println("Insert admin data successfully!");
            adminData.close();

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Initializer.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (FileNotFoundException e) {
			System.out.println("Error: could not find file.");
			e.printStackTrace();
		}
	}

	public void insertTemporaryPlateData () {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        File plateFile = new File("data/temporary_license_plate.csv");
		try {
			Scanner plateData = new Scanner(plateFile);
            plateData.nextLine();

            while (plateData.hasNextLine()) {
                PreparedStatement pStmt = connection.prepareStatement("insert into parking.temporary_license_plate (user_id, plate_number, time_created) values (?,?,?)");

                String data = plateData.nextLine();
                String[] dataList = data.split(",");

                String userID = dataList[0];
                String plateNumber = dataList[1];
                Timestamp timeCreated = new Timestamp(format.parse(dataList[2]).getTime());

                pStmt.setString(1, userID);
                pStmt.setString(2, plateNumber);
                pStmt.setTimestamp(3, timeCreated);

                pStmt.executeUpdate();
                pStmt.close();
            }

            System.out.println("Insert temporary license plate data successfully!");
            plateData.close();

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Initializer.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (FileNotFoundException e) {
			System.out.println("Error: could not find file.");
			e.printStackTrace();
		} catch (ParseException e) {
			System.out.println("Error: Could not parse timestamp data.");
			e.printStackTrace();
		}
	}

	public void insertUpdateFormData () {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        File formFile = new File("data/update_form.csv");
		try {
			Scanner formData = new Scanner(formFile);
            formData.nextLine();

            while (formData.hasNextLine()) {
                PreparedStatement pStmt = connection.prepareStatement("insert into parking.update_form (user_id, time_made, field_to_update, new_value) values (?,?,?,?)");

                String data = formData.nextLine();
                String[] dataList = data.split(",");

                String userID = dataList[0];
                Timestamp timeMade = new Timestamp(format.parse(dataList[1]).getTime());
                String updateField = dataList[2];
                String newValue = dataList[3];

                pStmt.setString(1, userID);
                pStmt.setTimestamp(2, timeMade);
                pStmt.setString(3, updateField);
                pStmt.setString(4, newValue);

                pStmt.executeUpdate();
                pStmt.close();
            }

            System.out.println("Insert update form data successfully!");
            formData.close();

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Initializer.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (FileNotFoundException e) {
			System.out.println("Error: could not find file.");
			e.printStackTrace();
		} catch (ParseException e) {
			System.out.println("Error: Could not parse timestamp data.");
			e.printStackTrace();
		}
	}

	public void insertReservationData () {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        File reservationFile = new File("data/reservation.csv");
		try {
			Scanner reservationData = new Scanner(reservationFile);
            reservationData.nextLine();

            while (reservationData.hasNextLine()) {
                PreparedStatement pStmt = connection.prepareStatement("insert into parking.reservation (user_id, time_created, reservation_time_in, reservation_time_out, license_plate, application_type, employee_id, lot_id, spot_id) values (?,?,?,?,?,?,?,?,?)");
                String data = reservationData.nextLine();
                String[] dataList = data.split(",");
                String userID = dataList[0];
                Timestamp timeCreated = new Timestamp(format.parse(dataList[1]).getTime());
                Timestamp timeIn = new Timestamp(format.parse(dataList[2]).getTime());
                Timestamp timeOut = new Timestamp(format.parse(dataList[3]).getTime());
                String plate = dataList[4];
                String applicationType = dataList[5];
                String employeeID = dataList[6];
                String lotID = dataList[7];
                int spotID = Integer.parseInt(dataList[8]);

                pStmt.setString(1, userID);
                pStmt.setTimestamp(2, timeCreated);
                pStmt.setTimestamp(3, timeIn);
                pStmt.setTimestamp(4, timeOut);
                pStmt.setString(5, plate);
                pStmt.setString(6, applicationType);
                pStmt.setString(7, employeeID);
                pStmt.setString(8, lotID);
                pStmt.setInt(9, spotID);

                pStmt.executeUpdate();
                pStmt.close();
            }

            System.out.println("Insert reservation data successfully!");
            reservationData.close();

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Initializer.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (FileNotFoundException e) {
			System.out.println("Error: could not find file.");
			e.printStackTrace();
		} catch (ParseException e) {
			System.out.println("Error: Could not parse timestamp data.");
			e.printStackTrace();
		}
	}

    public void insertAllData() {
        insertLotData();
        insertSpotData();
        insertUserData();
        insertMemberData();
        insertEmployeeData();
        insertAdminData();
        insertTemporaryPlateData();
        insertUpdateFormData();
        insertReservationData();
    }
}
