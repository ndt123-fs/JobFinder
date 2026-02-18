package vn.hoidanit.jobhunter.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.service.PermissionService;
import vn.hoidanit.jobhunter.utils.error.IdInvalidException;

import java.util.Optional;

@Service
public class PermissionServiceImpl implements PermissionService {
    private PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission permission) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(permission.getModule(), permission.getApiPath(), permission.getMethod());
    }

    public Permission findPermissionById(Long id) {
        Optional<Permission> opPermission = this.permissionRepository.findById(id);
        return opPermission.orElse(null);
    }

    @Override
    public Permission handleCreatePermission(Permission permission) throws IdInvalidException {
        // bien nguyen thuy khong duoc phep null
        boolean check = this.isPermissionExist(permission);
        if (check) {
            throw new IdInvalidException("Permission da ton tai !");
        }
        return this.permissionRepository.save(permission);
    }

    @Override
    public Permission handleUpdatePermission(Permission permission) throws IdInvalidException {
        //check_id
        Permission permissionDB = this.findPermissionById(permission.getId());
        if (permissionDB == null) {
            throw new IdInvalidException("Id: " + permission.getId() + " khong ton tai !");
        }
        //check_permission
        if (this.isPermissionExist(permission)) {
            if (this.isSameName(permission)) {
                throw new IdInvalidException("Permission da ton tai ");
            }

        }

        permissionDB.setName(permission.getName());
        permissionDB.setApiPath(permission.getApiPath());
        permissionDB.setMethod(permission.getMethod());
        permissionDB.setModule(permission.getModule());

        return this.permissionRepository.save(permissionDB);


    }

    @Override
    public ResultPaginationDTO handleGetListPermission(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pagePermission = this.permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        ResultPaginationDTO result = new ResultPaginationDTO();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pagePermission.getTotalPages());
        meta.setTotal(pagePermission.getTotalElements());

        result.setMeta(meta);
        result.setResult(pagePermission.getContent());
        return result;

    }

    @Override
    public void deletePermission(long id) throws IdInvalidException {
        Permission permission = this.findPermissionById(id);
        if (permission == null) {
            throw new IdInvalidException("Permission voi id : " + id + " khong ton tai !");
        } else {
            // trong table role_permission
            Optional<Permission> opPermission = this.permissionRepository.findById(id);
            Permission currentPermission = opPermission.get();
            currentPermission.getRoles().forEach(role -> role.getPermissions().remove(currentPermission));

            //delete
            this.permissionRepository.delete(currentPermission);
        }
    }

    @Override
    public Permission fetchPermissionById(Long id) {
        Optional<Permission> perOptional = this.permissionRepository.findById(id);
        return perOptional.orElse(null);
    }

    @Override
    public boolean isSameName(Permission permission) {
        Permission perDB = this.fetchPermissionById(permission.getId());
        if (perDB != null) {
            if (perDB.getName().equals(permission.getName())) return true;
        }
        return false;
    }

}
