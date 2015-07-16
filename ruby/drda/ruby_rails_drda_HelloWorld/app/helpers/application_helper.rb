module ApplicationHelper
=begin
Topics
1 Connectivity
1.1 VCAP
1.2 Authentication
1.3 Misc.
2 Create Table
3 Insert
3.1 Single row insert
3.2 multiple row insert
4 Update
5 Delete (keep structure, delete value)
6 Drop (delete everything)
7 Queries
7.1 Find one
7.2 find all
7.3 Misc.
=end

	def runHelloWorld()
		outPut = Array.new
		# using local connection for testing, use VCAP for deployment to bluemix
		connStr = "DRIVER={IBM DB2 ODBC DRIVER};DATABASE=rubyDb;HOSTNAME=lxvm-l165.lenexa.ibm.com;PORT=8412;"\
		"PROTOCOL=TCPIP;UID=informix;PWD=password;"
		dbconn = IBM_DB.connect connStr, "informix", "password"
=begin  for use with bluemix
vcap_services = JSON.parse(ENV('VCAP_SERVICES'))
dbcred = vcap_services["credentials"]
db = dbcred["db"]
hostname = dbcred["hostname"]
port = dbcred["port"]
uid = dbcred["username"]
pwd = dbcred["password"]
connStr = "DATABASE=" + db + ";HOSTNAME=" + hostName + ";PORT=" + port+ ";UID="+uid+";PWD="+pwd"
dbconn = IBM_DB.connect connStr
=end
		if dbconn
			outPut.push("Connected Successfully to database")
		else
			outPut.push("Failed to Connect to Database")
			return outPut
		end

		stmt = IBM_DB.exec(dbconn, "DROP TABLE IF EXISTS test_ruby_drda")
		# create table
		stmt = IBM_DB.exec(dbconn, "CREATE TABLE test_ruby_drda (name varchar(255),  value integer)")
		unless stmt
			outPut.push("Create table failed")
			return outPut
		end
		# Single row Insert
		stmt = IBM_DB.exec(dbconn, "INSERT INTO test_ruby_drda Values ('test1', 1)")
		unless stmt
			outPut.push("Failed to Insert Single Row")
			return outPut
		end
		outPut.push("Successfully Inserted Single Row into Table")
		# multiple row insert
		# Currently there is no support for batch inserts with ibm_db
		outPut.push("Currently there is no support for batch inserts with ibm_db")
		outPut.push("  ")
		outPut.push("Adding extra entries to table for later query tests")
		stmt = IBM_DB.exec(dbconn, "INSERT INTO test_ruby_drda Values ('test1', 5)")
		unless stmt
			outPut.push("Failed to Insert 1st Row")
			return outPut
		end
		stmt = IBM_DB.exec(dbconn, "INSERT INTO test_ruby_drda Values ('test2', 2)")
		unless stmt
			outPut.push("Failed to Insert 2nd Row")
			return outPut
		end
		stmt = IBM_DB.exec(dbconn, "INSERT INTO test_ruby_drda Values ('test3', 3)")
		unless stmt
			outPut.push("Failed to Insert 3rd Row")
			return outPut
		end
		# 3 Queries
		outPut.push("  ")
		outPut.push("Queries")
		outPut.push("Find one document in a table that matches a query condition")
		stmt = IBM_DB.exec(dbconn, "SELECT * from test_ruby_drda where name LIKE '%test2%'")
		unless stmt
			outPut.push("Failed to select one document")
			return outPut
		end
		expectedHash = {"name"=>"test2", "value"=>2}
		resultHash = IBM_DB.fetch_assoc(stmt)
		unless expectedHash == resultHash
			outPut.push("Result does not equal expected")
			return outPut
		end
		outPut.push("Find multiple documents that match query condition")
		stmt = IBM_DB.exec(dbconn, "SELECT * from test_ruby_drda where name LIKE '%test1%'")
		unless stmt
			outPut.push("Failed to select multiple documents")
			return outPut
		end
		resultArray = Array.new
		resultHash = IBM_DB.fetch_assoc(stmt)
		while resultHash
			resultArray.push(resultHash)
			resultHash = IBM_DB.fetch_assoc(stmt)
		end
		expectedArray = [{"name"=>"test1", "value"=>1}, {"name"=>"test1", "value"=>5}]
		unless expectedArray == resultArray
			outPut.push("Results do not equal expected")
			return outPut
		end
		outPut.push("  ")
		outPut.push("Find all documents in a table")
		stmt = IBM_DB.exec(dbconn, "SELECT * from test_ruby_drda")
		unless stmt
			outPut.push("Failed to select all documents")
			return outPut
		end
		resultArray = Array.new
		resultHash = IBM_DB.fetch_assoc(stmt)
		while resultHash
			resultArray.push(resultHash)
			resultHash = IBM_DB.fetch_assoc(stmt)
		end
		outPut.push(resultArray)
		expectedArray = [{"name"=>"test1", "value"=>1}, {"name"=>"test1", "value"=>5}, {"name"=>"test2", "value"=>2}, {"name"=>"test3", "value"=>3}]
		unless expectedArray == resultArray
			outPut.push("Results do not equal expected")
			return outPut
		end
		outPut.push(" ")
		outPut.push("Update documents in a table")
		stmt = IBM_DB.exec(dbconn, "UPDATE test_ruby_drda SET value = 20 WHERE name = 'test2'")
		unless stmt
			outPut.push("Failed to Update")
			return outPut
		end
		outPut.push("Successfully Updated Value")
		outPut.push("Delete documents in a table")
		stmt = IBM_DB.exec(dbconn, "DELETE FROM test_ruby_drda where name like '%test1%'")
		unless stmt
			outPut.push("Failed to Delete Value")
			return outPut
		end
		outPut.push("Successfully deleted Values")
		outPut.push(" ")
		outPut.push("Drop table")
		stmt = IBM_DB.exec(dbconn, "DROP TABLE IF EXISTS test_ruby_drda")
		unless stmt
			outPut.push("Failed to Drop Table")
			return outPut
		end
		outPut.push("Successfully dropped table")
		IBM_DB.close(dbconn)
		outPut.push(" ")
		outPut.push("Connection closed")
		return outPut
	end


end
