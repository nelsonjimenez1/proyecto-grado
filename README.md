Steps to run the tool:

Step 1:
Add graph.xlsx file into the input folder.
Step 2:
Open a console at the project folder root.
Step 3:
Execute the command "mvn package".
Step 4:
Execute the command "java -jar target/patition-tool-1.0-shaded.jar <inputPath> <groupId> <port>".
Where inputPath is the monolith path, groupId is the project groupId where the files of code begins and port is the initial port of the microservices, it must be greater than 2222.

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
	3. the first microservice: port parameter
	k. n microservice: port parameter + (n-1)
4. The project names for microservices are defined by the microservice column(last column) in the graph(sheet "nodes").
5. The graph structure is:
	1. sheet 1 "Nodes": columns are id, package, name, label, type, subType, microservice.
	2. sheet 2 "Edges": colmns are sourceId, destinyId, type, label.

Docker advices:
1. You must create a network to compile microservices projects
2. You must create images of each microservice with "docker build -t <image name> .", run the command at the root of the microservice folder
3. You must create container of register microservice image  with "docker run --name register --network <microservices net name> -dp
   1111:1111 <register image name> java -jar app.jar"
4. You must run command "docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' <register container name>"
   in order to get the register IP
5. You must create container of each microsevices and microservices-web project(endpoint) image with "docker run --name <container name>
   --network <microservices net name> -dp <microservice port>:<microservice port> <microservice image name> java -jar app.jar   --registration.server.hostname=<register IP>".
   The command parameter "registration.server.hostname" indicates to the microservice in which IP the register is located
