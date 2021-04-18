Steps to run the tool:

Step 1:
Add graph.xlsx file into the input folder.
Step 2:
Open a console at the project folder root.
Step 3:
Execute the command "mvn package".
Step 4:
Execute the command "java -jar target/patition-tool-1.0-shaded.jar <inputPath> <groupId>".
Where inputPath is the monolith path and groupId is the project groupId.


Specs:
1. The repository types that are admitted for the tool are:
	CrudRepository
	PaginAndSortingRepository
	JPARepository
2. The tool will generate "microservices-register" and "microservices-web" projects and n projects for n microservices.
   Where "microservice-web" has the end-points for all generated microservices. 
3. The microservice ports are:
	1. "microsevices-register": 1111
	2. "microservices-web": 2222
	3. the first microservice: 3333
	n. n microservice: 3333 +(n-1)
4. The docker network created for  the microservices is called "microservices-net".
5. The project names for microservices are defined by the microservice column(last column) in the graph(sheet "nodes").
6. If you want to re-run the tool, you have to delete the output folder, then execute this command "docker network rm microsevices-net"
   and delete the generated container/images.
7. The graph structure is:
	1. sheet 1 "Nodes": columns are id, package, name, label, type, subType, microservice.
	2. sheet 2 "Edges": colmns are sourceId, destinyId, type, label.

 
 