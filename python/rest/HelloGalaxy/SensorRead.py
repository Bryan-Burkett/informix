import requests, json, time

url="http://localhost:8080/demo/sensordata"
iterations=10 # the number of times to read a value
delay=5 # the delay in seconds between iterations

i=0
while i < iterations or iterations == 0:
	payload={'query':'{"_id":1}', 'fields':'{"timestamp":1,"temperature":1}' , 'sort': '{"timestamp":-1}' , 'limit':'1'  }	
	reply = requests.get(url, params=payload)
	if reply.status_code == 200:
		doc = reply.json()
		print doc
		#print "latest reading " + str(doc.get('value'))
	else:
		print "status code: " + str(reply.status_code)
		print "content: " + reply.content
	i+=1
	time.sleep(delay)
