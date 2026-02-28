# User and Transaction Relationship Visualization System
This project is a Spring Boot + Neo4j based backend system designed to detect suspicious user relationships and transaction patterns using graph database analysis.
It identifies:
- Users sharing the same device
- Users sharing the same IP address
- Suspicious transaction clusters
- Fraud relationship networks
The system is fully Dockerized and runs using containerized Spring Boot and Neo4j.
## Architecture
Browser
   ↓
Spring Boot REST API (Docker)
   ↓
Neo4j Graph Database (Docker)
## Tech Stack
- Java 17
- Spring Boot
- Spring Data Neo4j
- Neo4j 5.x
- Docker
- Maven
- Git
## How to Run the Project
1.	Download zip file of the project and extract.
2.	Install Docker https://www.docker.com/products/docker-desktop/
Works for: Windows, Mac, Linux
3.	Open PowerShell/CMD for windows or Terminal for MAC
Navigate to project folder using command-
```bash
cd path/to/project
```
4.	Create Docker Network
```bash
docker network create relationship-network
```
5.	Start Neo4j container using command
```bash
docker run -d --name neo4j \
--network relationship-network \
-p 7474:7474 \
-p 7687:7687 \
-e NEO4J_AUTH=neo4j/password \
neo4j:5.20
```
Access Neo4j UI- http://localhost:7474

6.	Build spring Boot App
First Build JAR using command:
```bash
.\mvnw clean package -DskipTests
```
 for windows
```bash
./mvnw clean package -DskipTests
```
for MAC/Linux
Now Build Docker Image
```bash
docker build -t relationshipsystem-app .
```
7.	Start Application Container using command
```bash
docker run -d --name relationship-app \
--network relationship-network \
-p 8080:8080 \
-e SPRING_NEO4J_URI=bolt://neo4j:7687 \
-e SPRING_NEO4J_AUTHENTICATION_USERNAME=neo4j \
-e SPRING_NEO4J_AUTHENTICATION_PASSWORD=password \
relationshipsystem-app
```
Access UI using- http://localhost:8080
Now the System is almost ready to use. Just few Steps for data generation.
9.	Generate and clean old Data
In a new Tab in the same browser where UI is already accessed, use mentioned http for different data generation and cleaning actions.
http://localhost:8080/api/data/reset - to delete all data.
http://localhost:8080/api/data/generate-demo - generate demo data which contains 
- At least 5-10 Users with shared attributes. 
- At least 10-15 Transactions with a mix of direct and indirect links. 
- 3-5 examples of Shared Attributes (e.g., phone, email) that cause user-to user links. 
- 2-3 Transaction-to-Transaction links based on IP, Device ID, or similar identifiers
http://localhost:8080/api/data/generate-users - to generate 200 users
http://localhost:8080/api/data/generate-large - to generate 100,000 transactions 
Note- Use generate-users and generate-large should be used together to generate bigger data set for visualization.


