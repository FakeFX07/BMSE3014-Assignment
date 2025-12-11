package repository.interfaces;

public interface IAdminRepository {
    boolean authenticate(String name, String password);
}