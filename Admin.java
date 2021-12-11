package com.placement.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Admin {
	@Id 
	private long aid;
	private String aname;
	private String password;
}
