package dev.blackping.shop.object;

public class SessionObject {
	String id;
	String nickname;
	int power;
	
	public SessionObject(String id, String nickname, int power) {
		this.id = id;
		this.nickname = nickname;
		this.power = power;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getPower() {
		return power;
	}
	public void setPower(int power) {
		this.power = power;
	}

	@Override
	public String toString() {
		return "SessionObject [id=" + id + ", nickname=" + nickname + "]";
	}
	
}
