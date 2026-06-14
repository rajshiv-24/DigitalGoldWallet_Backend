package com.cg.dto;

public class UserRequestDTO {

    private String name;        
    private String email;       
    private String password;
    private Integer addressId;  

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Integer getAddressId() { return addressId; }
    public void setAddressId(Integer addressId) { this.addressId = addressId; }
	public UserRequestDTO(String name, String email, String password, Integer addressId) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
		this.addressId = addressId;
	}
	public UserRequestDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
}
