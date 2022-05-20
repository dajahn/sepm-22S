# Ticketline 4.0

## Introduction of the project
Ticketline is a company operating throughout Austria, specializing in the sale of tickets for various events (e.g. cinema, theater, opera, concerts, etc.), as well as the sale of merchandising articles (or fan articles) for these events. In the nation's capitals, there are sales outlets for this purpose, where customers can can obtain information and buy tickets and fan merchandise directly.

To support the sales activities, a dedicated IT system called Ticketline Kassa, which is used by the employees in the sales outlets. It uses a client-server architecture. The server part is formed by a central database which is operated in the company's data center. The clients are rich client applications that are installed on the sales staff's computers and all access the central database.
Since the existing client application is based on outdated technology and the maintenance, as well as further development, costs too high, the management decided to replace the application. It was also decided that Ticketline should not only be an in-house application for employees, but should also be available directly to customers in the form of a webshop. After evaluating different technologies, the choice was made to use Java SE, Spring and Angular as the new platform.

An Apache Tomcat is to be used as the server, which will provide the Ticketline functionalities via REST interfaces. The main goal of the new development is to improve the processes within the application in order to guarantee a fast, smooth and direct sale of tickets and articles to the customers.

## Getting started

### Frontend
To run the frontend, enter the command `ng serve`.

### Backend
To run the backend, enter the command `mvn spring-boot:run`.

### MailServer
For testing purposes we use the smtp mock server 'mailhog'. It emulates a smtp server without sending mails. Instead, the mails can be viewed in a web dashboard which can be accessed under [`http://localhost:8025`](http://localhost:8025).

#### Starting the server:
In order to start the mailhog server, you will have to start the docker-compose file as follows.
```shell
docker-compose up -d
```

#### Access:
The smtp sever is accessible at `localhost:1025`
