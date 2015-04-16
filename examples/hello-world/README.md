This example contains Hello World capability. It is the simplest capability and shows the minimum requirements to develop one.

How can I develop a capability
==============================
The goal of this section is to show how a developer can develop an OSGI bundle containing a capability, to be deployed in MQNaaS.

Prerequisites
-------------
These are the requirements to develop a capability:

* OSGi bundle

  In order to pack and deploy a capability, it is necessary to create an OSGi bundle as a JAR file. It is possible to find more information on OSGi [here](http://www.osgi.org/Technology/WhatIsOSGi). Bundles are the OSGi components made by the developers.
  
  In this case the option chosen to build bundles is to use a Maven plugin called [Apache Felix Maven Bundle Plugin](http://felix.apache.org/documentation/subprojects/apache-felix-maven-bundle-plugin-bnd.html). It facilitates the job of building the JAR file with the OSGI bundle requisites (basically the MANIFEST headers). This code portion of the project's `pom.xml` file shows the usage of the plugin:
  
  ```xml
  <project>
    ...
    <build>
  		<plugins>
  		...
  			<plugin>
  				<groupId>org.apache.felix</groupId>
  				<artifactId>maven-bundle-plugin</artifactId>
  				<extensions>true</extensions>
  				<configuration>
  					<instructions>
  						<Import-Package>*</Import-Package>
  						<Export-Package>
  							org.mqnaas.examples.helloworld
  						</Export-Package>
  					</instructions>
  				</configuration>
  			</plugin>
  		</plugins>
  	</build>
  </project>
  ```
  
  In this case, only `Import-Package` and `Export-Package` instructions are configured. This is a common way to proceed. There are more instructions defined in [Maven parent project POM file](/examples/pom.xml#L127).
  
  Moreover, it is necessary to define a different `packaging` for the Maven project. Instead of common `jar` type or similar, `bundle` type must be used. This simple line in the `pom.xml` file defines it:
  
  ```xml
  <project>
    ...
    <packaging>bundle</packaging>
    ...
  </project>
  ```
  
The capability
--------------
### Capability interface
It is time to develop a capability. First of all, a capability must be defined as a Java interface. Each method of the capability interface will be a service. Any capability Java interface must extend [ICapability Java interface](/core.api/src/main/java/org/mqnaas/core/api/ICapability.java).

In this example capability, there will be only a method receiving a plain Java String and returning another one. This portion of code shows it:

```java
public interface IHelloWorldCapability extends ICapability {
  ...
  public String hello(String name);
  
}
```

This is one of the simplest capability definitions one can develop.

### Capability implementation
A capability implementation is simply a Java class implementing an ICapability interface. There can be one or more implementations, but only one at the time can be used. In this example, one single implementation is provided and used.

First of all, each method of the interface must be implemented, as usual. In this case, a simple approach could be:

```java
public class HelloWorldCapability implements IHelloWorldCapability {

	@Override
	public String hello(String name) {
		return "Hello " + name + "!";
	}

}
```

But there are still things to be done. A capability implementation must contain these three mandatory methods: `activate`, `deactivate` and `isSupporting`.

The first two methods are called just after capability initialization and just before capability uninitialization respectively. Both are defined in the [IApplication interface](/core.api/src/main/java/org/mqnaas/core/api/IApplication.java), extended by `ICapability`.

`isSupporting` is responsible of matching resources and capabilities, in other words, which capabilities would be bound to which resources. In this example, we want the Hello World capability to match with resources of Type `OTHER` and Model `hello-world` (this is an arbitrary decision for this example). The method will look like:

```java
public static boolean isSupporting(IRootResource resource) {
	return resource.getDescriptor().getSpecification().getType().equals(Type.OTHER)
			&& resource.getDescriptor().getSpecification().getModel().equals("hello-world");
}
```

As seen in the code, method receives an `IRootResource` object and returns a boolean. It should return `true` if this capability should be bound to this resource, and `false` otherwise. Any logic can be applied, taking into account all the available information.

  <img src="/docs/images/warning.png" height=25 /> The compiler will not detect a capability implementation without `isSupporting` method, but an error will raise at runtime.
  
Apache Karaf Feature
--------------------
In order to ease the deployment of the OSGi bundle in MQNaaS it is necessary to create an [Apache Karaf feature](https://karaf.apache.org/manual/latest/users-guide/provisioning.html). Karaf features are defined in an XML file located in the Java resources folder ([/src/main/resources/features.xml](/examples/hello-world/src/main/resources/features.xml)). This is the definition of Hello Word Karaf feature:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<features xmlns="http://karaf.apache.org/xmlns/features/v1.2.0" name="mqnaas">

	<!-- MQNaaS features repository -->
	<repository>mvn:org.mqnaas/mqnaas/${mqnaas-version}/xml/features</repository>

	<feature name="mqnaas-examples-helloworld" version="${project.version}">
		<!-- dependencies features -->		
		<feature version="${mqnaas-version}">mqnaas</feature>
		
		<!-- no bundle dependencies -->
		
		<!-- included bundles -->
		<bundle>mvn:${project.groupId}/${project.artifactId}/${project.version}</bundle>
	</feature>

</features>
```

This simple Karaf feature needs these elements:
* An Apache Karaf feature repository, for dependency features. MQNaaS has a Maven repository where basic Karaf features are available.
* The Hello World feature definition itself composed by:
  * MQNaaS as dependant feature.
  * Bundle dependendencies. In this case there are not OSGi bundle dependencies, but usually there will be.
  * Included bundles. Bundles that define this feature, usually only one.

  <img src="/docs/images/info.png" height=25 /> Note that many Maven properties are used in the Karaf feature definition file. These properties are implicit or defined in Maven POM project files, like [the project itself](pom.xml) or [the parent project one](../pom.xml#L11). They are processed by the [Maven Resources Plugin](https://maven.apache.org/plugins/maven-resources-plugin/) in order to obtain actual values in the final XML built file. Usage of this plugin is defined in [the Maven parent POM file](../pom.xml#L73)
  
  <img src="/docs/images/info.png" height=25 /> Apache Karaf features file is deployed as Maven artifact using [Build Helper Maven Plugin](http://mojo.codehaus.org/build-helper-maven-plugin/). Usage of this plugin is defined in [the Maven parent POM file](../pom.xml#L98). 

Building
--------

In order to build the Hello World project, follow these steps:

* It is necessary to have at least Git client, Oracle Java SE 6, Maven 3 and a decent development machine.
* Download latest Apache Karaf 3.0.x [here](https://karaf.apache.org/index/community/download.html) and extract it in any user's folder.
* Using Git client, clone MQNaaS code into any user's folder:

  ```
  git clone https://github.com/dana-i2cat/mqnaas
  ```

* Build MQNaaS with:

  ```
  cd mqnaas/
  mvn clean install
  ```
  
  If everything worked as expected, this output should be something like:
  
  ```
  ...
  [INFO] BUILD SUCCESS
  ...
  ```

* Execute Karaf as exposed [here](/README.md#executing).
* Install Hello World Karaf feature with:
  ```
  feature:repo-add mvn:org.mqnaas.examples/hello-world/0.0.1-SNAPSHOT/xml/features
  feature:install mqnaas-examples-helloworld
  ```
  
  MQNaaS will be installed, since it is one of the dependencies.
  
  Once it is done, it is possible to check if everything worked (no errors are present in the shell). Moreover, this command:
  
  ```
  list | grep "Hello World"
  ```
  
  should return something like this:
  
  ```
  176 | Active |  80 | 0.0.1.SNAPSHOT | MQNaaS :: Examples :: Hello World
  ```
  
  * Test capability
  
  The way to test the Hello world capability is to create a resource with the capability and calling the auto-published REST API. To use `cURL` as HTTP client is the simplest option.
  
    * Create a resource with this command in a shell:
    
    ```
    curl -i -X PUT -H "Content-Type: application/xml" -d '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <ns2:rootResourceDescriptor xmlns:ns2="org.mqnaas">
        <specification>
            <type>OTHER</type>
            <model>hello-world</model>
            <version>1.0</version>
        </specification>
        <endpoints>
          <endpoint />
        </endpoints>
    </ns2:rootResourceDescriptor>' http://localhost:9000/mqnaas/IRootResourceAdministration/
    ```
    
    The expected response should be something like this:
    
    ```
    HTTP/1.1 200 OK
    Content-Type: application/octet-stream
    Date: Tue, 14 Apr 2015 13:26:24 GMT
    Content-Length: 23
    Server: Jetty(8.1.15.v20140411)
    
    Other-hello-world-1.0-2
    ```
    
    * Call the capability with your name (in this sample, Alice is used as name):
    
    ```
    curl -i -X GET -H "Content-Type: application/xml" http://localhost:9000/mqnaas/IRootResourceAdministration/Other-hello-world-1.0-2/IHelloWorldCapability/hello?arg0=Alice
    ```
    
    With this expected response:
    
    ```
    HTTP/1.1 200 OK
    Content-Type: application/octet-stream
    Date: Tue, 14 Apr 2015 13:35:26 GMT
    Content-Length: 42
    Server: Jetty(8.1.15.v20140411)
    
    Hello, Alice from Other-hello-world-1.0-2.
    ```