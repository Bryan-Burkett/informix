##
# Python Sample Application: Connection to Informix using DRDA
##

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
# 4 Update documents in a table
# 5 Delete documents in a table
# 6 Drop a table
##

import ibm_db
import json 
import os
from flask import Flask, render_template

app = Flask(__name__)

class DataFormat:
    def __init__(self, name, value):
        self.name = name
        self.value = value

url = ""
database = ""
port = int(os.getenv('VCAP_APP_PORT', 2047))

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
    conn = ibm_db.connect(url, '', '')
    commands.append("Connected to " + url)
    
    # set up variables and data
    tableName = "pythonDRDATest"
    user1 = DataFormat("test1", 1)
    user2 = DataFormat("test2", 2)
    user3 = DataFormat("test3", 3)
    
    # 1 Create table
    commands.append("\n#1 Create table")
    
    sql = "create table " + tableName + "(name varchar(255),  value integer)"
    ibm_db.exec_immediate(conn, sql)
         
    commands.append( "\tCreate a table named: " + tableName)
    commands.append("\tCreate Table SQL: " + sql)
    
    # 2 Inserts
    commands.append("\n#2 Inserts")
    # 2.1 Insert a single document into a table
    commands.append("#2.1 Insert a single document into a table")
    
    sql = "insert into " + tableName + " values(?,?)"
    statement = ibm_db.prepare(conn, sql)
    ibm_db.bind_param(statement, 1, user1.name)
    ibm_db.bind_param(statement, 2, user1.value)
    ibm_db.execute(statement)
    
    commands.append("\tCreate Document -> " + user1.name + " : " + str(user1.value))
    commands.append("\tSingle Insert SQL: " + sql)
    
    sql = "insert into " + tableName + " values(?,?)"
    statement = ibm_db.prepare(conn, sql)
    ibm_db.bind_param(statement, 1, user2.name)
    ibm_db.bind_param(statement, 2, user2.value)
    ibm_db.execute(statement)
    
    commands.append("\tCreate Document -> " + user2.name + " : " + str(user2.value))
    commands.append("\tSingle Insert SQL: " + sql)
    
    sql = "insert into " + tableName + " values(?,?)"
    statement = ibm_db.prepare(conn, sql)
    ibm_db.bind_param(statement, 1, user3.name)
    ibm_db.bind_param(statement, 2, user3.value)
    ibm_db.execute(statement)
    
    commands.append("\tCreate Document -> " + user3.name + " : " + str(user3.value))
    commands.append("\tSingle Insert SQL: " + sql)
    
    # 2.2 Insert multiple documents into a table
    # Currently there is no support for batch inserts with ibm_db
    commands.append("#2.2: Insert multiple documents into a table. \n\tCurrently there is no support batch inserts")
    
    # 3 Queries
    commands.append("\n#3 Queries")
    
    # 3.1 Find one document in a table that matches a query condition 
    commands.append("#3.1 Find one document in a table that matches a query condition")
    
    sql = "select * from " + tableName + " where name LIKE '" + user1.name + "'"
    stmt = ibm_db.exec_immediate(conn, sql)
    dictionary = ibm_db.fetch_both(stmt)
    
    commands.append("\tFind document with name: " + user1.name)
    commands.append("\tFirst document with name -> name: " +  str(dictionary[0]) + " value: " + str(dictionary[1]))
    commands.append("\tQuery By name SQL: " + sql)
    
    # 3.2 Find documents in a table that match a query condition
    commands.append("#3.2 Find documents in a table that match a query condition")
    
    sql = "select * from " + tableName + " where name LIKE '" + user1.name + "'"
    stmt = ibm_db.exec_immediate(conn, sql)
    dictionary = ibm_db.fetch_both(stmt)
    
    commands.append("\tFind all documents with name: " + user1.name)
    while dictionary != False:
        commands.append("\tFound Document -> name: " + str(dictionary[0]) + " value: " + str(dictionary[1]))
        dictionary = ibm_db.fetch_both(stmt)
    commands.append( "\tQuery All By name SQL: " + sql)
    
    # 3.3 Find all documents in a table
    commands.append("#3.3 Find all documents in a table")
    
    sql = "select * from " + tableName
    stmt = ibm_db.exec_immediate(conn, sql)
    dictionary = ibm_db.fetch_both(stmt)
    
    commands.append( "\tFind all documents in table: " + tableName)
    while dictionary != False:
        commands.append("\tFound Document -> name: " + str(dictionary[0]) + " value: " + str(dictionary[1]))
        dictionary = ibm_db.fetch_both(stmt)
    commands.append("\tFind All Documents SQL: " + sql)
    
    
    # 4 Update documents in a table
    commands.append("\n#4 Update documents in a table")
    
    sql = "update " + tableName + " set value = ? where name = ?"
    statement = ibm_db.prepare(conn, sql)
    ibm_db.bind_param(statement, 1, 4)
    ibm_db.bind_param(statement, 2, user2.name)
    ibm_db.execute(statement)
    
    commands.append( "\tDocument to update: " + user2.name)
    commands.append("\tUpdate By name SQL: " + sql)
    
    
    # 5 Delete documents in a table
    commands.append("\n#5 Delete documents in a table")
    
    sql = "delete from " + tableName + " where name like '" + user1.name + "'"
    ibm_db.exec_immediate(conn, sql)
    
    commands.append("\tDelete documents with name: " + user1.name)
    commands.append("\tDelete By name SQL: " + sql)
    
    # 6 Drop a table
    commands.append("\n#6 Drop a table")
    
    sql = "drop table " + tableName;
    ibm_db.exec_immediate(conn, sql)
    
    commands.append("\tDrop table: " + tableName)
    commands.append("\tDrop Table SQL: " + sql)
    
    ibm_db.close(conn)
    commands.append("\nConnection closed")
    return commands

@app.route("/")
def printCommands():
    parseVCAP()
    commands = doEverything()
    return render_template('index.html', commands=commands)

if (__name__ == "__main__"):
    app.run(host='0.0.0.0',port=port)