## RMI support

RMI stands for Remote Method Invocation and is a standard way of calling Java method interfaces where caller and callee (client and server) are not located within the same JVM. So the object passed to the method as argument as well as the method return value are transmitted over the wire.

As a client Citrus is able to connect to some RMI registry that exposes some remote interfaces. As a server Citrus implements such a RMI registry and handles incoming method calls with providing the respective return value.

**Note**
The RMI components in Citrus are kept in a separate Maven module. So you should check that the module is available as Maven dependency in your project

```xml
<dependency>
  <groupId>com.consol.citrus</groupId>
  <artifactId>citrus-rmi</artifactId>
  <version>2.7.1-SNAPSHOT</version>
</dependency>
```

As usual Citrus provides a customized rmi configuration schema that is used in Spring configuration files. Simply include the citrus-rmi namespace in the configuration XML files as follows.

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xmlns:citrus="http://www.citrusframework.org/schema/config"
      xmlns:citrus-rmi="http://www.citrusframework.org/schema/rmi/config"
      xsi:schemaLocation="
      http://www.springframework.org/schema/beans
      http://www.springframework.org/schema/beans/spring-beans.xsd
      http://www.citrusframework.org/schema/config
      http://www.citrusframework.org/schema/config/citrus-config.xsd
      http://www.citrusframework.org/schema/rmi/config
      http://www.citrusframework.org/schema/rmi/config/citrus-rmi-config.xsd">

      [...]

      </beans>
```

Now you are ready to use the customized Http configuration elements with the citrus-rmi namespace prefix.

Read the next section in order to find out more about the RMI message support in Citrus.

### RMI client

On the client side we want to call e remote interface. We need to specify the method to call as well as all method arguments. The respective method return value is receivable within the test case for validation. Citrus provides a client component for RMI that sends out service invocation calls.

```xml
<citrus-rmi:client id="rmiClient1"
      host="localhost"
      port="1099"
      binding="newsService"/>

      <citrus-rmi:client id="rmiClient2"
        server-url="rmi://localhost:1099/newsService"/>
```

The client component in the Spring application context receives host and port configuration of a valid RMI service registry. Either by specifying a proper server url or by giving host, port and binding properties. The service binding is the name of the service that we would like to address in the registry. Now we are ready to use this client referenced by its id or name in a test case for a message sending action.

**XML DSL** 

```xml
<send endpoint="rmiClient">
    <message>
        <payload>
            <service-invocation xmlns="http://www.citrusframework.org/schema/rmi/message">
              <remote>com.consol.citrus.rmi.remote.NewsService</remote>
              <method>getNews</method>
            </service-invocation>
        </payload>
    </message>
</send>
```

**Java DSL** 

```java
@CitrusTest
public void rmiClientTest() {
  send(rmiClient)
      .message(RmiMessage.invocation(NewsService.class, "getNews"));
}
```

We are using the usual Citrus send message action referencing the **rmiClient** as endpoint. The message payload is a special Citrus message that defines the service invocation. We define the **remote** interface as well as the **method** to call. Citrus RMI client component will be able to interpret this message content and call the service method.

The method return value is receivable for validation using the very same client endpoint.

**XML DSL** 

```xml
<receive endpoint="rmiClient">
    <message>
        <payload>
            <service-result xmlns="http://www.citrusframework.org/schema/rmi/message">
              <object type="java.lang.String" value="This is news from RMI!"/>
            </service-result>
        </payload>
    </message>
</receive>
```

**Java DSL** 

```java
@CitrusTest
public void rmiClientTest() {
    receive(rmiClient)
        .message(RmiMessage.result("This is news from RMI!"));
}
```

In the sample above we receive the service result and expect a **java.lang.String** object return value. The return value content is also validated within the service result payload.

Of course we can also deal with method arguments.

**XML DSL** 

```xml
<send endpoint="rmiClient">
    <message>
        <payload>
            <service-invocation xmlns="http://www.citrusframework.org/schema/rmi/message">
                <remote>com.consol.citrus.rmi.remote.NewsService</remote>
                <method>setNews</method>
                <args>
                  <arg value="This is breaking news!"/>
                </args>
            </service-invocation>
        </payload>
    </message>
</send>
```

```java
@CitrusTest
public void rmiServerTest() {
    send(rmiClient)
        .message(RmiMessage.invocation(NewsService.class, "setNews")
              .argument("This is breaking news!"));
}
```

This completes the basic remote service call. Citrus invokes the remote interface method and validates the method return value. As a tester you might also face errors and exceptions when calling the remote interface method. You can catch and assert these remote exceptions verifying your error scenario.

**XML DSL** 

```xml
<assert exception="java.rmi.RemoteException">
    <when>
        <send endpoint="rmiClient">
            <message>
                <payload>
                    <service-invocation xmlns="http://www.citrusframework.org/schema/rmi/message">
                        [...]
                    </service-invocation>
                </payload>
            </message>
        </send>
    </when>
<assert/>
```

We assert the ***RemoteException*** to be thrown while calling the remote service method. This is how you can handle some sort of error situation while calling remote services. In the next section we will handle RMI communication where Citrus provides the remote interfaces.

### RMI server

On the server side Citrus needs to provide remote interfaces with methods callable for clients. This means that Citrus needs to support all your remote interfaces with method arguments and return values. The Citrus RMI server is able to bind your remote interfaces to a service registry. All incoming RMI client method calls are automatically accepted and the method arguments are converted into a Citrus XML service invocation representation. The RMI method call is then passed to the running test for validation.

Let us have a look at the Citrus RMI server component and how you can add it to the Spring application context.

```xml
<citrus-rmi:server id="rmiServer"
      host="localhost"
      port="1099"
      interface="com.consol.citrus.rmi.remote.NewsService"
      binding="newService"
      create-registry="true"
      auto-start="true"/>
```

The RMI server component uses properties such as **host** and **port** to define the service registry. By default Citrus will connect to this service registry and bind its remote interfaces to it. With the attribute **create-registry** Citrus can also create the registry for you.

You have to give Citrus the fully qualified remote interface name so Citrus can bind it to the service registry and handle incoming method calls properly. In your test case you can then receive the incoming method calls on the server in order to perform validation steps.

**XML DSL** 

```xml
<receive endpoint="rmiServer">
    <message>
        <payload>
            <service-invocation xmlns="http://www.citrusframework.org/schema/rmi/message">
              <remote>com.consol.citrus.rmi.remote.NewsService</remote>
              <method>getNews</method>
            </service-invocation>
        </payload>
        <header>
            <element name="citrus_rmi_interface" value="com.consol.citrus.rmi.remote.NewsService"/>
            <element name="citrus_rmi_method" value="getNews"/>
        </header>
    </message>
</receive>
```

**Java DSL** 

```java
@CitrusTest
public void rmiServerTest() {
    receive(rmiServer)
        .message(RmiMessage.invocation(NewsService.class, "getNews"));
}
```

As you can see Citrus converts the incoming service invocation to a special XML representation which is passed as message payload to the test. As this is plain XML you can verify the RMI message content as usual using Citrus variables, functions and validation matchers.

Since we have received the method call we need to provide some return value for the client. As usual we can specify the method return value with some XML representation.

**XML DSL** 

```xml
<send endpoint="rmiServer">
    <message>
        <payload>
          <service-result xmlns="http://www.citrusframework.org/schema/rmi/message">
            <object type="java.lang.String" value="This is news from RMI!"/>
          </service-result>
        </payload>
    </message>
</send>
```

**Java DSL** 

```java
@CitrusTest
public void rmiServerTest() {
    send(rmiServer)
        .message(RmiMessage.result("This is news from RMI!"));
}
```

The service result is defined as object with a **type** and **value** . The Citrus RMI remote interface method will return this value to the calling client. This would complete the successful remote service invocation. At this point we also have to think of choosing to raise some remote exception as service outcome.

**XML DSL** 

```xml
<send endpoint="rmiServer">
    <message>
        <payload>
          <service-result xmlns="http://www.citrusframework.org/schema/rmi/message">
            <exception>Something went wrong<exception/>
          </service-result>
        </payload>
    </message>
</send>
```

**Java DSL** 

```java
@CitrusTest
public void rmiServerTest() {
    send(rmiServer)
        .message(RmiMessage.exception("Something went wrong"));
}
```

In the example above Citrus will not return some object as service result but raise a **java.rmi.RemoteException** with respective error message as specified in the test case. The calling client will receive the exception accordingly.

