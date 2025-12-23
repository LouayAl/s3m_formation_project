package com.s3m.formation.security.jwt;

import com.s3m.formation.security.util.SecurityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class JwtTestController {

    @GetMapping("/whoami")
    public Map<String, Object> whoAmI() {
        return Map.of(
                "entrepriseId", SecurityUtils.getEntrepriseId()
        );
    }
}
