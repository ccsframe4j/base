package cc.creamcookie.security;

import cc.creamcookie.security.dto.SignDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class VUSecurity {

	public static String getPrincipal() {
		Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (obj instanceof UserDetails) {
			return ((UserDetails) obj).getUsername();
		} else {
			return "Guest";
		}
	}


	public static boolean isSigned() {
		try {
			Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			return (obj instanceof UserDetails);
		}
		catch(Exception ex) {
			return false;
		}
	}



	public static Object getData() {
		try {
			SignDetails obj = (SignDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			return obj.getObject();
		}
		catch(Exception ex) {
			return null;
		}
	}

	public static long getLong(String key) {
		try {
			SignDetails obj = (SignDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			return obj.getLong(key);
		}
		catch(Exception ex) {
			return -1;
		}
	}

	public static String getString(String key) {
		try {
			SignDetails obj = (SignDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			return obj.getString(key);
		}
		catch(Exception ex) {
			return null;
		}
	}


	public static boolean allGranted(String[] checkForAuths) {
		Set<String> userAuths = getUserAuthorities();
		for (String auth : checkForAuths) {
			if (userAuths.contains(auth))
				continue;
			return false;
		}
		return true;
	}


	public static boolean anyGranted(String[] checkForAuths) {
		Set<String> userAuths = getUserAuthorities();
		for (String auth : checkForAuths) {
			if (userAuths.contains(auth))
				return true;
		}
		return false;
	}


	public static boolean noneGranted(String[] checkForAuths) {
		Set<String> userAuths = getUserAuthorities();
		for (String auth : checkForAuths) {
			if (userAuths.contains(auth))
				return false;
		}
		return true;
	}



	private static Set<String> getUserAuthorities() {
		Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Set<String> roles = new HashSet<String>();
		if (obj instanceof UserDetails) {
			Collection<? extends GrantedAuthority> gas = ((UserDetails) obj).getAuthorities();
			for (GrantedAuthority ga : gas) {
				roles.add(ga.getAuthority());
			}
		}
		return roles;
	}



}
