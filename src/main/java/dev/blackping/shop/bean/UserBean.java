package dev.blackping.shop.bean;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class UserBean {
	int no;
	String nickname;
	String profileimage;
	int power;
	
	public UserBean(int no, String nickname, String profileimage, int power) {
		this.no = no;
		this.nickname = nickname;
		this.profileimage = profileimage;
		this.power = power;
	}
}
