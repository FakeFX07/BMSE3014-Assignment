package controller;

import repository.impl.AdminRepository;
import service.impl.AdminService;
import service.interfaces.IAdminService;

public class AdminController {

    private final IAdminService adminService;

    public AdminController(IAdminService adminService) {
        this.adminService = adminService;
    }


    public AdminController() {
        this.adminService = new AdminService(new AdminRepository());
    }


    public boolean login(String name, String password) {
        return adminService.login(name, password);
    }
}