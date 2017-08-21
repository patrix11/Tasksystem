package se.group.tasksystem.data.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.data.domain.AuditorAware;

public class CustomAuditor implements AuditorAware<String> {

	@Override
	public String getCurrentAuditor() {
		String hostname = "Unknown host";
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return hostname;
	}
}
