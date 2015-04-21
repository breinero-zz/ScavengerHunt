Project Specifications
======================

Circuit Breaker
---------------

What causes the circuit breaker to pop 
How does it get reset
What happens when it is in 'popped' state
Notifications logging and alerts

#Conditions for Popping#
- E-Stop, i.e. circuit breaker pop by command (via JMX)
- Triggered by Conditions

##E-Stop##

###Required Functions###

####Pop####
Force the the breaker to pop

####Reset####
Enable the circuit again
	
####Trigger####

Pop if latency exceeds threshold.
Pop if operation count threshold
Pop if concurrent operations exceeds threshold
Little's Law concurrent ops = (number of requests per second) * ( time to service each request ).

Should the breaker automatically be reset? 
if so how?
throttling?
	
#####Threshold specification#####
Interface threshold 
Concrete implementations Latency, OperationsCount, ConcurrentOps 

Class CircuitBreaker
Method
	trip( Callback cb )
	reset()

	