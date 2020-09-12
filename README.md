# jaxrs-protobuf

Classes to assist reading and writinoutputg protobufs with JAX-RS.

## Proto helpers

To serialize a Java type into a protobuf, simply make sure it implements either the `WritesProto` or `BuildsProto` interface:

```
interface MyType extends BuildsProto<MyTypeProto> { 

  @Override
  default MyTypeProto toProto() { ... }

}
```

## JAX-RS serialization

Return a type that implements the above and annotate with the protobuf media type:

```
interface MyResource {

  @GET
  @Produces(ProtobufMediaType.APPLICATION_PROTOBUF)
  MyType get(@QueryParam("id") String id);

}
```
Note that the providers may need to be installed manually.
