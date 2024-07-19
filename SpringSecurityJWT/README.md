# Spring-security-basic
En este primer proyecto se realiza la creación de las politicas y configuraciones necesarias 
para el uso de Spring Security, pero en este caso no se llega a implementar JWT, ya que primero
se buscó conocer y profundizar en la base de Spring Security

### **Se sigue la guía del video:**
### https://www.youtube.com/watch?v=IPWBQDMIYkc

La arquitectura seguida dentro de los componentes y configuraciones de spring security aplicadas
en este proyecto son los de la siguiente imagen (tomada del video)

Se sigue la guía del video:
https://www.youtube.com/watch?v=IPWBQDMIYkc

En el tema de la base de datos usada se utilizó docker

```
docker run --name microservice -e MYSQL_ROOT_PASSWORD=150919 -d -p 3306:3306 mysql:latest
```