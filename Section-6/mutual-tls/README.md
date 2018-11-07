# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 6.3: Mutual TLS between Java EE microservices with Istio

### Step 1: Create Kubernetes cluster

In this first step we are going to create a Kubernetes cluster on GCP. Issue the
following command fire up the infrastructure:
```
$ make prepare cluster
```

### Step 2: Install Istio

In this step we are going to install the latest (1.0.3) version of Istio. We are
 going to install the mutual TLS version here. Also, we are labeling the `default`
namespace to perform the Istio sidecar injection automatically.

```
$ make get-istio
$ make istio-install
```

### Step 3: Deploy Alphabet Showcase

This showcase demonstrates more advances features like introducing delays,
failure and circuit breakers.

```
$ export INGRESS_HOST=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.status.loadBalancer.ingress[0].ip}')

$ echo $INGRESS_HOST spelling.cloud >> /etc/hosts

$ make alphabet-demo

$ http get spelling.cloud/api/spelling\?word=abc
$ http get spelling.cloud/api/spelling\?word=hello

$ http get spelling.cloud/api/spelling\?word=abc Accept-Language:de
$ http get spelling.cloud/api/spelling\?word=hello Accept-Language:de
```

### Step X: Delete Kubernetes cluster

Do not forget to shutdown everything, otherwise you will have a bad surprise on
your credit card bill at the end of the month!

```
$ make clean
```
