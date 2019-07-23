FROM bitnami/cassandra:3

RUN curl https://gist.githubusercontent.com/tjake/fb166a659e8fe4c8d4a3/raw/37a65834430fdd699dbef8a102ec0e412c6195cc/blogpost.yaml --output /tmp/blogpost.yaml

RUN echo -n '#!/bin/bash\n\n/opt/bitnami/cassandra/tools/bin/cassandra-stress user profile=/tmp/blogpost.yaml ops\(insert=1\) -mode native cql3 user=$CASSANDRA_USER password=$CASSANDRA_PASSWORD\n' > /tmp/stress-write.sh && chmod a+x /tmp/stress-write.sh

RUN echo -n '#!/bin/bash\n\n/opt/bitnami/cassandra/tools/bin/cassandra-stress user profile=/tmp/blogpost.yaml ops\(singlepost=2,timeline=1\) -mode native cql3 user=$CASSANDRA_USER password=$CASSANDRA_PASSWORD\n' > /tmp/stress-read.sh && chmod a+x /tmp/stress-read.sh
