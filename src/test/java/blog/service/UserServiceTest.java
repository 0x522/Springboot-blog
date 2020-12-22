package blog.service;


import blog.entity.User;
import blog.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserMapper mockMapper;
    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @InjectMocks
    UserService userService;

    @Test
    void testSave() {
        //调用userService
        //验证userService将请求转发给了userMapper
        //given:
        when(bCryptPasswordEncoder.encode("myPassword")).thenReturn("myEncodedPassword");
        //when:
        userService.save("myUser", "myPassword");
        //verify:
        verify(mockMapper).save("myUser", "myEncodedPassword");
    }

    @Test
    void testGetUserByUsername() {
        userService.getUserByUsername("myUser");

        verify(mockMapper).findUserByUsername("myUser");
    }

    @Test
    void throwExceptionWhenUserNotFound() {
        //设置断言,当某个行为执行时一定会抛出异常,不然就会测试失败
        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("myUser"));
    }

    @Test
    void returnUserDetailsWhenUserFound() {
        //写入Mock的User数据
        when(mockMapper.findUserByUsername("myUser"))
                .thenReturn(
                        new User(123, "myUser", "myEncodedPassword"));
        UserDetails userDetails = userService.loadUserByUsername("myUser");
        Assertions.assertEquals("myUser", userDetails.getUsername());
        Assertions.assertEquals("myEncodedPassword", userDetails.getPassword());
    }

}