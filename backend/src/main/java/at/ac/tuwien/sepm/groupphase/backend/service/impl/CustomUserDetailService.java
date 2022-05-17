package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateUpdateUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserRole;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserStatus;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.AuthenticatedUser;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.util.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

@Service
public class CustomUserDetailService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    @Autowired
    public CustomUserDetailService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.userValidator = userValidator;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.trace("loadUserByUsername with {}", email);
        try {
            User user = findApplicationUserByEmail(email);
            return new AuthenticatedUser(user);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public User findApplicationUserByEmail(String email) {
        LOGGER.trace("findApplicationUserByEmail with {}", email);
        User user = userRepository.findUserByEmail(email);
        if (user != null) {
            return user;
        }
        throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
    }

    @Override
    public User registerUser(CreateUpdateUserDto userDto, boolean adminRole) {
        LOGGER.trace("registerUser with {}", userDto);
        userValidator.validateUser(userDto, adminRole);
        userDto.setStatus(UserStatus.OK);
        User u = userMapper.createUpdateUserDtoToUser(userDto);
        if (!adminRole) {
            u.setRole(UserRole.CUSTOMER);
        }
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        return userRepository.save(u);
    }

    @Override
    public User updateUser(CreateUpdateUserDto userDto, Long id, boolean adminRole) {
        LOGGER.trace("updateUser with {}", userDto);
        userValidator.validateUser(userDto, id, adminRole);
        Optional<User> tmp = userRepository.findById(id);
        if (tmp.isEmpty()) {
            throw new NotFoundException("User with ID " + id + " not found!");
        }
        User curr = tmp.get();
        User updated = userMapper.createUpdateUserDtoToUser(userDto);
        updated.setId(curr.getId());
        updated.setReadNews(curr.getReadNews());
        if (!adminRole) {
            updated.setRole(UserRole.CUSTOMER);
            updated.setStatus(UserStatus.OK);
        }
        updated.setPassword(passwordEncoder.encode(updated.getPassword()));
        return userRepository.save(updated);
    }

}
