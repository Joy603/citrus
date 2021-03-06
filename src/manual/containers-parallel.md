### Parallel

Parallel containers execute the embedded test actions concurrent to each other. Every action in this container will be executed in a separate Java Thread. Following example should clarify the usage:

**XML DSL** 

```xml
<testcase name="parallelTest">
    <actions>
        <parallel>
            <sleep/>
            
            <sequential>
                <sleep/>
                <echo>
                    <message>1</message>
                </echo>
            </sequential>
            
            <echo>
                <message>2</message>
            </echo>
            
            <echo>
                <message>3</message>
            </echo>
            
            <iterate condition="i lt= 5" 
                        index="i">
                <echo>
                    <message>10</message>
                </echo>
            </iterate>
        </parallel>
    </actions>
</testcase>
```

**Java DSL designer and runner** 

```java
@CitrusTest
public void paralletTest() {
    parallel().actions(
        sleep(),
        sequential().actions(
            sleep(),
            echo("1")
        ),
        echo("2"),
        echo("3"),
        iterate().condition("i lt= 5").index("i"))
            .actions(
                echo("10")
            )
    );
}
```

So the normal test action processing would be to execute one action after another. As the first action is a sleep of five seconds, the whole test processing would stop and wait for 5 seconds. Things are different inside the parallel container. Here the descending test actions will not wait but execute at the same time.

**Note**
Note that containers can easily wrap other containers. The example shows a simple combination of sequential and parallel containers that will archive a complex execution logic. Actions inside the sequential container will execute one after another. But actions in parallel will be executed at the same time.

