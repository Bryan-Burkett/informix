##
# Python Sample Application: Connection to Informix using Mongo
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

from pymongo.mongo_client import MongoClient
import json
import os
from flask import Flask, render_template

app = Flask(__name__)
url = ""
database = ""
port = int(os.getenv('VCAP_APP_PORT', 8080))

# parsing vcap services
def parseVCAP():
    global database
    global url
    
    altadb = json.loads(os.environ['VCAP_SERVICES'])['altadb-dev'][0]
    credentials = altadb['credentials']
    database = credentials['db']
    
    ssl = False
    if ssl == True:
        url = credentials['ssl_json_url']
    else:
        url = credentials['json_url']

     
def doEverything():
#     certfile = '/home/bryan/Downloads/baratheon.pem'
    conn = MongoClient(url)
    db = conn[database]
    
    commands = []
    collectionName = "pythonMongo"
    commands.append("Creating collection " + collectionName)
    collection = db[collectionName]
    
    #insert 1
    commands.append("# 1 Inserts")
    commands.append("# 1.1 Insert a single document to a collection")
    collection.insert({"name": "test1", "value": 1})
    commands.append("Inserted {\"name\": \"test1\", \"value\": 1}")
    
    #insert many
    commands.append("#1.2 Inserting multiple entries into collection")
    multiPost = [{"name": "test1", "value": 1},{"name": "test2", "value": 2}, {"name": "test3", "value": 3}] 
    collection.insert(multiPost)
    commands.append("Inserted \n {\"name\": \"test1\", \"value\": 1} \n {\"name\": \"test2\", \"value\": 2} \n {\"name\": \"test3\", \"value\": 3}")
     
    # Find 
    commands.append("#2 Queries")
    commands.append("#2.1 Find one that matches a query condition")
    commands.append(collection.find_one({"name": "test1"}))
     
    # Find all 
    commands.append("#2.2 Find all that match a query condition")
    for doc in collection.find({"name": "test1"}):
        commands.append(doc)
    
    # Display all documents
    commands.append( "#2.3 Find all documents in collection")
    for doc in collection.find():
        commands.append(doc)   
    
    # update document
    commands.append("#3 Updating Documents")
    collection.update({"name": "test3"}, {"$set": { "value": 4}})
    commands.append("Updated test3 with value 4")
     
    # delete document
    commands.append("#4 Delete Documents")
    collection.remove({"name": "test2"})  
    commands.append("Deleted all with name test2")
    
    # Display all collection names
    commands.append("#5 Get a list of all of the collections")
    commands.append( db.collection_names())
    
    commands.append("#6 Drop a collection")
    db.drop_collection(collectionName)
    conn.close()
    commands.append("Connection to database has been closed")
    return commands

@app.route("/")
def printCommands():
    parseVCAP()
    commands = doEverything()
    return render_template('index.html', commands=commands)

if (__name__ == "__main__"):
    app.run(host='0.0.0.0', port=port)