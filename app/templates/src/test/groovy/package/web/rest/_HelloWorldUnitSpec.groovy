package <%=packageName%>.test.web.rest;

import com.scripps.myapp.web.rest.IHelloWorld
import com.scripps.myapp.web.rest.impl.HelloWorld
import spock.lang.Specification

/**
 * Simple unit test for the hello world service.
 */
class HelloWorldUnitSpec extends Specification {

    def "basic hello world unit test"() {

        setup:
        IHelloWorld hw = new HelloWorld();

        when:
        def greeting = hw.sayHello("bob");

        then:
        greeting != null;
        'Hello, bob!' == greeting.getBody()
    }
}