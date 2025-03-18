FROM eclipse-temurin:21-jre-ubi9-minimal
COPY target/giftcard-payment-card-manager-1.0.0.jar app.jar
CMD ["java", "-Xms1g", "-jar", "app.jar"]