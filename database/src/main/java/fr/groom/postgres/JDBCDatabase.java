package fr.groom.postgres;

import java.sql.*;
import java.util.Properties;

public class JDBCDatabase {
	private static String url = "jdbc:postgresql://localhost/android_permissions_2";
	private static Connection connection;
	private static Properties props;

	static {
		props = new Properties();
		props.setProperty("user", "lgitzing");
		try {
			connection = DriverManager.getConnection(url, props);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static Connection createConnection() {
		try {
			connection = DriverManager.getConnection(url, props);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}

	public static Connection getConnection() {
		if (connection == null) {
			connection = createConnection();
		}
		try {
			if (connection.isClosed()) {
				connection = createConnection();
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return connection;
	}
	//
	public static void main(String[] args) {
		JDBCDatabase database = new JDBCDatabase();
		Connection connection =
				database.getConnection();
		String sql =
				"SELECT method.method_name, method.method_arguments, method.method_class, method.return_type FROM method " +
						"INNER JOIN method_found_in_api ON method.id = method_found_in_api.method_id " +
						"INNER JOIN api on method_found_in_api.api_id = api.id " +
						"WHERE api.api_level >= 27;";
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//			preparedStatement.setString(1, sensorType);
			try (ResultSet rs = preparedStatement.executeQuery()) {
				while (rs.next()) {
					String methodName = rs.getString("method_name");
//					date = rs.getTimestamp("submission_date").getTime();
//					data = rs.getDouble("sensor_value");
//					result = new DataRecord(data, date, "sensor", sensorType);
					System.out.println(methodName);
				}
				rs.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			preparedStatement.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

}

