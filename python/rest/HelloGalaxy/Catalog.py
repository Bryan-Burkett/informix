import json
import requests

##
# Python sample application: connecting to Informix using REST
#
# Working with collections and relational tables
##

# Topics
# 1 Collections
# 1.1 Implicitly create a collection by inserting into it
# 1.2 List all collections in a database
# 1.3 Query from a collection
# 1.4 Create a collection (explicitly)
# 1.5 Drop a collection
# 2 Relational Tables
# 2.1 Create a relational table
# 2.2 Insert into relational table
# 2.3 Query from a relational table
# 2.4 Drop a relational table

### Connection information ###
baseUrl="http://localhost:8080"
dbname="demo"
baseDbUrl=baseUrl+"/"+dbname
authInfo=('user','pass')
cookieName="informixRestListener.sessionId"

def printError(message, reply):
    print("Error: " + message)
    print("status code: " + str(reply.status_code))
    print("content: " + str(reply.content))

print("# 1 Collections")
print("# 1.1 Implicitly create a collection by inserting into it")
data = json.dumps({'firstName':'Luke', 'lastName':'Skywalker', 'age': 34})
reply = requests.post(baseDbUrl+"/people", data, auth=authInfo)
cookies = dict(cookieName=reply.cookies[cookieName])
if reply.status_code == 200:
    doc = reply.json()
    print ("inserted " + str(doc.get('n')) + " documents")
else:
    printError("Unable to insert document into collection", reply)
    
print("# 1.2 List all collections in a database")
reply = requests.get(baseDbUrl)
if reply.status_code == 200:
    doc = reply.json()
    collectionList = ""
    for c in doc:
        collectionList += "\'" + c + "\' "
    print ("Collections: ")
    print (collectionList)
else:
    printError("Unable to retrieve collection listing", reply)
    
print("# 1.3 Query from a collection")
reply = requests.get(baseDbUrl+"/people", auth=authInfo)
if reply.status_code == 200:
    doc = reply.json()
    print ("query results: " + str(doc))
else:
    printError("Unable to query a collection", reply)

print("# 1.4 Create a collection (explicitly)")
data = json.dumps({'name':'deleteMe'})
reply = requests.post(baseDbUrl,data, cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print(doc)
else:
    printError("Unable to create collection", reply)
    
print("# 1.5 Drop a collection")
reply = requests.delete(baseDbUrl + "/deleteMe", cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print(doc)
else:
    printError("Unable to drop collection", reply)
reply = requests.delete(baseDbUrl + "/people", cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print(doc)
else:
    printError("Unable to drop collection", reply)

print("# 2 Relational Tables")
print("# 2.1 Create a relational table")
data = json.dumps({'name':'demotable', 'options':{'columns':[ {'name':'id', 'type':'int', 'primaryKey':True}, {'name':'username', 'type':'varchar(30)'}, {'name':'state', 'type':'char(2)'} ]}})
reply = requests.post(baseDbUrl, data, cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print(doc)
else:
    printError("Unable to create relational table", reply)

print("# 2.2 Insert into relational table")
data = json.dumps({'id':101, 'username':'felix', 'state':'CA'})
reply = requests.post(baseDbUrl+"/demotable", data, auth=authInfo)
if reply.status_code == 200:
    doc = reply.json()
    print ("inserted " + str(doc[0].get('n')) + " documents")
else:
    printError("Unable to insert document into relational table", reply)

print("# 2.3 Query from relational table")
reply = requests.get(baseDbUrl+"/demotable", auth=authInfo)
if reply.status_code == 200:
    doc = reply.json()
    print ("query results: " + str(doc))
else:
    printError("Unable to query relational table", reply)

print("# 2.4 Drop a relational table")
reply = requests.delete(baseDbUrl + "/demotable", cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print(doc)
else:
    printError("Unable to drop relational table", reply)
