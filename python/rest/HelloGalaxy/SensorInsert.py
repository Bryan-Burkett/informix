import json,random,requests,time
from datetime import datetime

url="http://localhost:8080/demo/sensordata"
iterations=10 # the number of times to insert a value
delay=5 # the delay in seconds between iterations
minValue=0
maxValue=100

epoch = datetime.utcfromtimestamp(0)
def currentMillis():
	delta = datetime.now() - epoch
	ms = (delta.days * 24 * 60 * 60 + delta.seconds) * 1000 + delta.microseconds / 1000.0
	return ms

# Ensure the collection is empty
reply=requests.delete(url)

i=0
while i < iterations or iterations == 0:
	temperature=random.randrange(minValue,maxValue)
	timestamp=currentMillis()
	#print "timestamp: " + str(timestamp)
	print "temperature: " + str(temperature)
	data=json.dumps({'t': { '$date' : timestamp }, '_id':1, 'temperature': temperature})
	reply = requests.put(url, data)
	if reply.status_code == 200:
		doc = reply.json()
		print "inserted " + str(doc.get('n')) + " documents"
	else:
		print "status code: " + str(reply.status_code)
		print "content: " + reply.content
	i+=1
	time.sleep(delay)
