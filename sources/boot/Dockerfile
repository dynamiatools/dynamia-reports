# Imagen base ligera con Java 21
FROM eclipse-temurin:21-jdk-noble

# Zona horaria y localización (opcional)
ENV TZ=America/Bogota \
    LANG=es_CO.UTF-8 \
    LANGUAGE=es_CO:es \
    LC_ALL=es_CO.UTF-8

# Crea el directorio de la app
WORKDIR /app

# Copia el .jar compilado al contenedor
COPY target/*.jar app.jar

# Puerto expuesto (modificable)
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java","-jar","/app/app.jar"]
