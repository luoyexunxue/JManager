package com.uitd.web.application.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AddrHelper {
	/**
	 * 地址列表
	 */
	private static List<IPRegion> RelationMap = null;

	/**
	 * 通过IP获取地理位置
	 * 
	 * @param ip
	 * @return
	 */
	public static IPRegion getRegion(String ip) {
		if (RelationMap == null) {
			RelationMap = new ArrayList<IPRegion>();
			InputStream stream = AddrHelper.class.getResourceAsStream("/resource/ipregion/ipaddr.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			try {
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] str = line.split(",", -1);
					IPRegion item = new IPRegion();
					item.setBegin(Long.parseLong(str[0]));
					item.setEnd(Long.parseLong(str[1]));
					item.setParent(str[2]);
					item.setAddress(str[3]);
					item.setIsp(str[4]);
					RelationMap.add(item);
				}
				reader.close();
			} catch (NumberFormatException | IOException e) {
				e.printStackTrace();
			}
		}
		int pos1 = ip.indexOf(".");
		int pos2 = ip.indexOf(".", pos1 + 1);
		int pos3 = ip.indexOf(".", pos2 + 1);
		if (pos1 < 0 || pos2 < 0 || pos3 < 0)
			return null;
		long intIP = 0;
		try {
			long i0 = Long.parseLong(ip.substring(0, pos1));
			long i1 = Long.parseLong(ip.substring(pos1 + 1, pos2));
			long i2 = Long.parseLong(ip.substring(pos2 + 1, pos3));
			long i3 = Long.parseLong(ip.substring(pos3 + 1));
			intIP = (i0 << 24) + (i1 << 16) + (i2 << 8) + i3;
		} catch (NumberFormatException e) {
		}
		return intIP < 0 ? null : find(intIP, 0, RelationMap.size());
	}

	/**
	 * 快速查找IP地理位置
	 * 
	 * @param ip
	 * @param beginIndex
	 * @param endIndex
	 *            不包含
	 * @return
	 */
	private static IPRegion find(long ip, int beginIndex, int endIndex) {
		int middle = (beginIndex + endIndex) / 2;
		IPRegion item = RelationMap.get(middle);
		if (item.getBegin() > ip)
			return find(ip, beginIndex, middle);
		else if (item.getEnd() < ip)
			return find(ip, middle, endIndex);
		else
			return item;
	}
}