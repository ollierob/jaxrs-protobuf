# jaxrs-protobuf

Classes to assist reading and writinoutputg protobufs with JAX-RS.

To serialize a Java type into a protobuf, simply make sure it implements the `WritesProto` interface and install the relevant providers:

```
interface MyResource {

  @GET
  @Produces(ProtobufMediaType.APPLICATION_PROTOBUF)
  MyType get(@QueryParam("id") String id);

}
```
