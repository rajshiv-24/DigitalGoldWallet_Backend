package com.cg.dto;

import com.cg.enums.Role;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public class UserResponseDTO {

    private Integer userId;
    private String name;
    private String email;
    private Role role;
    private BigDecimal balance;       
    private LocalDateTime createdAt;

     
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
	public UserResponseDTO(Integer userId, String name, String email, Role role, BigDecimal balance, LocalDateTime createdAt,
			String street, String city, String state, String postalCode, String country) {
		super();
		this.userId = userId;
		this.name = name;
		this.email = email;
		this.role = role;
		this.balance = balance;
		this.createdAt = createdAt;
		this.street = street;
		this.city = city;
		this.state = state;
		this.postalCode = postalCode;
		this.country = country;
	}
	public UserResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
}
