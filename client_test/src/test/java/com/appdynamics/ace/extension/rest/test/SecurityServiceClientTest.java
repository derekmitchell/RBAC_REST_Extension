package com.appdynamics.ace.extension.rest.test;

import com.appdynamics.ace.extension.rest.client.AceRestClient;
import com.appdynamics.ace.extension.rest.client.RestClientException;
import com.appdynamics.ace.extension.rest.client.SecurityServiceClient;
import com.singularity.ee.controller.api.constants.EntityType;
import com.singularity.ee.controller.api.constants.PermissionAction;
import com.singularity.ee.controller.api.dto.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * Created by derek.mitchell on 8/12/16.
 */
public class SecurityServiceClientTest {

    private String CONTROLLER_URL = "http://192.168.56.20:8090/";
    private String CONTROLLER_USERNAME = "securitytest@customer1";
    private String CONTROLLER_PASSWORD = "sec!jga52ak$";

    private AceRestClient ace;
    private SecurityServiceClient ssc;

    @BeforeSuite
    public void setUp() throws Exception {
        ace = new AceRestClient(CONTROLLER_URL, CONTROLLER_USERNAME, CONTROLLER_PASSWORD);
        ssc = new SecurityServiceClient(ace);
        cleanup();
    }

    @AfterMethod
    public void tearDown() throws Exception {

    }

    @Test
    public void testCreateUser() throws Exception {
        User u1 = createNewUser("user1", "password1", SecurityProviderType.INTERNAL, "user1@test.com", new long[] {8});
        assertEquals(u1.getName(), "user1");
        assertEquals(u1.getSecurityProviderType(), SecurityProviderType.INTERNAL);
        assertEquals(u1.getEmail(), "user1@test.com");
        assertEquals(u1.getAccountRoleIds(), new long[] {8});
    }

    @Test(dependsOnMethods = { "testCreateUser" })
    public void testGetUser() throws Exception {
        User u1 = ssc.getUser("user1");
        assertEquals(u1.getName(), "user1");
        assertEquals(u1.getSecurityProviderType(), SecurityProviderType.INTERNAL);
        assertEquals(u1.getEmail(), "user1@test.com");
        assertEquals(u1.getAccountRoleIds(), new long[] {8});
    }

    @Test(dependsOnMethods = { "testGetUser" }, expectedExceptions = RestClientException.class)
    public void testGetUserNotFound() throws Exception {
        User u1 = ssc.getUser("dummyuser");
    }

    @Test(dependsOnMethods = { "testGetUserNotFound" })
    public void testGetAllUsers() throws Exception {
        List<User> users = ssc.getAllUsers();
        assertNotNull(users);
        for (User u : users) {
            if (u.getName().equals("user1")) {
                assertEquals(u.getEmail(), "user1@test.com");
            }
        }
    }

    @Test(dependsOnMethods = { "testGetAllUsers" })
    public void testUpdateUser() throws Exception {
        User olduser = ssc.getUser("user1");
        olduser.setAccountRoleIds(new long[] {8,9});
        ssc.updateUser(olduser);
        User newuser = ssc.getUser("user1");
        assertEquals(newuser.getAccountRoleIds(), new long[] {8,9});
    }

    @Test(dependsOnMethods = { "testUpdateUser" })
    public void testCreateGroup() throws Exception {
        long [] accountroles = new long [] {10, 11};
        Group g = createNewGroup("group1", SecurityProviderType.INTERNAL, "group for testing", accountroles);
        assertEquals(g.getName(), "group1");
        assertEquals(g.getSecurityProviderType(), SecurityProviderType.INTERNAL);
        assertEquals(g.getDescription(), "group for testing");
        assertEquals(g.getAccountRoleIds().length, 2);
    }

    @Test(dependsOnMethods = { "testCreateGroup" })
    public void testGetGroup() throws Exception {
        Group g = ssc.getGroup("group1");
        assertEquals(g.getName(), "group1");
        assertEquals(g.getSecurityProviderType(), SecurityProviderType.INTERNAL);
        assertEquals(g.getDescription(), "group for testing");
        assertEquals(g.getAccountRoleIds().length, 2);
    }

    @Test(dependsOnMethods = { "testGetGroup" }, expectedExceptions = RestClientException.class)
    public void testGetGroupNotFound() throws Exception {
        Group g = ssc.getGroup("dummygroup");
    }

    @Test(dependsOnMethods = { "testGetGroupNotFound" })
    public void testUpdateGroup() throws Exception {
        Group g1 = ssc.getGroup("group1");
        g1.setDescription("updated description");
        Group g2 = ssc.updateGroup(g1);
        assertEquals(g2.getDescription(), "updated description");
    }

    @Test(dependsOnMethods = { "testUpdateGroup" })
    public void testCreateAccountRole() throws Exception {
        Permission p1 = new Permission();
        p1.setAction(PermissionAction.CONFIG_DBMON);
        p1.setAllowed(true);
        EntityDefinition ed = new EntityDefinition();
        ed.setEntityType(EntityType.APPLICATION);
        ed.setEntityId(0);
        p1.setAffectedEntity(ed);

        AccountRole r = createNewAccountRole("role1", "role for testing", new Permission [] {p1});
        assertEquals(r.getName(), "role1");
        assertEquals(r.getDescription(), "role for testing");
        assertEquals(r.getPermissions().length, 1);
    }

    @Test(dependsOnMethods = { "testCreateAccountRole" })
    public void testGetAccountRole() throws Exception {
        AccountRole r = ssc.getAccountRole("role1");
        assertEquals(r.getName(), "role1");
        assertEquals(r.getDescription(), "role for testing");
        assertEquals(r.getPermissions().length, 1);
    }

    @Test(dependsOnMethods = { "testGetAccountRole" })
    public void testUpdateAccountRole() throws Exception {
        AccountRole r1 = ssc.getAccountRole("role1");
        r1.setDescription("updated role");
        AccountRole r2 = ssc.updateAccountRole(r1);
        assertEquals(r2.getDescription(), "updated role");
    }


    @Test(dependsOnMethods = { "testUpdateAccountRole" })
    public void testAssignUserToGroup() throws Exception {
         ssc.assignUserToGroup("user1", "group1");
    }

    @Test(dependsOnMethods = { "testAssignUserToGroup" })
    public void testGetGroupsForUser() throws Exception {
        List<Group> groups = ssc.getGroupsForUser("user1");
        assertEquals(groups.get(0).getName(), "group1");
    }


    @Test(dependsOnMethods = { "testGetGroupsForUser" })
    public void testRemoveUserFromGroup() throws Exception {
        ssc.removeUserFromGroup("user1", "group1");
        List<Group> groups = ssc.getGroupsForUser("user1");
        assertEquals(groups.size(), 0);
    }


    @Test(dependsOnMethods = { "testRemoveUserFromGroup" })
    public void testClearGroupsForUser() throws Exception {

        createNewGroup("group2",SecurityProviderType.INTERNAL, "2nd group for testing", new long [] {8});
        createNewGroup("group3",SecurityProviderType.INTERNAL, "3rd group for testing", new long [] {10});

        ssc.assignUserToGroup("user1", "group2");
        ssc.assignUserToGroup("user1", "group3");
        List<Group> groups = ssc.getGroupsForUser("user1");
        assertEquals(groups.size(), 2);

        ssc.clearGroupsForUser("user1");
        groups = ssc.getGroupsForUser("user1");
        assertEquals(groups.size(), 0);
    }

    @Test(dependsOnMethods = { "testClearGroupsForUser" })
    public void testGetAllGroups() throws Exception {
        int numgroups = ssc.getAllGroups().size();
        createNewGroup("group4",SecurityProviderType.INTERNAL, "4th group for testing", new long [] {10});
        List<Group> groups = ssc.getAllGroups();
        assertEquals(groups.size(), numgroups+1);
    }

    @Test(dependsOnMethods = { "testGetAllGroups" })
    public void testDeleteGroup() throws Exception {
        int numgroupsbefore = ssc.getAllGroups().size();
        ssc.deleteGroup("group4");
        int numgroupsafter = ssc.getAllGroups().size();
        assertEquals(numgroupsafter, numgroupsbefore-1);
    }


    @Test(dependsOnMethods = { "testDeleteGroup" })
    public void testGetAllAccountRoles() throws Exception {
        int numroles = ssc.getAllAccountRoles().size();
        createNewAccountRole("role2", "2nd role for testing", null);
        List<AccountRole> roles = ssc.getAllAccountRoles();
        assertEquals(roles.size(), numroles+1);
    }

    @Test(dependsOnMethods = { "testGetAllAccountRoles" })
    public void testAddAccountRoleToUser() throws Exception {
        User u = ssc.getUser("user1");
        int numrolesbefore = u.getAccountRoleIds().length;
        ssc.addAccountRoleToUser("user1", "role1");
        u = ssc.getUser("user1");
        int numrolesafter = u.getAccountRoleIds().length;
        assertEquals(numrolesafter, numrolesbefore+1);
    }

    @Test(dependsOnMethods = { "testAddAccountRoleToUser" })
    public void testAddAccountRoleToGroup() throws Exception {
        Group g = ssc.getGroup("group1");
        int numrolesbefore = g.getAccountRoleIds().length;
        ssc.addAccountRoleToGroup("group1", "role1");
        g = ssc.getGroup("group1");
        int numrolesafter = g.getAccountRoleIds().length;
        assertEquals(numrolesafter, numrolesbefore+1);
    }

    @Test(dependsOnMethods = { "testAddAccountRoleToGroup" })
    public void testRemoveAccountRoleFromUser() throws Exception {
        User u = ssc.getUser("user1");
        int numrolesbefore = u.getAccountRoleIds().length;
        ssc.removeAccountRoleFromUser("user1", "role1");
        u = ssc.getUser("user1");
        int numrolesafter = u.getAccountRoleIds().length;
        assertEquals(numrolesafter, numrolesbefore-1);
    }

    @Test(dependsOnMethods = { "testRemoveAccountRoleFromUser" })
    public void testRemoveAccountRoleFromGroup() throws Exception {
        Group g = ssc.getGroup("group1");
        int numrolesbefore = g.getAccountRoleIds().length;
        ssc.removeAccountRoleFromGroup("group1", "role1");
        g = ssc.getGroup("group1");
        int numrolesafter = g.getAccountRoleIds().length;
        assertEquals(numrolesafter, numrolesbefore-1);
    }


    @Test(dependsOnMethods = { "testRemoveAccountRoleFromGroup" })
    public void testDeleteAccountRole() throws Exception {
        int numrolesbefore = ssc.getAllAccountRoles().size();
        ssc.deleteAccountRole("role2");
        int numrolesafter = ssc.getAllAccountRoles().size();
        assertEquals(numrolesafter, numrolesbefore-1);
    }

    @Test(dependsOnMethods = { "testDeleteAccountRole" })
    public void testDeleteUser() throws Exception {
        int numusersbefore = ssc.getAllUsers().size();
        ssc.deleteUser("user1");
        int numusersafter = ssc.getAllUsers().size();
        assertEquals(numusersafter, numusersbefore-1);
    }

    private User createNewUser(String username, String password, SecurityProviderType securityProviderType, String email, long [] accountRoleIds)
            throws Exception
    {
        User u = new User();
        u.setName(username);
        u.setDisplayName(username);
        u.setPassword(password);
        u.setSecurityProviderType(securityProviderType);
        u.setEmail(email);
        u.setAccountRoleIds(accountRoleIds);

        return ssc.createUser(u);
    }

    private Group createNewGroup(String groupname, SecurityProviderType securityProviderType, String description, long [] accountRoleIds)
            throws Exception
    {
        Group g = new Group();
        g.setName(groupname);
        g.setSecurityProviderType(securityProviderType);
        g.setDescription(description);
        g.setAccountRoleIds(accountRoleIds);

        return ssc.createGroup(g);
    }

    private AccountRole createNewAccountRole(String rolename, String description, Permission[] p)
            throws Exception
    {
        AccountRole r = new AccountRole();
        r.setName(rolename);
        r.setDescription(description);
        r.setPermissions(p);

        return ssc.createAccountRole(r);
    }

    private void cleanup() {

        try {
            ssc.deleteGroup("group1");
        }
        catch (Exception e) {
            System.out.println("Error deleting group: " + e.getMessage());
        }

        try {
            ssc.deleteGroup("group2");
        }
        catch (Exception e) {
            System.out.println("Error deleting group: " + e.getMessage());
        }

        try {
            ssc.deleteGroup("group3");
        }
        catch (Exception e) {
            System.out.println("Error deleting group: " + e.getMessage());
        }

        try {
            ssc.deleteGroup("group4");
        }
        catch (Exception e) {
            System.out.println("Error deleting group: " + e.getMessage());
        }

        try {
            ssc.deleteUser("user1");
        }
        catch (Exception e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }

        try {
            ssc.deleteAccountRole("role1");
        }
        catch (Exception e) {
            System.out.println("Error deleting role: " + e.getMessage());
        }

        try {
            ssc.deleteAccountRole("role2");
        }
        catch (Exception e) {
            System.out.println("Error deleting role: " + e.getMessage());
        }
    }

}