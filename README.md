# Bank logic fantasy

Repository with examples of REST service and concurrency algorithms written in *Java*. It demonstrates projection of common sense and conformity to program implementation standard. As usual, codebase is split into main logic part and test logic part by default.

### Main logic

Repo contains piece of bank logic exposed via REST service over HTTP. This is example of money transferring between accounts implemented with *Java*, *Jersey Web Server*(any other server could be chosen instead of this one), *Java Concurrent API*: *Synchronization*, *Non-blocking Atomic*, *Reentrant Lock*.

### Test logic

Second part is part for solution testing (second but not least). This part includes unit-tests and integration tests. Unit-test scenarios validate functional correctness in concurrent environment where simultaneity is modeled via *Threads* and *Executors*. Also some concurrent utils like Threads, Executors, Locks, Latches are used to make atmosphere of randomness and unpredictability more diverse. All test logic is placed [here](./src/test/java/com/banking).

Integration tests verify overall instance of solution through REST API: [`AccountServiceRestIntegrationTest`](./src/test/java/com/banking/account/rest/AccountServiceRestIntegrationTest.java).

### Build scripts to operate with codebase 
Build tool is *Maven* and to perform script following commands could be used:
```
 mvn clean install test
```
