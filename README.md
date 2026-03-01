# User and Transaction Relationship Visualization System
This project is a Spring Boot + Neo4j based backend system designed to detect suspicious user relationships and transaction patterns using graph database analysis.
It identifies:
- Users sharing the same device
- Users sharing the same IP address
- Suspicious transaction clusters
- Fraud relationship networks
The system is fully Dockerized and runs using containerized Spring Boot and Neo4j.
## Architecture
Browser->Spring Boot REST API (Docker)->Neo4j Graph Database (Docker)
## Tech Stack
- Java 17
- Spring Boot
- Spring Data Neo4j
- Neo4j 5.x
- Docker
- Maven
- Git

## Features
### Graph-Based Relationship Visualization
- Interactive Neo4j graph visualization
- Displays:
   - Users
   - Transactions
   - Relationships between users and transactions
- Detects shared:
   - Devices
   - IP addresses
   - Suspicious transaction clusters

### Shortest Path Detection
- Find the shortest relationship path between two users
- Useful for:
   - Fraud ring detection
   - Relationship tracing
   - Network investigation

### Advanced Filtering
- Filter users by:
   - Minimum transaction count
- Filter transactions by:
   - Minimum transaction amount
- Search specific:
   - User
   - Transaction

### Export Graph Data
- Export current graph as JSON
- Useful for:
   - Reporting
   - Debugging
   - External analytics tools

### Depth Control
- Control graph expansion depth
- Prevents graph overload
- Improves visualization performance

### Live Graph Loading
- Load full graph dynamically
- Refresh data without restarting backend

## Controls

| Control                   | Description                                              |
| ------------------------- | -------------------------------------------------------- |
| **User 1 / User 2 Input** | Select two users to compute shortest path                |
| **Shortest Path**         | Calculates and visualizes the shortest relationship path |
| **Export JSON**           | Downloads current graph data as JSON                     |
| **Load Graph**            | Loads graph based on selected filters                    |
| **Depth Selector**        | Controls how many relationship levels to expand          |
| **Min User Transactions** | Filters users by minimum number of transactions          |
| **Filter Users**          | Applies user transaction filter                          |
| **Min Amount**            | Filters transactions above specified amount              |
| **Filter Transactions**   | Applies transaction amount filter                        |
| **Show All Users**        | Displays all users in the graph                          |
| **Show All Transactions** | Displays all transactions                                |
| **Search User**           | Finds and highlights specific user node                  |
| **Search Transaction**    | Finds and highlights specific transaction node           |
  
## How to Run the Project
1.	Download zip file of the project and extract.
2.	Install Docker https://www.docker.com/products/docker-desktop/
Works for: Windows, Mac, Linux

After Installation open Docker to make sure it's updated and working.

3. Open PowerShell/CMD for Windows or Terminal for MAC  
   Navigate to project folder using command:

   ```bash
   cd path/to/project
   ```

4. Start container using command:

   ```bash
   docker compose up -d
   ```
   
- Access Neo4j UI- http://localhost:7474
   - UserId-Neo4j
   - Password-password

- Access UI using- http://localhost:8080

  Now the System is almost ready to use. Just few Steps for data generation.

5. Generate and Clean Old Data

   In a new tab in the same browser where the UI is already accessed, use the mentioned HTTP endpoints for different data generation and cleaning actions.

   - http://localhost:8080/api/data/reset  
     → Delete all data.

   - http://localhost:8080/api/data/generate-demo  
     → Generate demo data which contains:

     - At least 5–10 users with shared attributes
     - At least 10–15 transactions with a mix of direct and indirect links
     - 3–5 examples of shared attributes (e.g., phone, email) that cause user-to-user links
     - 2–3 transaction-to-transaction links based on IP, Device ID, or similar identifiers

     After generation, open http://localhost:8080 again to access the UI and click **Generate Graph** to visualize the data.

   - http://localhost:8080/api/data/generate-users  
     → Generate 200 users.

   - http://localhost:8080/api/data/generate-large  
     → Generate 100,000 transactions.

   **Note:** Use `generate-users` and `generate-large` together to create a bigger dataset for visualization.



