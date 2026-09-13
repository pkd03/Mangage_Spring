package vn.spring.pkd.service.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.spring.pkd.entity.Role;
import vn.spring.pkd.entity.UserRole;
import vn.spring.pkd.repository.RoleRepository;
import vn.spring.pkd.repository.UserRepository;
import vn.spring.pkd.repository.UserRoleRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        vn.spring.pkd.entity.User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Tài khoản không tồn tại!");
        }
        List<UserRole> roleUser = userRoleRepository.findByUserId(user.getId());
        Role role = roleRepository.findById(roleUser.get(0).getRoleId()).get();
        org.springframework.security.core.userdetails.User userDetail =
                new org.springframework.security.core.userdetails.User(
                        user.getUsername(), user.getPassword(),rolesToAuthorities(List.of(role)));
        return userDetail;
    }
    private Collection<? extends GrantedAuthority> rolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());
    }
}
