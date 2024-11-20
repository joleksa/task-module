package com.example.task;

import com.example.dto.AppUserDto;
import com.example.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", url = "http://localhost:8080/api/users", configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("user")
    public ResponseEntity<AppUserDto> findUserById(@RequestParam Long id);

    @GetMapping("user")
    public ResponseEntity<AppUserDto> findUserByLogin(@RequestParam String login);

    @GetMapping("/all")
    public ResponseEntity<Page<AppUserDto>> getAllUsersSortedAndPaginated
            (@RequestParam int pageNo,
             @RequestParam int pageSize,
             @RequestParam String field,
             @RequestParam String direction);
}
