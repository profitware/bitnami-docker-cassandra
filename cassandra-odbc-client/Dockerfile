FROM centos:7

RUN yum -y install unixODBC unixODBC-devel wget
WORKDIR /opt/simba/cassandraodbc/

COPY simbacassandra-*x86_64.rpm .
RUN yum -y --nogpgcheck localinstall ./simbacassandra-*x86_64.rpm

RUN mkdir -p logs
COPY odbc.ini ./Setup/odbc.ini
COPY odbcinst.ini ./Setup/odbcinst.ini
COPY simba.cassandraodbc.ini ./lib/64/simba.cassandraodbc.ini
COPY SimbaApacheCassandraODBCDriver.lic ./lib/64/SimbaApacheCassandraODBCDriver.lic
