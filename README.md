#Where in NYC is Norbeardo?


##Overview


###The Application###

`Walking Tour` (needs a better name) is a geo-caching game for mobile apps. A tour guide posts a tour with challenges at multiple waypoints.  The challenger must reach and complete the waypoints within a given time. The challenger wins if they complete the course within the challenge time.

##The demo##
Somebody runs out ahead of Norberto and marks out a course of food spots he must eat at. Our hero Norberto then has to reach each vendor and and eat the selected food and get back to the conf before time runs out. Participants in the audience votes for or against Norbeardo

##Data Model

###Point of Interest###

```
    {
        _id: 9101
        user: <userId>,
        name: "Doug's Dogs",
        description: "The best hot-dog",
        clues: [
            "Hungry for a Coney Island?",
            "Ask for Dr. Frankenfurter",
            "Look for the hot dog stand"
        ]
        "geometry": {
            "type": "Point",
            "coordinates": [125.6, 10.1]
        }
    }
```

###Field Definitions###
- '_id' unique POI identifier. Also serves as a cheat code, entered when the contestant completes goal
- 'user' The user who posted this point of interest
- 'name' The name of this POI
- 'description' What and why the user added this POI to the tour. E.G. "I like this place because... "
- 'clues' a 3 element array of hints to help the tourer discover the POI goal. Ordered in increasing helpfulness
- GEO JSON location of the POI


###REST API###

####Alert contestant when POI is within 100 meters####

####GET####

```
    <host:port>/poi?tour=1234,lat=40.87304&lon=-73.871275&max=100
```

#####Response

```
    Content-Type: "text"
    "Hungry for a Coney Island?"
```

###Check if the goal code is right###

####GET

```
    <host:port>/poi?tour=1234,code=5678
```

Response
```
    Content-Type: "text"
    "Congradulations! <goal description>"
```

###POST###

#####URL

```
    <host:port>/poi
```

Request Body, where content-type: application/json

```
    {
        user: 1234,
        description: "some text"
        "geometry": {
            "type": "Point",
            "coordinates": [125.6, 10.1]
        }
    }
```

####PUT####

URL
```
    <host:port>poi/9101
```
Request Body, content-type: application/json

```
    {
        clues: [
            "Hungry for a Coney Island?",
            "Ask for Dr. Frankenfurter",
            "Look for the hot dog stand"
        ] 
    }
```

####Delete####

#####URL

```
    /poi/9101
```

##Tour


A tour is a virtual course, marked out by a challenger. 


###Document Model###
```
    {
        name: <string>,
        user: <user_id>,
        time: <seconds>,
        checkpoints: [
            { 
                poi: <_id>,
                time: <seconds> 
            }   
        ]
        "geometry": {
          "type": "LineString",
          "coordinates": [
            [102.0, 0.0], [103.0, 1.0], [104.0, 0.0], [105.0, 1.0]
          ]
        }
        
    }
```

####Field Definitions####
- 'name' The tour name
- 'user' The challenger who authored the tour
- 'time' The total time the tour should take
- 'checkpoints' The list of goals the contestant must complete to win the tour
- 'checkpoints.poi' A reference the the point-of-interest document that describes the checkpoint
- 'checkpoints.time' The amount of time it should take to complete this goal
- 'geometry' The line string defining the tour course.

###REST API###

####GET####

#####URL 
```
    // by proximity 
    <host:port>/tour?lat=40.87304&lon=-73.871275&max=100

    // by user
    <host:port>/tour?user=1234

    // by explicit id
    <host:port>/tour/1234
```

####POST####

#####URL
```
    <host:port>/tour
```

#####Request Body
```
    {
        name: <string>,
        user: <user_id>
    }  
```

####PUT####
#####URL
```
    <host:port>/tour/1234
```

#####URL
```
    <host:port>/tour/1234?checkpoint=<poi_ref>
```  
#####Request Body
```
    //uneeded 
```

####DELETE####

#####URL
```
    // only explicit delete supported
    <host:port>/tour/1234
```

##USER

###Document Model###
```
    {
        _id: <int>,
        name: <string>,
        email: <string>,
        pass: <hash>,
        description: <string>
    }
```

###Field Definitions###
- _id: Unique identifier for user (immutable)
- name: User's name 
- email: User's email
- pass: Hashed passphrase
- description: Blurb about the user (140 character limit)


###Depedencies 

####Firehose
Walking Tour uses the [Firehose Toolkit](https://github.com/breinero/Firehose) to for [code instrumentation](https://github.com/breinero/Firehose/tree/doa/src/main/java/com/bryanreinero/firehose/metrics), [circuit breaking](https://github.com/breinero/Firehose/tree/doa/src/main/java/com/bryanreinero/firehose/circuitbreaker) and [Data Access Object registries](https://github.com/breinero/Firehose/tree/doa/src/main/java/com/bryanreinero/firehose/dao). IMPORTANT: Please note, these Firehose features are currently in the [doa](https://github.com/breinero/Firehose/tree/doa) branch, and not yet supported in master.

#####Firehose's code instrumentation package 
is used to keep operation specific performance statistics. 

#####Firehose's crcuit breaker package 
prevents this server from continuing to execute operations which are likely to fail or cause DDOS'ing downstream databases. 

#####Firehose's Data Access Object package 
gives the web server the the ability to configure and register Data Access Objects in an extensible and easy to manage system.

####HUM 
Walking Tour uses the [HUM Servlet Execution Engine](https://github.com/breinero/HUM) to define its behaviors. The HUM engine allows user to define servlet behavior based on a set configurations, or Specificaions rather than writing any code.

##Todos##
- Improve JMX integration
- Move class [ContexConfigulator](https://github.com/breinero/mobileDemo/blob/master/src/com/mongodb/web/tour/servlet/ContexConfigulator.java) into the [HUM](https://github.com/breinero/HUM) project as it performs serlvet configuration that can be parameterized for all users of HUM.