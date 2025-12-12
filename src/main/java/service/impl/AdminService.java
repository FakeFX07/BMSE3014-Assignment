package service.impl;

import service.interfaces.IAdminService;
import repository.interfaces.IAdminRepository;
import util.PasswordUtil;

public class AdminService implements IAdminService {
    
    private final IAdminRepository adminRepository;

    public AdminService(IAdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public boolean login(String name, String password) {
        if (name == null || password == null) {
            return false;
        }
        // Hash the input password before authentication
        String hashedPassword = PasswordUtil.hashPassword(password);
        return adminRepository.authenticate(name, hashedPassword);
    }
}