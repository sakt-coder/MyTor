MyTor

Goal - to create an object i/o stream between two sockets such that the data is relayed through 3 volunteer servers in between and encrypted such that it is not possible to trace the ip of the user and he can contact the server anonymously
TODO - make methods and variables private


Future goal-add verification that the volunteer has not manipulated the data and data received is from correct sender
-option for new tor circuit

Classes:-
>Volunteer - this class will be run as the volunteer relay
	- there must be an indicator showing that the volunteer server is active
-CipherInputStream next, CipherOutputStream next
-CipherInputStream prev, CipherOutputStream prev
-Private key, Public key
-Socket prev, next
-thread to allow communication
-Indicator
-method to extend path
-method to generate key

>FwdThread/RevThread - this is a thread that is based on a volunteer
			-this reads objects from prev and next and passes on 
			-initially reads extendPath from prev and extends the path

>Client - this will be the class that the client will use
 		-client will choose any 3 of the active volunteer servers
-CipherInputStream next,CipherOutputStream next
-Public key Alice,Bob,Carol,Server
-Sql to read volunteer servers ip

>TorObjectStream-creating objects of this class initializes the circuit and provides readObject() and writeObject()

How to maintain active servers list
we will have a directory of active servers ip
and we can ping to test if one is active

Lets say that our 3 volunteer servers are Alice, Bob and Carol

>Client sends connection request to Alice
>Alice replies with her public key
>Secure connection is established between client and alice
>Client sends Alice extendPath object encrypted with Alice's key
>Alice decrypts extendPath object and extends the path to BoB
>Bob replies with his public key and alice sends it to client
>Client sends the extendPath object encrypted with Bob's key
>Alice forwards it to Bob
>Bob extends the path to Carol and Carol replies with public key
>Further path is extended from Carol to Server
>Server replies with public key which can be used to send data
>Hence the path between Client and Server is established

We need a library to encrypt and decrypt objects based on given RSA key

RSA encryption:-
>User communicates the public key which is the product of two large prime numbers
>this public key can be used to encrypt any data which can be decrypted only using the private key(the two large prime numbers)