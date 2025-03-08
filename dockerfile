FROM maven:3.6.3-openjdk-17
WORKDIR / tests
COPY . .
CMD mvn clean test