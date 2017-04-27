# QDrill 
Distributing classification from within your SQL statements!
http://cs.queensu.ca/~khalifa/qdrill
### IBM SPSS Modeler Predictive Extensions
### Apache Drill Extension (https://drill.apache.org/)

## What is QDrill?
QDrill is a Consumable Analytics solution that addresses the shortage of skilled data analysts by offering analytic functionality in a form more familiar to in-house expertise. 

QDrill introduces the Analytics Adaptor extension for Apache Drill, a schema-free SQL query engine for non-relational storage. The Analytics Adaptor uses the new Distributed Analytics Query Language (DAQL) and the new Data Mining Distrinbution Algorithm (DMD) for invoking classification algorithms from within the Drill standard SQL query statements and distribute their execution. The adaptor allows using any sequential single-node data mining library (e.g. WEKA) and makes its algorithms run in a distributed fashion without having to rewrite them. QDrill also inhirets from Apache Drill the ability to access a large number of data stores. 

QDrill is scalable, offers an easy interface, no storage overhead and distributes ALL classification algorithms of WEKA, with the ability to extend to use algorithms from other data mining libraries.

## QDrill Architecture
<img align="center" src="https://github.com/skhalifa/QDrill/blob/master/Qdrill.png?raw=true" alt="QDrill Architecture">

## QDrill Supported Algorithms
<img align="center" src="https://github.com/skhalifa/QDrill/blob/master/QDrillAlg.png?raw=true" alt="QDrill Supported Algorithms">

## Current QDrill limitations
1. Supports Classification Only with nominal Labels that need to be specified during training
2. Numeric Attributes only


---
## Requirements to modify QDrill (Not required if you just want to use QDrill)
----
Clone both projects in this repository: drill 1.2 and Weka.
- drill 1.2 project is the modified Apache Drill 1.2 supporting the new Distributed Analytics Query Language (DAQL) and the new Analytics Adaptor for distributed execution of clustering algorithms.

- Weka project is the Analytics Plugin for Weka where the data transformation between Drill format and Weka's ARFF format is made and the WEKA APIs are used to execute WEKA algorithms. This project can also act as a template for developing Analytics plugins for other data mining libraries like R.

---
## Local Installation Instructions (Follow these steps to run Modeler on a single local Drill/QDrill node)
----
1. Download the following:

  a. QDrill 1.2 tar file (based on Apache Drill 1.2 and WEKA 3.7.13): https://github.com/skhalifa/QDrill/releases/tag/qdrill12
  
  b. QDrill Modeler Custom Node V0.91: https://github.com/skhalifa/QDrill/blob/master/QDrillv0.91.mpe?raw=true
  
  c. MapR Drill ODBC 64 Driver 1.02.01.1001: http://package.mapr.com/tools/MapR-ODBC/MapR_Drill/MapRDrill_odbc_v1.2.1.1000/MapRDrillODBC64.msi
  
  d. IBM SPSS Modeler 18 [Free 30-day trial]
  
  e. SPSS_Modeler_REssentials_18.0_Win64 https://www-01.ibm.com/marketing/iwm/iwm/web/preLogin.do?source=swg-tspssp
  
  f. R x64 3.2.1 https://cran.r-project.org/bin/windows/base/old/3.2.1/
  
  g. RODBC package [In R, type the following command: install.packages("RODBC")]

2. Install & Run Drill/QDrill Server (For more info, https://drill.apache.org/docs/install-drill-introduction/):
```bash
>> untar qdrill-1.2.0.tar
>> cd <path-to>\qdrill-1.2.0\bin
>> WINDOWS [Start the server and SQL shell] >> sqlline -u "jdbc:drill:schema=dfs.root;zk=local" -n admin -p admin
>> LINUX [Start the server] >> ./drillbit.sh start
>> LINUX [Start the SQL shell] >> ./drill-conf
```
3. Wait a minute for QDrill to start, then go to Drill/QDrill web interface (http://localhost:8047/) and enable the Storage Plugins you will be using. For more details, please check https://drill.apache.org/docs/connect-a-data-source-introduction/

4. Install:

  a. MapR Drill ODBC 64 Driver 1.02.01.1001 and configure it to the IP address of your drill server.
  
  b. RODBC package in R [In R, type the following command: install.packages("RODBC")]
  
  c. SPSS_Modeler_REssentials_18.0_Win64
  
5. Open SPSS Modeler, Go to the "Extensions" Tab and select "Custom Node Dialog Builder..." -> Hit Open -> Select QDrillv0.91.mpe -> Hit Install. Now you should see the QDrill node in your Modeling Palette undeer Classification.


---
## Distributed Installation Instructions (Follow these steps to run Modeler on a Drill/QDrill cluster)
----
1. Download the following:

  a. QDrill 1.2 tar file (based on Apache Drill 1.2 and WEKA 3.7.13): https://github.com/skhalifa/QDrill/releases/tag/qdrill12
  
  b. QDrill Modeler Custom Node V0.91: https://github.com/skhalifa/QDrill/blob/master/QDrillv0.91.mpe?raw=true
  
  c. MapR Drill ODBC 64 Driver 1.02.01.1001: http://package.mapr.com/tools/MapR-ODBC/MapR_Drill/MapRDrill_odbc_v1.2.1.1000/MapRDrillODBC64.msi
  
  d. IBM SPSS Modeler 18 [Free 30-day trial]
  
  e. SPSS_Modeler_REssentials_18.0_Win64 https://www-01.ibm.com/marketing/iwm/iwm/web/preLogin.do?source=swg-tspssp
  
  f. R x64 3.2.1 https://cran.r-project.org/bin/windows/base/old/3.2.1/
  
  g. RODBC package [In R, type the following command: install.packages("RODBC")]

2. Install & Run Drill/QDrill <b>on all Drill/QDrill cluster nodes</b> (For more info, https://drill.apache.org/docs/install-drill-introduction/):
```bash
>> untar qdrill-1.2.0.tar
>> cd <path-to>\qdrill-1.2.0\bin
>> LINUX [Start the server and SQL shell] >> ./drillbit.sh start
```
3. Wait a minute for QDrill to start, then go to Drill/QDrill web interface (http://server-ip:8047/) and enable the Storage Plugins you will be using. For more details, please check https://drill.apache.org/docs/connect-a-data-source-introduction/

4. Install <b>on your local computer where Modeler is running</b>:

  a. MapR Drill ODBC 64 Driver 1.02.01.1001 and configure it to the IP address of your drill server.
  
  b. RODBC package in R [In R, type the following command: install.packages("RODBC")]
  
  c. SPSS_Modeler_REssentials_18.0_Win64
  
5. Open SPSS Modeler, Go to the "Extensions" Tab and select "Custom Node Dialog Builder..." -> Hit Open -> Select QDrillv0.91.mpe -> Hit Install. Now you should see the QDrill node in your Modeling Palette under Classification.

---
## Using QDrill custom node from Modeler
----
Now you should see the QDrill node in your Modeling Palette under Classification.

1. Create a simple stream as follows:
<img src="https://github.com/skhalifa/QDrill/blob/master/q2.png?raw=true"/>

2. Configure the Source node to use the ODBC driver and to is the QDrill model storage adaptor (required to save WEKA models)
<img src="https://github.com/skhalifa/QDrill/blob/master/q3.png?raw=true"/>

3. Configure the QDrill node:
<img src="https://github.com/skhalifa/QDrill/blob/master/q4.png?raw=true"/>

 a. <b>Connecction String</b>, you can leave the default value which works with the default name for the ODBC connection (MapR Drill ODBC Driver for Drill DSN) and a localhost Drill/QDrill server. If you changed the name your ODBC connection or if you want to connect to a Drill/QDrill server that is not running on your localhost, you will need to update the connection string to the new values.
 
 
 b. <b> Model Name</b>, specify the name of the predictive model you want to create (must be unique).
 
 c. <b> Data Mining Algorithm</b>, choose one of the WEKA classification algorithms (all algorithms will be distributed on the QDrill cluster)
 
 d. <b>Classes</b>, enumerate the name of the classes (labels) you have in your data separated by commas.
 
 e. <b>Training Data</b>, the name of your trianing data source. For example iris.data.csv to use the iris.data.csv file on your HDFS. For data stored in MongoDB, use mongo.<database-name>.`<collection-name>` where the collection name MUST be between the quotes ` and `. For example mongo.test.'iris.data' to use the test.iris.data MongoDB document.
 
 f. <b>Scoring Data</b>, same as Training Data. However, scoring data is the data you want to predict its label. <b> A dummy Label attribute must be added as the last column in your scoring data file.</b> 
 
 g. <b>Number of Partitions</b>, is the number of threads you want to while building your model. More threads means faster training but can cause a drop in model's accuracy if your have a small dataset. 
 
 <b>Current QDrill Modeler custom node limitations:</b>
 
1. All Training data attributes must be concatenated using commas "," into a single attribute called "columns" (all lower case)

2. The Label attribute must be the last value in the "columns" string of the Training records.

3. All Scoring data attributes must be concatenated using commas "," into a single attribute called "columns" (all lower case)

4. A dummy Label attribute must be added as the last value in the "columns" string of the Scoring records at it must have a valid class label value.

---
## Using QDrill without Modeler (You can run these SQL queries from Java, php, R, Python or any other application using the JDBC/ODBC connection)
----
### Distributed Training of a WEKA Model Using DAQL
```mysql
SQL-1> USE dfs.tmp;
SQL-2> ALTER SESSION SET `store.format`='model';
SQL-3> TRAIN MODEL <model name> AS 
       SELECT qdm_ensemble_weka(mymodel)       
  FROM (SELECT qdm_ensemble_weka(‘<algorithm>',‘<args>',    
                       data.columns, data.label_column) as mymodel                        
       	    FROM (SELECT columns, qdm_LADP(<num parts>,             
                         columns, label_column) as partition                          
       		FROM `<Data Source>`          
       		WHERE <conditions>          
      	    ) as data            
  	    GROUP BY data.partition);
```
This statement trains a WEKA classifier ensemble using QDrill's LADP and DMD algorithms in a distributed fashion. The first SQL statement changes the storage location to a writable location. The second SQL statement tells the Drill Storage Adaptor to use the introduced Model Storage Plugin to save the model after training. The third SQL statement consists of three nested DAQL statement: 

•	The inner statement invokes the LADP partitioning algorithm using the qdm_LADP UDF with arguments: the number of partitions <num parts>, the record’s attributes <columns> and the record’s label <label_column>, respectively. This statement fetches the training data from any Drill-supported data store using the FROM clause. The FROM clause can also have a join between two heterogeneous data sources. The WHERE clause specifies any conditions on the records to fetch. 

•	The middle statement uses the new qdm_ensemble_weka UDF to train a classifier for each data partition using the GROUP BY clause to send records belonging to different partitions to the different Worker Nodes. The new qdm_ensemble_weka UDF defines the classifier algorithm, set its arguments, specify the data columns to use for training and specify the label column, respectively. 

•	The outer statement uses the new qdm_ensemble_weka UDF to aggregate the classifiers trained on the Worker Nodes into an Ensemble. Finally, the statement uses the TRAIN MODEL clause to save the Ensemble under <model name>.


### Distributed Scoring of a Trained WEKA Model Using DAQL
```mysql
SQL-1> USE dfs.tmp;
SQL-2> ALTER SESSION SET `store.format`=csv';
SQL-3> CREATE TABLE <results> AS 
       SELECT mydata.columns,       
               qdm_score_weka(mymodel.columns[0], mydata.columns)                
       FROM `<Data Source>` AS mydata        
                         APPLYING <model name> AS mymodel                         
       WHERE <conditions>;
```     
The first SQL statement changes the storage location to a writable location. The second SQL statement tells the Drill Storage Adaptor to save the scored records in CSV format. The third SQL statement fetches the unlabeled data using the FROM clause. The APPLYING keyword in the FROM clause tells Drill to fetch the trained model file <model name>. The WHERE clause specifies any conditions on the records to fetch. The SQL then uses the new qdm_score_weka UDF to apply the trained model on the unlabeled data. The UDF specifies the model and the data columns to use for scoring, respectively. This UDF outputs a label for each record in the unlabeled dataset. Finally, the SQL statement uses the CREATE TABLE clause to save the records along with their label in a new table <results>.

---
## Video Tutorials
----
- QDrill in Action Part 1: https://youtu.be/4UP1WpYUmXo
- QDrill in Action Part 2: https://youtu.be/vHxMLuxscAQ
- QDrill Architecture Explanation: https://youtu.be/X7FuFe1jtfg

---
## Further Documentation
----
- Check Apache Drill documentation for more details (Anything that works for Apache Drill will work for QDrill). There are more samples as well there: https://drill.apache.org/docs/

---
## License
----
[Apache 2.0]


## Publications
----
S.Khalifa, P. Martin, D. Rope, M. McRoberts, and C. Statchuk. 2016. QDrill: Query-Based Distributed Consumable Analytics for Big Data. IEEE International Congress on Big Data (BigData Congress). https://github.com/skhalifa/QDrill/blob/master/QDrill_20160212IEEE_CameraReady.pdf

## Contributors
----
  - Shadi Khalifa (khalifa@cs.queensu.ca)
