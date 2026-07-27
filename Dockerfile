# use jdk version 11 from our base image
FROM eclipse-temurin:11

# Go to the app directory inside the container
WORKDIR /app

# Copy the application into the /app directory inside the container
COPY . . 

# Run the application
CMD ["java", "-jar", "target/Scrabble-1.0-SNAPSHOT.jar"]

