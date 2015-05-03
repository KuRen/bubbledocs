package pt.tecnico.bubbledocs.service.remote;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import pt.tecnico.bubbledocs.exception.DuplicateEmailException;
import pt.tecnico.bubbledocs.exception.DuplicateUsernameException;
import pt.tecnico.bubbledocs.exception.InvalidEmailException;
import pt.tecnico.bubbledocs.exception.InvalidUsernameException;
import pt.tecnico.bubbledocs.exception.LoginBubbleDocsException;

@Ignore("Takes too much time to test always and it's tested on system testing")
public class IdRemoteServicesTest extends SdRemoteServicesTest {

    private IDRemoteServices service = null;

    @Override
    @Before
    public void setUp() {
        service = new IDRemoteServices();
    }

    @Override
    @After
    public void tearDown() {
        try {
            service.removeUser("user");
        } catch (LoginBubbleDocsException e) {
        }

        service = null;
    }

    @Test
    public void successCreateUser() {

        service.createUser("user", "user@example.com");
    }

    @Test
    public void createMultipleUsers() {
        try {
            service.createUser("user", "user@example.com");
            service.createUser("user2", "user2@example.com");
            service.createUser("user3", "user3@example.com");
        } finally {
            service.removeUser("user2");
            service.removeUser("user3");
        }

    }

    @Test(expected = DuplicateEmailException.class)
    public void duplicateEmail() {
        service.createUser("user", "user@example.com");
        service.createUser("user2", "user@example.com");
    }

    @Test(expected = InvalidEmailException.class)
    public void invalidEmail_0() {
        service.createUser("user", null);
    }

    @Test(expected = InvalidEmailException.class)
    public void invalidEmail_1() {
        service.createUser("user", "use$r1@example.com");
    }

    @Test(expected = InvalidEmailException.class)
    public void invalidEmail_2() throws InvalidEmailException {
        service.createUser("user", "user@exa$mple.com");
    }

    @Test(expected = InvalidEmailException.class)
    public void invalidEmail_3() throws InvalidEmailException {
        service.createUser("user", "user@example.co$m");
    }

    @Test(expected = InvalidEmailException.class)
    public void invalidEmail_4() throws InvalidEmailException {
        service.createUser("user", "userexample.com");
    }

    @Test(expected = InvalidEmailException.class)
    public void invalidEmail_5() throws InvalidEmailException {
        service.createUser("user", "@example.com");
    }

    @Test(expected = InvalidEmailException.class)
    public void invalidEmail_6() throws InvalidEmailException {
        service.createUser("user", "user@.com");
    }

    @Test(expected = InvalidEmailException.class)
    public void invalidEmail_7() throws InvalidEmailException {
        service.createUser("user", "user@com");
    }

    @Test(expected = InvalidEmailException.class)
    public void invalidEmail_8() throws InvalidEmailException {
        service.createUser("user", "user@@example.com");
    }

    @Test(expected = InvalidEmailException.class)
    public void invalidEmail_9() throws InvalidEmailException {
        service.createUser("user", "sexy_bóy_69@users.brazzers.com");
    }

    @Test(expected = InvalidEmailException.class)
    public void invalidEmail_10() throws InvalidEmailException {
        service.createUser("user", "");
    }

    @Test
    public void validEmails() throws InvalidEmailException {
        try {
            service.createUser("user", "a@b.c");
            service.createUser("user2", "a_b@b.c");
            service.createUser("user3", "a_b.c@b.c");
            service.createUser("user4", "a@b.c.d");
            service.createUser("user5", "a_b.c@b.c.d.e");
            service.createUser("user6", "aaa_bbb.ccc@aaa.bbb.ccc.ddd.eee");
        } finally {
            service.removeUser("user2");
            service.removeUser("user3");
            service.removeUser("user4");
            service.removeUser("user5");
            service.removeUser("user6");
        }

    }

    @Test(expected = DuplicateUsernameException.class)
    public void duplicateUser() {
        service.createUser("user", "user@example.com");
        service.createUser("user", "user2@example.com");
    }

    @Test(expected = InvalidUsernameException.class)
    public void invalidUser_0() {
        service.createUser("", "user@example.com");
    }

    @Test(expected = InvalidUsernameException.class)
    public void invalidUser_1() {
        service.createUser(null, "user@example.com");
    }

    @Test
    public void renewToRandomPassword() {
        service.createUser("user", "email@example.com");
        service.renewPassword("user");
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void renewNullUserPassword() {
        service.renewPassword(null);
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void renewEmtpyUserPassword() {
        service.renewPassword("");
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void renewNonExistingUserPassword() {
        service.renewPassword("user99");
    }

    @Test
    public void removeUser() {
        service.createUser("user", "email@example.com");
        service.removeUser("user");
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void removeNullUser() {
        service.removeUser(null);
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void removeEmptyUser() {
        service.removeUser("");
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void removeNonExistingUser() {
        service.removeUser("user99");
    }

    //Assuming pre loaded data on server
    @Test
    public void authenticate() {
        service.loginUser("alice", "Aaa1");
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void authenticateNullUser() {
        service.createUser("user", "email@example.com");
        service.loginUser(null, "pw");
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void authenticateEmptyUser() {
        service.createUser("user", "email@example.com");
        service.loginUser("", "pw");
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void authenticateNonExistingUser() {
        service.createUser("user", "email@example.com");
        service.loginUser("user99", "pw");
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void authenticateNullPassword() {
        service.createUser("user", "email@example.com");
        service.loginUser("user", null);
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void authenticateEmptyPassword() {
        service.createUser("user", "email@example.com");
        service.loginUser("user", "");
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void authenticateWrongPassword() {
        service.createUser("user", "email@example.com");
        service.loginUser("user", "notpw");
    }

}
