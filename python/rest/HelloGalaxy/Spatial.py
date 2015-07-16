import json, requests

# Topics
# 1    Insert GeoJSON data into a collection
# 2    Index spatial data
# 3    Query spatial data
# 3.1  $near, $nearSphere
# 3.2  $geoWithin
# 3.3  $geoIntersects
# 4    geoNear command

atlanta=json.dumps({'_id':'Atlanta', 'location': {'type': 'Point', 'coordinates': [0.0, -10.0] }, 'pop': 443000, 'region': 'south' })
cleveland=json.dumps({'_id':'Cleveland', 'location': {'type': 'Point', 'coordinates': [0.0, 10.0] }, 'pop': 391000, 'region': 'midwest' })
raleigh=json.dumps({'_id':'Raleigh', 'location': {'type': 'Point', 'coordinates': [5.0, 2.0] }, 'pop': 423000, 'region': 'south' })

#baseUrl="http://localhost:8080"
baseUrl="http://10.168.8.1:8080"
collectionUrl=baseUrl+"/spatial/cities"
authInfo=('user','pass')
cookieName="informixRestListener.sessionId"

requests.delete(collectionUrl)

def insertToCollection(url,document):
	reply = requests.post(url,document)
	if reply.status_code == 200:
		doc = reply.json()
		print "insert response: " + str(doc)
	else:
		print "Unable to insert " + str(document)
		print "status_code " + str(reply.status_code)
		print "content: " + reply.content

# 1. Insert GeoJSON data
insertToCollection(collectionUrl,atlanta)
insertToCollection(collectionUrl,cleveland)
insertToCollection(collectionUrl,raleigh)

# 2. Index spatial data
# Create a spatial index on a BSON field using Mongo syntax to create a "2dspehere" index
# Mongo Shell Equivalent: db.cities.ensureIndex({'location': '2dsphere'})
insertToCollection(baseUrl+'/spatial/system.indexes', json.dumps({'ns' : 'spatial.cities' , 'key' : { 'location' : '2dsphere' } , 'name' : 'location_2dsphere' }))

# 3. Query spatial data
# Four supported query operators: $near, $nearSphere, $geoWithin, $geoIntersects

# 3.1 $near, $nearSphere
# $near and $nearSphere are synonyms
# db.collection.find( {'location': { '$near': { '$geometry': { 'type': 'Point', 'coordinates': [ 2.0, 3.0 ] }, '$maxDistance': 100 } } } )
reply = requests.get(collectionUrl, params={'query': "{'location': {'$near': {'$geometry': { 'type': 'Point', 'coordinates': [2.0, 8.0] }, '$maxDistance': 1000000.0 } } }" })
cookies = dict(cookieName=reply.cookies[cookieName])
if reply.status_code == 200:
	doc = reply.json()
	for o in doc:
		print o
else:
	print "Unable to query cities"
	print "status code: " + str(reply.status_code)
	print "content: " + reply.content

# $maxDistance is optional

# 3.2 $geoWithin
#db.collection.find( {'location': { '$geoWithin': { '$geometry': { 'type': 'Polygon', 'coordinates': [ [2.0, 3.0], [0.0, 0.0] ] } } } } )

# 3.3 $geoIntersects

