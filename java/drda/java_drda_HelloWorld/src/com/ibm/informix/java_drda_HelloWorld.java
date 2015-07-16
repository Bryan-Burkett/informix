/*-
 * Java Sample Application: Connection to Informix using DRDA
 */

/*-
 * Topics
 * 1 Create table
 * 2 Inserts
 * 2.1 Insert a single document into a table
 * 2.2 Insert multiple documents into a table
 * 3 Queries
 * 3.1 Find one document in a table that matches a query condition
 * 3.2 Find documents in a table that match a query condition
 * 3.3 Find all documents in a table
 * 4 Update documents in a table
 * 5 Delete documents in a table
 * 6 Drop a table
 */
package com.ibm.informix;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.ibm.db2.jcc.DB2Driver;

public class java_drda_HelloWorld {
	
	public static String DRDAURL;
	public static List<String> everything = new ArrayList<String>();
	
	
    public static void main(String[] args) {
    	
//    	if (args[0] != null)
//    		DRDAURL = args[0];
//    	else	
//    	
    	
        doEverything();
        
        //print log
        for (String s : everything)
        	System.out.println(s);
        
    }

    public static List<String> doEverything() {
        
        try {
        	
        	parseVcap();
        	//initialize some variables
        	String tableName = "sqlitest3";
        	String sql = "";
            PreparedStatement statement = null;
            Properties prop = new Properties();
            Connection conn;
            //<------------------------------------->
            
            //connect to database
            conn = new DB2Driver().connect(DRDAURL, prop);
            if (conn != null)
                everything.add("Connected to: " + DRDAURL);
            //<------------------------------------->
            
            everything.add("\nTopics");
            
            
            // 1 Create table
            everything.add("\n1 Create table");
            
            sql = "create table if not exists " + tableName + " (id varchar(255),  value integer)";
            statement = conn.prepareStatement(sql);
            statement.executeUpdate();
            
            everything.add("\tCreate a table named: " + tableName);
            everything.add("\tCreate Table SQL: " + sql);
            //<------------------------------------->
            
            // 2 Inserts
            everything.add("\n2 Inserts");
            
            
            // 2.1 Insert a single document into a table
            everything.add("2.1 Insert a single document into a table");
            
            DataFormat singleInsert = new DataFormat("TestId000", 4);
            sql = "insert into " + tableName + " values(?,?)";
            statement = conn.prepareStatement(sql);
            statement.setString(1, singleInsert.id);
            statement.setInt(2, singleInsert.value);
            statement.executeUpdate();
            statement.close();
            
            everything.add("\tCreate document -> " + singleInsert.toString());
            everything.add("\tSingle Insert SQL: " + sql);
            //<------------------------------------->
            
            //2.2 Insert multiple documents into a table
            everything.add("\n2.2 Insert multiple documents into a table");
            
            List<DataFormat> listOfData = new ArrayList<DataFormat>();
            listOfData.add(new DataFormat("TestId000", 0));
            listOfData.add(new DataFormat("TestId001", 1));
            listOfData.add(new DataFormat("TestId010", 2));
            listOfData.add(new DataFormat("TestId011", 3));
            sql = "insert into " + tableName + " values(?,?)";
            statement = conn.prepareStatement(sql);
            for (DataFormat data : listOfData) {
                statement.setString(1, data.id);
                statement.setInt(2, data.value);
                statement.addBatch();	}
            statement.executeBatch();
            statement.close();
            
            for (DataFormat dataElement : listOfData)
                everything.add("\tCreate Document -> " + dataElement.toString());
            everything.add("\tMultiple Insert SQL: " + sql + " (executed as batch)");
            //<------------------------------------->
            
            //3 Queries
            everything.add("\n3 Queries");
            
            
            //3.1 Find one document in a table that matches a query condition
            everything.add("3.1 Find one document in a table that matches a query condition");
            
            String nameToFind = "TestId000";
            List<DataFormat> dataWithName = new ArrayList<DataFormat>();
            sql = "select * from " + tableName + " where id LIKE '" + nameToFind.trim() + "'";
            statement = conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            while (rs.next())
                dataWithName.add(new DataFormat(rs.getString(1), rs.getInt(2)));
            DataFormat firstDocument = dataWithName.get(0);
            statement.close();
            
            everything.add("\tFind document with ID: " + nameToFind);
            everything.add("\tFirst document with ID -> " + firstDocument.toString());
            everything.add("\tQuery By ID SQL: " + sql);
            //<------------------------------------->
            
            //3.2 Find documents in a table that match a query condition
            everything.add("\n3.2 Find documents in a table that match a query condition");
            
            String nameToFindAll = "TestId000";
            List<DataFormat> dataWithNameAll = new ArrayList<DataFormat>();
            sql = "select * from " + tableName + " where id LIKE '" + nameToFindAll.trim() + "'";
            statement = conn.prepareStatement(sql);
            ResultSet rsAll = statement.executeQuery();
            while (rsAll.next())
                dataWithNameAll.add(new DataFormat(rsAll.getString(1), rsAll.getInt(2)));
            statement.close();
            
            everything.add("\tFind all documents with ID: " + nameToFindAll);
            for (DataFormat foundData : dataWithNameAll)
                everything.add("\tFound Document -> " + foundData.toString());
            
            everything.add("\tQuery All By ID SQL: select * from " + tableName + " where id LIKE '" + nameToFindAll.trim() + "'");
            //<------------------------------------->
            
            //3.3 Find all documents in a table
            everything.add("\n3.3 Find all documents in a table");
            
            List<DataFormat> everythingInTable = new ArrayList<DataFormat>();
            sql = "select * from " + tableName;
            statement = conn.prepareStatement(sql);
            rs = statement.executeQuery();
            while (rs.next())
                everythingInTable.add(new DataFormat(rs.getString(1), rs.getInt("value")));
            statement.close();
            
            everything.add("\tFind all documents in table: " + tableName);
            for (DataFormat elementInTable : everythingInTable)
                everything.add("\tFound document -> " + elementInTable.toString());
            everything.add("\tFind All Documents SQL: " + sql);
            //<------------------------------------->
            
            //4 Update documents in a table
            everything.add("\n4 Update documents in a table");
            
            String nameToUpdate = "TestId010";
            int updatedValue = 6;
            statement.close();
            sql = "update " + tableName + " set value = ? where id  = ?";
            statement = conn.prepareStatement(sql);
            statement.setInt(1, updatedValue);
            statement.setString(2, nameToUpdate);
            statement.executeUpdate();
            statement.close();
            
            everything.add("\tDocument to update: " + nameToUpdate);
            everything.add("\tUpdate By ID SQL: " + sql);
            //<------------------------------------->
            
            //5 Delete documents in a table
            everything.add("\n5 Delete documents in a table");
            
            String nameToDelete = "TestId001";
            sql = "delete from " + tableName + " where id like '" + nameToDelete.trim() + "'";
            statement = conn.prepareStatement(sql);
            statement.executeUpdate();
            statement.close();
            
            everything.add("\tDelete documents with ID: " + nameToDelete);
            everything.add("\tDelete By ID SQL: " + sql);
            //<------------------------------------->
            
            //6 Drop a table
            everything.add("\n6 Drop a table");
            
            sql = "drop table " + tableName;
            statement = conn.prepareStatement(sql);
            statement.executeUpdate();
            statement.close();
            
            everything.add("\tDrop table: " + tableName);
            everything.add("\tDrop Table SQL: " + sql);
            //<------------------------------------->
            
            //Complete
            everything.add("\nComplete!");

        } catch (Exception e) {
            System.err.println("[ERROR] "
                    + (e instanceof SQLException ? " Error Code : "
                            + ((SQLException) e).getErrorCode() : "")
                    + " Message : " + e.getMessage());
            e.printStackTrace();
        }
        return everything;
    }
    
	public static void parseVcap() {

		StringReader stringReader = new StringReader(
				System.getenv("VCAP_SERVICES"));
		JsonReader jsonReader = Json.createReader(stringReader);
		JsonObject vcap = jsonReader.readObject();
		System.out.println("vcap: " + vcap);
		boolean ssl = true;
		if (ssl)
			DRDAURL = vcap.getJsonArray("altadb-dev").getJsonObject(0)
					.getJsonObject("credentials").getString("ssl_drda_url");
		else
			DRDAURL = vcap.getJsonArray("altadb-dev").getJsonObject(0)
					.getJsonObject("credentials").getString("drda_url");
		System.out.println(DRDAURL);
	}

}

class DataFormat {
    public final String id;
    public final int value;

    public DataFormat(String id, int value) {
        this.id = id;
        this.value = value;
    }

    public String toString() {
        return "id: " + this.id + " value: " + this.value + " ";
    }
}