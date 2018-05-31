package pit.webapi;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @RequestMapping("/user-details")
    protected ResponseEntity<UserDetails> getUserDetails(Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(user);
    }
}
