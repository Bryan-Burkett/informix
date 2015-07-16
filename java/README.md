##Pick the right client driver or API for your application:

###Informix JDBC driver (SQLI)
- Your best choice if you want to use extended Informix data types, like time series. 
- You write your queries as SQL statements, which can include prepared statements. 
- Supports all Informix data types, including extended data types and user-defined data types. 
- Uses the native Informix connection protocols.

###Data Server JDBC or ibm_db (CLI) driver (DRDA)
- Your best choice if your application needs to connect to DB2® databases. 
- You write your queries as SQL statements, which can include prepared statements. 
- Does not support Informix extended data types. 
- Uses the DRDA® connection protocols. 

###REST API
- Your best choice if you want direct connections without a client driver. 
- You write queries with the REST API. 
- Supports all Informix data types, including extended data types and user-defined data types. 
- Uses REST API connections through the Informix wire listener.

###MongoDB driver
- Your best choice for applications with JSON data. 
- You write queries with MongoDB commands. 
- Supports all Informix data types, including extended data types and user-defined data types. 
- Uses the MongoDB Wire protocol through the Informix wire listener. 