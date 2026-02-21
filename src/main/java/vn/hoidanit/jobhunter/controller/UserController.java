package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.user.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.user.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.user.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.EmailService;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.utils.anotations.ApiMessage;
import vn.hoidanit.jobhunter.utils.error.EmailInvalidException;
import vn.hoidanit.jobhunter.utils.error.IdInvalidException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder,EmailService emailService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // POST
    @PostMapping("/users")
    @ApiMessage("Create a user !")

    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User userCreate)
            throws EmailInvalidException {

        boolean isEmailExist = this.userService.isEmailExist(userCreate.getEmail());
        if (isEmailExist) {
            throw new EmailInvalidException(
                    "Email " + userCreate.getEmail() + " đã tồn tại, vui lòng dùng email khác !");
        }

        String hashPassword = this.passwordEncoder.encode(userCreate.getPassword());
        userCreate.setPassword(hashPassword);
        User newUser = this.userService.handleSaveUser(userCreate);
        ResCreateUserDTO resUser = this.userService.convertToResCreateUserDTO(newUser);
        // Gui gmail
        this.emailService.sendMailFromTemplateSync(userCreate.getEmail(),"Register User at Job Hunter",
                "register", userCreate.getName(), userCreate.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(resUser);
    }

    // GET
    @GetMapping("/users/{id}")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") Long id) throws Exception {
        User user = this.userService.handleGetUserById(id);
        if (user == null)
            throw new IdInvalidException("Id : " + id + " not exist in database !");
        ResUserDTO resUser = this.userService.convertToResUserDTO(user);
        return ResponseEntity.status(HttpStatus.OK).body(resUser);

    }

    @GetMapping("/users")
    @ApiMessage("fetch all user")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<User> spec, Pageable pageable) {
        // @RequestParam("current") Optional<String> currentOptional,
        // @RequestParam("pageSize") Optional<String> pageSizeOptional) {
        // String sCurrent = currentOptional.isPresent() ? currentOptional.get() : "";
        // String sPageSize = pageSizeOptional.isPresent() ? pageSizeOptional.get() :
        // "";

        // int current = Integer.parseInt(sCurrent);
        // int pageSize = Integer.parseInt(sPageSize);

        // Pageable pageable = PageRequest.of(current - 1, pageSize);

        ResultPaginationDTO userList = this.userService.handleGetAllUser(spec,
                pageable);
        return ResponseEntity.status(HttpStatus.OK).body(userList);

    }

    // PUT
    @PutMapping("/users")
    @ApiMessage("Update user successfully !")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User reqUser) throws IdInvalidException {
        User user = this.userService.handleUpdateUser(reqUser);
        if (user == null) {
            throw new IdInvalidException("Id with " + reqUser.getId() + " not exist in database !");
        }

        ResUpdateUserDTO resUpdate = this.userService.convertToResUpdateUserDTO(user);
        return ResponseEntity.status(HttpStatus.OK).body(resUpdate);

    }

    // DELETE
    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user successfully !")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") Long id) throws Exception {
        User user = this.userService.handleGetUserById(id);

        if (user == null) {
            throw new IdInvalidException("Id with " + id + " not exist in database !");

        }
        this.userService.handleDeleteUser(id);
        // return ResponseEntity.status(HttpStatus.OK).body("delete user successfully");
        return ResponseEntity.ok(null);
    }


}
