# Which partner’s Stars and tIMDb were used
We used Nate’s tIMDB and Kshitij’s stars code.
# Partner division of labor
Kshitij focused on writing the maps, nearest, and ways commands (KDTree-related section). Nate focused on the route command (Dijkstra-related section). Both of us system tested and unit tested our own commands while working together to help debug.

# Design details specific to your code
### How did we fit each other’s code together?
- To handle exceptions, we both agreed to use Kshitij’s custom `CommandException` class. Each command throws this type of exception when the arguments to the REPL are not formatted correctly or there is a SQL error. The exception then bubbles up to the REPL (or the GUI for stars and tIMDB), which deals with the exception accordingly. 
- We used Kshitij’s REPL and Command interface because this allowed us to use the `CommandException` class. The REPL takes in a hash map from command name to command class. When the user enters an input, the REPL class parses that into an array using a regex to split on whitespace, and then searches for the first word in that array in a hash map of command names to command objects. Each command class implements a `Command` interface, which requires a `runCommand()` method that takes in a string array and returns a string that is either printed to the screen or passed to the GUI.
- We used Kshitij’s front end code for tIMDB and stars because combining both of our front ends was challenging.
- We also debugged other issues from our previous projects.

### How do we store the node/way information?
We wanted to store our node information in such a way that:
1. Getting the edges of node that is already in memory is O(1)
2. We can easily cache the nodes (which contain edges within them so we are essentially caching edges)

To satisfy both of these constraints, we created `MinimalNode` and `Node` classes. A `MinimalNode` is intended to be a lightweight object that only stores the position and ID of each node. We used this object in our KDTree because the KDTree only requires position (which we required via a `CartesianPoint` interface) and because these objects didn't take a large amount of memory so loading all of them in the KDTree would not be very memory costly. Since the `MinimalNode` implements `CartesianPoint`, it needs a `getDistance()` method that the KDTree will use. `MinimalNode` implements a Euclidean distance even though our data is spherical (latitude/longitude) because the KDTree splitting properties do not work correctly on spherical data. We can make this assumption safely because we are working over a small geographic area, so assuming that it is flat will not lead to large error. 

A `Node` contains a `MinimalNode` field, which means that it has access to the position and ID of internal `MinimalNode`. To connect the `Node` and `MinimalNode` class, we have a hashmap from node ID to `MinimalNode`. Every time a `Node` is constructed, we look up the `MinimalNode` in the hashmap and add that as a field. This prevents us from creating two of the same objects and wasting memory. This also helps us ensure that when the cached nodes go out of memory that `MinimalNode` position data still stays in the KDTree (because it is still being referenced by something). The `Node` class also stores a set of `Way` objects that represents all of the traversable edges that the Node is connected to. In the case that there are two traversable edges with the same destination for a single node, we only chose to store one of them to preserve memory. The `Node` class is used by Dijkstra's, so it implements a haversine distance.

### Map Command
The Map command verifies that the SQL database contains the correct tables and column names and also that the database contains at least 1 node. It also builds a KDTree of just the traversable nodes in the SQL database. It will also remove the previously stored KDTree, hashmap from node ID to minimal node, and clear the cache of nodes that may not be present in the new database.

### Nearest Command
Running the Nearest command will run the nearest neighbor command in the KDTree (which only contains traversable nodes) to get the single nearest neighbor to the target destination.

### Ways Command
The Ways command queries the SQL database for all nodes that either start or end inside of the bounding box.

### Route Command
If the inputs are latitude longitude pairs, the route command will run nearest on both of these points to get the start and end node. It will then run an A* search to find a path between these two nodes if one exists.

If the inputs are cross streets, the route command queries the database to find the nodes at the intersection of the streets and then runs an A* search.

### Caching
We cache `Node` objects. These nodes contain the edges, so we are essentially caching edges between nodes, which makes Dijkstra's quicker after the first search.

# Runtime/Space Optimizations
In our Dijkstra’s code, we implemented an optional A* Search optimizer that optimizes which node the algorithm decides to traverse to by minimizing how close the next node is to our final destination in addition to the cost of the path. Currently this is being plugged in to our Dijkstra’s implementation, but we designed the optimizer such that users have to option to use it, as well as potentially implement their own optimizer for a different search algorithm. 

We also wrote highly efficient SQL queries. For example, we have one query that gets all of the traversable nodes in one fell swoop, allowing us to run commands like Way almost instantaneously.

In terms of space optimizations, we decided to only store MinimalNodes (Nodes with just IDs and positions) in the KDTree, since that was the only information we needed to run the relevant commands. 

We also implemented caching, as in tIMDB. This time, we decided to cache (node id —> Node object) pairs with corresponding edges filled in so that we can update the graph frontier incrementally. 

# How to run our tests
In order to run our unit tests (without recompiling), run `mvn test`. To recompile and test, run `mvn package`. In order to run our system tests, run the cs32-test executable with any system test located in the ./tests directory. Many of the system tests will require 60 seconds, so use the `-t 60` flag.

# Any tests you wrote and tried by hand
We have code to verify the results of our most our commands automatically. There were a few tests we did write by hand, specifically for the route command. For example, on the small database, we hand tested paths that were both reachable and non-reachable. These cases were easily hand verified by looking at the visualization of the small database posted on piazza. We also hand tested some large database tests by comparing intermediate nodes reached in our path to locations on google maps to see if they seemed reasonable. 

### Automated testing
In order to test our Dijkstra’s we wrote a naive depth first search that finds all of the paths from a source to a destination, and compared the smallest total weight of these paths to that of the path found by our dijkstras, making sure it was less than or equal to the smallest naive path. 

We validated our nearest command by writing a SQL query that iterates through the entire SQL database and finds the nearest node to a point (rather than using a KDTree) 

We validated our KDTree construction by iterating through the tree and checking the invariant: left child < current node < right child. We also checked that the KDtree was of the correct size. 


# How to build/run our program from the command line
In order to build our program, run `mvn package`. To run it, use `./run`