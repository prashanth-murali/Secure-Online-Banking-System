package com.securebank.bank.resources;

import com.securebank.bank.model.User;
import com.securebank.bank.services.LoggedInService;
import com.securebank.bank.services.UserRepository;
import com.securebank.bank.services.ValidationService;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    Logger logger = Logger.getLogger(UserRepository.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    LoggedInService loggedInService;

    @Autowired
    ValidationService validationService;

    @GET
    public List<User> getUsers(@HeaderParam("Authorization") String authorization) {
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        validationService.validateLoggedInUserIsAdmin(loggedInUser);

        return userRepository.findAll();
    }

    @GET
    @Path("/{userId}")
    public User getUser(@PathParam("userId") String userId, @HeaderParam("Authorization") String authorization){


        return userRepository.findById(userId);
    }

    @POST
    public User createUser(User user){
        user.setId(null);// ensure the user does not pass their own id to mongo
        return userRepository.save(user);
    }

    @PUT
    @Path("/{userId}")
    public User updateUser(@PathParam("userId") String userId, User user){
        User byId = userRepository.findById(userId);
        BeanUtils.copyProperties(user, byId);

        return userRepository.save(byId);
    }

    @DELETE
    @Path("/{userId}")
    public String deleteUser(@PathParam("userId") String userId){
        userRepository.deleteById(userId);
        return "{\"status\":\"success\"}";
    }
}
