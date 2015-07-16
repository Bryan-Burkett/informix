##
# Python Sample Application: Connection to Informix using REST
##

# Topics
# 1 Inserts
# 1.1 Insert a single document into a collection
# 1.2 Insert multiple documents into a collection
# 2 Queries
# 2.1 Find one document in a collection that matches a query condition  
# 2.2 Find documents in a collection that match a query condition
# 2.3 Find all documents in a collection
# 3 Update documents in a collection
# 4 Delete documents in a collection
# 5 Get a listing of collections
# 6 Drop a collection


import json
import os
import requests
from requests.adapters import HTTPAdapter
from requests.packages.urllib3.poolmanager import PoolManager
import ssl
from flask import Flask, render_template

app = Flask(__name__)

# parsing vcap services
# altadb = json.loads(os.environ['VCAP_SERVICES'])['altadb-dev'][0]
# credentials = altadb['credentials']
# ssl = False
# if ssl == True:
#     url = credentials['ssl_rest_url']
# else:
#     url = credentials['rest_url']
# print url

certfile = '/home/bryan/Downloads/baratheon.pem'
class TLSAdapter(HTTPAdapter):
    def init_poolmanager(self, connections, maxsize, block=False):
        self.poolmanager = PoolManager(num_pools=connections,
                                       maxsize=maxsize,
                                       block=block,
                                       ssl_version=ssl.PROTOCOL_TLSv1,
                                       ca_certs = certfile
                                       )
         
session = requests.Session()
session.mount('https://', TLSAdapter())


baseUrl="https://yourURL:port"
dbname="db"
baseDbUrl=baseUrl + "/" + dbname
authInfo=('username','password')
cookieName="informixRestListener.sessionId"
collectionName = "pythonREST"


commands = []

class City:
    def __init__(self, name, population, longitude, latitude, countryCode):
        self.name = name
        self.population = population
        self.longitude = longitude
        self.latitude = latitude
        self.countryCode = countryCode
    def toJSON(self):
        return json.loads("{\"name\" : \"%s\" , \"population\" : %d , \"longitude\" : %.4f , \"latitude\" : %.4f , \"countryCode\" : %d}"
                      %(self.name, self.population, self.longitude, self.latitude, self.countryCode))
        
kansasCity = City("Kansas City", 467007, 39.0997, 94.5783, 1)
seattle = City("Seattle", 652405, 47.6097, 122.3331, 1)
newYork = City("New York", 8406000, 40.7127, 74.0059, 1)
london = City("London", 8308000, 51.5072, 0.1275, 44)
tokyo = City("Tokyo", 13350000, 35.6833, -139.6833, 81)
madrid = City("Madrid", 3165000, 40.4000, 3.7167, 34)
melbourne = City("Melbourne", 4087000, -37.8136, -144.9631, 61)
sydney = City("Sydney", 4293000, -33.8650, -151.2094, 61)


def printError(message, reply):
    commands.append("Error: " + message)
    commands.append("status code: " + str(reply.status_code))
    commands.append("content: " + str(reply.content))

def doEverything():
    collectionName = "pythonRESTGalaxy"
    joinCollectionName = "pyRESTJoin"
    codeTableName = "codeTable"
    cityTableName = "cityTable"
    
    commands.append("# 1 Data Structures")
    commands.append( "# 1.1 Create Collection")
    data = json.dumps({"name": collectionName})
    reply = session.post(baseDbUrl, data, verify=certfile, auth=authInfo)
    if reply.status_code == 200:
        doc = reply.json()
        commands.append("Created collection")
    else:
        printError("Unable to create collection", reply)
        
    data = json.dumps({"name": joinCollectionName})
    reply = session.post(baseDbUrl, data)
    if reply.status_code == 200:
        doc = reply.json()
        commands.append("Created collection")
    else:
        printError("Unable to create collection", reply)
    
    commands.append("# 1.2 Create Table")
    data = json.dumps({"create" : codeTableName, "columns":[{"name":"countryCode","type":"int"},
                                                                        {"name": "countryName", "type": "varchar(50)"}]})
    
    reply = session.post(baseDbUrl + "/cmd", data)
    if reply.status_code == 200:
        doc = reply.json()
        commands.append("Created Table: " + codeTableName)
    else:
        printError("Unable to create table", reply)
    
    data= json.dumps({"create" : cityTableName, "columns":[{"name":"name","type":"varchar(50)"},
                                                               {"name": "population", "type": "int"}, 
                                                               {"name": "longitude", "type": "decimal(8,4)"},
                                                               {"name": "latitude", "type": "decimal(8,4)"}, 
                                                               {"name": "countryCode", "type": "int"}]})
    
    reply = session.post(baseDbUrl + "/cmd", data)
    if reply.status_code == 200:
        doc = reply.json()
        commands.append("Created Table: " + cityTableName)
    else:
        printError("Unable to create table", reply)
        
    commands.append("# 1 Inserts")
    commands.append( "# 1.1 Insert a single document to a collection")
    data = json.dumps(kansasCity.toJSON())
    reply = session.post(baseDbUrl + "/" + collectionName, data)
    if reply.status_code == 200:
        doc = reply.json()
        commands.append("Inserted " + str(doc.get('n')) + " document")
    else:
        printError("Unable to insert document", reply)
        
    reply = session.post(baseDbUrl + "/" + cityTableName, data)
    if reply.status_code == 200:
        doc = reply.json()
        commands.append("Inserted " + str(doc.get('n')) + " document")
    else:
        printError("Unable to insert document", reply)
    
    commands.append("# 1.2 Insert multiple documents to a collection")
    data = json.dumps([seattle.toJSON(), newYork.toJSON(), london.toJSON(), tokyo.toJSON(), madrid.toJSON()])
    reply = session.post(baseDbUrl + "/" + collectionName, data)
    if reply.status_code == 202:
        doc = reply.json()
        commands.append("Inserted " + str(doc.get('n')) + " documents")
    else:
        printError("Unable to insert multiple documents", reply)
       
    commands.append("# 2 Queries")
    commands.append("# 2.1 Find a document in a collection that matches a query condition")
    query = json.dumps({"longitude": {"$gt" : 40.0}})
    reply = session.get(baseDbUrl + "/" + collectionName + "?query=" + query)
    if reply.status_code == 200:
        docs = reply.json()
        commands.append("Query result: " + str(docs[0]))
    else:
        printError("Unable to query documents in collection", reply)
           
    commands.append("# 2.2 Find all documents in a collection that match a query condition")
    query = json.dumps({"longitude": {"$gt" : 40.0}})
    reply = session.get(baseDbUrl + "/" + collectionName + "?query=" + query)
    if reply.status_code == 200:
        docs = reply.json()
        commands.append("Query result: ")
        for doc in docs:
            commands.append(str(doc))
    else:
        printError("Unable to query documents in collection", reply)
       
    commands.append("# 2.3 Find all documents in a collection")
    reply = session.get(baseDbUrl+ "/" + collectionName)
    if reply.status_code == 200:
        docs = reply.json()
        commands.append("query result: ")
        for doc in docs:
            commands.append(str(doc))
    else:
        printError("Unable to query documents in collection", reply)
    
    commands.append("# 2.4 Count documents in a collection")
    cmd = "$cmd"
    query = json.dumps({"count": collectionName, "query": {"longitude": {"$lt" : 40.0}}})
    reply = session.get(baseDbUrl + "/" + cmd + "?query=" + query)
    if reply.status_code == 200:
        docs = reply.json()
        commands.append("Number of documents match query: " +str(int(docs[0].get('n'))))
    else:
        printError("Unable to count documents in collection", reply)
    
    commands.append("# 2.5 Order documents in a collection")
    sort = json.dumps({"population": 1})
    reply = session.get(baseDbUrl + "/" + collectionName + "?sort=" + sort)
    if reply.status_code == 200:
        docs = reply.json()
        commands.append("Sorted result: ")
        for doc in docs:
            commands.append(str(doc))
    else:
        printError("Unable to sort documents in collection", reply)
        
    commands.append("# 2.6 Find distinct documents in a collection")
    cmd = "$cmd"
    query = json.dumps({"distinct": collectionName, "key": "countryCode", "query": {"longitude": {"$lt" : 40.0}}})
    reply = session.get(baseDbUrl + "/" + cmd + "?query=" + query)
    if reply.status_code == 200:
        docs = reply.json()
        commands.append("Number of documents match query: " + str(docs))
    else:
        printError("Unable to count documents in collection", reply)
    
    commands.append("# 2.7 Join collection")
    cmd = "$cmd"
    query = json.dumps({"distinct": collectionName, "query": {"longitude": {"$lt" : 40.0}}})
    reply = session.get(baseDbUrl + "/" + cmd + "?query=" + query)
    if reply.status_code == 200:
        docs = reply.json()
        commands.append("Number of documents match query: " +str(docs))
    else:
        printError("Unable to join collections", reply)
        
    commands.append("#2.8 Batch Size")
       
    commands.append("# 2.9 Find all documents in a collection with projection")
    query = json.dumps({"longitude": {"$gt" : 40.0}})
    projection = json.dumps({"name" : 1, "population": 1, "_id": 0})
    reply = session.get(baseDbUrl + "/" + collectionName + "?query=" + query + "&fields=" + projection)
    if reply.status_code == 200:
        docs = reply.json()
        commands.append("Query result: ")
        for doc in docs:
            commands.append(str(doc))
    else:
        printError("Unable to query documents in collection", reply)   
       
    commands.append("# 3 Update documents in a collection")
    query = json.dumps({'name': seattle.name})
    data = json.dumps({'$set' : {'countryCode' : 999} })
    reply = session.put(baseDbUrl + "/" + collectionName + "?query=" + query, data)
    if reply.status_code == 200:
        doc = reply.json()
        commands.append("Updated " + str(doc.get('n')) + " documents")
    else:
        printError("Unable to update documents in collection", reply)
       
    commands.append("# 4 Delete documents in a collection")
    query = json.dumps({'name': tokyo.name})
    reply = session.delete(baseDbUrl + "/" + collectionName + "?query=" + query)
    if reply.status_code == 200:
        doc = reply.json()
        commands.append("Deleted " + str(doc.get('n')) + " documents")
    else:
        printError("Unable to delete documents in collection", reply)
       
    commands.append("# 5 Get a listing of collections")
    reply = session.get(baseDbUrl)
    if reply.status_code == 200:
        doc = reply.json()
        dbList = ""
        for db in doc:
            dbList += "\'" + db + "\' "
        commands.append("Collections: " + str(dbList))
    else:
        printError("Unable to retrieve collection listing", reply)
           
    commands.append("# 6 Drop a collection")
    reply = session.delete(baseDbUrl + "/" + collectionName)
    if reply.status_code == 200:
        doc = reply.json()
        commands.append("Delete collection result: " + str(doc))
    else:
        printError("Unable to drop collection", reply)
        
    reply = session.delete(baseDbUrl + "/" + joinCollectionName)
    if reply.status_code == 200:
        doc = reply.json()
        commands.append("Delete collection result: " + str(doc))
    else:
        printError("Unable to drop collection", reply)
        
    reply = session.delete(baseDbUrl + "/" + codeTableName)
    if reply.status_code == 200:
        doc = reply.json()
        commands.append("Delete collection result: " + str(doc))
    else:
        printError("Unable to drop collection", reply) 
           
    reply = session.delete(baseDbUrl + "/" + cityTableName)
    if reply.status_code == 200:
        doc = reply.json()
        commands.append("Delete collection result: " + str(doc))
    else:
        printError("Unable to drop collection", reply)
      
@app.route("/")
def printCommands():
    doEverything()
    return render_template('index.html', commands=commands)
 
if (__name__ == "__main__"):
    app.run(port=8888)