package com.example.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.ServiceUnavailableException;
import java.net.URI;
import java.util.Optional;

@RestController
@Slf4j
public class Service {
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/discoveryClient")
    public String discoveryPing() throws RestClientException,
            ServiceUnavailableException {
        /*URI service = serviceUrlLoadBalanced()
                .map(s -> s.resolve("/ping"))
                .orElseThrow(ServiceUnavailableException::new);
        log.info("URL={}", service);*/
        return restTemplate.getForEntity("http://Service1/ping", String.class)
                .getBody();
    }

    @GetMapping("/ping")
    public String ping() {
        log.info("pong");
        return "pong";
    }


    public Optional<URI> serviceUrl() {
        discoveryClient.getInstances("Service1").stream().map(Object::toString).forEach(log::info);
        return discoveryClient.getInstances("Service1")
                .stream()
                .map(si -> si.getUri())
                .findFirst();
    }

    public Optional<URI> serviceUrlLoadBalanced() {
        return Optional.ofNullable(loadBalancerClient.choose("Service1")).map(ServiceInstance::getUri);
    }
}
