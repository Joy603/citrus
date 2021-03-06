### Including custom test actions

Now we have a look at the opportunity to add custom test actions to the test case flow. Let us start this section with an example:

**XML DSL** 

```xml
<testcase name="ActionReferenceTest">
    <actions>
        <action reference="cleanUpDatabase"/>
        <action reference="mySpecialAction"/>
    </actions>
</testcase>
```

The generic <action> element references Spring beans that implement the Java interface ***com.consol.citrus.TestAction*** . This is a very fast way to add your own action implementations to a Citrus test case. This way you can easily implement your own actions in Java and include them into the test case.

In the example above the called actions are special database cleanup implementations. The actions are defined as Spring beans in the Citrus configuration and get referenced by their bean name or id.

```xml
<bean id="cleanUpDatabase" class="my.domain.citrus.actions.SpecialDatabaseCleanupAction">
    <property name="dataSource" ref="testDataSource"/>
</bean>
```

The Spring application context holds your custom bean implementations. You can set properties and use the full Spring power while implementing your custom test action in Java. Let us have a look on how such a Java class may look like.

```java
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;

public class SpecialDatabaseCleanupAction extends AbstractTestAction {

    @Autowired
    private DataSource dataSource;
    
    @Override
    public void doExecute(TestContext context) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        
        jdbcTemplate.execute("...");
    }

}
```

All you need to do in your Java class is to implement the Citrus ***com.consol.citrus.TestAction*** interface. The abstract class ***com.consol.citrus.actions.AbstractTestAction*** may help you to start with your custom test action implementation as it provides basic method implementations so you just have to implement the ***doExecute()*** method.

When using the Java test case DSL you are also quite comfortable with including your custom test actions.

**Java DSL designer and runner** 

```java
@Autowired
private SpecialDatabaseCleanupAction cleanUpDatabaseAction;

@CitrusTest
public void genericActionTest() {
    echo("Now let's include our special test action");
    
    action(cleanUpDatabaseAction);
    
    echo("That's it!");
}
```

Using anonymous class implementations is also possible.

**Java DSL designer and runner** 

```java
@CitrusTest
public void genericActionTest() {
    echo("Now let's call our special test action anonymously");
    
    action(new AbstractTestAction() {
        public void doExecute(TestContext context) {
            // do something
        }
    });
    
    echo("That's it!");
}
```

