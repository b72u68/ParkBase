
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
 
public class profileJTable {

	@SuppressWarnings("unused")
	private final DefaultTableModel tableModel = new DefaultTableModel();

	public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {

		ResultSetMetaData metaData = rs.getMetaData();

		// names of columns
		Vector<String> columnNames = new Vector<String>();
		int columnCount = metaData.getColumnCount();
		columnNames.add("Field");
		columnNames.add("Value");
		ArrayList<String> colNames = new ArrayList<String>();
		for (int column = 1; column <= columnCount; column++) {
			colNames.add(metaData.getColumnName(column));
			System.out.println(colNames.get(column-1));
		}
		rs.next();
		// data of the table
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
			Vector<Object> vector = new Vector<Object>();
			vector.add(colNames.get(columnIndex-1));
			
			vector.add(rs.getString(columnIndex));
			data.add(vector);
		}
		// return data/col.names for JTable
		return new DefaultTableModel(data, columnNames); 

	}

}
