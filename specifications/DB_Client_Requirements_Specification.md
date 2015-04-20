#Database Operations Requirement Specifications

####Version 1.0.0

##Overview
A complete list of all operations, (queries, commands, aggregations) sent by the application client to the database with sufficient detail such that the Operations Team can declare required indexes, allocate necessary hardware and provision a cluster to meet current and expected loads.

Each line item describes an individual operation, which can be identified by name and type, [query|insert|update|command]. A operation descriptor is composed of a set of operation specific fields relevant to the operation type


##Operations

###Operation: "Get near Points of Interest”
This operation is used to query for the next check point in a tour challenge. 
####Namespace: 'walkingtour.poi'

####Operation Type 'find'

#####Query Predicates
```
{ 
	tour: 5678,
	'Location.geometry' :
	{
    	'$near' {
        	'$geometry': {
     	       type: "Point",
        	    coordinates: [
            	    -72.01,
                	38.56
 	           ]
    	    }
	    '$maxDistance': 100
    	}
	}
}
```
#####Projection 
```
{description: 1, _id: 0}
```

#####Result Set Size
10 documents on average, 51 bytes / per document
#####SLA 
95th percentile within 10ms
#####Frequency
1000/second between 0400 GMT to 1500 GMT, 430/second all other hours
#####Availability
100% availability
#####Consistency Tolerance
Eventual consistency within 60 second lag
#####Growth Projections 
Frequency is expected to increase to 2000/second by end of year.

###Operation: "Get Clue”
User is having trouble finding the goal, and needs a clue to help them identify the goal.

####Namespace: 'walkingtour.poi'

####Operation Type 'find'

#####Query Predicates
```
{ _id: 9101 }
```
#####Projection 
```
{ 
	clues: { $slice: [ 2,1] }, 
	_id: 0, 
	user: 0, 
	name: 0, 
	geometry: 0, 
	description: 0 
} 
```

#####Result Set Size
1 document, 51 bytes / per document
#####SLA 
95th percentile within 10ms
#####Frequency
1000/second between 0400 GMT to 1500 GMT, 430/second all other hours
#####Availability
100% availability
#####Consistency Tolerance
Eventual consistency within 60 second lag
#####Growth Projections 
Frequency is expected to increase to 2000/second by end of year.

###Operation: "Check Goal Code”

User queries submits a goal code to confrm they have found the right checkpoint

####Namespace: 'walkingtour.poi'

####Operation Type 'find'

#####Query Predicates
```
{ _id: 9101, tour: 1234 }
```
#####Projection 
```
{ 
	description: 1,
	_id: 0 
} 
```

#####Result Set Size
1 document, 51 bytes / per document
#####SLA 
95th percentile within 10ms
#####Frequency
1000/second between 0400 GMT to 1500 GMT, 430/second all other hours
#####Availability
100% availability
#####Consistency Tolerance
Eventual consistency within 60 second lag
#####Growth Projections 
Frequency is expected to increase to 2000/second by end of year.

###Operation: "Insert New POI”

User queries submits a goal code to confrm they have found the right checkpoint

####Namespace: 'walkingtour.poi'

####Operation Type 'insert'

#####Inserted Document
```
{
	"_id" : 9101,
	"user" : 1234,
	"name" : "Doug's Dogs",
	"clues" : [
		"Hungry for a Coney Island?",
		"Ask for Dr. Frankenfurter",
		"Look for the hot dog stand"
	],
	"geometry" : {
		"type" : "Point",
		"coordinates" : [
			125.6,
			10.1
		]
	},
	"description" : "The best hot-dog in the city"
}
```

#####Payload
1 document, 285 bytes / per document
#####SLA 
95th percentile within 10ms
#####Frequency
10/second
#####Availability
100% availability
#####Durability
Replication to at least 2 data centers
#####Growth Projections 
Frequency is expected to increase to 20/second by end of year.

##Data Model

#####Schema Descriptor

```
{
    fields: [
        { name: "user",
          required: true,
          values: [
          	{ type: "int", min: "$minInteger", max: "$maxValue" }
          ]   
        },
        {	name: "userId",
            required: true,
            values: [
            	{ type: "int", min: "$minInteger", max: "$maxValue" }
            ]
        },
        {
            name: "clues",
            required: false,
            values: [
            	{ type: "array", min: 0, max: 3 }
            ]
        },
        {
            name: "clues.$",
            required: false,
            values: [
            	{ type: "string", regex: "\.144" }
            ]
        },
        {
        	name: "geometry",
        	required: true,
        	values: [
        		{ type: "object", uri: "bryanreinero.com/schema/poi.json" }
        	]
        },
        {
         	name: "description",
         	required: true,
         	values: [
         		{ type: "string", regex: "\.144" }
         	]
        }
    ]
}
```
######Sample Document 
```
{
	"_id" : 9101,
	"user" : 1234,
	"name" : "Doug's Dogs",
	"clues" : [
		"Hungry for a Coney Island?",
		"Ask for Dr. Frankenfurter",
		"Look for the hot dog stand"
	],
	"geometry" : {
		"type" : "Point",
		"coordinates" : [
			125.6,
			10.1
		]
	},
	"description" : "The best hot-dog in the city"
}
```

