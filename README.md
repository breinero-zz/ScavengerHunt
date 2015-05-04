#Where in NYC is Norbeardo?


##Overview


###The Application###

Norbeardo is a geo-caching / scavenger hunt game for mobile apps. A tour guide posts a tour with challenges at multiple waypoints.  The challenger must reach and complete the waypoints within a given time. The challenger wins if they complete the course within the challenge time.

####Data Model
Scavenger Hunt uses three main entities 

- [Waypoint](https://github.com/breinero/mobileDemo/blob/master/Tour_Schema.md#tour-waypoint)
- [User](https://github.com/breinero/mobileDemo/blob/master/Tour_Schema.md#user)
- [Checkin](https://github.com/breinero/mobileDemo/blob/master/Tour_Schema.md#checkin)

##Server API

###Waypoints
A waypoint is a item or activity at a set location, which people participating in a tour must find. A set of related waypoints are grouped to gether to form a tour, or scavenger hunt. A tourist completes the scavenger hunt once he/she reaches and completes all waypoints on the tour.
   
####Example Waypoint
```
{	_id: ObjectId(),
	tour: UUDI
	user: UUID,
    name: "Doug's Dogs",
    desc: "The best hot-dog",
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

####Operations

##### GET /waypoints/1234
Get waypoint with id 1234

#####GET /waypoints/tour/5678
Get all waypoints belonging to tour 5678

#####GET /waypoints/user/9101/tour/1234
Get all of user 9101's waypoints which belong to tour 5678

#####PUT /waypoints/1234/desc/
Update waypoint 1234's description string
######Request Body
```
"The best hot-dog in the city"
```

#####GET /waypoints?lat=40.87304&lon=-73.871275&max=100
Find a tour to follow with 100 meters of the point defined by the coordinate parameters

####POST /waypoint/user/9101/tour/1234
Add a new waypoint owned by user 9101 to be part of tour 1234

######Request Body
```
    {
        name: "Doug's Dogs",
        desc: "The best hot-dog",
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

###Users
Need a session token once the user has authenticated

####Operations

#####GET /users/9101
Get the specified user doc, sans pass

#####HTTPS POST /users
Register new user
######Request Body
```
{
    name: <string>,
    email: <string>,
    pass: <String>,
    description: <140 char String>
}
```
######Response
```
204
Location: /user/uuid
```

###Checkin
Represents an event log detailing a user's current physical location as they progress through the tour

####Operations

#####GET /checkins/user/UUID/tour/UUID
All checkins for a given user tour
######Response
```
{
	_id: ObjectId(),
	user: UUID,
	tour: tourId,
	timestamp: ISODate(),
	type: ['inprog'|'goal']
	geometry: {
		type: "Point",
		coordinates: [ long, lat ]
	},
	...
}
```
#####POST /checkins/user/UUID/tour/UUID
Log a user's current location while on tour 
######Request Body
```
{
	timestamp: ISODate(),
	type: ['inprog'|'goal']
	geometry: {
		type: "Point",
		coordinates: [ long, lat ]
	}
}
```

#####POST /checkins/user/UUID/tour/UUID
Same behaviour as POST. Checkins are immutable.

#####Delete
Unsupported. Checkins are immutable.

##The demo
Somebody runs out ahead of Norberto and marks out a course of food spots he must eat at. Our hero Norberto then has to reach each vendor and and eat the selected food and get back to the conf before time runs out. Participants in the audience votes for or against Norbeardo

##Depedencies 

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
