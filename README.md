# issue-54542

Minimal Quarkus reproducer for a REST route ambiguity where two resource methods
have the same effective path pattern and both receive numeric path parameters.

The reproducer is in `src/main/java/br/com/will/AmbiguousRouteResource.java`:

```java
@GET
@Path("v1/{parentId}")
public List<DTO> findOwnershipLineageByParentIdentifier(@PathParam("parentId") Long parentId) {
    Log.info("Calling findOwnershipLineageByParentIdentifier");
    return List.of(new DTO(parentId, "findOwnershipLineageByParentIdentifier"));
}

@GET
@Path("v1/{product}")
public DTO getById(@PathParam("product") Long product) {
    Log.info("Calling getById");
    return new DTO(product, "getById");
}
```

Both methods resolve to `GET /api-path/v1/{number}`. Quarkus starts without a
build-time error or warning, and requests are silently routed to only one method.

## Reproduce

Run the test:

```powershell
.\mvnw test
```

On Unix-like shells:

```shell
./mvnw test
```

Or start Quarkus in dev mode:

```powershell
.\mvnw quarkus:dev
```

Then call:

```powershell
curl http://localhost:8080/api-path/v1/1
curl http://localhost:8080/api-path/v1/42
```

Actual behavior observed with Quarkus 3.36.0:

```json
{"id":1,"source":"getById"}
{"id":42,"source":"getById"}
```

The logs also show only `getById` being called:

```text
Calling getById
Calling getById
```

`findOwnershipLineageByParentIdentifier` is never called for those requests, and
there is no build-time diagnostic that the two methods are structurally
identical.

## Expected diagnostic

Because `v1/{parentId}` and `v1/{product}` have the same path structure and both
parameters are `Long`, the application should report the ambiguous resource
mapping instead of selecting one method silently.

## Branches

The `master` branch uses the `quarkus-rest` extension.

There will also be a `resteasy` branch using the `quarkus-resteasy` extension,
so the same reproducer can be compared between both REST implementations.

With `quarkus-resteasy`, the ambiguous route is reported at request time with a
warning like this:

```text
WARN  [org.jboss.resteasy.resteasy_jaxrs.i18n] (executor-thread-1) RESTEASY002142: Multiple resource methods match request "GET /api-path/v1/42". Selecting one. Matching methods: [public br.com.will.AmbiguousRouteResource$DTO br.com.will.AmbiguousRouteResource.getById(java.lang.Long), public java.util.List br.com.will.AmbiguousRouteResource.findOwnershipLineageByParentIdentifier(java.lang.Long)]

INFO  [br.com.will.AmbiguousRouteResource] (executor-thread-1) Calling getById
```