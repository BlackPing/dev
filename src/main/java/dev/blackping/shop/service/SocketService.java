package dev.blackping.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.blackping.shop.DAO.AutoDAOInterface;

@Service
public class SocketService {
	@Autowired
	AutoDAOInterface adi;
	
	public void restkey(String namespace) {
		adi.sql("SL", namespace, "-select", null);
	}
}
