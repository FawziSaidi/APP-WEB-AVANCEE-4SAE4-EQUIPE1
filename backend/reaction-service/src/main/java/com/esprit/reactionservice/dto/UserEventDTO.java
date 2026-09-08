package com.esprit.reactionservice.dto;

/**
 * DTO reçu depuis RabbitMQ (publié par user-service).
 * Mêmes champs que dans user-service, publication-service et commentaire-service.
 */
public class UserEventDTO {

    private Integer id;
    private String  name;
    private String  lastName;
    private String  email;
    private boolean enabled;

    public UserEventDTO() {}

    public UserEventDTO(Integer id, String name, String lastName,
                        String email, boolean enabled) {
        this.id       = id;
        this.name     = name;
        this.lastName = lastName;
        this.email    = email;
        this.enabled  = enabled;
    }

    public Integer getId()                        { return id; }
    public void    setId(Integer id)              { this.id = id; }
    public String  getName()                      { return name; }
    public void    setName(String name)           { this.name = name; }
    public String  getLastName()                  { return lastName; }
    public void    setLastName(String lastName)   { this.lastName = lastName; }
    public String  getEmail()                     { return email; }
    public void    setEmail(String email)         { this.email = email; }
    public boolean isEnabled()                    { return enabled; }
    public void    setEnabled(boolean enabled)    { this.enabled = enabled; }

    @Override
    public String toString() {
        return "UserEventDTO{id=" + id + ", name='" + name + "', lastName='" + lastName + "'}";
    }
}