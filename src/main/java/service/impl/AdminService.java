package service.impl;

import service.interfaces.IAdminService;
import repository.interfaces.IAdminRepository;

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
        return adminRepository.authenticate(name, password);
    }
}