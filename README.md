# JettyWithSpringBoot

This project demonstrates how to get started with using SSL on a Embedded Jetty Server in Spring Boot and to perform HTTP/2 Request.

Initially, you will need to get rid of using Tomcat from `started-web`. And include `jetty` in `build.gradle`.

1) Generate Self-Signed Certificate using `keytool`. `keytool` is bundled with JDK, so it has to be available already.
2) Put the generated `.jks` file under `src/main/resources`. During Start up, the files in this directory gets copied to `classpath`.
3) Look at the `application.properties` for a sample path using `classpath`.
4) When the server starts up, the jks file is read from `classpath` and SSL is supported by the Spring Boot Application.

Still the request will be using HTTP1.1 since JDK8 does not support HTTP/2 out of box.

So, we need to include the conscrypt library as in `build.gradle`. 

Now, perform `curl -k -v https://localhost:8080/greeting` which gives output like

*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to localhost (127.0.0.1) port 8080 (#0)
* ALPN, offering h2
* ALPN, offering http/1.1
* successfully set certificate verify locations:
*   CAfile: /etc/ssl/certs/ca-certificates.crt
  CApath: /etc/ssl/certs
* TLSv1.3 (OUT), TLS handshake, Client hello (1):
* TLSv1.3 (IN), TLS handshake, Server hello (2):
* TLSv1.3 (IN), TLS Unknown, Certificate Status (22):
* TLSv1.3 (IN), TLS handshake, Unknown (8):
* TLSv1.3 (IN), TLS handshake, Certificate (11):
* TLSv1.3 (IN), TLS handshake, CERT verify (15):
* TLSv1.3 (IN), TLS handshake, Finished (20):
* TLSv1.3 (OUT), TLS change cipher, Client hello (1):
* TLSv1.3 (OUT), TLS Unknown, Certificate Status (22):
* TLSv1.3 (OUT), TLS handshake, Finished (20):
* SSL connection using TLSv1.3 / TLS_AES_256_GCM_SHA384
* ALPN, server accepted to use h2
* Server certificate:
*  subject: C=Unknown; ST=Unknown; L=Unknown; O=Unknown; OU=Unknown; CN=Unknown
*  start date: Oct  1 18:51:17 2019 GMT
*  expire date: Sep  7 18:51:17 2119 GMT
*  issuer: C=Unknown; ST=Unknown; L=Unknown; O=Unknown; OU=Unknown; CN=Unknown
*  SSL certificate verify result: self signed certificate (18), continuing anyway.
* Using HTTP2, server supports multi-use
* Connection state changed (HTTP/2 confirmed)
* Copying HTTP/2 data in stream buffer to connection buffer after upgrade: len=0
* TLSv1.3 (OUT), TLS Unknown, Unknown (23):
* TLSv1.3 (OUT), TLS Unknown, Unknown (23):
* TLSv1.3 (OUT), TLS Unknown, Unknown (23):
* Using Stream ID: 1 (easy handle 0x5589e4c10580)
* TLSv1.3 (OUT), TLS Unknown, Unknown (23):
> GET /greeting HTTP/2
> Host: localhost:8080
> User-Agent: curl/7.58.0
> Accept: */*
> 
* TLSv1.3 (IN), TLS Unknown, Unknown (23):
* Connection state changed (MAX_CONCURRENT_STREAMS updated)!
* TLSv1.3 (OUT), TLS Unknown, Unknown (23):
* TLSv1.3 (IN), TLS Unknown, Unknown (23):
* TLSv1.3 (IN), TLS Unknown, Unknown (23):
* TLSv1.3 (IN), TLS Unknown, Unknown (23):
* TLSv1.3 (IN), TLS Unknown, Unknown (23):
< HTTP/2 200 
< date: Tue, 01 Oct 2019 20:07:45 GMT
< content-type: application/json;charset=utf-8
< 
* TLSv1.3 (IN), TLS Unknown, Unknown (23):
* TLSv1.3 (IN), TLS Unknown, Unknown (23):
* TLSv1.3 (IN), TLS Unknown, Unknown (23):
* Connection #0 to host localhost left intact
{"id":1,"content":"Hello, World!"}


Hence, we have achieved HTTP/2 using Jetty and Spring Boot.
