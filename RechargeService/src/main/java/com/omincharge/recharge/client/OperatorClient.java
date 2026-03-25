package com.omincharge.recharge.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@FeignClient(name = "operator-service")
public interface OperatorClient {

    @GetMapping("/api/v1/operators/{id}")
    Map<String, Object> getOperatorById(@PathVariable Long id);

    @GetMapping("/api/v1/operators/plans/{planId}")
    Map<String, Object> getPlanById(@PathVariable Long planId);
}