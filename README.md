# What is Cassandra?

> [Apache Cassandra](http://cassandra.apache.org) is a free and open-source distributed database management system designed to handle large amounts of data across many commodity servers, providing high availability with no single point of failure. Cassandra offers robust support for clusters spanning multiple datacenters, with asynchronous masterless replication allowing low latency operations for all clients.

# TL;DR;

To build cassandra-odbc-client image you need to download *.rpm file which contains Simba driver for cassandra from official site: https://www.simba.com/drivers/cassandra-odbc-jdbc/. You need to register and place order (Free Trial). After that, you should place your RPM package as well as license file *.lic in the same folder of Dockerfile e.g. /cassandra-odbc-client

```bash
$ docker-compose build  # Build containers (see directories cassandra and cassandra-jdbc-client)
$ docker-compose up -d  # Run containers as daemons
$
$ sleep 60              # Wait a minute till Cassandra server starts
$
$                       # Stress test Cassandra creating blogposts table and writing the contents
$ docker-compose exec cassandra /tmp/stress-write.sh
$
$                       # Stress test Cassandra reading the contents of the table (optional)
$ docker-compose exec cassandra /tmp/stress-read.sh
$
$                       # Run query against JDBC Cassandra connector from other container
$ echo '(jdbc/query cassandra-db ["select * from blogposts limit 1"])' | docker-compose exec -T cassandra-jdbc-client lein repl
$                       # It is okay if some CLI artifacts outputted - it is the contents of the table's first row
$
$                       # Run query against ODBC Cassandra connector from other container
$ echo 'SELECT * FROM blogposts LIMIT 1' | docker-compose exec -T cassandra-odbc-client isql cassandraSimba64
$                       # It is okay if some CLI artifacts outputted - it is the contents of the table's first row
$
$ docker-compose rm -fs # Stop and remove containers when done
```

# Why use Bitnami Images?

* Bitnami closely tracks upstream source changes and promptly publishes new versions of this image using our automated systems.
* With Bitnami images the latest bug fixes and features are available as soon as possible.
* Bitnami containers, virtual machines and cloud images use the same components and configuration approach - making it easy to switch between formats based on your project needs.
* All our images are based on [minideb](https://github.com/bitnami/minideb) a minimalist Debian based container image which gives you a small base container image and the familiarity of a leading linux distribution.
* All Bitnami images available in Docker Hub are signed with [Docker Content Trust (DTC)](https://docs.docker.com/engine/security/trust/content_trust/). You can use `DOCKER_CONTENT_TRUST=1` to verify the integrity of the images.
* Bitnami container images are released daily with the latest distribution packages available.


> This [CVE scan report](https://quay.io/repository/bitnami/cassandra?tab=tags) contains a security report with all open CVEs. To get the list of actionable security issues, find the "latest" tag, click the vulnerability report link under the corresponding "Security scan" field and then select the "Only show fixable" filter on the next page.

# Why use a non-root container?

Non-root container images add an extra layer of security and are generally recommended for production environments. However, because they run as a non-root user, privileged tasks are typically off-limits. Learn more about non-root containers [in our docs](https://docs.bitnami.com/containers/how-to/work-with-non-root-containers/).

# How to deploy Cassandra in Kubernetes?

Deploying Bitnami applications as Helm Charts is the easiest way to get started with our applications on Kubernetes. Read more about the installation in the [Bitnami Cassandra Chart GitHub repository](https://github.com/bitnami/charts/tree/master/bitnami/cassandra).

Bitnami containers can be used with [Kubeapps](https://kubeapps.com/) for deployment and management of Helm Charts in clusters.

# Supported tags and respective `Dockerfile` links

> NOTE: Debian 8 images have been deprecated in favor of Debian 9 images. Bitnami will not longer publish new Docker images based on Debian 8.

Learn more about the Bitnami tagging policy and the difference between rolling tags and immutable tags [in our documentation page](https://docs.bitnami.com/containers/how-to/understand-rolling-tags-containers/).


* [`3-rhel-7`, `3.11.4-rhel-7-r67` (3/rhel-7/Dockerfile)](https://github.com/bitnami/bitnami-docker-cassandra/blob/3.11.4-rhel-7-r67/3/rhel-7/Dockerfile)
* [`3-ol-7`, `3.11.4-ol-7-r155` (3/ol-7/Dockerfile)](https://github.com/bitnami/bitnami-docker-cassandra/blob/3.11.4-ol-7-r155/3/ol-7/Dockerfile)
* [`3-debian-9`, `3.11.4-debian-9-r139`, `3`, `3.11.4`, `3.11.4-r139`, `latest` (3/debian-9/Dockerfile)](https://github.com/bitnami/bitnami-docker-cassandra/blob/3.11.4-debian-9-r139/3/debian-9/Dockerfile)

Subscribe to project updates by watching the [bitnami/cassandra GitHub repo](https://github.com/bitnami/bitnami-docker-cassandra).

# Get this image

The recommended way to get the Bitnami Cassandra Docker Image is to pull the prebuilt image from the [Docker Hub Registry](https://hub.docker.com/r/bitnami/cassandra).

```bash
$ docker pull bitnami/cassandra:latest
```

To use a specific version, you can pull a versioned tag. You can view the [list of available versions](https://hub.docker.com/r/bitnami/cassandra/tags/) in the Docker Hub Registry.

```bash
$ docker pull bitnami/cassandra:[TAG]
```

If you wish, you can also build the image yourself.

```bash
$ docker build -t bitnami/cassandra:latest https://github.com/bitnami/bitnami-docker-cassandra.git
```

# Persisting your application

If you remove the container all your data and configurations will be lost, and the next time you run the image the database will be reinitialized. To avoid this loss of data, you should mount a volume that will persist even after the container is removed.

For persistence you should mount a directory at the `/bitnami` path. If the mounted directory is empty, it will be initialized on the first run.

```bash
$ docker run \
    -v /path/to/cassandra-persistence:/bitnami \
    bitnami/cassandra:latest
```

or using Docker Compose:

```yaml
cassandra:
  image: bitnami/cassandra:latest
  volumes:
    - /path/to/cassandra-persistence:/bitnami
```

# Connecting to other containers

Using [Docker container networking](https://docs.docker.com/engine/userguide/networking/), a Cassandra server running inside a container can easily be accessed by your application containers.

Containers attached to the same network can communicate with each other using the container name as the hostname.

## Using the Command Line

In this example, we will create a Cassandra client instance that will connect to the server instance that is running on the same docker network as the client.

### Step 1: Create a network

```bash
$ docker network create app-tier --driver bridge
```

### Step 2: Launch the Cassandra server instance

Use the `--network app-tier` argument to the `docker run` command to attach the Cassandra container to the `app-tier` network.

```bash
$ docker run -d --name cassandra-server \
    --network app-tier \
    bitnami/cassandra:latest
```

### Step 3: Launch your Cassandra client instance

Finally we create a new container instance to launch the Cassandra client and connect to the server created in the previous step:

```bash
$ docker run -it --rm \
    --network app-tier \
    bitnami/cassandra:latest cqlsh --username cassandra --password cassandra cassandra-server
```

## Using Docker Compose

When not specified, Docker Compose automatically sets up a new network and attaches all deployed services to that network. However, we will explicitly define a new `bridge` network named `app-tier`. In this example we assume that you want to connect to the Cassandra server from your own custom application image which is identified in the following snippet by the service name `myapp`.

```yaml
version: '2'

networks:
  app-tier:
    driver: bridge

services:
  cassandra:
    image: 'bitnami/cassandra:latest'
    networks:
      - app-tier
  myapp:
    image: 'YOUR_APPLICATION_IMAGE'
    networks:
      - app-tier
```

> **IMPORTANT**:
>
> 1. Please update the **YOUR_APPLICATION_IMAGE_** placeholder in the above snippet with your application image
> 2. In your application container, use the hostname `cassandra` to connect to the Cassandra server

Launch the containers using:

```bash
$ docker-compose up -d
```

# Configuration

## Environment variables

 When you start the cassandra image, you can adjust the configuration of the instance by passing one or more environment variables either on the docker-compose file or on the docker run command line. If you want to add a new environment variable:

 * For docker-compose add the variable name and value under the application section:
```yaml
cassandra:
  image: bitnami/cassandra:latest
  environment:
    - CASSANDRA_TRANSPORT_PORT_NUMBER=7000
```

 * For manual execution add a `-e` option with each variable and value:

```bash
 $ docker run --name cassandra -d -p 7000:7000 --network=cassandra_network \
    -e CASSANDRA_TRANSPORT_PORT_NUMBER=7000 \
    -v /your/local/path/bitnami/cassandra:/bitnami \
    bitnami/cassandra
```

**In case you do not mount custom configuration files**, the following variables are available for configuring cassandra:

 - `CASSANDRA_TRANSPORT_PORT_NUMBER`: Inter-node cluster communication port. Default: **7000**
 - `CASSANDRA_JMX_PORT_NUMBER`: JMX connections port. Default: **7199**
 - `CASSANDRA_CQL_PORT_NUMBER`: Client port. Default: **9042**.
 - `CASSANDRA_USER`: Cassandra user name. Defaults: **cassandra**
 - `CASSANDRA_PASSWORD_SEEDER`: Password seeder will change the Cassandra default credentials at initialization. In clusters, only one node should be marked as password seeder. Default: **no**
 - `CASSANDRA_PASSWORD`: Cassandra user password. Default: **cassandra**
 - `CASSANDRA_NUM_TOKENS`: Number of tokens for the node. Default: **256**.
 - `CASSANDRA_HOST`: Hostname used to configure Cassandra. It can be either an IP or a domain. If left empty, it will be resolved to the machine IP.
 - `CASSANDRA_CLUSTER_NAME`: Cluster name to configure Cassandra.. Defaults: **My Cluster**
 - `CASSANDRA_SEEDS`: Hosts that will act as Cassandra seeds. No defaults.
 - `CASSANDRA_ENDPOINT_SNITCH`: Snitch name (which determines which data centers and racks nodes belong to). Default **SimpleSnitch**
 - `CASSANDRA_ENABLE_RPC`: Enable the thrift RPC endpoint. Default :**true**
 - `CASSANDRA_DATACENTER`: Datacenter name for the cluster. Ignored in **SimpleSnitch** endpoint snitch. Default: **dc1**.
 - `CASSANDRA_RACK`: Rack name for the cluster. Ignored in **SimpleSnitch** endpoint snitch. Default: **rack1**.

## Configuration file

The image looks for configurations in `/opt/bitnami/cassandra/conf/`. You can mount a volume at `/bitnami/cassandra/conf/` and copy/edit the configurations in the `/path/to/cassandra-persistence/conf/`. The default configurations will be populated to the `conf/` directory if it's empty.

For example, in order to override the `cassandra.yaml` configuration file:

### Step 1: Write your custom `cassandra.yaml` file
You can download the basic cassandra.yaml file like follows

```bash
wget https://raw.githubusercontent.com/apache/cassandra/trunk/conf/cassandra.yaml
```

Perform any desired modifications in that file

### Step 2: Run the Cassandra image with the designed volume attached.

```bash
$ docker run --name cassandra \
    -p 7000:7000  \
    -e CASSANDRA_TRANSPORT_PORT_NUMBER=7000 \
    -v /path/to/cassandra.yaml:/bitnami/cassandra/conf/cassandra.yaml:ro \
    -v /your/local/path/bitnami/cassandra:/bitnami \
    bitnami/cassandra:latest
```

or using Docker Compose:

```yaml
version: '2'

services:
  cassandra:
    image: bitnami/cassandra:latest
    environment:
      - CASSANDRA_TRANSPORT_PORT_NUMBER=7000
    volumes:
      - /path/to/cassandra.yaml:/bitnami/cassandra/conf/cassandra.yaml:ro
      - /your/local/path/bitnami/cassandra:/bitnami
```

After that, your changes will be taken into account in the server's behaviour. Note that you can override any other Cassandra configuration file, such as `rack-dc.properties`.

Refer to the [Cassandra configuration reference](https://docs.datastax.com/en/cassandra/3.0/cassandra/configuration/configCassandra_yaml.html) for the complete list of configuration options.


## Setting the server password on first run

Passing the `CASSANDRA_PASSWORD` environment variable along with `CASSANDRA_PASSWORD_SEEDER=yes` when running the image for the first time will set the Cassandra server password to the value of `CASSANDRA_PASSWORD`.

```bash
$ docker run --name cassandra \
    -e CASSANDRA_PASSWORD_SEEDER=yes \
    -e CASSANDRA_PASSWORD=password123 \
    bitnami/cassandra:latest
```

or using Docker Compose:

```yaml
cassandra:
  image: bitnami/cassandra:latest
  environment:
    - CASSANDRA_PASSWORD_SEEDER=yes
    - CASSANDRA_PASSWORD=password123
```

## Setting up a cluster

A cluster can easily be setup with the Bitnami Cassandra Docker Image. **In case you do not mount custom configuration files**, you can use the following environment variables:

 - `CASSANDRA_HOST`: Hostname used to configure Cassandra. It can be either an IP or a domain. If left empty, it will be resolved to the machine IP.
 - `CASSANDRA_CLUSTER_NAME`: Cluster name to configure Cassandra. Defaults: **My Cluster**
 - `CASSANDRA_SEEDS`: Hosts that will act as Cassandra seeds. No defaults.
 - `CASSANDRA_ENDPOINT_SNITCH`: Snitch name (which determines which data centers and racks nodes belong to). Default **SimpleSnitch**
 - `CASSANDRA_PASSWORD_SEEDER`: Password seeder will change the Cassandra default credentials at initialization. Only one node should be marked as password seeder. Default: **no**
 - `CASSANDRA_PASSWORD`: Cassandra user password. Default: **cassandra**

### Step 1: Create a new network.

```bash
$ docker network create cassandra_network
```

### Step 2: Create a first node.

```bash
$ docker run --name cassandra-node1 \
  --net=cassandra_network \
  -p 9042:9042 \
  -e CASSANDRA_CLUSTER_NAME=cassandra-cluster \
  -e CASSANDRA_SEEDS=cassandra-node1,cassandra-node2 \
  -e CASSANDRA_PASSWORD_SEEDER=yes \
  -e CASSANDRA_PASSWORD=mypassword \
  bitnami/cassandra:latest
```
In the above command the container is added to a cluster named `cassandra-cluster` using the `CASSANDRA_CLUSTER_NAME`. The `CASSANDRA_CLUSTER_HOSTS` parameter set the name of the nodes that set the cluster so we will need to launch other container for the second node. Finally the `CASSANDRA_NODE_NAME` parameter allows to indicate a known name for the node, otherwise cassandra will generate a randon one.

### Step 3: Create a second node

```bash
$ docker run --name cassandra-node2 \
  --net=cassandra_network \
  -e CASSANDRA_CLUSTER_NAME=cassandra-cluster \
  -e CASSANDRA_SEEDS=cassandra-node1,cassandra-node2 \
  -e CASSANDRA_PASSWORD=mypassword \
  bitnami/cassandra:latest
```

In the above command a new cassandra node is being added to the cassandra cluster indicated by `CASSANDRA_CLUSTER_NAME`.

You now have a two node Cassandra cluster up and running which can be scaled by adding/removing nodes.

With Docker Compose the cluster configuration can be setup using:

```yaml
version: '2'
services:
  cassandra-node1:
    image: bitnami/cassandra:latest
    environment:
      - CASSANDRA_CLUSTER_NAME=cassandra-cluster
      - CASSANDRA_SEEDS=cassandra-node1,cassandra-node2
      - CASSANDRA_PASSWORD_SEEDER=yes
      - CASSANDRA_PASSWORD=password123

  cassandra-node2:
    image: bitnami/cassandra:latest
    environment:
      - CASSANDRA_CLUSTER_NAME=cassandra-cluster
      - CASSANDRA_SEEDS=cassandra-node1,cassandra-node2
      - CASSANDRA_PASSWORD=password123
```
## Initializing with custom scripts

When the container is executed for the first time, it will execute the files with extensions `.sh` and `.cql` located at `/docker-entrypoint-initdb.d`. This behavior can be skipped by setting the environment variable `CASSANDRA_IGNORE_INITDB_SCRIPTS`.

In order to have your custom files inside the docker image you can mount them as a volume.

```bash
$ docker run --name cassandra \
  -v /path/to/init-scripts:/docker-entrypoint-initdb.d \
  -v /path/to/cassandra-persistence:/bitnami
  bitnami/cassandra:latest
```
Or with docker-compose

```yaml
cassandra:
  image: bitnami/cassandra:latest
  volumes:
    - /path/to/init-scripts:/docker-entrypoint-initdb.d
    - /path/to/cassandra-persistence:/bitnami
```

## Configuration file

The image looks for configurations in `/bitnami/cassandra/conf/`. As mentioned in [Persisting your application](#persisting-your-application) you can mount a volume at `/bitnami` and copy/edit the configurations in the `/path/to/cassandra-persistence/cassandra/conf/`. The default configurations will be populated to the `conf/` directory if it's empty.

### Step 1: Run the Cassandra image

Run the Cassandra image, mounting a directory from your host.

```bash
$ docker run --name cassandra \
    -v /path/to/cassandra-persistence:/bitnami \
    bitnami/cassandra:latest
```

or using Docker Compose:

```yaml
cassandra:
  image: bitnami/cassandra:latest
  volumes:
    - /path/to/cassandra-persistence:/bitnami
```

### Step 2: Edit the configuration

Edit the configuration on your host using your favorite editor.

```bash
vi /path/to/cassandra-persistence/cassandra/conf/cassandra.yaml
```

### Step 3: Restart Cassandra

After changing the configuration, restart your Cassandra container for changes to take effect.

```bash
$ docker restart cassandra
```

or using Docker Compose:

```bash
$ docker-compose restart cassandra
```

Refer to the [configuration](http://docs.datastax.com/en/cassandra/3.x/cassandra/configuration/configTOC.html) manual for the complete list of configuration options.

# TLS Encryption
The Bitnami Cassandra Docker image allows configuring TLS encryption between nodes and between server-client. This is done by mounting in `/bitnami/cassandra/secrets` two files:

 - `keystore`: File with the server keystore
 - `truststore`: File with the server truststore

Apart from that, the following environment variables must be set:

 - `CASSANDRA_KEYSTORE_PASSWORD`: Password for accessing the keystore.
 - `CASSANDRA_TRUSTSTORE_PASSWORD`: Password for accessing the truststore.
 - `CASSANDRA_INTERNODE_ENCRYPTION`: Sets the type of encryption between nodes. The default value is `none`. Can be set to `all`, `none`, `dc` or `rack`.
 - `CASSANDRA_CLIENT_ENCRYPTION`: Enables client-server encryption. The default value is `false`.

# Logging

The Bitnami Cassandra Docker image sends the container logs to the `stdout`. To view the logs:

```bash
$ docker logs cassandra
```

or using Docker Compose:

```bash
$ docker-compose logs cassandra
```

You can configure the containers [logging driver](https://docs.docker.com/engine/admin/logging/overview/) using the `--log-driver` option if you wish to consume the container logs differently. In the default configuration docker uses the `json-file` driver.

# Maintenance

## Upgrade this image

Bitnami provides up-to-date versions of Cassandra, including security patches, soon after they are made upstream. We recommend that you follow these steps to upgrade your container.

### Step 1: Get the updated image

```bash
$ docker pull bitnami/cassandra:latest
```

or if you're using Docker Compose, update the value of the image property to
`bitnami/cassandra:latest`.

### Step 2: Stop and backup the currently running container

Stop the currently running container using the command

```bash
$ docker stop cassandra
```

or using Docker Compose:

```bash
$ docker-compose stop cassandra
```

Next, take a snapshot of the persistent volume `/path/to/cassandra-persistence` using:

```bash
$ rsync -a /path/to/cassandra-persistence /path/to/cassandra-persistence.bkp.$(date +%Y%m%d-%H.%M.%S)
```

### Step 3: Remove the currently running container

```bash
$ docker rm -v cassandra
```

or using Docker Compose:

```bash
$ docker-compose rm -v cassandra
```

### Step 4: Run the new image

Re-create your container from the new image.

```bash
$ docker run --name cassandra bitnami/cassandra:latest
```

or using Docker Compose:

```bash
$ docker-compose up cassandra
```

# Notable Changes

## 3.11.3-r129

-The Cassandra container now adds the possibility to inject custom initialization scripts by mounting cql and sh files in `/docker-entrypoint-initdb.d`. See [this section](#initializing-with-custom-scripts) for more information.

## 3.11.2-r22

- The Cassandra container has been migrated to a non-root user approach. Previously the container ran as the `root` user and the Cassandra daemon was started as the `cassandra` user. From now on, both the container and the Cassandra daemon run as user `1001`. As a consequence, the data directory must be writable by that user. You can revert this behavior by changing `USER 1001` to `USER root` in the Dockerfile.

# Contributing

We'd love for you to contribute to this container. You can request new features by creating an [issue](https://github.com/bitnami/bitnami-docker-cassandra/issues), or submit a [pull request](https://github.com/bitnami/bitnami-docker-cassandra/pulls) with your contribution.

# Issues

If you encountered a problem running this container, you can file an [issue](https://github.com/bitnami/bitnami-docker-cassandra/issues). For us to provide better support, be sure to include the following information in your issue:

- Host OS and version
- Docker version (`docker version`)
- Output of `docker info`
- Version of this container (`echo $BITNAMI_IMAGE_VERSION` inside the container)
- The command you used to run the container, and any relevant output you saw (masking any sensitive information)

# License
Copyright (c) 2016-2019 Bitnami

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
