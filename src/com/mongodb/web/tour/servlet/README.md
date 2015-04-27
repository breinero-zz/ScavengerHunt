# Servlet Configuration 

## MongoClients

##DAO Descriptor

```
{ 
	DAOs: [
		{
			name: "Poi",
			mongoURI: "localhost:27017",
			namespace: "test.myCollection",
			wc: {
				w: 1,
				tags: []
			},
			readPref: {
				mode: "primary",
				tags: []
			},
			breaker: {  
            	thresholds: [ 
                	{ opsPerSec: 5000.0 }, 
                	{ latency: 1000.0 }, 
                	{ concurrency: 50.0 } 
            	] 
       		}
        }
    ] 
}
```