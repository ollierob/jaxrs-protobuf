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
If using subclasses that serialize differently, you might want to consider overriding a method that returns a mutable `MessageBuilder`:
```
interface MyType extends BuildsProto<MyTypeProto> { 

  String id();

  @Override
  default MyTypeProto toProto() {
    return this.toProtoBuilder().build();
  }
  
  default MyTypeProto.Builder toProtoBuilder() { 
    return MyTypeProto.Builder.newBuilder().setId(this.id());
  }

}

class MyClass implements MyType {

  @Override
  public MyTypeProto.Builder toProtoBuilder() {
      return MyType.super.toProtoBuilder().setType("myclass");
  }
  
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
