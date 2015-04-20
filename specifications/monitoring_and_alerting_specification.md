#Monitoring and Alerting Specification
This document describes the specific monitoring metrics with alerting thresholds required for the production cluster.

## MongoDB Deployment 
###Version
3.0.2 
### Storage Engine 
MMapV1

##Metrics
###opcounters
Number of queries, updates, inserts, deletes, getmore and (other) commands per second. Gives basic overview of what the system is doing.
####Threshold
Greater than 32,000 per second


###getmore
In opcounters graph, the getmore opcounter can sometimes point to unnecessarily large result sets.
####Threshold
greater than 100 per second


###memory
In the memory graph, focus on *resident memory* to know how much memory MongoDB has consumed.
####threshold
greater than 80% availble RAM

###page faults
This is the primary metric to know whether the working data set fits in RAM or not. When data has to be fetched from disk, it generates a page fault.
####Threshold
Greater than 10,000 / second

###lock %
How often the server is blocked by the write lock. This should be as close to 0 as possible, but on a write heavy system values from 20% to 50% are still manageable.
####Threshold
Greater than or equal to 70% 

###queues
Number of operations that are blocked and waiting for the lock. Even if lock percentage is high, if there are no other operations blocked, there's no reason for concern.
####Threshold
Greater than 100

###replication lag
This is the minimum time for a write to be replicated to secondary node. Excessive replication lag diminishes the worth of failover nodes and adversely effects the latency for write operations using a write concern of w > 1.
####Threshold
Greater than 30 Seconds

###oplog window
This is the amount of time in write operations which the oplog covers, this window represents how long a replicating secondary can be down before loosing it's ability to catch up to the primary. This should not be allowed to drop below 24 hours.
####Threshold
Less than 48 hours

###background flush average
Mongod applies all writes in memory first, flushing writes to disk in the background. Excessive flush times indicate an IO bottleneck.
####Threshold
Greater than 5 seconds