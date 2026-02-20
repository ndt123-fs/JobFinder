package vn.hoidanit.jobhunter.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.utils.error.IdInvalidException;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Role handleCreateRole(Role role) throws IdInvalidException {
        //checkName
        boolean checkName = this.roleRepository.existsByName(role.getName());
        if (checkName) {
            throw new IdInvalidException(role.getName() + " da ton tai !");
        }
        // phải check != null nếu k check giả sử db chưa có thì stream() sẽ bi null
        if (role.getPermissions() != null) {
            List<Long> idList = role.getPermissions().stream().map(Permission::getId).toList();
            List<Permission> lstPermission = this.permissionRepository.findByIdIn(idList);
            role.setPermissions(lstPermission);

        }

        return this.roleRepository.save(role);
    }

    @Override
    public Role fetchRoleById(Long id) {
        Optional<Role> opRole = this.roleRepository.findById(id);
        return opRole.orElse(null);
    }

    @Override
    public Role handleUpdateRole(Role role) throws IdInvalidException {
        Role roleDB = this.fetchRoleById(role.getId());
        if (role.getPermissions() != null) {
            List<Long> listIdPer = role.getPermissions().stream().map(Permission::getId).toList();
            List<Permission> lstPermission = this.permissionRepository.findByIdIn(listIdPer);
            role.setPermissions(lstPermission);
        }
        // checkId

        if (roleDB == null) {
            throw new IdInvalidException("Role voi id : " + role.getId() + " khong ton tai !");
        }
        // checkName
//        boolean checkName = this.roleRepository.existsByName(role.getName());
//        if (checkName) {
//            throw new IdInvalidException("Role voi name: " +role.getName() +" da ton tai !");
//        }


        roleDB.setName(role.getName());
        roleDB.setDescription(role.getDescription());
        roleDB.setActive(role.isActive());
        roleDB.setPermissions(role.getPermissions());
        roleDB = this.roleRepository.save(roleDB);
        return roleDB;

    }

    @Override
    public ResultPaginationDTO handleGetAllRole(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        ResultPaginationDTO result = new ResultPaginationDTO();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageRole.getTotalPages());
        meta.setTotal(pageRole.getTotalPages());
        result.setMeta(meta);
        result.setResult(pageRole.getContent());

        return result;


    }

    @Override
    public void deleteRole(long id) {
        this.roleRepository.deleteById(id);
    }
}

