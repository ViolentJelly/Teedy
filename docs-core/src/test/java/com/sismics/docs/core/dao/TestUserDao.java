package com.sismics.docs.core.dao;

import com.sismics.docs.BaseTransactionalTest;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.util.context.ThreadLocalContext;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * Tests for UserDao authentication branches.
 */
public class TestUserDao extends BaseTransactionalTest {
    @Test
    // 验证正常用户名和密码可以通过认证。
    public void testAuthenticateSuccess() throws Exception {
        User createdUser = createUser("auth_success");
        ThreadLocalContext.get().getEntityManager().flush();

        User authenticated = new UserDao().authenticate("auth_success", "12345678");

        Assert.assertNotNull(authenticated);
        Assert.assertEquals(createdUser.getId(), authenticated.getId());
    }

    @Test
    // 验证密码错误时，认证会返回 null。
    public void testAuthenticateWrongPasswordReturnsNull() throws Exception {
        createUser("auth_wrong_password");
        ThreadLocalContext.get().getEntityManager().flush();

        User authenticated = new UserDao().authenticate("auth_wrong_password", "wrong_password");

        Assert.assertNull(authenticated);
    }

    @Test
    // 验证禁用用户和不存在的用户都会认证失败。
    public void testAuthenticateDisabledAndUnknownUserReturnNull() throws Exception {
        User disabledUser = createUser("auth_disabled");
        disabledUser.setDisableDate(new Date());
        new UserDao().update(disabledUser, disabledUser.getId());
        ThreadLocalContext.get().getEntityManager().flush();

        User disabledAuthenticated = new UserDao().authenticate("auth_disabled", "12345678");
        User unknownAuthenticated = new UserDao().authenticate("auth_unknown", "12345678");

        Assert.assertNull(disabledAuthenticated);
        Assert.assertNull(unknownAuthenticated);
    }
}
