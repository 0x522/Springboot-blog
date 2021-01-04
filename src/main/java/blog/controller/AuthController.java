package blog.controller;

import blog.entity.LoginResult;
import blog.entity.Result;
import blog.entity.User;
import blog.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

@RestController
public class AuthController {
    private AuthenticationManager authenticationManager;
    private UserService userService;

    @Inject
    public AuthController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @GetMapping("/auth")//判断用户的登录状态
    @ResponseBody
    public Result auth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser =
                userService.getUserByUsername(authentication != null ? authentication.getName() : null);
        if (loggedInUser == null) {
            return LoginResult.success("用户没有登录", false, null);
        } else {
            return LoginResult.success("用户已经登录", true, loggedInUser);
        }
    }

    @GetMapping("/auth/logout")
    @ResponseBody
    public Result logout() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedInUser = userService.getUserByUsername(username);
        if (loggedInUser == null) {
            return LoginResult.failure("用户尚未登录", false);
        } else {
            SecurityContextHolder.clearContext();
            return LoginResult.success("注销成功", false);
        }
    }

    @PostMapping("/auth/register")
    @ResponseBody
    public Result register(@RequestBody Map<String, String> usernameAndPassword) {
        String username = usernameAndPassword.get("username");
        String password = usernameAndPassword.get("password");
        if (username == null || password == null) {
            return LoginResult.failure("用户名或者密码是空");
        }
        if (!username.matches("^\\w{1,15}$")) {
            return LoginResult.failure("用户名不符合规则，请输入1-15位字符数字下划线汉字");
        }
        if (!password.matches("^.{6,16}$")) {
            return LoginResult.failure("密码不符合规则，请输入6-16位任意字符");
        }
        try {
            userService.save(username, password);
            return LoginResult.success("注册成功", true);
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            return LoginResult.failure("用户已经存在");
        }

    }


    @PostMapping("/auth/login")
    @ResponseBody
    public Result login(@RequestBody Map<String, Object> usernameAndPasswordJson) {
        String username = usernameAndPasswordJson.get("username").toString();
        String password = usernameAndPasswordJson.get("password").toString();
        UserDetails userDetails;
        try {
            userDetails = userService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            return LoginResult.failure("用户不存在");
        }
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        try {
            authenticationManager.authenticate(token);
            //把用户信息保存在内存的某个地方
            //cookie
            SecurityContextHolder.getContext().setAuthentication(token);
            return LoginResult.success("登录成功", true, userService.getUserByUsername(username));
        } catch (BadCredentialsException e) {
            return LoginResult.failure("密码不正确");
        }
    }


}
