package r5a08.finduser.ms_user.dto;

public class UserDTO {

    private Long id;
    private String prenom;
    private String nom;
    private String email;

    public UserDTO(Long id, String prenom, String nom, String email) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getNom() {
        return nom;
    }

    public String getEmail() {
        return email;
    }
}