module ApplicationHelper
	# port 8081
	def runHelloWorld()

		outPut = Array.new
		# using local connection for testing, use VCAP for deployment to bluemix
		mongo_client = Mongo::Client.new(["lxvm-l165.lenexa.ibm.com:27018"])
=begin  for use with bluemix
vcap_services = JSON.parse(ENV('VCAP_SERVICES'))
dbcred = vcap_services["credentials"]
hostname = dbcred["hostname"]
port = dbcred["port"]
mongo_client = Mongo::Client.new([hostname:port])
=end
		db = mongo_client.database
		#outPut.push(db.inspect) works, so shows db is actual database object
		collectionName = "rubyMongo"
		outPut.push("Creating collection 'rubyMongo' in admin database")
		outPut.push(" ")
		mongo_client[:collectionName].drop # make sure it does not already exist
		collection = mongo_client[:collectionName]
		collection.create

		outPut.push("Insert a single document to a collection") 
		collection.insert_one({ :name => "test1", :value => 1})
		outPut.push("Inserted {\"name\": \"test1\", \"value\": 1}" )  
		outPut.push(" ")

		outPut.push("Inserting multiple entries into collection")
		multiPost = [{:name => "test1", :value => 1},{:name => "test2", :value => 2}, {:name => "test3", :value => 3}] 
		collection.insert_many(multiPost)
		outPut.push("Inserted {name: \"test1\", value: 1} {name: \"test2\", value: 2} {name: \"test3\", value: 3}")

		outPut.push("Find one that matches a query condition")
		item = collection.find({name: "test1"}).limit(1)
		outPut.push(item.to_json)
		outPut.push(" ")

		outPut.push("Find all that match a query condition")
		# First input another entry for test1 so that there are multiple docs with same name
		collection.insert_one({:name => "test1", :value => 10})
		collection.find(:name => "test1").each do |document|
			outPut.push(document.to_json)
		end
		outPut.push(" ")

		outPut.push("Find all documents in collection")
		collection.find().each do |document|
			outPut.push(document.to_json)
		end

		outPut.push("Updating entry with attribute")
		collection.find(:name => "test3").update_many("$set" => { :value => 4})
		outPut.push("Updated test3 with value 4")
		outPut.push(" ")

		outPut.push("Delete all with attribute")
		collection.find(:name => "test2").delete_many 
		outPut.push("Deleted all with name test2")
		outPut.push(" ")
=begin
		outPut.push("Get a list of all of the collections")
		outPut.push(db.collection_names)
		Having issues with following
		database.collections and database.collection_names and database.list_collections
		gives: The regular expression /system\.|\$/ is not supported. () 
=end
		outPut.push("Drop a collection")
		collection.drop

		db.drop
		outPut.push(" ")
		outPut.push("Test Finished")
	end
end
