# BeeAware-Service
## App SOAR 2024

### Pollenic Allergy Monitoring Platform
Allergies affect a large portion of the population, and their symptoms can be
exacerbated by various environmental factors, including weather conditions.
The platform's goal is to provide users with a channel for information on
high-risk periods based on daily pollen forecasts to help them anticipate
potential symptoms. Additionally, it will allow users to log their perceived
symptoms daily on a subjective scale so they can compare these symptoms with
the pollen load index. Users can also record days when an antihistamine was
taken to observe its impact on symptoms. All this information will be grouped
in a graph, providing users a simple and effective visualization tool to review
their personal history, helping them act accordingly or discuss with a healthcare
professional. The platform will rely on a Google Pollen API for pollen load
forecasts to retrieve pollen indices based on the user's specified location.

### Getting Started

To initialize the service project follow these steps:
1. **Create an `application.properties` file**:
    - Place this file in the `/resources` directory.
    - Add your Google API_KEY (for the Google Pollen API) in the file.

    Example: `BeeAwareService/src/main/resources/application.properties`
   ```properties
    API_KEY='YOUR_GOOGLE_API_KEY_HERE'
    ```

2. **Docker Configuration**:
    A Docker configuration is available if you want to try the service without
    installing Payara.
   - Simply navigate to the `BeeAwareService/docker` directory.
   - And run the following command:
    ```shell
    docker-compose up
    ```

3. **Testing the API**:
   - A Postman configuration file is provided to test the API with all our endpoints.
   - You can find the file at:
      `BeeAwareService/BEEZZER_SERVICE.postman_collection.json`

### Core Service Components

1. **BeezzerService**:
    - Core processing service for aggregating data and providing user-centric functionalities.

2. **GeoApiService**:
    - Manages interactions with the Google Maps API.

3. **ForeCastService**:
    - Handles the retrieval and processing of pollen forecasts with the Google Pollen API.
