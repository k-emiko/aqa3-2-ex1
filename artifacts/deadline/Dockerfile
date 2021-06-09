FROM openjdk:8-slim
WORKDIR ./app
COPY . .
CMD ["./wait-for-it.sh", "--timeout=20", "mysql:3306", "--", "java", "-jar", "app-deadline.jar"]
EXPOSE 9999

