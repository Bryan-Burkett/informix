import json
import requests

##
# Python sample application: connecting to Informix using REST
##

# Topics
# 1 Inserts
# 1.1 Insert a single document to a collection
# 1.2 Insert multiple documents to a collection
# 2 Queries
# 2.1 Find all documents in a collection
# 2.2 Find documents in a collection that match a query condition
# 2.3 Add a projection clause to a query
# 2.4 Find documents in a collection and retrieve using a cursor
# 3 Update documents in a collection
# 4 Delete documents in a collection
# 5 Get a listing of collections
# 6 Drop a collection
# 7 Run a command

### Connection information ###
baseUrl="http://localhost:8080"
dbname="test"
baseDbUrl=baseUrl + "/" + dbname
authInfo=('user','pass')
cookieName="informixRestListener.sessionId"

def printError(message, reply):
    print("Error: " + message)
    print("status code: " + str(reply.status_code))
    print("content: " + str(reply.content))

print("# 1 Inserts")
print("# 1.1 Insert a single document to a collection")
data = json.dumps({'firstName':'Luke', 'lastName':'Skywalker', 'age': 34})
reply = requests.post(baseDbUrl+"/people", data, auth=authInfo)
if reply.status_code == 200:
    doc = reply.json()
    print("inserted " + str(doc.get('n')) + " documents")
else:
    printError("Unable to insert document", reply)

print("# 1.2 Insert multiple documents to a collection")
data = json.dumps([{'firstName':'Leia', 'lastName':'Skywalker', 'age': 34}, {'firstName':'Anakin', 'lastName':'Skywalker', 'age': 55} ] )
reply = requests.post(baseDbUrl+"/people", data, auth=authInfo)
if reply.status_code == 202:
    doc = reply.json()
    print("inserted " + str(doc.get('n')) + " documents")
else:
    printError("Unable to insert multiple documents", reply)

print("# 2 Queries")
print("# 2.1 Find all documents in a collection")
reply = requests.get(baseDbUrl+"/people", None, auth=authInfo)
if reply.status_code == 200:
    doc = reply.json()
    print("query result: " + str(doc))
else:
    printError("Unable to query documents in collection", reply)

print("# 2.2 Find documents in a collection that match a query condition")
query = json.dumps({'firstName':'Luke'})
reply = requests.get(baseDbUrl+"/people?query=" + query, None, auth=authInfo)
if reply.status_code == 200:
    doc = reply.json()
    print("query result: " + str(doc))
else:
    printError("Unable to query documents in collection", reply)

print("# 2.3 Add a projection clause to a query")
projection = json.dumps({'firstName':1, 'age': 1, '_id':0})
reply = requests.get(baseDbUrl+"/people?fields=" + projection, None, auth=authInfo)
if reply.status_code == 200:
    doc = reply.json()
    print("query result: " + str(doc))
else:
    printError("Unable to query documents in collection", reply)

print("# 2.4 Find documents in a collection and retrieve using a cursor")
projection = json.dumps({'colname':1, 'tabid': 1, 'coltype':1})
reply = requests.get(baseDbUrl+"/syscolumns?fields=" + projection, None, auth=authInfo)
if reply.status_code == 200:
    fetchNum = 1
    cursor_id = reply.headers['cursorid']
    cookies = dict()
    cookies[cookieName]=reply.cookies[cookieName]
    headers = {'cursorid':cursor_id}
    print ("reply headers: " + str(reply.headers))
    print ("cursor id: " + cursor_id)
    print ("cookies = " + str(cookies))
    print ("fetch " + str(fetchNum) + ": " + str(reply.json()))
    moreRows = (cursor_id != 0)
    while (moreRows):
        reply = requests.get(baseDbUrl+"/syscolumns?fields=" + projection, None, headers=headers,cookies=cookies)
        fetchNum += 1
        print ("fetch " + str(fetchNum) + ": " + str(reply.json()))
        if reply.status_code == 200:
            moreRows = reply.headers.get('cursorid') != None
        else : 
            moreRows = False
            printError("Unable to get more documents from a cursor", reply)       
else:
    printError("Unable to query documents in collection using a cursor", reply)

print("# 3 Update documents in a collection")
query = json.dumps({'firstName': 'Luke'})
data = json.dumps({'$set' : {'age' : 35} })
reply = requests.put(baseDbUrl+"/people?query=" + query, data, auth=authInfo)
if reply.status_code == 200:
    doc = reply.json()
    print("updated " + str(doc.get('n')) + " documents")
else:
    printError("Unable to update documents in collection", reply)

print("# 4 Delete documents in a collection")
query = json.dumps({'age': { '$gt': 50} })
reply = requests.delete(baseDbUrl+"/people?query=" + query, auth=authInfo)
if reply.status_code == 200:
    doc = reply.json()
    print("deleted " + str(doc.get('n')) + " documents")
else:
    printError("Unable to delete documents in collection", reply)

print("# 5 Get a listing of collections")
reply = requests.get(baseDbUrl)
if reply.status_code == 200:
    doc = reply.json()
    dbList = ""
    for db in doc:
        dbList += "\'" + db + "\' "
    print("Collections: " + str(dbList))
else:
    printError("Unable to retrieve collection listing", reply)
    
print("# 6 Drop a collection")
reply = requests.delete(baseDbUrl+"/people", auth=authInfo)
if reply.status_code == 200:
    doc = reply.json()
    print("delete collection result: " + str(doc))
else:
    printError("Unable to drop collection", reply)

print("# 7 Run a command")
command = json.dumps({'dbStats':1})
reply = requests.get(baseDbUrl+"/$cmd?query=" + command, None, auth=authInfo)
if reply.status_code == 200:
    doc = reply.json()
    print("command result: " + str(doc))
else:
    printError("Unable to run command", reply)
