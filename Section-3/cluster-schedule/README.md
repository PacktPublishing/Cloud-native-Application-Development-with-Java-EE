# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 3.3: Clustered Scheduling and Coordination with EJBs

### Step 1: Multiple deployments with Docker Compose

To simulate a clustered deployment we are going to start the same service as two individual containers.
Add the following snippet to your `docker-compose.yml` file:
```yaml
version: "3"

services:  
  cluster-schedule-1:
    build:
      context: .
    image: cluster-schedule:1.0.1
    ports:
    - "8081:8080"
    networks:
    - jee8net

  cluster-schedule-2:
    image: cluster-schedule:1.0.1
    ports:
    - "8082:8080"
    networks:
    - jee8net

networks:
  jee8net:
    driver: bridge
``` 

### Step 2: 
