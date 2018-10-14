FROM qaware/zulu-centos-payara-micro:8u181-5.183

CMD ["--hzconfigfile", "/opt/payara/hazelcast.xml", "--deploymentDir", "/opt/payara/deployments"]

COPY src/main/docker/* /opt/payara/
COPY build/libs/jcache-api.war /opt/payara/deployments/
