### Gzip message validation

Gzip is a famous message compression library. When dealing with large message content the compression might be a good way to optimize the message transportation.
Citrus is able to handle gzipped message payloads on send and receive operations. When sending compressed data we just have to use the message type **gzip**.

```xml
<send endpoint="messageEndpoint">
    <message type="gzip">
        <data>Hello World!</data>
    </message>
</send>
```

Just use the **type="gzip"** message type in the send operation. Citrus now converts the message payload to a gzip binary stream as payload.

When validating gzip binary message content the messages are compared with a given control message in binary base64 String representation. The gzip binary data is 
automatically unzipped and encoded as base64 character sequence in order to compare with an expected content.

The received message content is using gzip format but the actual message content does not have to be base64 encoded. Citrus is doing this conversion automatically 
before validation takes place. The binary data can be anything e.g. images, pdf or plaintext content.

The default message validator for gzip messages is active by default. Citrus will pick this message validator for all messages of **type="gzip_base64"** . The default message validator implementation 
can be overwritten by placing a Spring bean with id **defaultGzipBinaryBase64MessageValidator** to the Spring application context.

```xml
<bean id="defaultGzipBinaryBase64MessageValidator" class="com.consol.citrus.validation.text.GzipBinaryBase64MessageValidator"/>
```

In the test case receiving action we tell Citrus to use gzip message validation.

```xml
<receive endpoint="messageEndpoint">
    <message type="gzip_base64">
        <data>citrus:encodeBase64('Hello World!')</data>
    </message>
</receive>
```

With the message format type **type="gzip_base64"** Citrus performs the gzip base64 character sequence validation. Incoming message content is automatically unzipped and encoded as base64 String and 
compared to the expected data. This way we can make sure that the binary content is as expected.

**Note**
If you are using http client and server components the gzip compression support is built in with the underlying Spring and http commons libraries. So in http communication
you just have to set the header **Accept-Encoding=gzip** or **Content-Encoding=gzip**. The message data is then automatically zipped/unzipped before Citrus gets the message data
for validation. Read more about this http specific gzip compression in [chapter http](http.md).