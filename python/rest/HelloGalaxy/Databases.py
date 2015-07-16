import json
import requests

##
# Python sample application: connecting to Informix using REST
#
# Working with databases
##

# Topics
# 1 Databases
# 1.1 List all databases
# 1.2 Delete all databases
# 1.3 Create a database (explicitly)
# 1.4 Delete a database

### Connection information ###
baseUrl="http://localhost:8080"
authInfo=('user','pass')
cookieName="informixRestListener.sessionId"

def printError(message, reply):
    print("Error: " + message)
    print("status code: " + str(reply.status_code))
    print("content: " + str(reply.content))

print("# 1.1 List all databases")
reply = requests.get(baseUrl)
cookies = dict(cookieName=reply.cookies[cookieName])
if reply.status_code == 200:
    doc = reply.json()
    print("Databases: " + str(doc))
else:
    printError("Unable to retrieve database listing",reply)

print("# 1.2 Delete all databases")
reply = requests.delete(baseUrl, cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print(doc)
else:
    printError("Unable to drop all databases",reply)

print("# 1.3 Create a database (explicitly)")
data = json.dumps({'name':'demo'})
reply = requests.post(baseUrl,data, cookies=cookies)
if reply.status_code == 201:
    doc = reply.json()
    print(doc)
else:
    printError("Unable to create database",reply)

print("# Verify the database was created by listing all databases")
reply = requests.get(baseUrl, cookies=cookies)
doc = reply.json()
print("Databases: " + str(doc))

print("# 1.4 Delete a database")
reply = requests.delete(baseUrl + "/demo", cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print(doc)
else:
    printError("Unable to drop database",reply)