# jaxrs-protobuf

Classes to assist reading and writinoutputg protobufs with JAX-RS.

To serialize a Java type into a protobuf, simply make sure it implements either the `WritesProto` or `BuildsProto` interface and install the relevant providers:

```
interface MyType extends BuildsProto<MyTypeProto> { 

  @Override
  default MyTypeProto toProto() { ... }

}

interface MyResource {

  @GET
  @Produces(ProtobufMediaType.APPLICATION_PROTOBUF)
  MyType get(@QueryParam("id") String id);

}
```
