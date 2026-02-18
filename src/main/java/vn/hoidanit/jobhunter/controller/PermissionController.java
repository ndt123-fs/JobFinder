package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.PermissionService;
import vn.hoidanit.jobhunter.utils.anotations.ApiMessage;
import vn.hoidanit.jobhunter.utils.error.IdInvalidException;

@RestController
@Validated
@RequestMapping("/api/v1")
public class PermissionController {
    private final  PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permissions !")

    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission) throws IdInvalidException {

        return ResponseEntity.status(HttpStatus.CREATED.value()).body(this.permissionService.handleCreatePermission(permission));
    }
    @PutMapping("/permissions")
    @ApiMessage("Update a permission !")
    public ResponseEntity<Permission>  updatePermission(@Valid @RequestBody Permission permission) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.ACCEPTED.value()).body(this.permissionService.handleUpdatePermission(permission));

    }
    @GetMapping("/permissions")
    @ApiMessage("Get all permission !")
    public ResponseEntity<ResultPaginationDTO> getAllPermission(@Filter Specification<Permission> spec, Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK.value()).body(this.permissionService.handleGetListPermission(spec, pageable));

    }
    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete permission !")
    public ResponseEntity<Void> deletePermission(@PathVariable("id")  Long id) throws IdInvalidException {
        this.permissionService.deletePermission(id);
        return ResponseEntity.status(HttpStatus.OK.value()).body(null);
    }


}
