module CatalogHelper
=begin  for use with bluemix
vcap_services = JSON.parse(ENV('VCAP_SERVICES'))
dbcred = vcap_services["credentials"]
hostname = dbcred["hostname"]
port = dbcred["port"]
mongo_client = Mongo::Client.new([hostname:port])
=end
	def runCatalog()

		def code_2xx?(responseCode)
			# response codes in 200's are returned upon success
			# this function should be more robust, but works for this specific use
			if responseCode.start_with?('2')
				return true
			else
				return false
			end
		end 

		# using local connection for testing, use VCAP for deployment to bluemix
		host = 'hostname'
		port = '23716'
		dbname="restdb"
		collectionName = "people"
		# create array to store info for output
		outPut = Array.new
		# Setup by dropping db if exists
		http = Net::HTTP.new(host, port)
		request = Net::HTTP::Get.new('/')
		response = http.request(request)
		outPut.push("Existing databases: #{response.body}") # intial databases
		if response.body.include? "#{dbname}"
			outPut.push("Dropping #{dbname}")
			request = Net::HTTP::Delete.new("/#{dbname}")
			response = http.request(request)
			unless code_2xx?(response.code)
				outPut.push("Failed to drop #{dbname}")
			end
		end
		outPut.push(" ")
		outPut.push("Implicitly create a collection by inserting into it")
		data = "{'firstName':'Luke', 'lastName':'Skywalker', 'age': 34})"  # data is in json format
		request = Net::HTTP::Post.new("/#{dbname}/#{collectionName}")
		request.content_type = 'application/json'
		request.body = data
		response = http.request(request)
		unless code_2xx?(response.code)
			outPut.push("Failed to create collection: #{collectionName}")
		end

		outPut.push(" ")
		outPut.push("List all collections in a database")
		request = Net::HTTP::Get.new("/#{dbname}")
		response = http.request(request)
		outPut.push("Existing collections: #{response.body}")

		outPut.push(" ")
		outPut.push("Query from a collection")
		request = Net::HTTP::Get.new("/#{dbname}/#{collectionName}")
		response = http.request(request)
		if code_2xx?(response.code)
			outPut.push("Query results: #{response.body}")
		else
			outPut.push("Query failed")
		end

		outPut.push(" ")
		outPut.push("Create a collection (explicitly)")
		data = "{'name':'deleteMe'}"
		request = Net::HTTP::Post.new("/#{dbname}")
		request.content_type = 'application/json'
		request.body = data
		response = http.request(request)
		unless code_2xx?(response.code)
			outPut.push("Failed to create collection: #{collectionName}")
		end

		outPut.push(" ")
		outPut.push("Drop a collection")
		request = Net::HTTP::Delete.new("/#{dbname}/deleteMe")
		response = http.request(request)
		unless code_2xx?(response.code)
			outPut.push("Failed to drop collection: deleteMe")
		end
		request = Net::HTTP::Delete.new("/#{dbname}/#{collectionName}")
		response = http.request(request)
		unless code_2xx?(response.code)
			outPut.push("Failed to drop collection: #{collectionName}")
		end

		outPut.push(" ")
		outPut.push("Create a relational table")
		data = "{ name: 'demotable', options:{columns:[ {name:'id', type:'int', primaryKey:true}, {name:'username', type:'varchar(30)'}, {name:'state', type:'char(2)'} ]}}"
		request = Net::HTTP::Post.new("/#{dbname}")
		request.body = data
		response = http.request(request)
		unless code_2xx?(response.code)
			outPut.push("Failed to create relational table")
			outPut.push("#{response.code}, #{response.body}")
		end

		outPut.push(" ")
		outPut.push("Insert into relational table")
		data = "{'id':101, 'username':'felix', 'state':'CA'}"
		request = Net::HTTP::Post.new("/#{dbname}/demotable")
		request.body = data
		response = http.request(request)
		unless code_2xx?(response.code)
			outPut.push("Failed to insert into relational table")
			outPut.push("#{response.code}, #{response.body}")
		end





		outPut.push(" ")
		request = Net::HTTP::Get.new("/#{dbname}")
		response = http.request(request)
		outPut.push("Existing collections: #{response.body}")


		return outPut
	end
    
end