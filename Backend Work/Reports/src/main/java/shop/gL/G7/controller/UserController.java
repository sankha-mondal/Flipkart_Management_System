package shop.gL.G7.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import shop.gL.G7.entity.User;
import shop.gL.G7.security.JWT.JwtProvider;
import shop.gL.G7.service.UserService;
import shop.gL.G7.wb.request.LoginForm;
import shop.gL.G7.wb.response.JwtResponse;
import shop.gL.G7.wb.response.UserListResponse;


@CrossOrigin
@RequestMapping
@RestController
public class UserController {

    @Autowired
    UserService userService;


    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginForm loginForm) {
        // throws Exception if authentication failed

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtProvider.generate(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findOne(userDetails.getUsername());
            return ResponseEntity.ok(new JwtResponse(jwt, user.getEmail(), user.getName(), user.getRole()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @PostMapping("/register")
    public ResponseEntity<User> save(@RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.save(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<User> update(@RequestBody User user, Principal principal) {

        try {
            if (!principal.getName().equals(user.getEmail())) throw new IllegalArgumentException();
            return ResponseEntity.ok(userService.update(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PatchMapping("/add/admin/{email}")
    public ResponseEntity<User> updateAdmin(@PathVariable("email") String email) {

        try {      
            return ResponseEntity.ok(userService.update(email));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PatchMapping("/remove/admin/{email}")
    public ResponseEntity<User> removeAdmin(@PathVariable("email") String email) {

        try {  
            return ResponseEntity.ok(userService.removeAdmin(email));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
//-------------------------------------------------------------------------------------------------
    
    
    
    
    //  User defined | Query: Record deleted by Email:-
	
	//  http://localhost:8080/api/deleteUserByEmail/{email}
//    @CrossOrigin(origins = "http://localhost:4200")
	@DeleteMapping(value = "/delete/customer/{email}")
	public String deleteUserByEmail(@PathVariable("email") String email) {
    	System.out.println("inside the delete controller");
		return userService.deleteUserByEmail(email);
//    	return "hi";
	}
    
    
    
    
    
    
    
//    @GetMapping(value="hello")
//    public String hello() {
//    	return "Hello";
//    }
    


    @GetMapping("/profile/{email}")
    public ResponseEntity<User> getProfile(@PathVariable("email") String email, Principal principal) {
        if (principal.getName().equals(email)) {
            return ResponseEntity.ok(userService.findOne(email));
        } else {
            return ResponseEntity.badRequest().build();
        }

    }
    
    @GetMapping("/getusers")
    public Page<User> getAllUsers(@RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
    	
    	PageRequest request = PageRequest.of(page - 1, size);
        Page<User> userPage;
    	
    	userPage = userService.findAll(request);
    	return userPage;
    	
    }
    
    
    
    
    
    
    
    
    
    
}
