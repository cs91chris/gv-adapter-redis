# GreenVulcano VCL Adapter for Redis

This is the implementation of a GreenVulcano VCL Adapter for the Redis database.

## Prerequisites

You need to install the GreenVulcano engine on the Apache Karaf container. Please refer to [this link](https://github.com/green-vulcano/gv-engine/blob/master/quickstart-guide.md) for further reference.

In order to install the bundle in Apache Karaf to use it for a GreenVulcano application project, you need to install its dependencies. Open the Apache Karaf terminal by running the Karaf executable and type the following command:

```shell
bundle:install -s -l 81 mvn:redis.clients/jedis/3.3.0
```

Having done that, use the ``list`` command to make sure bundle are in ``Active`` status.

Then, you need to install the VCL adapter bundle itself in Apache Karaf.

## Installation

Clone or download this repository on your computer, and then run ``mvn install`` in its root folder.

Then, run this command in the karaf shell to install the actual extension:

```shell
bundle:install -s -l 96 mvn:<PATH_PROJECT>/target/gvvcl-redis-<VERSION>.jar
```

## Usage Example

Here's an example:

```xml
<redis-call name="test" endpoint="localhost" as-json="true" type="call">
	<Set type="redisOperation" key="number" value="0"/>
	<Set type="redisOperation" key="testKey" value="testValue"/>
	<Set type="redisOperation" key="testKey1" value="testValue1"/>
	<Set type="redisOperation" key="testKey2" value="testValue2"/>
	<Set type="redisOperation" key="testKey2" value="testValue2" append="true"/>
	<Get type="redisOperation" key="testKey"/>
	<Delete type="redisOperation" keys="testKey"/>
	<Keys type="redisOperation" pattern="testKey*"/>
	<Sum type="redisOperation" key="number" value="2"/>
	<Sum type="redisOperation" key="number" value="-1"/>
</redis-call>

```

With Placeholders:

	- redisGET{{endpoint::key}}
	- redisINCR{{endpoint::key}}
	- redisDECR{{endpoint::key}}
