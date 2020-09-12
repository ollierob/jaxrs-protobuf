# jaxrs-protobuf

Classes to assist reading and writinoutputg protobufs with JAX-RS.

## Proto helpers

To serialize a Java type into a protobuf, simply make sure it implements either the [`WritesProto`](https://github.com/ollierob/jaxrs-protobuf/blob/master/src/main/java/net/ollie/protobuf/WritesProto.java) or `BuildsProto` interface:

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

Read or write types that implement the above interfaces, and annotate with the protobuf media type:

```
interface MyResource {

  @GET
  @Produces(ProtobufMediaType.APPLICATION_PROTOBUF)
  MyType get(@QueryParam("id") String id);
  
  @POST
  @Consumes(ProtobufMediaType.APPLICATION_PROTOBUF)
  Response post(MyType object);

}
```
Note that you may need to install the [providers](https://github.com/ollierob/jaxrs-protobuf/tree/master/src/main/java/net/ollie/protobuf/jaxrs) that do this manually. How you do this depends on your JAX-RS implementation - for example, if using [`resteasy-guice`](https://mvnrepository.com/artifact/org.jboss.resteasy/resteasy-guice) then you simply `bind` the providers.
