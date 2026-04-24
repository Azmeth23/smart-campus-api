##Sensor and Room Management API Smart Campus

A REST API built with JAX-RS (Jersey 3) and Grizzly for managing campus rooms, sensors, and sensor readings. Data is stored in memory using ConcurrentHashMap and ArrayList.



## API Overview

The API is versioned under /api/v1 and structured around three core resources: rooms, sensors, and sensor readings.

Discovery endpoint: /api/v1/                            
Room collection: /api/v1/rooms                       
Single room: /api/v1/rooms/{roomId}              
Sensor collection: /api/v1/sensors                     
Filter sensors by type: /api/v1/sensors?type=CO2            
Reading history for a sensor: /api/v1/sensors/{sensorId}/readings 


Error handling: 

API returns 409 when deleting a room that still has sensors, 422 when a sensor references a non-existent room, 403 when posting to a sensor in MAINTENANCE and a global 500 catch all that never exposes a stack trace



## Build and Run


1. Clone the repository and 

2. Build:
```bash
cd cw/smart-campus-api
mvn clean package
```

3. Start the server:

```bash
java -jar target/smart-campus-api-1.0-SNAPSHOT.jar
```

The server starts at `http://localhost:8080/api/v1`. Press Enter in the terminal to stop it.

---

## Sample curl Commands

Discovery:
```bash
curl http://localhost:8080/api/v1/
```

Create a room:
```bash
curl -X POST http://localhost:8080/api/v1/rooms -H "Content-Type: application/json" -d "{\"id\":\"LIB-301\",\"name\":\"Library Quiet Study\",\"capacity\":40}"
```

Get all rooms:
```bash
curl http://localhost:8080/api/v1/rooms
```

Register a sensor:
```bash
curl -X POST http://localhost:8080/api/v1/sensors -H "Content-Type: application/json" -d "{\"id\":\"CO2-001\",\"type\":\"CO2\",\"status\":\"ACTIVE\",\"currentValue\":0.0,\"roomId\":\"LIB-301\"}"
```

Filter sensors by type:
```bash
curl "http://localhost:8080/api/v1/sensors?type=CO2"
```

Post a sensor reading:
```bash
curl -X POST http://localhost:8080/api/v1/sensors/CO2-001/readings -H "Content-Type: application/json" -d "{\"id\":\"READ-001\",\"timestamp\":1714000000000,\"value\":415.2}"
```

Get reading history:
```bash
curl http://localhost:8080/api/v1/sensors/CO2-001/readings
```

Delete a room that still has sensors (expect 409):
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```

Register a sensor with a non-existent roomId (expect 422):
```bash
curl -X POST http://localhost:8080/api/v1/sensors -H "Content-Type: application/json" -d "{\"id\":\"ERR-001\",\"type\":\"CO2\",\"status\":\"ACTIVE\",\"currentValue\":0.0,\"roomId\":\"FAKE-99\"}"
```

Post a reading to a MAINTENANCE sensor (expect 403):
```bash
curl -X POST http://localhost:8080/api/v1/sensors/MAINT-001/readings -H "Content-Type: application/json" -d "{\"id\":\"READ-ERR\",\"timestamp\":1714000000000,\"value\":99.9}"
```

---

## Report

### Part A

**Question 1: Explain the default lifecycle of a JAX-RS Resource class. Elaborate on how this impacts in-memory data management and race condition prevention.**

For every incoming HTTP request JAX-RS creates a new instance of each resource class by default. The runtime instantiates the resource class, services the request, and then discards the object. Shared data lives in DataStore (private static final DataStore INSTANCE = new DataStore()). All resource classes call DataStore.getInstance() to access the single shared store.

For thread safety, ConcurrentHashMap is used for both rooms and sensors. ConcurrentHashMap permits concurrent reads and writes without full locking to prevent data corruption when the server handles multiple requests at the same time.

**Question 2: Why is the provision of Hypermedia (HATEOAS) considered a hallmark of advanced RESTful design? How does it benefit client developers compared to static documentation?**

HATEOAS is a hallmark of advanced RESTful design because it makes the API self-discoverable. Instead of requiring clients to know all endpoint URLs in advance, the server includes links to related resources and possible actions in its responses, such as the discovery endpoint returning /api/v1/rooms and /api/v1/sensors. This benefits client developers because they can navigate the API dynamically and rely less on static documentation.

References:
API7.ai, 2023. Hypermedia APIs: HATEOAS and Its Applications. Available at: https://api7.ai/learning-center/api-101/hypermedia-apis [Accessed 23 Apr. 2026].

---

### Part B

**Question 1: When returning a list of rooms, what are the implications of returning only IDs versus returning full room objects? Consider network bandwidth and client-side processing.**

Returning only IDs (e.g. ["LIB-301", "ENG-101"]) is extremely network efficient, but it forces the client to make N additional requests (one per ID) to retrieve the details it needs, producing the N+1 problem. For a list of 10000 rooms that is 10000 extra round-trips, increasing latency and server load. Returning full objects in a single response is the better approach for most use cases.

**Question 2: Is the DELETE operation idempotent in your implementation? Describe what happens if a client sends the same DELETE request for a room multiple times.**

The DELETE operation in this implementation is idempotent. During the first DELETE on /rooms/LIB-301 with no sensors, the room is removed from the map and 204 No Content is returned. During the second DELETE, dataStore.getRooms().get("LIB-301") returns null. The null check skips the sensor guard. ConcurrentHashMap.remove() on a non-existent key is a no-op and 204 No Content is returned again. The server state after the first and second calls is identical. The room is absent in both cases, satisfying idempotency.

---

### Part C

**Question 1: Explain the technical consequences if a client sends data as text/plain or application/xml to a method annotated @Consumes(MediaType.APPLICATION_JSON). How does JAX-RS handle this?**

When a resource method is annotated with @Consumes(MediaType.APPLICATION_JSON), JAX-RS will only route requests whose Content-Type is application/json. If a client sends text/plain or application/xml, the runtime does not match the method and returns HTTP 415 Unsupported Media Type before the method body executes.

References:
IBM, n.d. Defining media types for resources in RESTful applications. Available at: https://www.ibm.com/docs/en/was/8.5.5?topic=applications-defining-media-types-resources-in-restful [Accessed 22 Apr. 2026].

**Question 2: Contrast @QueryParam filtering with a path-based design. Why is the query parameter approach superior?**

Query parameters are preferable for filtering as they are optional modifiers on a collection resource. Path segments are meant to identify resources. This makes GET /api/v1/sensors?type=CO2 easier to extend, easier to combine with other filters, and more consistent with RESTful design than /api/v1/sensors/type/CO2.

References:
CodingTechRoom, 2024. Best Practices: When to Use @QueryParam vs @PathParam in REST APIs. Available at: https://codingtechroom.com/question/best-practices-when-to-use-queryparam-vs-pathparam-in-rest-apis [Accessed 22 Apr. 2026].

---

### Part D

**Question 1: Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity compared to one massive controller?**

The sub-resource locator pattern improves API architecture by delegating nested request handling to separate resource classes. This keeps each class focused on a single responsibility. In JAX-RS, the runtime uses the object returned by a sub-resource locator to continue request processing, allowing the API to grow without turning one controller into something large and difficult to maintain.

References:
Oracle, n.d. Subresources and Runtime Resource Resolution. Available at: https://docs.oracle.com/javaee/7/tutorial/jaxrs-advanced003.htm [Accessed 23 Apr. 2026].
Red Hat, 2023. Chapter 14. JAX-RS Resource Locators and Sub Resources. Available at: https://docs.redhat.com/en/documentation/jboss_enterprise_application_platform_common_criteria_certification/5/html/resteasy_reference_guide/jax-rs_resource_locators_and_sub_resources [Accessed 23 Apr. 2026].

---

### Part E

**Question 1: Why is HTTP 422 often considered more semantically accurate than 404 when the issue is a missing reference inside a valid JSON payload?**

When a user sends a valid JSON body containing roomId: GHOST-99 and that room does not exist, the request was well-formed and reached the correct endpoint. 404 Not Found is reserved for when the requested URL resource does not exist (e.g. GET /api/v1/rooms/GHOST-99). Returning 404 for a POST body reference error would mislead the client into thinking the /sensors endpoint does not exist. 422 Unprocessable Entity is precise. The client knows its URL was correct, its JSON was valid, but the content needs to change by using a real roomId. This distinction makes the API easier to consume and debug.

References:
Beeceptor, n.d. 400 Bad Request vs 422 Unprocessable Entity. Available at: https://beeceptor.com/docs/concepts/400-vs-422/ [Accessed 23 Apr. 2026].
First Rank SEO, 2024. What Is A 422 Response Code? Available at: https://firstrank.ca/what-is-a-422-response-code/ [Accessed 23 Apr. 2026].

**Question 2: From a cybersecurity standpoint, explain the risks of exposing internal Java stack traces to external API consumers. What specific information could an attacker gather?**

Jersey, Grizzly, and Jackson class names appear in traces. Since they are open-source, an attacker can study their source code to find attack vectors applicable to the installed version. The call sequence shows the control flow of the application, making room for crafted inputs designed to trigger specific vulnerable branches. Stack frames also reveal full package names, class names, and file paths, exposing the application's internal structure so specific classes can be targeted.

References:
GitHub, 2018. Information exposure through a stack trace. Available at: https://codeql.github.com/codeql-query-help/java/java-stack-trace-exposure/ [Accessed 23 Apr. 2026].
Rapid7, 2021. OWASP Top 10 Deep Dive: Injection and Stack Traces. Available at: https://www.rapid7.com/blog/post/2021/10/19/owasp-top-10-deep-dive-injection-and-stack-traces-from-a-hackers-perspective/ [Accessed 23 Apr. 2026].

---

### Part F

**Question 1: Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than inserting Logger.info() calls inside every resource method?**

With 10+ resource methods across 3 resource classes, manual logging means copying the same logger call repeatedly. A single filter logs every request automatically, satisfying the DRY principle. A developer adding a new resource method might forget the logger call, but the filter applies to every method without any action required, ensuring consistency. To change the log format, log level, or add a request correlation ID, only one class needs to be edited rather than every method individually.

References:
Payara, 2025. Nugget Friday: Mastering Jakarta REST Filter Chains. Available at: https://payara.fish/blog/nugget-friday-mastering-jakarta-rest-filter-chains/ [Accessed 23 Apr. 2026].
YouTube. Available at: https://www.youtube.com/watch?v=nDW6DQSNrIY [Accessed 23 Apr. 2026].
