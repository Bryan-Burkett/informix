import json, requests

# Topics
# 1
# 2

#baseurl="http://localhost:8080"
baseurl="http://10.168.8.1:8080/spatial/cities"
authInfo=('user','pass')
cookieName="informixRestListener.sessionId"

# 1. Insert GeoJSON data
reply = requests.post(baseurl,atlanta)
cookies = dict(cookieName=reply.cookies[cookieName])
if reply.status_code == 200:
	doc = reply.json()
	print doc
else:
	print "Unable to insert city"	
	print "status_code: " + str(reply.status_code)
	print "content: " + reply.content

