package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.user.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.user.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.user.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;

public interface UserService {

    User handleSaveUser(User user);

    User handleGetUserById(Long id);

    ResultPaginationDTO handleGetAllUser(Specification<User> spec, Pageable pageable);

    User handleUpdateUser(User reqUser);

    void handleDeleteUser(Long id);

    User handleGetUserByUsername(String email);

    boolean isEmailExist(String email);

    ResCreateUserDTO convertToResCreateUserDTO(User user);

    ResUpdateUserDTO convertToResUpdateUserDTO(User user);

    ResUserDTO convertToResUserDTO(User user);

    void handleUpdateRefreshToken(String token, String email);

    User getUserByRefreshTokenAndEmail(String token, String email);

}
