# --- Stage 1: Build the .jar file using Maven ---
# We use a specific version of Maven with Java 17 to ensure consistency.
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory inside the temporary build container.
WORKDIR /app

# Copy only the pom.xml first to leverage Docker's build cache.
# Dependencies will only be re-downloaded if this file changes.
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of your application's source code.
COPY src ./src

# Compile the code and package it into a single executable .jar file, skipping tests.
RUN mvn package -DskipTests

# --- Stage 2: Create the final, lightweight image ---
# We use a minimal Java Runtime Environment for a smaller and more secure final image.
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory for the final image.
WORKDIR /app

# Copy the compiled .jar file from the 'build' stage into our final image.
COPY --from=build /app/target/*.jar app.jar

# Inform Docker that the application will listen on port 8081.
EXPOSE 8081

# The command that will be executed when the container starts.
ENTRYPOINT ["java", "-jar", "app.jar"]