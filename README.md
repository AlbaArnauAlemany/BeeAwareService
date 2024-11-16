BeeAware-Service
App SOAR 2024

Pollenic Allergy Monitoring Platform
Allergies affect a large portion of the population, and their symptoms can be exacerbated by various environmental factors, including weather conditions. The platform's goal is to provide users with a channel for information on high-risk periods based on daily pollen forecasts to help them anticipate potential symptoms. Additionally, it will allow users to log their perceived symptoms daily on a subjective scale so they can compare these symptoms with the pollen load index. Users can also record days when an antihistamine was taken to observe its impact on symptoms. All this information will be grouped in a graph, providing users a simple and effective visualization tool to review their personal history, helping them act accordingly or discuss with a healthcare professional. The platform will rely on a Google Pollen API for pollen load forecasts to retrieve pollen indices based on the user's specified location.

To initialize the service project, create un application.properties in /resources and add your Google APY_KEY (with google pollen api)

Example:
BeeAwareService/src/main/resources/application.properties
API_KEY=AIzagfggfhj54KJHGJHjkhkjjYVOByqwEQ

A docker is available if you want to try the service without installing Payara, just go to :  
BeeAwareService/docker
and run the command :
docker-compose up
