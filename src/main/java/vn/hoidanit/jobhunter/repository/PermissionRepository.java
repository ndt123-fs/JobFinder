package vn.hoidanit.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.utils.error.IdInvalidException;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {


    List<Permission> findByIdIn(List<Long> id);

    boolean existsByModuleAndApiPathAndMethod(String module, String apiPath, String method);



}
