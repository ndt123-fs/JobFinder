package vn.hoidanit.jobhunter.service.impl;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.user.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.user.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.user.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;

import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserServiceImpl(UserRepository userRepository, CompanyService companyService,RoleService roleService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    @Override
    public User handleSaveUser(User user) {
        //check company
        if (user.getCompany() != null) {
            Optional<Company> com = this.companyService.findById(user.getCompany().getId());
            user.setCompany(com.orElse(null));
        }
        //check role
        if(user.getRole() != null){
            Role role = this.roleService.fetchRoleById(user.getRole().getId());
            user.setRole(role);
        }
        return this.userRepository.save(user);

    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    @Override
    public void handleDeleteUser(Long id) {
        this.userRepository.deleteById(id);
    }

    @Override
    public User handleGetUserById(Long id) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    @Override
    public ResultPaginationDTO handleGetAllUser(Specification<User> spec,
            Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        ResultPaginationDTO result = new ResultPaginationDTO();
        // Trang nao:FE
        meta.setPage(pageable.getPageNumber() + 1);
        // So record lay theo yeu cau:FE
        meta.setPageSize(pageable.getPageSize());
        // Tong so trang hien co , chia theo pagesize cua FE
        meta.setPages(pageUser.getTotalPages());
        // Tong record hien trong trang do
        meta.setTotal(pageUser.getTotalElements());
        result.setMeta(meta);

        result.setResult(pageUser.getContent()
                .stream().map(item -> this.convertToResUserDTO(item)).collect(Collectors.toList()));

        return result;

    }

    @Override
    public User handleUpdateUser(User reqUser) {
        User currentUser = this.handleGetUserById(reqUser.getId());
        if (currentUser != null) {
            currentUser.setName(reqUser.getName());
            currentUser.setAge(reqUser.getAge());
            currentUser.setGender(reqUser.getGender());
            currentUser.setAddress(reqUser.getAddress());
            //check company
            if (reqUser.getCompany() != null) {
                Optional<Company> company = this.companyService.findById(reqUser.getCompany().getId());
                currentUser.setCompany(company.isPresent() ? company.get() : null);
            }
            //check role
            if(reqUser.getRole() != null){
                Role role = this.roleService.fetchRoleById(reqUser.getRole().getId());
                currentUser.setRole(role);
            }
            // update
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    @Override
    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    @Override
    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO resUser = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser com = new ResCreateUserDTO.CompanyUser();
        resUser.setId(user.getId());
        resUser.setEmail(user.getEmail());
        resUser.setName(user.getName());
        resUser.setAge(user.getAge());
        resUser.setAddress(user.getAddress());
        resUser.setGender(user.getGender());
        resUser.setCreatedAt(user.getCreatedAt());

        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            resUser.setCompany(com);
        }

        return resUser;
    }

    @Override
    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO resUser = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser com = new ResUpdateUserDTO.CompanyUser();
        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            resUser.setCompany(com);
        }
        resUser.setId(user.getId());
        resUser.setName(user.getName());
        resUser.setAge(user.getAge());
        resUser.setAddress(user.getAddress());
        resUser.setGender(user.getGender());
        resUser.setUpdatedAt(user.getUpdatedAt());

        return resUser;
    }

    @Override
    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO resUser = new ResUserDTO();
        ResUserDTO.UserCompany com = new ResUserDTO.UserCompany();
        ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();
         if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            resUser.setCompany(com);

        }
         if(user.getRole() != null){
             roleUser.setId(user.getRole().getId());
             roleUser.setName(user.getRole().getName());
             resUser.setRole(roleUser);
         }

        resUser.setId(user.getId());
        resUser.setName(user.getName());
        resUser.setEmail(user.getEmail());
        resUser.setAge(user.getAge());
        resUser.setAddress(user.getAddress());
        resUser.setGender(user.getGender());
        resUser.setUpdatedAt(user.getUpdatedAt());
        resUser.setCreatedAt(user.getCreatedAt());



        return resUser;
    }

    @Override
    public void handleUpdateRefreshToken(String token, String email) {
        User user = this.handleGetUserByUsername(email);
        if (user != null) {

            user.setRefreshToken(token);
            this.userRepository.save(user);
        }
    }

    @Override
    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

}
