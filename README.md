# ODL-Messenger-Tutorial

## Overview
The following tutorial will breakdown step by step how to create Opendaylight project then we will use Yang to model our Messenger example and generate the abstraction layer of a Messenger to Opendaylight. To be continuo 

## Messenger Parts
There are several parts that make up this Messenger step-by-step example. During this example we illustrate how the yang model is providing abstraction for us, and how MD-SAL provides the plumbing (wiring) to hook everything up. We will define the project structure and the parent pom.xml gathering the different modules we'll build.
* Part 1 of this example will define the Messenger data model (north-bound interface) and will provide a read-only implementation to retrieve operational data on the Messenger.
* Part 2 will add and implement a remote procedure call which will allow the user to interact with the operations restconf interface, as well as see status changes to operational data.
* Part 3 illustrates how a user can modify configuration data via restconf and how our Messenger can listener for those changes. 
* Part 4
* Part 5 

### Prerequisites
* Java JDK 1.8+
* Maven 3.3.9+

= Prepare the Project Structure =
The first we want to do is prepare the bundle structure, as such we will be using an archetype generator to create the project skeleton.

create your project with the archetype by typing:

<pre>
mvn archetype:generate -DarchetypeGroupId=org.opendaylight.controller -DarchetypeArtifactId=opendaylight-startup-archetype \
-DarchetypeRepository=http://nexus.opendaylight.org/content/repositories/<Snapshot-Type>/ \
-DarchetypeCatalog=http://nexus.opendaylight.org/content/repositories/<Snapshot-Type>/archetype-catalog.xml \
-DarchetypeVersion=<Archetype-Version>
</pre>

You need to enter the proper <Archetype-Version> and <Snapshot-Type> that depends on the ODL release you want to work in. for example:
* For the current Master (Carbon) use Snapshot-Type='''opendaylight.snapshot''' Archetype-Version='''1.3.0-SNAPSHOT'''
* For Boron "SR0" use Snapshot-Type='''opendaylight.release''' Archetype-Version='''1.2.0-Boron'''
* For Boron SR1 use Archetype-Version='''opendaylight.release''' Archetype-Version='''1.2.1-Boron-SR1'''
* For the Boron snapshot use Snapshot-Type='''opendaylight.snapshot''' Archetype-Version='''1.2.2-SNAPSHOT'''

Note each version of the archetype generates version numbers in pom.xml dependencies for its intended ODL revision. 

Respond to the prompts (Please note that groupid and artifactid need to be all lower case):
<pre>
Define value for property 'groupId': : org.opendaylight.Messenger
Define value for property 'artifactId': : Messenger
Define value for property 'version':  1.0-SNAPSHOT: : 0.1.0-SNAPSHOT
Define value for property 'package':  org.opendaylight.Messenger: : 
Define value for property 'classPrefix':  ${artifactId.substring(0,1).toUpperCase()}${artifactId.substring(1)}
Define value for property 'copyright': : you/me/whatever
</pre>

Once completed, the project structure should look like this:
<pre>
[Root directory]
   api/
   artifacts/
   cli/
   features/
   impl/
   it/
   karaf/
   pom.xml
</pre>

That maven command has generated 7 bundles and 1 bundle aggregator. The aggregator is represented by the pom.xml file at the root of the project and it will "aggregate" the sub-bundles into "modules". This aggregator pom.xml file is of type "pom" so there are no jar files generated from this. The subfolders represent bundles and will also have their own pom.xml files, each of these file will generate a jar file at the end. It also create the target/ and src/ directories and deploy-site.xml.

Lets go over what each bundles (module) do:

* api : This is where we define the Messenger model. It has api in its name because it will be used by RestConf to define a set of rest APIs.
* artifacts: This is where the bundles gets generated as 
* cli: This bundle is used to provides a Karaf CLI functionality to Messenger.
* features: This bundle is used to deploy the Messenger into the karaf instance. It contains a feature descriptor or features.xml file.
* impl: This is where we tell what to do with the Messenger. This bundle depends on the api to defines its operations.
* it: This is used to test the Messenger work within the integration test of Opendaylight. 
* karaf: This is the instance in which we will be deploying our Messenger. Once compile, it creates a distribution that we can execute to run the karaf instance.

Here is highlighted the important sections of the aggregator pom.xml file:
The ''pom.xml'' file works as aggregator and defines the parent project, and will declare the modules presented in the structure above:

<pre>
...
  <parent>
    <groupId>org.opendaylight.odlparent</groupId>
    <artifactId>odlparent</artifactId>
    <version>1.7.0-Boron</version>
    <relativePath/>
  </parent>
...
  <groupId>org.opendaylight.Messenger</groupId>
  <artifactId>Messenger-aggregator</artifactId>
  <version>0.1.0-SNAPSHOT</version>
  <name>Messenger</name>
  <packaging>pom</packaging>
...
  <modules>
    <module>api</module>
    <module>impl</module>
    <module>karaf</module>
    <module>features</module>
    <module>artifacts</module>
    <module>cli</module>
    <module>it</module>
  </modules>
...
</pre>

To Be Continuo
