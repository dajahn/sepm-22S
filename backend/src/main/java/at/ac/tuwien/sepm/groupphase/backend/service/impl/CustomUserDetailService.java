package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateUpdateUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PagedUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserRole;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserStatus;
import at.ac.tuwien.sepm.groupphase.backend.exception.CouldNotLockUserException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.AuthenticatedUser;
import at.ac.tuwien.sepm.groupphase.backend.service.ResetPasswordService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.util.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final ResetPasswordService resetPasswordService;
    private final OrderRepository orderRepository;

    @Autowired
    public CustomUserDetailService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        UserMapper userMapper,
        UserValidator userValidator,
        ResetPasswordService resetPasswordService,
        OrderRepository orderRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.userValidator = userValidator;
        this.resetPasswordService = resetPasswordService;
        this.orderRepository = orderRepository;
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

    @Transactional
    @Override
    public void addFailedLoginAttemptToUser(UserLoginDto userDto) {
        LOGGER.trace("addFailedLoginAttemptToUser() with {}", userDto);

        User u = userRepository.findUserByEmail(userDto.getEmail());
        if (u == null) {
            LOGGER.debug("user with mail: {} does not exist!", userDto.getEmail());
            return;
        }

        u.setFailedLoginAttempts(u.getFailedLoginAttempts() + 1);
        if (u.getFailedLoginAttempts() >= 5) {
            LOGGER.debug("user with mail: {} is now LOCKED!", userDto.getEmail());
            u.setStatus(UserStatus.LOCKED);

            if (u.getRole() == UserRole.ADMIN) {
                resetPasswordService.forgotPassword(u.getEmail());
            }
        }

        userRepository.save(u);
    }

    @Override
    public void resetFailedLoginAttemptsForUser(UserLoginDto userLoginDto) {
        LOGGER.trace("resetFailedLoginAttemptsForUser() with {}", userLoginDto);
        //User exists for sure
        User u = userRepository.findUserByEmail(userLoginDto.getEmail());
        u.setFailedLoginAttempts(0);
        userRepository.save(u);
    }

    @Override
    public UserStatus getUserStatus(UserLoginDto userDto) {
        LOGGER.trace("userIsBlocked() with {}", userDto);
        User u = userRepository.findUserByEmail(userDto.getEmail());
        if (u == null) {
            LOGGER.debug("user with mail: {} does not exist!", userDto.getEmail());
            return null;
        }
        return u.getStatus();
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

    @Override
    public List<User> getLockedUser() {
        LOGGER.trace("getLockedUser()");
        return this.userRepository.loadLockedUser();
    }

    @Transactional
    @Override
    public User updateLockingState(Long id, boolean locked, String mail) {
        LOGGER.trace("updateLockingState({},{})", id, locked);
        Optional<User> user = this.userRepository.findById(id);

        if (user.isEmpty()) {
            LOGGER.debug("User was empty so cannot be (un)-locked!");
            throw new NotFoundException("User not found!");
        }
        if (user.get().getEmail().equals(mail)) {
            LOGGER.debug("User cannot lock himself, Email: {}", mail);
            throw new CouldNotLockUserException("User cannot (un)-lock himself!");
        }

        if (locked) {
            user.get().setStatus(UserStatus.LOCKED);
        } else {
            user.get().setStatus(UserStatus.OK);
            user.get().setFailedLoginAttempts(0);
        }

        userRepository.save(user.get());
        return user.get();
    }

    @Transactional
    @Override
    public PagedUserDto getUser(UserSearchDto userSearchDto, int page, int size) {
        LOGGER.info("{},{},{}", userSearchDto.getNameSearch(), userSearchDto.getRole(), userSearchDto.getStatus());
        int role = -1;
        int status = -1;

        if (userSearchDto.getRole() != null) {
            role = userSearchDto.getRole().ordinal();
        }

        if (userSearchDto.getStatus() != null) {
            status = userSearchDto.getStatus().ordinal();
        }

        Pageable pageable = PageRequest.of(page, size);

        List<User> u =  this.userRepository.loadUsers(userSearchDto.getNameSearch(), role, status, pageable);
        List<UserDto> userDtos = userMapper.entitiesToUserDto(u);

        Long totalCount = this.userRepository.getMatchingUsersCount(userSearchDto.getNameSearch(), role, status);

        PagedUserDto pagedUserDto = new PagedUserDto();
        pagedUserDto.setUsers(userDtos);
        pagedUserDto.setTotalCount(totalCount);
        return pagedUserDto;
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        LOGGER.info("deleteUser(Long id) with id = {]", id);
        orderRepository.deleteAllByUserId(id);
        userRepository.deleteById(id);
    }
}
