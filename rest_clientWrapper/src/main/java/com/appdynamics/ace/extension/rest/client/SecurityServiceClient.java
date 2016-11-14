package com.appdynamics.ace.extension.rest.client;

import com.singularity.ee.controller.api.dto.Group;
import com.singularity.ee.controller.api.dto.User;
import com.singularity.ee.controller.api.dto.AccountRole;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: stefan.marx
 * Date: 22.10.13
 * Time: 02:52
 * To change this template use File | Settings | File Templates.
 */
public class SecurityServiceClient {
    private final WebResource _restClient;

    public SecurityServiceClient(AceRestClient aceClient) {
        _restClient = aceClient.createClient().path("aceSecurityService");
    }

    public List<User> getAllUsers() throws RestClientException{

        ClientResponse response = _restClient.path("getAllUsers").accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getEntity(new GenericType<List<User>>(){});
        }
        else throw new RestClientException(response);
    }

    public User  getUser(String username) throws RestClientException {
        ClientResponse response = _restClient.path("user")
                .path(username)
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getEntity(User.class);
        }
        else throw new RestClientException(response);

    }

    public User createUser(User src) throws RestClientException {
        ClientResponse response = _restClient.path("user")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, src);

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getEntity(User.class);
        }
        else throw new RestClientException(response);


    }

    public User updateUser(User src) throws RestClientException {
        ClientResponse response = _restClient.path("user")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .put(ClientResponse.class, src);

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getEntity(User.class);
        }
        else throw new RestClientException(response);
    }

    public void deleteUser(String username) throws RestClientException {
        ClientResponse response = _restClient.path("user")
                .path(username)
                .accept(MediaType.APPLICATION_JSON)
                .delete(ClientResponse.class);

        if (response.getStatus() >= 300 ) {

           throw new RestClientException(response);
        }
    }

    public List<Group> getGroupsForUser(String username) throws RestClientException{

        ClientResponse response = _restClient.path("userGroups")
                .path(username)
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getEntity(new GenericType<List<Group>>(){});
        }
        else throw new RestClientException(response);
    }

    public Group getGroup(String groupname) throws RestClientException {
        ClientResponse response = _restClient.path("groups")
                .path(groupname)
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getEntity(Group.class);
        }
        else throw new RestClientException(response);

    }

    public void deleteGroup(String groupname) throws RestClientException {
        ClientResponse response = _restClient.path("group")
                .path(groupname)
                .accept(MediaType.APPLICATION_JSON)
                .delete(ClientResponse.class);

        if (response.getStatus() >= 300 ) {

            throw new RestClientException(response);
        }
    }

    public void clearGroupsForUser(String username) throws RestClientException{

        ClientResponse response = _restClient.path("userGroups")
                .path(username)
                .accept(MediaType.APPLICATION_JSON)
                .delete(ClientResponse.class);
        if (response.getStatus() >=300) {
            throw new RestClientException(response);
        }
    }


    public void assignUserToGroup(String username, String groupname) throws RestClientException {
        ClientResponse response = _restClient.path("userGroups")
                .path(username)
                .path(groupname)
                .accept(MediaType.APPLICATION_JSON)
                .put(ClientResponse.class);
        if (response.getStatus() >=300) {
            throw new RestClientException(response);
        }
    }

    public Group createGroup(Group g) throws RestClientException {
        ClientResponse response = _restClient.path("userGroups")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, g);

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getEntity(Group.class);
        }
        else throw new RestClientException(response);
    }

    public void removeUserFromGroup(String username, String groupname) throws RestClientException{

        ClientResponse response = _restClient.path("userGroups")
                .path(username)
                .path(groupname)
                .accept(MediaType.APPLICATION_JSON)
                .delete(ClientResponse.class);
        if (response.getStatus() >=300) {
            throw new RestClientException(response);
        }
    }

    public AccountRole createAccountRole(AccountRole r) throws RestClientException {
        ClientResponse response = _restClient.path("createAccountRole")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, r);

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getEntity(AccountRole.class);
        }
        else throw new RestClientException(response);
    }

    public AccountRole getAccountRole(String rolename) throws RestClientException {
        ClientResponse response = _restClient.path("accountRoles")
                .path(rolename)
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getEntity(AccountRole.class);
        }
        else throw new RestClientException(response);

    }

    public void deleteAccountRole(String rolename) throws RestClientException {
        ClientResponse response = _restClient.path("role")
                .path(rolename)
                .accept(MediaType.APPLICATION_JSON)
                .delete(ClientResponse.class);

        if (response.getStatus() >= 300 ) {

            throw new RestClientException(response);
        }
    }

    public List<Group> getAllGroups() throws RestClientException{

        ClientResponse response = _restClient.path("getAllGroups").accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getEntity(new GenericType<List<Group>>(){});
        }
        else throw new RestClientException(response);
    }

    public List<AccountRole> getAllAccountRoles() throws RestClientException{

        ClientResponse response = _restClient.path("getAllAccountRoles").accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getEntity(new GenericType<List<AccountRole>>(){});
        }
        else throw new RestClientException(response);
    }

    public Group updateGroup(Group src) throws RestClientException {
        ClientResponse response = _restClient.path("userGroups")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .put(ClientResponse.class, src);

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getEntity(Group.class);
        }
        else throw new RestClientException(response);
    }

    public AccountRole updateAccountRole(AccountRole src) throws RestClientException {
        ClientResponse response = _restClient.path("updateAccountRole")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .put(ClientResponse.class, src);

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getEntity(AccountRole.class);
        }
        else throw new RestClientException(response);
    }

    public void addAccountRoleToUser(String username, String rolename) throws RestClientException {
        ClientResponse response = _restClient.path("userRoles")
                .path(username)
                .path(rolename)
                .accept(MediaType.APPLICATION_JSON)
                .put(ClientResponse.class);
        if (response.getStatus() >=300) {
            throw new RestClientException(response);
        }
    }

    public void addAccountRoleToGroup(String groupname, String rolename) throws RestClientException {
        ClientResponse response = _restClient.path("groupRoles")
                .path(groupname)
                .path(rolename)
                .accept(MediaType.APPLICATION_JSON)
                .put(ClientResponse.class);
        if (response.getStatus() >=300) {
            throw new RestClientException(response);
        }
    }

    public void removeAccountRoleFromUser(String username, String rolename) throws RestClientException{

        ClientResponse response = _restClient.path("userRoles")
                .path(username)
                .path(rolename)
                .accept(MediaType.APPLICATION_JSON)
                .delete(ClientResponse.class);
        if (response.getStatus() >=300) {
            throw new RestClientException(response);
        }
    }


    public void removeAccountRoleFromGroup(String groupname, String rolename) throws RestClientException{

        ClientResponse response = _restClient.path("groupRoles")
                .path(groupname)
                .path(rolename)
                .accept(MediaType.APPLICATION_JSON)
                .delete(ClientResponse.class);
        if (response.getStatus() >=300) {
            throw new RestClientException(response);
        }
    }
}
