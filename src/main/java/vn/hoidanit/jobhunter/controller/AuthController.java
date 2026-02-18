package vn.hoidanit.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.request.ReqLoginDTO;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResLoginDTO;
import vn.hoidanit.jobhunter.service.SecurityUtil;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.utils.anotations.ApiMessage;
import vn.hoidanit.jobhunter.utils.error.IdInvalidException;

import vn.hoidanit.jobhunter.utils.error.MissingRequestCookieException;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
                          UserService userService,PasswordEncoder passwordEncoder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder  = passwordEncoder;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        // Nap input gom username / password vao security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());
        // Xac thuc nguoi dung login dung chua => can viet ham loadUserByUserName
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Set thong tin nguoi dung vao security context

        SecurityContextHolder.getContext().setAuthentication(authentication);
        // create token

        ResLoginDTO res = new ResLoginDTO();
        User user = this.userService.handleGetUserByUsername(loginDTO.getUsername());
        if (user != null) {

            ResLoginDTO.UserLogin currentUser = new ResLoginDTO.UserLogin(user.getId(), user.getEmail(),
                    user.getName(), user.getRole());
            res.setUser(currentUser);
        }
        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res);
        res.setAccessToken(access_token);
        String refresh_Token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);
        // update refresh_token vao db
        this.userService.handleUpdateRefreshToken(refresh_Token, loginDTO.getUsername());
        // set cookies
        ResponseCookie resCookies = ResponseCookie.from("refresh_token", refresh_Token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);
    }

    @GetMapping("/auth/account")
    @ApiMessage("Fetch account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        User currentUser = this.userService.handleGetUserByUsername(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userAccount = new ResLoginDTO.UserGetAccount();
        if (currentUser != null) {

            userLogin.setId(currentUser.getId());
            userLogin.setEmail(currentUser.getEmail());
            userLogin.setName(currentUser.getName());
            userLogin.setRole(currentUser.getRole());

            userAccount.setUser(userLogin);
        }
        return ResponseEntity.ok().body(userAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get User by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "no_refresh_token") String refresh_token)
            throws IdInvalidException {
        if (refresh_token.equals("no_refresh_token")) {
            throw new IdInvalidException("Bạn không có refresh token ở cookies !");
        }
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodedToken.getSubject();
        // check user va token
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if (currentUser == null) {

            throw new IdInvalidException("Refresh token không hợp lệ !");
        }
        ResLoginDTO res = new ResLoginDTO();
        User user = this.userService.handleGetUserByUsername(email);
        if (user != null) {

            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(user.getId(), user.getEmail(),
                    user.getName(), user.getRole());
            res.setUser(userLogin);
        }
        String access_token = this.securityUtil.createAccessToken(email, res);

        res.setAccessToken(access_token);
        String new_refresh_Token = this.securityUtil.createRefreshToken(email, res);
        // update refresh_token vao db
        this.userService.handleUpdateRefreshToken(new_refresh_Token, email);
        // set cookies
        ResponseCookie resCookies = ResponseCookie.from("refresh_token", new_refresh_Token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);

    }
    @PostMapping("/auth/register")
    @ApiMessage("Register user !")
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User postManUser) throws IdInvalidException{
        boolean checkMail = this.userService.isEmailExist(postManUser.getEmail());
        if(checkMail) {
            throw new IdInvalidException("Email " + postManUser.getEmail() + " da ton tai , vui long nhap ten khac !");
        }
        String hashPassWord = this.passwordEncoder.encode(postManUser.getPassword());
        postManUser.setPassword(hashPassWord);
        User user = this.userService.handleSaveUser(postManUser);
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(this.userService.convertToResCreateUserDTO(user));
    }


    @PostMapping("/auth/logout")
    @ApiMessage("Logout user !")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        if (email.equals("")) {
            throw new IdInvalidException("Access token không hợp lệ !");
        }
        this.userService.handleUpdateRefreshToken(null, email);

        ResponseCookie deleteSpringCookie = ResponseCookie.from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0).build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString()).body(null);
    }

}
