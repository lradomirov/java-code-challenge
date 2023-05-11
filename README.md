## Notes

There is an `Application` and a few ApplicationTest classes. I recommend running `Application.main`, then one of `ApplicationTest.main` at a time. View the logs and `numbers.log` for info. Restart `Application` in between runs for clean state.

You can view `numbers.log` in the same directory as the project after each run. I also recommend deleting `numbers.log` after everything as it gets pretty big!

One assumption I made is that we never get negative numbers from clients. As such, we can use a data structure to store all possible values (0 -> 999,999,999) in memory. By keeping an efficient Set of Integers, we can easily deduplicate values and write the ones we haven't seen to disk. The capacity (and jvm memory) can be increased to handle more than 1,000,000,000 numbers, but a new IntSet implementation is required to handle negatives.

To get data resiliency, I just synchronously write to disk via `numbers.log`. In the worst case, we'll get a partial write of a value on an unexpected application crash. We can improve performance by buffering the writes and writing to disk in a background thread periodically, but we are more at risk for data loss (size of the buffer).

It would also be wise to explore preventing the corruption of `numbers.log` in the event of a crash. The implementation may depend on the operating system, but we might use a temporary file to handle all writes and periodically make a copy it in place of our primary file. Some form of redundancy (a backup) might help as well.

## How to Run This Application

Use gradle to build the application

`./gradlew clean build`

I used Intellij to run the classes, but if you want/prefer command line just open up a couple of terminals and run. Just make sure the shadowjar exists at that path (root directory assumed):

```shell
# run the server
java -classpath build/libs/coding-challenge-shadow.jar com.codingchallenge.Application

# run the one of the test classes
java -classpath build/libs/coding-challenge-shadow.jar com.codingchallenge.ApplicationDisconnectTest
java -classpath build/libs/coding-challenge-shadow.jar com.codingchallenge.ApplicationLoadTest
java -classpath build/libs/coding-challenge-shadow.jar com.codingchallenge.ApplicationTerminateTest
```

I added some notes to the expected logs below via `<--`. The suffixes below are `M = million` and `T = trillion`.

## ApplicationDisconnectTest

A client connects and writes 0 -> 2.999M numbers. At 3M, it writes an incorrect value. The server terminates the connection silently.

```
starting up server...
server is now running on port 4000
received 0 unique, 0 duplicate numbers. total unique: 0
received 0 unique, 0 duplicate numbers. total unique: 0
worker accepted client connection       <-- first client with faulty write at 3M
received 915977 unique, 0 duplicate numbers. total unique: 915977
received 1000372 unique, 0 duplicate numbers. total unique: 1916349
received 1009567 unique, 0 duplicate numbers. total unique: 2925916
worker accepted client connection       <-- second client that sends termination signal
shutting server down...
emptying numbers.log buffer
successfully emptied numbers.log buffer
```

## ApplicationLoadTest

10 clients are attempting to connect concurrently. The server only accepts 5. At number 5M, each client disconnects and the pending client connects for a total of 10 connect/disconnect statements.

```
starting up server...
server is now running on port 4000
received 0 unique, 0 duplicate numbers. total unique: 0
worker accepted client connection   <-- 5 clients connect
worker accepted client connection
worker accepted client connection
worker accepted client connection
worker accepted client connection
received 2143676 unique, 8301091 duplicate numbers. total unique: 2143676
received 2471076 unique, 9380489 duplicate numbers. total unique: 4614752
worker closed client connection     <-- first 5 clients disconnect, remaining 5 connect
worker accepted client connection
worker closed client connection
worker accepted client connection
worker closed client connection
worker accepted client connection
worker closed client connection
worker accepted client connection
worker closed client connection
worker accepted client connection
received 385249 unique, 10983718 duplicate numbers. total unique: 5000001
received 0 unique, 8796828 duplicate numbers. total unique: 5000001
worker closed client connection    <-- last 5 clients disconnect
worker closed client connection
worker closed client connection
received 0 unique, 6251740 duplicate numbers. total unique: 5000001
worker closed client connection
worker closed client connection
worker accepted client connection  <-- terminate signal
shutting server down...
emptying numbers.log buffer
successfully emptied numbers.log buffer
```

## ApplicationTerminateTest

2 clients connect concurrently. One client writes indefinitely (to 1T numbers) and the other writes "terminate" after 5M numbers. We expect to see the application terminate immediately. It would be good to debug to confirm where the indefinitely writing client left off to confirm `numbers.log` is accurate. Remember to terminate the test process as it is long-running (1T numbers over the socket!).

```
starting up server...
server is now running on port 4000
worker accepted client connection  <-- both clients connect and start writing...
worker accepted client connection
received 1537507 unique, 735420 duplicate numbers. total unique: 1537507
received 1887448 unique, 874946 duplicate numbers. total unique: 3424955
received 1868345 unique, 863227 duplicate numbers. total unique: 5293300
received 1921892 unique, 920954 duplicate numbers. total unique: 7215192
received 1842013 unique, 892560 duplicate numbers. total unique: 9057205
shutting server down...          <-- terminate signal received
emptying numbers.log buffer
successfully emptied numbers.log buffer
```

## Additional Info

There are two `IntSet` implementations with main methods as well. You can run these to see performance characteristics on your machine. The `Application` runs with `BooleanIntSet` by default.
