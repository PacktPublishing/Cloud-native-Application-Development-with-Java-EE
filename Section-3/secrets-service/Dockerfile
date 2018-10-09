FROM qaware/zulu-centos-payara-micro:8u181-5.183

CMD ["--postdeploycommandfile", "/opt/payara/post-deploy.asadmin"]

COPY src/main/docker/post-deploy.asadmin /opt/payara/
COPY build/libs/secrets-service.war /opt/payara/deployments/
