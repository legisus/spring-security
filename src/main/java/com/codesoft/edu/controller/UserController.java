package com.codesoft.edu.controller;

import com.codesoft.edu.model.User;
import com.codesoft.edu.service.RoleService;
import com.codesoft.edu.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_USER')")
    public String create(Model model) {
        model.addAttribute("user", new User());
        return "create-user";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_USER')")
    public String create(@Validated @ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            return "create-user";
        }
        user.setPassword(user.getPassword());
        user.setRole(roleService.readById(2));
        User newUser = userService.create(user);
        return "redirect:/todos/all/users/" + newUser.getId();
    }

    @GetMapping("/{id}/read")
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_USER')")
    public String read(@PathVariable long id, Model model) {
        User user = userService.readById(id);
        model.addAttribute("user", user);
        return "user-info";
    }

    @GetMapping("/{id}/update")
    @PreAuthorize("hasRole('ROLE_ADMIN') || @authorizationComponent.mayDeleteUpdateUser(#id)")
    public String update(@PathVariable long id, Model model) {
        User user = userService.readById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.getAll());
        return "update-user";
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("hasRole('ADMIN') || @authorizationComponent.mayDeleteUpdateUser(#id)")
    public String update(@PathVariable long id, Model model, @Validated @ModelAttribute("user") User user, @RequestParam("roleId") long roleId, BindingResult result) {
        User oldUser = userService.readById(id);
        if (result.hasErrors()) {
            user.setRole(oldUser.getRole());
            model.addAttribute("roles", roleService.getAll());
            return "update-user";
        }
        if (oldUser.getRole().getName().equals("USER")) {
            user.setRole(oldUser.getRole());
        } else {
            user.setRole(roleService.readById(roleId));
        }
        userService.update(user);
        return "redirect:/users/" + id + "/read";
    }



    @GetMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN') || @authorizationComponent.mayDeleteUpdateUser(#id)")
//    public String delete(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails) { //another way
    public String delete(@PathVariable("id") Long id) {

//One more different way to get current user name
//        String userNameForDeletion = userService.readById(id).getEmail();
//        //String currentUserName = SecurityConfig.getCurrentUserName(); //another way
//        String currentUserName = userDetails.getUsername();

//        if (!userNameForDeletion.equals(currentUserName)) {
//            log.error("User " + currentUserName + " tried to delete user " + userNameForDeletion);
//            return "redirect:/users/all";
//        }
//
//        log.info("currentUserName : " + currentUserName);
//        log.info("userNameForDeletion : " + userNameForDeletion);

        userService.delete(id);
        return "redirect:/users/all";
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_USER')")
    public String getAll(Model model) {
        model.addAttribute("users", userService.getAll());
        return "users-list";
    }
}
