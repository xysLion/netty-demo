package com.ancun.task.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 
 * @author fish
 * 
 */
public class HostUtil {

	private static final Logger logger = LoggerFactory.getLogger(HostUtil.class);

	private static final HostInfo hostInfo = new HostInfo();

	private static final Ipv4Info ipv4Info = new Ipv4Info();

	public static final HostInfo getHostInfo() {
		return hostInfo;
	}

	public static final Ipv4Info getIpv4Info() {
		return ipv4Info;
	}

	public static final class HostInfo {
		private final String HOST_NAME;

		private final String HOST_ADDRESS;

		private HostInfo() {
			String hostName;
			String hostAddress;
			try {
				InetAddress localhost = InetAddress.getLocalHost();
				hostName = localhost.getHostName();
				hostAddress = localhost.getHostAddress();
			} catch (UnknownHostException e) {
				logger.error("error then get host info", e);
				hostName = "localhost";
				hostAddress = "127.0.0.1";
			}
			HOST_NAME = hostName;
			HOST_ADDRESS = hostAddress;

		}

		public final String getName() {
			return HOST_NAME;
		}

		public final String getAddress() {
			return HOST_ADDRESS;
		}
	}

	public static final class Ipv4Info {
		private final Set<String> address;

		private Ipv4Info() {
			Set<String> as = new LinkedHashSet<String>();
			try {
				for (Enumeration<NetworkInterface> netInterfaces = NetworkInterface
						.getNetworkInterfaces(); netInterfaces
						.hasMoreElements();) {
					NetworkInterface ni = (NetworkInterface) netInterfaces
							.nextElement();
					for (Enumeration<InetAddress> en = ni.getInetAddresses(); en
							.hasMoreElements();) {
						InetAddress ip = en.nextElement();
						if (!ip.isLoopbackAddress()
								&& (ip instanceof Inet4Address)) {
							as.add(ip.getHostAddress());
						}
					}
				}
			} catch (SocketException e) {
				logger.error("error then getNetworkInterfaces.", e);
			}
			address = Collections.unmodifiableSet(as);
		}

		/**
		 * 
		 * @return
		 */
		public final Collection<String> getAddress() {
			return address;
		}

		/**
		 * 
		 * @return
		 */
		public final String getFristAddress() {
			if (address.isEmpty()) {
				return null;
			}
			return address.iterator().next();
		}

		/**
		 * 
		 * @param ip
		 * @return
		 */
		public final boolean isMyAddress(String ip) {
			return this.address.contains(ip);
		}

		/**
		 * 判断ip是否为内网地址
		 * 内网IP是以下面几个段的IP.用户可以自己设置.常用的内网IP地址:
		 * 10.0.0.0~10.255.255.255
		 * 172.16.0.0~172.31.255.255
		 * 192.168.0.0~192.168.255.255
		 *
		 * @param ip
		 * @return
		 */
		public final boolean isInner(String ip)
		{
			//正则表达式
			String reg = "(10|172|192)\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})";
			Pattern p = Pattern.compile(reg);
			Matcher matcher = p.matcher(ip);
			return matcher.find();
		}

		/**
		 * 取得内网ip
		 * 如果不存在则返回第一个ip
		 *
		 * @return
		 */
		public final String getLocalAddress() {
			// 遍历所有接口ip 如果是内网ip则直接返回
			for (String ip : address) {
				if (isInner(ip)) {
					return ip;
				}
			}
			 return getFristAddress();
		}
	}

	public static void main(String[] args) throws Exception{
		HostUtil.Ipv4Info ip = HostUtil.getIpv4Info();
		System.out.println("fristAddress:");
		System.out.println(ip.getFristAddress());
		System.out.println("=================================");
		for (String ipString : ip.getAddress()) {
			System.out.println(ipString);
		}
		System.out.println("===========================================");
		System.out.println(ip.getLocalAddress());
	}
}
