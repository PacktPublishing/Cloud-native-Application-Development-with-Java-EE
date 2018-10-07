FROM qaware/zulu-centos-payara-micro:8u181-5.183

CMD ["--postdeploycommandfile", "/opt/payara/post-deploy.asadmin"]

COPY src/main/docker/* /opt/payara/
COPY build/activemq/activemq-rar-5.15.6.rar /opt/payara/deployments/activemq-rar.rar
COPY build/libs/messaging-service.war /opt/payara/deployments/