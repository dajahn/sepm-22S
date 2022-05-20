# Ticketline 4.0

## Introduction of the project

## Getting started

### Frontend

### Backend

### MailServer
For testing purposes we use the smtp mock server 'mailhog'. It emulates a smtp server without sending mails. Instead, the mails can be viewed in a web dashboard which can be accessed under [`http://localhost:8025`](http://localhost:8025).

#### Starting the server:
In order to start the mailhog server, you will have to start the docker-compose file as follows.
```shell
docker-compose up -d
```

#### Access:
The smtp sever is accessible at `localhost:1025`