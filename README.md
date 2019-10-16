# JettyWithSpringBoot

This project demonstrates how to get started with using SSL on a Embedded Jetty Server in Spring Boot and to perform HTTP/2 Request.

Initially, you will need to get rid of using Tomcat from `started-web`. And include `jetty` in `build.gradle`.

1) Generate Self-Signed Certificate using `keytool`. `keytool` is bundled with JDK, so it has to be available already.
2) Put the generated `.jks` file under `src/main/resources`. During Start up, the files in this directory gets copied to `classpath`.
3) Look at the `application.properties` for a sample path using `classpath`.
4) When the server starts up, the jks file is read from `classpath` and SSL is supported by the Spring Boot Application.

Still the request will be using HTTP1.1 since JDK8 does not support HTTP/2 out of box.

So, we need to include the conscrypt library as in `build.gradle`. 

Now, perform `curl -v http://localhost:8080/greeting` which gives info that it is connected through HTTP2.
Then, perform `curl -v -k http://localhost:5432/greeting` which uses traditional HTTP1.1 for serving the request.

Hence, we have achieved HTTP/2 using Jetty and Spring Boot using the Conscrypt Library.
