package org.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "session-service", url = "${session.service.url:http://session-service:8081}")
public interface SessionServiceClient {

    @GetMapping("/api/sessions/active/{courierId}")
    boolean isActive(@PathVariable("courierId") String courierId);
}