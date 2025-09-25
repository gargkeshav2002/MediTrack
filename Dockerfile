FROM openjdk:17-jdk
ADD target/hms-keshavgarg.jar hms-keshavgarg.jar
ENTRYPOINT ["java","-jar","/hms-keshavgarg.jar"]