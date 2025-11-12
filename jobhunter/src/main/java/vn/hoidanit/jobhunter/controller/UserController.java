package vn.hoidanit.jobhunter.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;

@RestController
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/user")
	public User createUser(@RequestBody User userPostman) {
		User user = this.userService.handleCreateUser(userPostman);
		return user;
	}

	@DeleteMapping("/user/{id}")
	public String createUser(@PathVariable("id") Long id) {
		this.userService.handleDeleteUser(id);
		return "Delete user";
	}
	
	@GetMapping("/user/{id}")
	public User getUserById(@PathVariable("id") long id) {
		return this.userService.fetchUserById(id);
	}
	

	@GetMapping("/user")
	public List<User> getUserById() {
		return this.userService.fetchAllUser();
	}
	
	@PutMapping("/user")
	public User updateUser(@RequestBody User user) {
		User ericUser = this.userService.handleUpdateUSer(user);
		return ericUser;
	}
}
