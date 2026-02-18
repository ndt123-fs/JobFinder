package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.utils.error.IdInvalidException;

public interface PermissionService {
    Permission handleCreatePermission(Permission permission) throws IdInvalidException;

    Permission handleUpdatePermission(Permission permission) throws IdInvalidException;

    ResultPaginationDTO handleGetListPermission(Specification<Permission> spec, Pageable pageable);

    void deletePermission(long id) throws IdInvalidException;

    boolean isSameName(Permission permission);

    Permission fetchPermissionById(Long id);
}
