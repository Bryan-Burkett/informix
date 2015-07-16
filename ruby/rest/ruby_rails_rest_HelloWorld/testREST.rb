
require 'net/http'
require 'uri'

$stdout.reopen("out.txt", "w")
$stderr.reopen("err.txt", "w")
uri = URI.parse("http://lxvm-l165.lenexa.ibm.com:27018")
#response = Net::HTTP.get_response(uri)
#Net::HTTP.get_print(uri)

http = Net::HTTP.new(uri.host, uri.port)
response = http.request(Net::HTTP::Get.new(uri.request_uri))

Net::HTTP.get_print(uri)

commands.append( "# 1.1 Insert a single document to a collection")
data = json.dumps({'name': 'test1', 'value': 1})
reply = requests.post(baseDbUrl + "/" + collectionName, data, auth=authInfo)
if reply.status_code == 200:
    doc = reply.json()
    commands.append("inserted " + str(doc.get('n')) + " documents")
else:
    printError("Unable to insert document", reply)







=begin
require 'rest-client'

$stdout.reopen("out.txt", "w")
$stderr.reopen("err.txt", "w")
baseUrl="http://lxvm-l165.lenexa.ibm.com:27018"
collectionName = "rubyREST"
#resource = RestClient::Resource.new("#{baseUrl}/#{collectionName}")
#resource.get
reply = RestClient::Request.execute(:method => :get, :url => "http://lxvm-l165.lenexa.ibm.com:27018", :verify_ssl => OpenSSL::SSL::VERIFY_NONE)
#reply = RestClient.get 'http://example.com/resource'
=end