import json
import requests

##
# Python sample application: connecting to Informix using REST
#
# Working with timeseries data and objects
##

# Topics
# 1 TimeSeries Row Types
# 1.1 List all TimeSeries-compatible row types
# 1.2 Create a new TimeSeries row type
# 1.3 Find a TimeSeries row type
# 2 TimeSeries Calendar
# 2.1 Create a TimeSeries calendar
# 2.2 Find a TimeSeries calendar
# 3 TimeSeries Containers
# 3.1 Create a TimeSeries container
# 3.2 Find a TimeSeries container
# 4 TimeSeries Tables & and VTI views
# 4.1 Create a TimeSeries table
# 4.2 Create a TimeSeries VTI view
# 4.3 Insert into TimeSeries through the VTI view
# 4.4 Query TimeSeries using the VTI view
# 5 Delete TimeSeries objects
# 5.1 Delete a TimeSeries VTI view
# 5.2 Delete a TimeSeries table
# 5.3 Delete a TimeSeries container
# 5.4 Delete a TimeSeries calendar
# 5.5 Delete a TimeSeries row type

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

print("# 1 TimeSeries Row Types")
print("# 1.1 Create a new TimeSeries row type")
data = json.dumps({ 'name': 'reading', 'fields': [ {'name':'tstamp', 'type':'datetime year to fraction(5)'}, {'name':'temp', 'type':'float'}, {'name':'hum', 'type':'float'} ] })
reply = requests.post(baseDbUrl + "/system.timeseries.rowType", data, auth=authInfo)
cookies = dict(cookieName=reply.cookies[cookieName])
if reply.status_code == 200:
    doc = reply.json()
    print ("created " + str(doc[0].get('n')) + " row type")
else:
    printError("Unable to create TimeSeries row type", reply)

print("# 1.2 List all TimeSeries-compatible row types")
reply = requests.get(baseDbUrl + "/system.timeseries.rowType", cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print(doc)
else:
    printError("Unable list TimeSeries row type", reply)

print("# 1.3 Find a TimeSeries row type")
query = json.dumps({ 'name':'reading'})
reply = requests.get(baseDbUrl + "/system.timeseries.rowType?query=" + query, cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print("query results: " + str(doc))
else:
    printError("Unable to query TimeSeries row type", reply)


print("# 2 TimeSeries Calendar")
print("# 2.1 Create a TimeSeries calendar")
data = json.dumps({"name":"ts_10min", "calendarStart":"2015-01-01 00:00:00", "patternStart":"2015-01-01 00:00:00", "pattern":{"type":"minute", "intervals":[{"duration":"1","on":"true"}, {"duration":"9","on":"false"}]}})
reply = requests.post(baseDbUrl + "/system.timeseries.calendar", data, cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print ("created " + str(doc[0].get('n')) + " calendar")
else:
    printError("Unable to create TimeSeries calendar", reply)
    
print("# 2.2 Find a TimeSeries calendar")
query = json.dumps({ 'name':'ts_10min'})
reply = requests.get(baseDbUrl + "/system.timeseries.calendar?query=" + query, cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print("query results: " + str(doc))
else:
    printError("Unable to query TimeSeries calendar", reply)


print("# 3 TimeSeries Containers")
print("# 3.1 Create a TimeSeries container")
data = json.dumps({"name":"c_0", "rowTypeName":"reading", "dbspaceName":"dbspace1", "firstExtent":1000, "nextExtent":500})
reply = requests.post(baseDbUrl + "/system.timeseries.container", data, cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print ("created " + str(doc[0].get('n')) + " container")
else:
    printError("Unable to create TimeSeries container", reply)
    
print("# 3.2 Find a TimeSeries container")
query = json.dumps({ 'name':'c_0'})

reply = requests.get(baseDbUrl + "/system.timeseries.container?query=" + query, cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print("query results: " + str(doc))
else:
    printError("Unable to query TimeSeries container", reply)

print("# 4 TimeSeries Tables & and VTI views")
print("# 4.1 Create a TimeSeries table")
data = json.dumps({'name':'reading_data', 'options':{'columns':[ {'name':'sensor_id', 'type':'int', 'primaryKey':True}, {'name':'tsdata', 'type':'Timeseries(reading)'} ]}})
reply = requests.post(baseDbUrl, data, cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print(doc)
else:
    printError("Unable to create TimeSeries table", reply)
    
print("# 4.2 Create a TimeSeries VTI view")
data = json.dumps({'name':'reading_data_v', 'options':{"timeseriesVirtualTable": { "baseTableName":"reading_data", "newTimeseries":"calendar(ts_10min),origin(2015-01-01 00:00:00.00000),container(c_0)", "virtualTableMode":0, "timeseriesColumnName":"tsdata"} } })
reply = requests.post(baseDbUrl, data, cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print(doc)
else:
    printError("Unable to create TimeSeries VTI view", reply)

print("# 4.3 Insert into TimeSeries through the VTI view")
data = json.dumps({'sensor_id':1001, 'tstamp':'2015-06-01 16:00:00', 'temp':71, 'hum':0.85})
reply = requests.post(baseDbUrl + "/reading_data_v", data, cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print ("inserted " + str(doc[0].get('n')) + " row")
else:
    printError("Unable to insert into TimeSeries VTI view", reply)
data = json.dumps({'sensor_id':1001, 'tstamp':'2015-06-01 17:00:00', 'temp':69, 'hum':0.88})
reply = requests.post(baseDbUrl + "/reading_data_v", data, cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print ("inserted " + str(doc[0].get('n')) + " row")
else:
    printError("Unable to insert into TimeSeries VTI view", reply)
data = json.dumps({'sensor_id':1001, 'tstamp':'2015-06-01 18:00:00', 'temp':69, 'hum':0.93})
reply = requests.post(baseDbUrl + "/reading_data_v", data, cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print ("inserted " + str(doc[0].get('n')) + " row")
else:
    printError("Unable to insert into TimeSeries VTI view", reply)

print("# 4.4 Query TimeSeries using the VTI view")
reply = requests.get(baseDbUrl + "/reading_data_v", cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print("query results: " + str(doc))
else:
    printError("Unable to query from TimeSeries VTI view", reply)

print("# 5 Delete TimeSeries objects")
print("# 5.1 Delete a TimeSeries VTI view")
reply = requests.delete(baseDbUrl + "/reading_data_v", cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print(doc)
else:
    printError("Unable to drop Timeseries VTI view", reply)

print("# 5.2 Delete a TimeSeries table")
reply = requests.delete(baseDbUrl + "/reading_data", cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print(doc)
else:
    printError("Unable to drop Timeseries table", reply)

print("# 5.3 Delete a TimeSeries container")
query = json.dumps({ 'name':'c_0'})
reply = requests.delete(baseDbUrl + "/system.timeseries.container?query=" + query, cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print ("deleted " + str(doc.get('n')) + " container")
else:
    printError("Unable to delete TimeSeries container", reply)

print("# 5.4 Delete a TimeSeries calendar")
query = json.dumps({ 'name':'ts_10min'})
reply = requests.delete(baseDbUrl + "/system.timeseries.calendar?query=" + query, cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print ("deleted " + str(doc.get('n')) + " calendar")
else:
    printError("Unable to delete TimeSeries calendar", reply)
    
print("# 5.5 Delete a TimeSeries row type")
query = json.dumps({ 'name':'reading'})
reply = requests.delete(baseDbUrl + "/system.timeseries.rowType?query=" + query, cookies=cookies)
if reply.status_code == 200:
    doc = reply.json()
    print ("deleted row type")
else:
    printError("Unable to delete TimeSeries row type", reply)