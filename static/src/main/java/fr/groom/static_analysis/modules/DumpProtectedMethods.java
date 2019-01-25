package fr.groom.static_analysis.modules;

import fr.groom.Main;
import fr.groom.postgres.JDBCDatabase;
import fr.groom.static_analysis.StaticAnalysis;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

import java.sql.*;
import java.util.*;

/**
 * This module dumps all the methods that are protected by an android permission in the given apk
 */
public class DumpProtectedMethods extends Module<List<String>> implements IModule {
	private String storageField = "protected_methods";
	//	DatabaseConnection databaseConnection;
	int minApiLevel;
	Set<String> applicationPermissions;
	Set<String> targetMethods = new HashSet<>();
	private String[] protectedMethodPatterns = {"com.ti.", "android.", "com.android."};
	Connection databaseConnection;

//	public DumpProtectedMethods(StaticAnalysis staticAnalysis) {
//		super(new ArrayList<>(), ModuleType.UNITLEVEL, staticAnalysis);
//		DatabaseConfiguration dbConfig = Configuration.v().getDatabaseConfiguration();
//		boolean auth = dbConfig.getAuthenticationConfiguration().isPerformAuthentication();
//		this.databaseConnection = new DatabaseConnection();
//		databaseConnection.configure(
//				dbConfig.getUrl(),
//				dbConfig.getPort(),
//				"excavator_data",
//				auth ? dbConfig.getAuthenticationConfiguration().getUsername() : null,
//				auth ? dbConfig.getAuthenticationConfiguration().getPassword() : null,
//				auth ? dbConfig.getAuthenticationConfiguration().getAuthSourceDatabaseName() : null
//		);
//		databaseConnection.connection();
//		this.minApiLevel = this.app.getMinAPILevel();
//		this.applicationPermissions = this.app.getPermissions();
//	}

	public DumpProtectedMethods(StaticAnalysis staticAnalysis) {
		super(new ArrayList<>(), ModuleType.UNITLEVEL, staticAnalysis);
		databaseConnection = JDBCDatabase.getConnection();
		String sql =
				"SELECT method.method_name, method.method_arguments, method.method_class, method.return_type FROM method " +
						"INNER JOIN method_found_in_api ON method.id = method_found_in_api.method_id " +
						"INNER JOIN api on method_found_in_api.api_id = api.id " +
						"WHERE api.api_level >= " + app.getMinAPILevel() + ";";
		try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(sql)) {
//			preparedStatement.setString(1, sensorType);
			try (ResultSet rs = preparedStatement.executeQuery()) {
				while (rs.next()) {
					String methodName = rs.getString("method_name");
					String methodClass = rs.getString("method_class");
					Array methodArguments = rs.getArray("method_arguments");
					String returnType = rs.getString("return_type");
					SootClass sootClass = Scene.v().getSootClass(methodClass);
					ResultSet argumentSet = methodArguments.getResultSet();
					String[] nullable = (String[]) methodArguments.getArray();
					//<android.location.LocationManager: void setTestProviderLocation(java.lang.String,android.location.Location)> (LOCATION_INFORMATION)

					String signature = "<" + methodClass + ": " + returnType + " " + methodName + "(" + String.join(",", nullable) + ")>";
					targetMethods.add(signature);
//					try {
//
//						SootMethod sootMethod = Scene.v().getMethod(signature);
//						System.out.println(signature);
//					} catch (RuntimeException e) {
//						System.out.println("method not found: " + signature);
//					}
				}
				rs.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void executeModule(SootClass sootClass, SootMethod sootMethod, Unit unit) {
		Stmt stmt = (Stmt) unit;
		if (stmt.containsInvokeExpr()) {
			InvokeExpr invokeExpr = stmt.getInvokeExpr();
			SootMethod invokedMethod = invokeExpr.getMethod();
			if (Arrays.stream(protectedMethodPatterns).anyMatch(p -> invokedMethod.getDeclaringClass().getName().startsWith(p))) {
				this.resultHandler(invokedMethod.getSignature());
			}
		}
	}

	@Override
	public void processResults() {

	}

	@Override
	public void saveResults() {
		JSONObject field = new JSONObject();
		JSONObject dataUpdate = new JSONObject();
		JSONArray protectedMethods = new JSONArray(this.data);
		field.put(this.storageField, protectedMethods);
		dataUpdate.put("$set", field);
		JSONObject condition = new JSONObject();
		condition.put("sha256", this.staticAnalysis.getApp().getSha256());
		this.storage.update(condition, dataUpdate, Main.STATIC_COLLECTION);
	}

	@Override
	public void resultHandler(Object result) {
		String signature = (String) result;
		if (targetMethods.contains(signature)) {
			this.data.add(signature);
		}
	}
//
	//	@Override
//	public void resultHandler(Object result) {
//		for (int i = minApiLevel; i <= Application.MAX_API_LEVEL; i++) {
//			StringBuilder collectionName = new StringBuilder("mappings_");
//			collectionName.append(i);
//			MongoCollection mappingCollection = databaseConnection.getDatabase().getCollection(collectionName.toString());
//			Document filter = new Document("_id", result);
//			Object queryResult = mappingCollection.find(filter).first();
//			if (queryResult != null) {
//				Document parsedResult = (Document) queryResult;
//				Document protectedMethod = new Document();
//				protectedMethod.append("method_signature", result);
//				protectedMethod.append("api_level", i);
//
//				ArrayList<String> permissions = (ArrayList<String>) ((Document) queryResult).get("permissions");
//				ArrayList<Document> permissionResults = new ArrayList<>();
//				for (String permission : permissions) {
//					StringBuilder permissionCollectionName = new StringBuilder("permission_");
//					permissionCollectionName.append(i);
//					MongoCollection permissionCollection = databaseConnection.getDatabase().getCollection(permissionCollectionName.toString());
//					Document permissionFilter = new Document("_id", permission);
//					Object permissionQueryResult = permissionCollection.find(permissionFilter).first();
//					if (permissionQueryResult != null) {
//						Document permissionObject = (Document) permissionQueryResult;
//						String protectionLevel = permissionObject.getString("protectionLevel");
//						boolean violation = !applicationPermissions.contains(permission);
//						Document permissionResult = new Document();
//						permissionResult.append("name", permission);
//						permissionResult.append("protectionLevel", protectionLevel);
//						permissionResult.append("violation", violation);
//						permissionResults.add(permissionResult);
//					}
//				}
//				protectedMethod.append("permissions", permissionResults);
//				this.data.add(protectedMethod);
//			}
//		}
//	}

	@Override
	public void onFinish() {
		saveResults();
//
	}

}
