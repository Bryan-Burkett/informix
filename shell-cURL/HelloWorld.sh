#!/bin/bash

# cURL is a command-line tool as well as a library for transferring data using network protocols such as HTTP and FTP.A
#
# Important Flags
# -X, --request <command>
#   Specifies a custom request method to use when communicating with the HTTP server. The default value is GET. Use POST, PUT, or DELETE to use other HTTP methods.
# -d, --data <data>
#   Sends the specified data in a POST request to the HTTP server.
# -H, --header <header>
#   Extra header to use when getting a web page.
# -i, --include
#   Include the HTTP-headers in the output. Useful with the REST listener to retrieve the cursorId.
# -b, --cookie <file name>
#   Specifies the file to read cookies from before an operation.
# -c, --cookie-jar <file name>
#   Specifies the file to write cookies to after an operation.
# -u, --user <user:password>
#   Specify the user name and password to use for server authentication. If you just give a user name, without entering a colon, curl will prompt you for a password.

# Read a single record from the namespace demo.people (db.collection) 
curl -b cookies.txt -c cookies.txt -X GET -i http://localhost:8080/demo/people?batchSize=1

# Assuming the cursorId is 4, get one more record from the query result set
curl -b cookies.txt -c cookies.txt -X GET -i -H "cursorId: 4" http://localhost:8080/demo/people?batchSize=1

# Insert a single record to the namespace demo.people
# When using cURL from the command-line some characters will need to be escaped, using the \ character, so that they are sent to the HTTP server and are not stripped out by the shell.
curl -b cookies.txt -c cookies.txt -X POST -i -H "Content-Type: application/json" --data-ascii \{name:\"lance\",age:34\} http://localhost:8080/demo/people

