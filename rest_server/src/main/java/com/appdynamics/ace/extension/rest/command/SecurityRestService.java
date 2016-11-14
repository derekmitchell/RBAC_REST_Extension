package com.appdynamics.ace.extension.rest.command;

import com.appdynamics.ace.extension.rest.command.api.DataNotFoundException;
import com.appdynamics.ace.extension.rest.command.api.RestException;
import com.appdynamics.ace.extension.rest.util.BeanLocator;
import com.singularity.ee.controller.api.dto.Group;
import com.singularity.ee.controller.api.dto.User;
import com.singularity.ee.controller.api.dto.AccountRole;
import com.singularity.ee.controller.api.dto.LDAPGroup;
import com.singularity.ee.controller.api.exceptions.ServerException;
import com.singularity.ee.controller.api.services.model.IAccountManager;
import com.singularity.ee.controller.api.services.model.IGroupManager;
import com.singularity.ee.controller.api.services.model.IUserManager;
import com.singularity.ee.controller.api.services.model.IAccountRoleManager;
import com.singularity.ee.controller.api.services.model.ILDAPConnector;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: stefan.marx
 * Date: 22.10.13
 * Time: 02:07
 * To change this template use File | Settings | File Templates.
 */

@Path("aceSecurityService")
public class SecurityRestService {


    private final IUserManager _userManager;
    private final IAccountManager _accountManager;
    private final IGroupManager _groupManager;
    private final IAccountRoleManager _accountRoleManager;
    private final ILDAPConnector _ldapConnector;


    public SecurityRestService() {
        _userManager = BeanLocator.getInstance().getGlobalBeanInstance(IUserManager.class);
        _accountManager = BeanLocator.getInstance().getGlobalBeanInstance(IAccountManager.class);
        _groupManager = BeanLocator.getInstance().getGlobalBeanInstance(IGroupManager.class);
        _accountRoleManager = BeanLocator.getInstance().getGlobalBeanInstance(IAccountRoleManager.class);
        _ldapConnector = BeanLocator.getInstance().getGlobalBeanInstance(ILDAPConnector.class);
    }

    @GET
    @Path("user/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@PathParam("username") String userName) throws DataNotFoundException {

        try {
            User[] users = _userManager.getAllUsers();

            for (User user : users) {
                if (user.getProviderUniqueName().equals(userName)) return user;

            }

            throw new DataNotFoundException("User " + userName + " not found. (" + _accountManager.getCurrentAccount().getName() + ")");
        } catch (ServerException e) {
            throw new DataNotFoundException("Error finding Userdata : " + e.getMessage());
        }

    }

    @GET
    @Path("getAllUsers")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers() throws DataNotFoundException {
        try {
            ArrayList<User> list = new ArrayList<User>();
            Collections.addAll(list, _userManager.getAllUsers());

            return list;
        } catch (ServerException e) {
            throw new DataNotFoundException("Userdata not found :" + e.getMessage());
        }
    }


    public List<User> findUSerByParent(String parentUserName) throws DataNotFoundException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @POST
    @Path("user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public User createUser(User userData) throws RestException {

        try {
            return _userManager.createUser(userData);
        } catch (ServerException e) {
            throw new RestException(e.getMessage());
        }

    }

    @PUT
    @Path("user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public User updateUser(User userData) throws RestException {
        try {

            return _userManager.updateUser(userData);
        } catch (ServerException e) {
            throw new RestException(e.getMessage());
        }
    }


    @DELETE
    @Path("user/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteUser(@PathParam("username") String userName) throws RestException {

        try {
            User u = getUser(userName);
            _userManager.deleteUser(u.getId());


        } catch (ServerException e) {
            throw new RestException("Couldn't delete User (" + userName + ") : " + e.getMessage());
        }

    }


    @GET
    @Path("userGroups/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Group> getGroupsOnUser(@PathParam("username") String userName) throws RestException {

        try {
            User u = getUser(userName);
            Group[] groups = _userManager.getGroupsForUser(u.getId());

            ArrayList<Group> erg = new ArrayList<Group>();

            Collections.addAll(erg, groups);

            return erg;


        } catch (ServerException e) {
            throw new RestException("Couldn't delete User (" + userName + ") : " + e.getMessage());
        }

    }

    @GET
    @Path("getAllGroups")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Group> getAllGroups() throws DataNotFoundException {
        try {
            ArrayList<Group> list = new ArrayList<Group>();
            Collections.addAll(list, _groupManager.getAllGroups());

            return list;
        } catch (ServerException e) {
            throw new DataNotFoundException("Groups not found :" + e.getMessage());
        }
    }

    @POST
    @Path("userGroups")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Group createGroup(Group group) {
        try {
            return _groupManager.createGroup(group);
        } catch (ServerException e) {
            throw new RestException(e.getMessage());
        }
    }

    @PUT
    @Path("userGroups")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Group updateGroup(Group groupData) throws RestException {
        try {

            return _groupManager.updateGroup(groupData);
        } catch (ServerException e) {
            throw new RestException(e.getMessage());
        }
    }

    @DELETE
    @Path("userGroups/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteGroupsOnUser(@PathParam("username") String userName) throws RestException {

        try {
            User u = getUser(userName);
            Group[] groups = _userManager.getGroupsForUser(u.getId());

            for (Group g : groups) {
                _userManager.removeGroupMembership(u.getId(), g);
            }
        } catch (ServerException e) {
            throw new RestException("Couldn't delete User (" + userName + ") : " + e.getMessage());
        }

    }

    @DELETE
    @Path("userGroups/{username}/{groupname}")
    @Produces(MediaType.APPLICATION_JSON)
    public void removeUserFromGroup(@PathParam("username") String userName, @PathParam("groupname") String groupName) throws RestException {

        Group group = getGroup(groupName);

        try {
            User u = getUser(userName);
            Group g = getGroup(groupName);
            _userManager.removeGroupMembership(u.getId(), g);
        } catch (ServerException e) {
            throw new RestException("Couldn't remove User (" + userName + ") from Group (" + groupName + "): " + e.getMessage());
        }
    }

    @PUT
    @Path("userGroups/{username}/{groupname}")
    @Produces(MediaType.APPLICATION_JSON)
    public void addGroupMembership(@PathParam("username") String userName, @PathParam("groupname") String groupName) throws RestException {

        Group g = getGroup(groupName);

        try {
            User u = getUser(userName);
            _userManager.addGroupMembership(u.getId(), g);

        } catch (ServerException e) {
            throw new RestException("Couldn't add User (" + userName + ") to Group (" + groupName + ") : " + e.getMessage());
        }

    }

    @GET
    @Path("getAllAccountRoles")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AccountRole> getAllAccountRoles() throws DataNotFoundException {
        try {
            ArrayList<AccountRole> list = new ArrayList<AccountRole>();
            Collections.addAll(list, _accountRoleManager.getAllAccountRoles());

            return list;
        } catch (ServerException e) {
            throw new DataNotFoundException("Couldn't get Roles :" + e.getMessage());
        }
    }

    @POST
    @Path("createAccountRole")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AccountRole createAccountRole(AccountRole role) {
        try {
            return _accountRoleManager.createAccountRole(role);
        } catch (ServerException e) {
            throw new RestException(e.getMessage());
        }
    }

    @PUT
    @Path("updateAccountRole")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AccountRole updateAccountRole(AccountRole role) throws RestException {
        try {
            return _accountRoleManager.updateAccountRole(role);
        } catch (ServerException e) {
            throw new RestException(e.getMessage());
        }
    }

    @PUT
    @Path("userRoles/{username}/{rolename}")
    @Produces(MediaType.APPLICATION_JSON)
    public void addAccountRoleToUser(@PathParam("username") String userName, @PathParam("rolename") String roleName) throws RestException {

        AccountRole r = getAccountRole(roleName);

        try {
            User u = getUser(userName);
            _accountRoleManager.addAccountRoleToUsers(r.getId(), u.getId());
        } catch (ServerException e) {
            throw new RestException("Couldn't add User (" + userName + ") to Role (" + roleName + "): " + e.getMessage());
        }
    }

    @PUT
    @Path("groupRoles/{groupname}/{rolename}")
    @Produces(MediaType.APPLICATION_JSON)
    public void addAccountRoleToGroup(@PathParam("groupname") String groupName, @PathParam("rolename") String roleName) throws RestException {

        try {
            long groupId = -1;

            // First, get the group id
            Group[] groups = _groupManager.getAllGroups();
            for (Group g : groups) {
                System.out.println("Tries :" + g.getName() + "  WITH " + groupName);
                if (g.getName().equals(groupName)) {
                    groupId = g.getId();
                    break;
                }
            }

            if (groupId == -1) {
                throw new RestException("Couldn't find Group " + groupName);
            }

            // then, get the role id and assign the group to the role
            AccountRole[] roles = _accountRoleManager.getAllAccountRoles();
            for (AccountRole r : roles) {
                System.out.println("Tries :" + r.getName() + "  WITH " + roleName);
                if (r.getName().equals(roleName)) {
                    _accountRoleManager.addAccountRoleToGroups(r.getId(), groupId);
                    return;
                }
            }

            throw new RestException("Couldn't find Role " + roleName);


        } catch (ServerException e) {
            throw new RestException("Couldn't add Group (" + groupName + ") to Role (" + roleName + "): " + e.getMessage());
        }
    }

    @DELETE
    @Path("group/{groupname}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteGroup(@PathParam("groupname") String groupName) throws RestException {

        Group group = getGroup(groupName);

        try {
            _groupManager.deleteGroup(group.getId());

        } catch (ServerException e) {
            throw new RestException("Couldn't delete Group (" + groupName + ") : " + e.getMessage());
        }

    }

    @DELETE
    @Path("role/{rolename}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteAccountRole(@PathParam("rolename") String roleName) throws RestException {

        AccountRole role = getAccountRole(roleName);

        try {
            _accountRoleManager.deleteAccountRole(role.getId());

        } catch (ServerException e) {
            throw new RestException("Couldn't delete Role (" + roleName + ") : " + e.getMessage());
        }

    }

    @DELETE
    @Path("userRoles/{username}/{rolename}")
    @Produces(MediaType.APPLICATION_JSON)
    public void removeAccountRoleFromUser(@PathParam("username") String userName, @PathParam("rolename") String roleName) throws RestException {

        User u = getUser(userName);
        AccountRole role = getAccountRole(roleName);

        try {
            _accountRoleManager.removeAccountRoleFromUsers(role.getId(), u.getId());
        } catch (ServerException e) {
            throw new RestException("Couldn't remove User (" + userName + ") from Role (" + roleName + "): " + e.getMessage());
        }
    }

    @DELETE
    @Path("groupRoles/{groupname}/{rolename}")
    @Produces(MediaType.APPLICATION_JSON)
    public void removeAccountRoleFromGroup(@PathParam("groupname") String groupName, @PathParam("rolename") String roleName) throws RestException {

        Group group = getGroup(groupName);
        AccountRole role = getAccountRole(roleName);

        try {
            _accountRoleManager.removeAccountRoleFromGroups(role.getId(), group.getId());
        } catch (ServerException e) {
            throw new RestException("Couldn't remove Group (" + groupName + ") from Role (" + roleName + "): " + e.getMessage());
        }
    }

    @GET
    @Path("groups/{groupname}")
    @Produces(MediaType.APPLICATION_JSON)
    public Group getGroup(@PathParam("groupname") String groupName) throws DataNotFoundException, RestException {

        try {
            // First, get the group id
            Group[] groups = _groupManager.getAllGroups();
            for (Group g : groups) {
                System.out.println("Tries :" + g.getName() + "  WITH " + groupName);
                if (g.getName().equals(groupName)) {
                    return g;
                }
            }
        } catch (ServerException e) {
            throw new RestException("Error finding Group " + groupName + ":" + e.getMessage());
        }

        throw new DataNotFoundException("Couldn't find Group " + groupName);
    }

    @GET
    @Path("accountRoles/{rolename}")
    @Produces(MediaType.APPLICATION_JSON)
    public AccountRole getAccountRole(@PathParam("rolename") String roleName) throws DataNotFoundException, RestException {

        try {
            // First, get the group id
            AccountRole[] roles = _accountRoleManager.getAllAccountRoles();
            for (AccountRole r : roles) {
                System.out.println("Tries :" + r.getName() + "  WITH " + roleName);
                if (r.getName().equals(roleName)) {
                    return r;
                }
            }
        } catch (ServerException e) {
            throw new RestException("Error finding Role " + roleName + ":" + e.getMessage());
        }

        throw new DataNotFoundException("Couldn't find Role " + roleName);
    }

    @GET
    @Path("getAllLDAPGroups")
    @Produces(MediaType.APPLICATION_JSON)
    public List<LDAPGroup> getAllLDAPGroups() throws DataNotFoundException {
        try {
            ArrayList<LDAPGroup> list = new ArrayList<LDAPGroup>();
            Collections.addAll(list, _ldapConnector.getAllGroups());

            return list;
        } catch (ServerException e) {
            throw new DataNotFoundException("Error retrieving LDAP groups :" + e.getMessage());
        }
    }
}
