FROM qaware/zulu-centos-payara-micro:8u181-5.183

CMD ["--logproperties", "/opt/payara/logging.properties", "--deploymentDir", "/opt/payara/deployments"]

COPY logging.properties /opt/payara/
COPY build/libs/logging-service.war /opt/payara/deployments/
