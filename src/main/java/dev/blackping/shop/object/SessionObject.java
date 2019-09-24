package dev.blackping.shop.object;

public class SessionObject {
	String id;
	String nickname;
	
	public SessionObject(String id, String nickname) {
		this.id = id;
		this.nickname = nickname;
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
	
	@Override
	public String toString() {
		return "SessionObject [id=" + id + ", nickname=" + nickname + "]";
	}
	
}
