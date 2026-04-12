package com.esprit.applicationservice.dto;

public class UserEventDTO {
    private Integer id;
    private String name;
    private String lastName;
    private String email;
    private boolean enabled;

    public UserEventDTO() {}
    public Integer getId()              { return id; }
    public void setId(Integer id)       { this.id = id; }
    public String getName()             { return name; }
    public void setName(String name)    { this.name = name; }
    public String getLastName()         { return lastName; }
    public void setLastName(String v)   { this.lastName = v; }
    public String getEmail()            { return email; }
    public void setEmail(String email)  { this.email = email; }
    public boolean isEnabled()          { return enabled; }
    public void setEnabled(boolean v)   { this.enabled = v; }

    @Override
    public String toString() {
        return "UserEventDTO{id=" + id + ", name='" + name + "', lastName='" + lastName + "'}";
    }
}