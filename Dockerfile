FROM openjdk:17
ADD build/libs/password_cracker-0.0.1-SNAPSHOT.jar password_cracker-0.0.1-SNAPSHOT.jar
ADD .env .env
EXPOSE 8080
ENV SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/password_cracker
ENTRYPOINT ["java", "-jar", "password_cracker-0.0.1-SNAPSHOT.jar"]