# Integrate Spring Boot with Docker

## Prepare
We need:  
1. a Docker daemon server installed on Linux host(or VM)  
2. a spring-boot project to be integated into Docker  
3. a Docker Hub account  

Version:  
Docker 1.12.0  
Maven 3.3  
Spring Boot 1.4.2.RELEASE  
docker-maven-plugin spotify 0.4.11  

## Procedure
### 1. Docker daemon server
version: 1.12.0, based on Linux kernel 3.x  
In this case, CentOS 7.

#### 1.1 CA
1. Generate CA
Follow this guide, [Protect Docker daemon socket](https://docs.docker.com/engine/security/https/)

**Mind:**  
Make sure your IP is added into extfile.cnf.

#### 1.2 Startup and connection
1. Start up daemon with certificate mode:
> $ dockerd --tlsverify --tlscacert=ca.pem --tlscert=server-cert.pem --tlskey=server-key.pem \
  -H=0.0.0.0:2376

2. Test connection:
> $ docker --tlsverify --tlscacert=ca.pem --tlscert=cert.pem --tlskey=key.pem \
  -H=$HOST:2376 version
  
**Possible error:**  
> Cannot connect to the Docker daemon. Is the docker daemon running on this host?

Solution: check ip or firewalld, use curl command to detect.

### 2. Spring Boot
You can use Gradle or Maven to integerate with docker, in this case, we use Maven 3.x.

#### 2.1 pom.xml config

```
            <plugin>
                <groupId>com.spotify</groupId>
                <artifactId>docker-maven-plugin</artifactId>
                <version>${docker-maven.version}</version>
                <configuration>
                    <!-- Connect config for Windows Boot2docker -->
                    <!-- Mind!! Unable to push from this docker daemon server, unknown reason -->
                    <!--<dockerHost>https://192.168.59.103:2376</dockerHost>
                    <dockerCertPath>C:\Users\DT266\.boot2docker\certs\boot2docker-vm</dockerCertPath>-->
                    <!-- Connect config for Linux docker server -->
                    <!-- Mind!! docker daemon must start in secure mode, and CA must include local ip -->
                    <dockerHost>https://10.200.157.84:2376</dockerHost>
                    <!-- Use certs on local machine -->
                    <!--<dockerCertPath>C:\Users\DT266\.docker</dockerCertPath>-->
                    <!-- Use certs in project folder -->
                    <dockerCertPath>docker/certs</dockerCertPath>
                    <imageName>${docker.image.prefix}/${project.artifactId}</imageName>
                    <imageTags>v1</imageTags>
                    <dockerDirectory>src/main/docker</dockerDirectory>
                    <resources>
                        <resource>
                            <targetPath>/</targetPath>
                            <directory>${project.build.directory}</directory>
                            <include>${project.build.finalName}.jar</include>
                        </resource>
                    </resources>
                </configuration>
            </plugin>
```

#### 2.2 build command
Build docker image(and you'll find result on docker daemon server):
> mvn -X clean package docker:build

Follow this guide, [Spring Boot with Docker](https://spring.io/guides/gs/spring-boot-docker/)

**Possible error:**
> javax.net.ssl.SSLException: Unrecognized SSL message, plaintext connection?

Solution: Check if daemon server is startup in secure mode. This error may occur when daemon start without certificate.

### 3. Images management
Check images:
> docker image

(Requires tls secure by default configuration, or use "docker --tlsverify --tlscacert=ca.pem --tlscert=cert.pem --tlskey=key.pem   -H=$HOST:2376 image" instead)

Start up with specified name (_spring-boot_) and port mapping(visit 8080 of host to reach 8080 of container):
> docker run --name spring-boot -d -p 8080:8080 skyvoice/spring-boot-docker

Visit http://your-docker-host:8080/docker to check result.

Push this image into your Hub:
> docker login

> docker push skyvoice/spring-boot-docker

(Requires an account in Docker Hub and a prepared repository named _spring-boot-docker_. New version of Docker will not auto create repository any more.)
