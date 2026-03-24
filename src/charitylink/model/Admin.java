 package charitylink.model;

public class Admin extends User {
    private String role;

    public Admin(String id, String name, String email, String password, String role) {
        super(id, name, email, password);
        this.role = role;
    }

    @Override
    public String getRole() { return "ADMIN"; }

    public String getAdminRole() { return role; }
}
