FROM openjdk:21-slim

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .

# Copy the source code
COPY src ./src

# Package the application
RUN ["mvn", "clean", "package"]

# Specify the command to run the application
CMD ["java", "-jar", "target/Profile-1.0.jar"]

