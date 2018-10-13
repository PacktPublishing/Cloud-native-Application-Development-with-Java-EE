FROM qaware/zulu-centos-payara-micro:8u181-5.183

COPY post-deploy.asadmin /opt/payara/post-deploy.asadmin
COPY build/postgresql/* /opt/payara/libs/
COPY build/libs/cloud-native-jpa.war /opt/payara/deployments/

CMD ["--noCluster", "--addjars", "/opt/payara/libs/", "--postdeploycommandfile", "/opt/payara/post-deploy.asadmin"]
