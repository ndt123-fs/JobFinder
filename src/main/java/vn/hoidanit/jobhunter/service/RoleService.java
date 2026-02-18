package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.utils.error.IdInvalidException;

public interface RoleService {
    Role handleCreateRole(Role role) throws IdInvalidException;

    Role handleUpdateRole(Role role) throws IdInvalidException;

    Role fetchRoleById(Long id);

    ResultPaginationDTO handleGetAllRole(Specification<Role> spec, Pageable pageable);

    void deleteRole(long id);
}
