package vn.hoidanit.jobhunter.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.SecurityUtil;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.service.impl.UserServiceImpl;
import vn.hoidanit.jobhunter.utils.error.IdInvalidException;
import vn.hoidanit.jobhunter.utils.error.PermissionException;

import java.util.List;

public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

    @Override
    @Transactional // de thuc hien xong hay commit
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws PermissionException {
        // duong link trong controller
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        // duong link trong url gui
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">> RUN pre handler !");
        System.out.println(">>>>:PATH" + path);
        System.out.println(">>>>httpMethod :" + httpMethod);
        System.out.println(">>>>httpURI :" + requestURI);
        //check permission
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        // th email : null va user chua login
        if (!email.isEmpty()) {
            User user = this.userService.handleGetUserByUsername(email);
            if (user != null) {
                Role role = user.getRole();
                if (role != null) {
                    List<Permission> permissions = role.getPermissions(); // TRANSACTIONAL FIX O DAY ,permission su dung kieu lazy,
                    // nghia la khi lay duoc doi tuong role roi , mac dinh la k lay permission
                    // , nen buoc chung ta phai lay them permission nen goi xuong database 1 lan nua do vay ,
                    // dung transactianal giu phien dang nhap do va query them 1 lan nua xuong database
                    boolean isAllow = permissions.stream().anyMatch(item ->
                            item.getApiPath().equals(path) && item.getMethod().equals(httpMethod)
                    );
                    if (!isAllow) {
                        throw new PermissionException("Ban khong co quyen han !");
                    }
                } else {
                    throw new PermissionException("Ban khong co quyen han !");
                }
            }
        }
        return true;
    }

}
