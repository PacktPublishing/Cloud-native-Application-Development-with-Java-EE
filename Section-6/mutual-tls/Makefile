NAME = cloud-native-javaee
VERSION = 1.0.3
GCP = gcloud
K8S = kubectl

.PHONY: info

info:
	@echo "Mutual TLS between Java EE microservices with Istio"

prepare:
	@$(GCP) config set compute/zone europe-west1-b
	@$(GCP) config set container/use_client_certificate False

cluster:
	@$(GCP) container clusters create $(NAME) --num-nodes=7 --enable-autoscaling --min-nodes=7 --max-nodes=10
	@$(K8S) create clusterrolebinding cluster-admin-binding --clusterrole=cluster-admin --user=$$(gcloud config get-value core/account)
	@$(K8S) cluster-info

get-istio:
	@curl -L https://git.io/getLatestIstio | sh -
	@echo "Make sure to export the PATH variable."

istio-install:
	# deploy Istio
	@$(K8S) apply -f istio-$(VERSION)/install/kubernetes/helm/istio/templates/crds.yaml
	@sleep 5
	@$(K8S) apply -f istio-$(VERSION)/install/kubernetes/istio-demo-auth.yaml
	@sleep 5
	@$(K8S) get pods -n istio-system
	@$(K8S) label namespace default istio-injection=enabled
	@$(K8S) get svc istio-ingressgateway -n istio-system

access-token:
	@$(GCP) config config-helper --format=json | jq .credential.access_token

dashboard:
	@$(K8S) proxy & 2>&1
	@sleep 3
	@$(GCP) config config-helper --format=json | jq .credential.access_token
	@open http://localhost:8001/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/

grafana:
	@$(K8S) -n istio-system port-forward $$(kubectl -n istio-system get pod -l app=grafana -o jsonpath='{.items[0].metadata.name}') 8080:3000 & 2>&1
	@sleep 3
	@open http://localhost:8080/

prometheus:
	@$(K8S) -n istio-system port-forward $$(kubectl -n istio-system get pod -l app=prometheus -o jsonpath='{.items[0].metadata.name}') 8083:9090 & 2>&1
	@sleep 3
	@open http://localhost:8083/graph/

servicegraph:
	@$(K8S) -n istio-system port-forward $$(kubectl -n istio-system get pod -l app=servicegraph -o jsonpath='{.items[0].metadata.name}') 8082:8088 & 2>&1
	@sleep 3
	@open http://localhost:8082/force/forcegraph.html
	@open http://localhost:8082/dotviz

jaeger:
	@$(K8S) port-forward -n istio-system $$(kubectl get pod -n istio-system -l app=jaeger -o jsonpath='{.items[0].metadata.name}') 8084:16686 & 2>&1
	@sleep 3
	@open http://localhost:8084

alphabet-build:
	@cd microservices/alphabet-service/ && ./gradlew ass
	@cd microservices/alphabet-service/ && docker-compose build
	@docker push lreimer/alphabet-service:1.0.1

	@cd microservices/spelling-service/ && ./gradlew ass
	@cd microservices/spelling-service/ && docker-compose build
	@docker push lreimer/spelling-service:1.0.1

alphabet-demo:
	@$(K8S) apply -f showcase/spelling-service.yaml
	@$(K8S) apply -f showcase/spelling-gateway.yaml
	@$(K8S) apply -f showcase/spelling-service-virtual-service.yaml
	@$(K8S) apply -f showcase/spelling-service-destination.yaml

	@$(K8S) apply -f showcase/alphabet-service.yaml
	@$(K8S) apply -f showcase/alphabet-service-virtual-service.yaml
	@$(K8S) apply -f showcase/alphabet-service-destination.yaml

	@$(K8S) apply -f showcase/a-service.yaml
	@$(K8S) apply -f showcase/a-service-virtual-service.yaml
	@$(K8S) apply -f showcase/a-service-destination.yaml

	@$(K8S) apply -f showcase/b-service.yaml
	@$(K8S) apply -f showcase/b-service-virtual-service.yaml
	@$(K8S) apply -f showcase/b-service-destination.yaml

	@$(K8S) apply -f showcase/c-service.yaml
	@$(K8S) apply -f showcase/c-service-virtual-service.yaml
	@$(K8S) apply -f showcase/c-service-destination.yaml

clean:
	@$(K8S) delete --ignore-not-found=true -f istio-$(VERSION)/install/kubernetes/istio-demo.yaml
	@$(K8S) delete --ignore-not-found=true -f istio-$(VERSION)/install/kubernetes/helm/istio/templates/crds.yaml -n istio-system
	@rm -rf istio-$(VERSION)
	@$(GCP) container clusters delete $(NAME) --async --quiet
