FROM qaware/zulu-centos-payara-micro:8u181-5.183

CMD ["--noCluster", "--postbootcommandfile", "/opt/payara/post-boot.asadmin", "--deploymentDir", "/opt/payara/deployments"]

COPY post-boot.asadmin /opt/payara/
COPY build/libs/tracing-service.war /opt/payara/deployments/
