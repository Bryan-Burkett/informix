##
# Python Sample Application: Connection to Informix using DRDA
##

# Topics
# 1 Create table 
# 2 Inserts
# 2.1 Insert a single document into a table
# 2.2 Insert multiple documents into a table
# 3 Queries
# 3.1 Find one document in a table that matches a query condition
# 3.2 Find documents in a table that match a query condition
# 3.3 Find all documents in a table
# 3.4 Count documents with query
# 3.5 Order documents in a table
# 3.6 Join tables
# 3.7 Find distinct fields in a table
# 3.8 Find with projection clause
# 4 Update documents in a table
# 5 Delete documents in a table
# 6 Transactions
# 7 Commands
# 7.1 Count
# 7.2 Distinct
# 8 Drop a table

import ibm_db
import json 
import os
from flask import Flask, render_template

app = Flask(__name__)

class City:
    def __init__(self, name, population, longitude, latitude, countryCode):
        self.name = name
        self.population = population
        self.longitude = longitude
        self.latitude = latitude
        self.countryCode = countryCode
    def toSQL(self):
        return "('" + self.name + "', " + str(self.population) + ", " + str(self.longitude) + ", " + str(self.latitude) + ", " + str(self.countryCode) + ")"
             
        
kansasCity = City("Kansas City", 467007, 39.0997, 94.5783, 1)
seattle = City("Seattle", 652405, 47.6097, 122.3331, 1)
newYork = City("New York", 8406000, 40.7127, 74.0059, 1)
london = City("London", 8308000, 51.5072, 0.1275, 44)
tokyo = City("Tokyo", 13350000, 35.6833, -139.6833, 81)
madrid = City("Madrid", 3165000, 40.4000, 3.7167, 34)
melbourne = City("Melbourne", 4087000, -37.8136, -144.9631, 61)
sydney = City("Sydney", 4293000, -33.8650, -151.2094, 61)

# parsing vcap services
def parseVCAP():
    global database
    global url
    
    altadb = json.loads(os.environ['VCAP_SERVICES'])['altadb-dev'][0]
    credentials = altadb['credentials']
    database = credentials['db']
     
    ssl = False
    if ssl == True:
        url = credentials['ssl_drda_url']
    else:
        url = credentials['drda_url']
        host = credentials['host']
        port = credentials['port']
        username = credentials['username']
        password = credentials['password']
         
    url = "HOSTNAME=" + host + ";PORT=" + port + ";DATABASE="+ database + ";PROTOCOL=TCPIP;UID=" + username +";PWD="+ password + ";"
    
def doEverything():    
    commands = []
    
    # connect to database
    host = ""
    port = "9090"
    username = ""
    password = "u1gsagfzs8dk"
    database = "s0523215"
    url = "HOSTNAME=" + host + ";PORT=" + port + ";DATABASE="+ database + ";PROTOCOL=TCPIP;UID=" + username +";PWD="+ password + ";"
    conn = ibm_db.connect(url, '', '')
    commands.append("Connected to " + url)
    
    # set up variables and data
    tableName = "pythonDRDAGalaxy"
    
    # 1 Create table
    commands.append("\n#1 Create table")
     
    sql = "create table if not exists " + tableName + "(name VARCHAR(255), population INTEGER, longitude DECIMAL(8,4), latitude DECIMAL(8,4),countryCode INTEGER)"
    ibm_db.exec_immediate(conn, sql)
               
    commands.append( "\tCreate a table named: " + tableName)
    commands.append("\tCreate Table SQL: " + sql)
    
    # 2 Inserts
    commands.append("\n#2 Inserts")
    # 2.1 Insert a single document into a table
    commands.append("#2.1 Insert a single document into a table")
    
    sql = "insert into " + tableName + " values(?,?,?,?,?)"
    statement = ibm_db.prepare(conn, sql)
    ibm_db.bind_param(statement, 1, kansasCity.name)
    ibm_db.bind_param(statement, 2, kansasCity.population)
    ibm_db.bind_param(statement, 3, kansasCity.longitude)
    ibm_db.bind_param(statement, 4, kansasCity.latitude)
    ibm_db.bind_param(statement, 5, kansasCity.countryCode)
    ibm_db.execute(statement)
    
    
    commands.append("\tCreate Document -> " + kansasCity.name + " , " + str(kansasCity.population) + 
                     " , " + str(kansasCity.longitude) + " , " + str(kansasCity.latitude) + " , " + str(kansasCity.countryCode))
    commands.append("\tSingle Insert SQL: " + sql)
    
    # 2.2 Insert multiple documents into a table
    # Currently there is no support for batch inserts with ibm_db
    commands.append("#2.2: Insert multiple documents into a table. \n\tCurrently there is no support batch inserts")
    sql = "insert into " + tableName + " values(?,?,?,?,?)"
    statement = ibm_db.prepare(conn, sql)
    ibm_db.bind_param(statement, 1, seattle.name)
    ibm_db.bind_param(statement, 2, seattle.population)
    ibm_db.bind_param(statement, 3, seattle.longitude)
    ibm_db.bind_param(statement, 4, seattle.latitude)
    ibm_db.bind_param(statement, 5, seattle.countryCode)
    ibm_db.execute(statement)
    
    sql = "insert into " + tableName + " values(?,?,?,?,?)"
    statement = ibm_db.prepare(conn, sql)
    ibm_db.bind_param(statement, 1, newYork.name)
    ibm_db.bind_param(statement, 2, newYork.population)
    ibm_db.bind_param(statement, 3, newYork.longitude)
    ibm_db.bind_param(statement, 4, newYork.latitude)
    ibm_db.bind_param(statement, 5, newYork.countryCode)
    ibm_db.execute(statement)
    
    #Alternate way to insert without bindings
    sql = "insert into " + tableName + " values" + tokyo.toSQL()
    ibm_db.exec_immediate(conn, sql)
    
    sql = "insert into " + tableName + " values" + madrid.toSQL()
    ibm_db.exec_immediate(conn, sql)
    
    sql = "insert into " + tableName + " values" + melbourne.toSQL()
    ibm_db.exec_immediate(conn, sql)
    
    # 3 Queries
    commands.append("\n#3 Queries")
    
    # 3.1 Find one document in a table that matches a query condition 
    commands.append("#3.1 Find one document in a table that matches a query condition")
    
    sql = "select * from " + tableName + " where population > 8000000 and countryCode = 1"
    stmt = ibm_db.exec_immediate(conn, sql)
    dictionary = ibm_db.fetch_both(stmt)
    
    commands.append("\tFirst document matching query -> name: " +  str(dictionary[0]) + ", population: " + str(dictionary[1]) + ", longitude: " +
                    str(dictionary[2]) + ", latitude: " + str(dictionary[3]) + ", countryCode: " + str(dictionary[4]))
    commands.append("\tQuery By name SQL: " + sql)
    
    # 3.2 Find documents in a table that match a query condition
    commands.append("#3.2 Find documents in a table that match a query condition")
    
    sql = "select * from " + tableName + " where population > 8000000 and longitude > 40.0"
    stmt = ibm_db.exec_immediate(conn, sql)
    dictionary = ibm_db.fetch_both(stmt)
    
    commands.append("\tFind all documents with name: " + kansasCity.name)
    while dictionary != False:
        commands.append("\tFound document name -> name: " +  str(dictionary[0]) + ", population: " + str(dictionary[1]) + ", longitude: " +
                    str(dictionary[2]) + ", latitude: " + str(dictionary[3]) + ", countryCode: " + str(dictionary[4]))
        dictionary = ibm_db.fetch_both(stmt)
    commands.append( "\tQuery All By name SQL: " + sql)
    
    # 3.3 Find all documents in a table
    commands.append("#3.3 Find all documents in a table")
    
    sql = "select * from " + tableName
    stmt = ibm_db.exec_immediate(conn, sql)
    dictionary = ibm_db.fetch_both(stmt)
    
    commands.append( "\tFind all documents in table: " + tableName)
    while dictionary != False:
        commands.append("\tFound document name -> name: " +  str(dictionary[0]) + ", population: " + str(dictionary[1]) + ", longitude: " +
                    str(dictionary[2]) + ", latitude: " + str(dictionary[3]) + ", countryCode: " + str(dictionary[4]))
        dictionary = ibm_db.fetch_both(stmt)
    commands.append("\tFind All Documents SQL: " + sql)
    
    commands.append("#3.4 Count documents in a table")
    sql = "select count(*) from " + tableName + " where longitude < 40.0"
    stmt = ibm_db.exec_immediate(conn, sql)
    dictionary = ibm_db.fetch_both(stmt)
    commands.append("Documents in table with longitude less than 40.0: " + str(len(dictionary)))
    
    commands.append("#3.5 Order documents in a table")
    sql = "select * from " + tableName + " order by population"
    stmt = ibm_db.exec_immediate(conn, sql)
    dictionary = ibm_db.fetch_both(stmt)
    while dictionary != False:
        commands.append("\tFound Document -> name: " + str(dictionary[0]) + " population: " + str(dictionary[1]) + " latitude: " +
                    str(dictionary[2]) + " longitude: " + str(dictionary[3]) + " countryCode: " + str(dictionary[4]))
        dictionary = ibm_db.fetch_both(stmt)
    
    commands.append("#3.6 Joins")
    tableJoin = "country";
    sql = "create table if not exists " + tableJoin + " (countryCode INTEGER, countryName VARCHAR(255))";
    stmt = ibm_db.exec_immediate(conn, sql)
    
    sql = "insert into " + tableJoin + " values (1,\"United States of America\")";
    stmt = ibm_db.exec_immediate(conn, sql)
    
    sql = "insert into " + tableJoin + " values (44,\"United Kingdom\")";
    stmt = ibm_db.exec_immediate(conn, sql)
    
    sql = "insert into " + tableJoin + " values (81,\"Japan\")";
    stmt = ibm_db.exec_immediate(conn, sql)
    
    sql = "insert into " + tableJoin + " values (34,\"Spain\")";
    stmt = ibm_db.exec_immediate(conn, sql)
    
    sql = "insert into " + tableJoin + " values (61,\"Australia\")";
    stmt = ibm_db.exec_immediate(conn, sql)
    
    sql = ("select table1.name, table1.population, table1.longitude, table1.latitude, table1.countryCode, table2.countryName from " + 
        tableName + " table1 inner join " + tableJoin + " table2 on table1.countryCode=table2.countryCode")
    stmt =ibm_db.exec_immediate(conn, sql)
    dictionary = ibm_db.fetch_both(stmt)
    while dictionary != False:
        commands.append("\tJoined -> name: " + str(dictionary[0]) + ", population: " + str(dictionary[1]) + ", latitude: " +
                    str(dictionary[2]) + ", longitude: " + str(dictionary[3]) + ", countryCode: " + str(dictionary[4]) + ", countryName: " + str(dictionary[5]))
        dictionary = ibm_db.fetch_both(stmt)
    
    commands.append("#3.7 Distinct documents in a table")
    sql = "select distinct countryCode from " + tableName + " where longitude > 40.0"
    stmt = ibm_db.exec_immediate(conn, sql)
    dictionary = ibm_db.fetch_both(stmt)
    commands.append("Distinct countryCodes of documents in table with longitude greater than 40.0: ")
    while dictionary != False:
        commands.append("\tJoined -> countryCode: " + str(dictionary[0]))
        dictionary = ibm_db.fetch_both(stmt)
        
    commands.append("#3.8 Projection Clause")
    sql = "select distinct name, countryCode from " + tableName + " where population > 8000000"
    stmt = ibm_db.exec_immediate(conn, sql)
    dictionary = ibm_db.fetch_both(stmt)
    commands.append("Projection of name and countryCode where population is greater than 8000000: ")
    while dictionary != False:
        commands.append("\tJoined -> name: " + str(dictionary[0]) + ", countryCode: " + str(dictionary[1]))
        dictionary = ibm_db.fetch_both(stmt)
    
    # 4 Update documents in a table
    commands.append("\n#4 Update documents in a table")
    
    sql = "update " + tableName + " set countryCode = ? where name = ?"
    statement = ibm_db.prepare(conn, sql)
    ibm_db.bind_param(statement, 1, 999)
    ibm_db.bind_param(statement, 2, seattle.name)
    ibm_db.execute(statement)
    
    commands.append( "\tDocument to update: " + seattle.name)
    commands.append("\tUpdate By name SQL: " + sql)
    
    
    # 5 Delete documents in a table
    commands.append("\n#5 Delete documents in a table")
    
    sql = "delete from " + tableName + " where name like '" + newYork.name + "'"
    ibm_db.exec_immediate(conn, sql)
    
    commands.append("\tDelete documents with name: " + newYork.name)
    commands.append("\tDelete By name SQL: " + sql)
    
    commands.append("\n#6 Transactions")
    ibm_db.autocommit(conn, False)
    
    sql = "insert into " + tableName + " values" + sydney.toSQL()
    ibm_db.exec_immediate(conn, sql)
    
    ibm_db.commit(conn)
    
    sql = "update " + tableName + " set countryCode = 998 where name = 'Seattle'"
    ibm_db.exec_immediate(conn, sql)
    
    ibm_db.rollback(conn)
    
    ibm_db.autocommit(conn, True)
    
    
    commands.append( "\tFind all documents in table: " + tableName)
    sql = "select * from " + tableName
    stmt = ibm_db.exec_immediate(conn, sql)
    dictionary = ibm_db.fetch_both(stmt)
    while dictionary != False:
        commands.append("\tFound document name -> name: " +  str(dictionary[0]) + ", population: " + str(dictionary[1]) + ", longitude: " +
                    str(dictionary[2]) + ", latitude: " + str(dictionary[3]) + ", countryCode: " + str(dictionary[4]))
        dictionary = ibm_db.fetch_both(stmt)
    
    commands.append("\n#7 Commands")
    commands.append("\n#7.1 Count")
    sql = "select count(*) from " + tableName
    stmt = ibm_db.exec_immediate(conn, sql)
    dictionary = ibm_db.fetch_both(stmt)
    commands.append("Number of documents in table " + str(len(dictionary)))
    
    # 8 Drop a table
    commands.append("\n#8 Drop a table")
    
    sql = "drop table " + tableName;
    ibm_db.exec_immediate(conn, sql)
    
    sql = "drop table " + tableJoin;
    ibm_db.exec_immediate(conn, sql)
    
    commands.append("\tDrop table: " + tableName)
    commands.append("\tDrop Table SQL: " + sql)
    
    
    ibm_db.close(conn)
    commands.append("\nConnection closed")
    return commands

@app.route("/")
def printCommands():
    commands = doEverything()
    return render_template('index.html', commands=commands)

if (__name__ == "__main__"):
    app.run(port=2047)
