package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.utils.anotations.ApiMessage;
import vn.hoidanit.jobhunter.utils.error.IdInvalidException;



@RestController
@Validated
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;
    public RoleController(RoleService roleService){
        this.roleService = roleService;
    }
    @PostMapping("/roles")
    @ApiMessage("Create a role !")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role ) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(this.roleService.handleCreateRole(role));
    }
    @PutMapping("/roles")
    @ApiMessage("Update a role !")
    public ResponseEntity<Role> UpdateRole(@Valid @RequestBody Role role ) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.OK.value()).body(this.roleService.handleUpdateRole(role));
    }
    @GetMapping("/roles")
    @ApiMessage("Get all role !")
    public ResponseEntity<ResultPaginationDTO> getAllRole(@Valid @Filter Specification<Role> spec, Pageable pageable) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.OK.value()).body(this.roleService.handleGetAllRole(spec,pageable));
    }
    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role !")
    public ResponseEntity<Void> deleteRole(@Valid @PathVariable("id") long id) throws IdInvalidException {
        this.roleService.deleteRole(id);
        return ResponseEntity.status(HttpStatus.OK.value()).body(null);
    }
    @GetMapping("/roles/{id}")
    @ApiMessage("Fetch role by id !")
    public ResponseEntity<Role> fetchRoleById(@PathVariable("id") long id ) throws IdInvalidException{
        Role role = this.roleService.fetchRoleById(id);
        if(role == null) {
            throw new IdInvalidException("Role voi id : " + id + " khong ton tai !");
        }
        return ResponseEntity.ok(role);

    }

}
