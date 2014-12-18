# hobknob-client-java

> A Java client library for Hobknob

## Installation

### Via Maven

To use this library in your project, the easiest way is via Maven. Add the following dependency to you pom.xml.

```xml
<dependency>
  <groupId>com.opentable.hobknob</groupId>
  <artifactId>hobknob-client-java</artifactId>
  <version>1.0</version>
</dependency>
```

### Building from source

Maven is used to build this project. Make sure you have a recent version of Maven installed, as well as a recent version of the JDK which supports Java 8.

```
mvn clean install
```

This will, amongst other things, output a .jar file into the 'target' directory.

##### Note:
By default, the tests are run in the Maven install task.
The tests are exclusively end-to-end and require a working instance of Etcd installed locally on port 4001 (the host and port can be configured in the TestBase class).
There is a Vagrantfile for convenience that sets up Vagrant to host Etcd, simply by using `vagrant up`.

If you *do not* want to run the tests when building the project, use:

```
mvn clean -Dmaven.test.skip=true install
```

## Usage

```java

String etcdHost = "localhost";
int etcdPort = 4001;
String applicationName = "radApp";
int cacheUpdateIntervalMs = 60 * 1000;

HobknobClient client = new HobknobClientFactory().create(etcdHost, etcdPort, applicationName, cacheUpdateIntervalMs);

boolean toggle1Status = client.get("Feature1"); // true
boolean toggle2Status = client.get("FeatureThatDoesNotExist"); // throws exception
boolean toggle3Status = client.getOrDefault("FeatureThatDoesNotExist", true); // true

boolean multiToggle1Status = client.get("Feature1", "com"); // true
boolean multiToggle2Status = client.get("Feature1", "ToggleThatDoesNotExist"); // throws exception
boolean multiToggle3Status = client.getOrDefault("Feature1", "ToggleThatDoesNotExist", true); // true

```

## Etcd

Feature toggles are stored in Etcd using the following convention:
`http://host:port/v2/keys/v1/toggles/applicationName/featureName/toggleName`

## API

### HobknobClientFactory().create(String etcdHost, int etcdPort, String applicationName, int cacheUpdateIntervalMs)

Creates a new feature toggle client.

- `etcdHost` the host of the Etcd instance
- `etcdPort` the port of the Etcd instance
- `applicationName` the name of the application used to find feature toggles
- `cacheUpdateIntervalMs` interval for the cache update in milliseconds, which loads all the applications toggles into memory

### client.get(String featureName)

Gets the boolean value of a simple feature toggle if it exists, otherwise throw exception

- `featureName` the name of the feature toggle, used with the application name to get the feature toggle value

### client.get(String featureName, String toggleName)

Gets the boolean value of a multi feature toggle if it exists, otherwise throw exception

- `featureName` the name of the feature
- `toggleName` the name of the toggle - only specify when getting values for non-simple feature toggles

### client.getOrDefault(String featureName, boolean defaultValue)

Gets the boolean value of a simple feature toggle if it exists, otherwise return the default value

- `featureName` the name of the feature, used with the application name to get the feature toggle value
- `defaultValue` the value to return if the feature value is not found

### client.getOrDefault(String featureName, String toggleName, boolean defaultValue)

Gets the boolean value of a multi feature toggle if it exists, otherwise return the default value

- `featureName` the name of the feature
- `toggleName` the name of the toggle - only specify when getting values for non-simple feature toggles
- `defaultValue` the value to return if the feature toggle value is not found

## Publishing to Maven repository

This project can be published to the central Maven repository to allow easy consumption.
The project is configured to release to the Sonatype OSS for staging and promotion to the central repository.

To deploy to the repository, enter the following:

```
mvn clean deploy
```

##### Prerequisite

You will need to set up a Sonatype Jira account to be able to publish this project: https://issues.sonatype.org/secure/Dashboard.jspa.

For signing, you will need GPG installed and set up to publish this project.
Follow the instruction here: http://central.sonatype.org/pages/working-with-pgp-signatures.html.

Once you have done the two steps above, you should be ready to configure Maven.
Edit or create the Maven settings file, usually stored at ~/.mv/settings.xml, to add your Jira account details and GPG details.
The file should look something like the following:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
      <server>
          <id>ossrh</id>
          <username>jira_username</username>
          <password>jira_password</password>
      </server>
  </servers>
  <profiles>
    <profile>
      <id>ossrh</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <gpg.executable>gpg2</gpg.executable>
        <gpg.passphrase>password</gpg.passphrase>
      </properties>
    </profile>
  </profiles>
</settings>
```