#Where in NYC is Norbeardo?


##Overview


###The Application###

`Walking Tour` (needs a better name) is a geo-caching game for mobile apps. A tour guide posts a tour with challenges at multiple waypoints.  The challenger must reach and complete the waypoints within a given time. The challenger wins if they complete the course within the challenge time.

##The demo##
Somebody runs out ahead of Norberto and marks out a course of food spots he must eat at. Our hero Norberto then has to reach each vendor and and eat the selected food and get back to the conf before time runs out. Participants in the audience votes for or against Norbeardo

##Tour
A tour is a colletions of waypoints, which together define a set of location and activities the tourist must complete.   
###Example Waypoint
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
###API

####GET
Find a tour to follow with 100 meters of the point defined by the coordinate parameters

#####URL
/tour?lat=40.87304&lon=-73.871275&max=100

#####Response
Returns the set of waypoint coordinates and waypoint descriptons for each eligble tour. The requesting client can user these points to map out the tours the user may choose to follow

```
    {
    	tour: <tourId>,
        description: "The best hot-dog",
        geometry: {
            type: "Point",
            coordinates: [125.6, 10.1]
        }
    },
   	...
```
####Post
Add a new waypoint

#####url
/tour/[tourId]/waypoint

```
    {
    	user: <userId>,
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
#####response
######Code
```
HTTP/1.1 201
Location: http://host:port/tour/1234/waypoint/1234/
```
####Put
Update a waypoint field
#####url
/tour/1234/waypoint/1234/[name|desc|clues|geometry]
####Delete
Remove a specific waypoint
#####url
/tour/1234/waypoint/1234
######Response
200
####Delete
Remove an entire tour
#####url
/tour/1234
######Response
```200```
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

####Delete

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


##USER
###API
####URL
/user/UUID
#####GET
Get the specified user doc, sans pass
#####POST
Register new user
######Request Body
```
{
    name: <string>,
    email: <string>,
    pass: <hash>,
    description: <string>
}
```
######Response
```
204
Location: /user/uuid
```


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

##Checkin
Represents an event log detailing a user's current physical location as they progress through the tour

###API
####URL
/user/UUID/tour/UUID
#####GET
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
#####POST
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
######Response
```
http: 1.1 201
Location: /user/UUID/checkin/UUID
```

#####Put
Same behaviour as POST. Checkins are immutable.

#####Delete
Unsupported. Checkins are immutable.

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
