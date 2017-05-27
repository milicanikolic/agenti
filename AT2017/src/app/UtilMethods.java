package app;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class UtilMethods {
	
	public UtilMethods() {
		
	}
	
	
	public static String getMasterAddress() {

		Properties prop = new Properties();

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream("config.properties");
		try {
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return prop.getProperty("masterAddress");
	}

	public static String getMasterAlias() {
		Properties prop = new Properties();

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream("config.properties");
		try {
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return prop.getProperty("masterAlias");
	}

	public static String getMasterPort() {
		Properties prop = new Properties();

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream("config.properties");
		try {
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return prop.getProperty("masterPort");
	}

	public static String getCurrentAddress() {

		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) networkInterfaces.nextElement();
				Enumeration<InetAddress> nias = ni.getInetAddresses();
				while (nias.hasMoreElements()) {
					InetAddress ia = (InetAddress) nias.nextElement();
					if (!ia.isLinkLocalAddress() && !ia.isLoopbackAddress() && ia instanceof Inet4Address) {
						return ia.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static String getCurrentName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getPort() {

		String fileName = System.getProperty("jboss.server.config.dir") + "\\standalone-full.xml";
		BufferedReader br1 = null;
		BufferedReader br2 = null;
		String portPart = "";
		String offset = "";

		try {

			String sCurrentLine;

			br1 = new BufferedReader(new FileReader(fileName));

			while ((sCurrentLine = br1.readLine()) != null) {

				if (sCurrentLine.contains("jboss.http.port")) {

					String[] foundPort = sCurrentLine.split(":");
					portPart = (foundPort[1].split("}"))[0];

					break;

				}

			}

			br2 = new BufferedReader(new FileReader(fileName));

			while ((sCurrentLine = br2.readLine()) != null) {

				if (sCurrentLine.contains("jboss.socket.binding.port-offset")) {

					String[] foundOffset = sCurrentLine.split(":");
					offset = (foundOffset[1].split("}"))[0];

					break;

				}

			}

			int portPartInt = Integer.parseInt(portPart);
			int offsetInt = Integer.parseInt(offset);
			int portInt = portPartInt + offsetInt;
			return String.valueOf(portInt);

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (br1 != null)
					br1.close();

				if (br2 != null)
					br2.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}

		return null;

	}

	public static List<String> getAgentNames()
	{
		Properties prop = new Properties();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream("config.properties");

		try {

			
			prop.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String str = prop.getProperty("agents");
		String[] lst = str.split(",");
		List<String> retVal = Arrays.asList(lst);
		return retVal;
	}
	
}
