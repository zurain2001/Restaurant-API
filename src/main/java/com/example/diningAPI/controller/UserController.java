package com.example.diningAPI.controller;

import com.example.diningAPI.model.User;
import com.example.diningAPI.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import java.util.Optional;

@RequestMapping("/users")
@RestController
public class UserController {
    @PersistenceUnit
    private EntityManagerFactory emf;
    public final UserRepository userRepository;

    public UserController(final UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createNewUser(@RequestBody User user){
        validateUser(user);
        userRepository.save(user);
    }

    @GetMapping("/{displayName}")
    public User getUser(@PathVariable String displayName){
        validateDisplayName(displayName);
        Optional<User> optionalExistingUser = userRepository.findUserByDisplayName(displayName);
        if(!optionalExistingUser.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        User existingUser = optionalExistingUser.get();
        existingUser.setId(null);
        return existingUser;

    }

    @PutMapping("/{displayName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUserInfo(@PathVariable String displayName, @RequestBody User updatedUser){
        validateDisplayName(displayName);
        Optional<User> optionalExistingUser = userRepository.findUserByDisplayName(displayName);
        if(optionalExistingUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        User existingUser = optionalExistingUser.get();
        copyUserInfoFrom(updatedUser, existingUser);

    }

    private void copyUserInfoFrom(User updatedUser, User existingUser){
        if(ObjectUtils.isEmpty(updatedUser.getDisplayName())){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if(!ObjectUtils.isEmpty(updatedUser.getCity())){
            existingUser.setCity(updatedUser.getCity());
        }
        if(!ObjectUtils.isEmpty(updatedUser.getState())){
            existingUser.setState(updatedUser.getState());
        }
        if(!ObjectUtils.isEmpty(updatedUser.getZipcode())){
            existingUser.setZipcode(updatedUser.getZipcode());
        }
        if(!ObjectUtils.isEmpty(updatedUser.getSeeDairy())){
            existingUser.setSeeDairy(updatedUser.getSeeDairy());
        }
        if(!ObjectUtils.isEmpty(updatedUser.getSeeEggs())){
            existingUser.setSeeEggs(updatedUser.getSeeEggs());
        }
        if(!ObjectUtils.isEmpty(updatedUser.getSeePeanuts())){
            existingUser.setSeePeanuts(updatedUser.getSeePeanuts());
        }
    }

    private void validateUser(User user){
        validateDisplayName(user.getDisplayName());
        Optional<User> existingUser = userRepository.findUserByDisplayName(user.getDisplayName());
        if(existingUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    private void validateDisplayName(String displayName){
        if(ObjectUtils.isEmpty(displayName)){
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }
}