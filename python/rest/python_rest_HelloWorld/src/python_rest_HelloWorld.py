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
url = ""
database = ""
port = int(os.getenv('VCAP_APP_PORT', 8080))

# parsing vcap services
def parseVCAP():
    global database
    global url
#     altadb = json.loads(os.environ['VCAP_SERVICES'])['altadb-dev'][0]
#     credentials = altadb['credentials']
#     ssl = False
#     if ssl == True:
#         url = credentials['ssl_rest_url']
#     else:
#         url = credentials['rest_url']
    url = "yourURL"
    database = "yourDatabase"
    
# certfile = '/home/bryan/Downloads/baratheon.pem'
class MyAdapter(HTTPAdapter):
    def init_poolmanager(self, connections, maxsize, block=False):
        self.poolmanager = PoolManager(num_pools=connections,
                                       maxsize=maxsize,
                                       block=block
#                                        ssl_version=ssl.PROTOCOL_TLSv1,
#                                        ca_certs = certfile
                                       )
         


# baseUrl="https://yourURL:port"
# dbname="db"
# baseDbUrl= url + "/" + database
authInfo=('username','password')
cookieName="informixRestListener.sessionId"
collectionName = "pythonREST"

commands = ["Start"]

def printError(message, reply):
    commands.append("Error: " + message)
    commands.append("status code: " + str(reply.status_code))
    commands.append("content: " + str(reply.content))
    
def doEverything():
    session = requests.Session()
    session.mount('http://', MyAdapter())
    baseDbUrl= url + "/" + database
    commands.append("# 1 Inserts")
    commands.append( "# 1.1 Insert a single document to a collection")
    data = json.dumps({'name': 'test1', 'value': 1})
    reply = session.post(baseDbUrl + "/" + collectionName, data)
    if reply.status_code == 200:
        doc = reply.json()
        commands.append("inserted " + str(doc.get('n')) + " documents")
    else:
        printError("Unable to insert document", reply)
    
    commands.append("# 1.2 Insert multiple documents to a collection")
    data = json.dumps([{'name': 'test1', 'value': 1}, {'name': 'test2', 'value': 2}, {'name': 'test3', 'value': 3} ] )
    reply = session.post(baseDbUrl + "/" + collectionName, data)
    if reply.status_code == 202:
        doc = reply.json()
        commands.append("inserted " + str(doc.get('n')) + " documents")
    else:
        printError("Unable to insert multiple documents", reply)
    
    commands.append("# 2 Queries")
    commands.append("# 2.1 Find a document in a collection that matches a query condition")
    query = json.dumps({'name':'test1'})
    reply = session.get(baseDbUrl + "/" + collectionName + "?query=" + query)
    if reply.status_code == 200:
        doc = reply.json()
        commands.append("query result: " + str(doc[0]))
    else:
        printError("Unable to query documents in collection", reply)
          
    commands.append("# 2.2 Find all documents in a collection that match a query condition")
    query = json.dumps({'name':'test1'})
    reply = session.get(baseDbUrl + "/" + collectionName + "?query=" + query)
    if reply.status_code == 200:
        doc = reply.json()
        commands.append("query result: " + str(doc))
    else:
        printError("Unable to query documents in collection", reply)
      
    commands.append("# 2.3 Find all documents in a collection")
    reply = session.get(baseDbUrl+ "/" + collectionName)
    if reply.status_code == 200:
        doc = reply.json()
        commands.append("query result: " + str(doc))
    else:
        printError("Unable to query documents in collection", reply)
      
    commands.append("# 3 Update documents in a collection")
    query = json.dumps({'name': 'test3'})
    data = json.dumps({'$set' : {'value' : 9} })
    reply = session.put(baseDbUrl + "/" + collectionName + "?query=" + query, data)
    if reply.status_code == 200:
        doc = reply.json()
        commands.append("updated " + str(doc.get('n')) + " documents")
    else:
        printError("Unable to update documents in collection", reply)
      
    commands.append("# 4 Delete documents in a collection")
    query = json.dumps({'name': 'test2'})
    reply = session.delete(baseDbUrl + "/" + collectionName + "?query=" + query)
    if reply.status_code == 200:
        doc = reply.json()
        commands.append("deleted " + str(doc.get('n')) + " documents")
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
        commands.append("delete collection result: " + str(doc))
    else:
        printError("Unable to drop collection", reply)

@app.route("/")
def printCommands():
    parseVCAP()
    doEverything()
    return render_template('index.html', commands=commands)
 
if (__name__ == "__main__"):
    app.run(host='0.0.0.0', port=port)