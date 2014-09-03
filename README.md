# hobknob-client-java

> A Java client library for Hobknob

## Installation

TODO (mention Vagrantfile)

### Building

TDOO

## Usage

```java

String etcdHost = "localhost";
int etcdPort = 4001;
String applicationName = "radApp";
int cacheUpdateIntervalMs = 60 * 1000;

HobknobClient client = new HobknobClientFactory().create(etcdHost, etcdPort, applicationName, cacheUpdateIntervalMs);

boolean toggle1Status = client.get("Toggle1"); // true
boolean toggle2Status = client.get("ToggleThatDoesNotExist"); // throws exception
boolean toggle3Status = client.getOrDefault("ToggleThatDoesNotExist", true); // true

```

## Etcd

Feature toggles are stored in Etcd using the following convention:
`http://host:port/v2/keys/v1/toggles/applicationName/toggleName`

## API

### HobknobClientFactory().create(String etcdHost, int etcdPort, String applicationName, int cacheUpdateIntervalMs)

Creates a new feature toggle client.

- `etcdHost` the host of the Etcd instance
- `etcdPort` the port of the Etcd instance
- `applicationName` the name of the application used to find feature toggles
- `cacheUpdateIntervalMs` interval for the cache update in milliseconds, which loads all the applications toggles into memory

### client.get(String toggleName)

Gets the boolean value of a feature toggle if it exists, otherwise throw exception

- `toggleName` the name of the toggle, used with the application name to get the feature toggle value


### client.getOrDefault(String toggleName, bool defaultValue)

Gets the boolean value of a feature toggle if it exists, otherwise return the default value

- `toggleName` the name of the toggle, used with the application name to get the feature toggle value
- `defaultValue` the value to return if the toggle value is not found
