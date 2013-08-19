/*
 * Copyright (C) Institute of Telematics, Lukas Ruge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambientdynamix.contextplugins.deviceinfo;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ambientdynamix.api.application.IContextInfo;
import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * The PingContextInfo is a simple extension of the standard IContextInfo Interface. It receives a String and stores this to be retreived.
 * 
 * 
 * @author lukas
 *
 */
public class DeviceInfo implements IContextInfo
{

	private final String TAG = "DEVICEPLUGIN";
	
	String ipv4="";
	String ipv6="";
	String mac="";
	String osVersion="";
	String apiLevel="";
	String deviceType="";
	String product="";
	String brand ="";
	String manufacturer="";
	String serial="";
	String board="";
	
	public static Parcelable.Creator<DeviceInfo> CREATOR = new Parcelable.Creator<DeviceInfo>() 
			{
			public DeviceInfo createFromParcel(Parcel in) 
			{
				return new DeviceInfo(in);
			}

			public DeviceInfo[] newArray(int size) 
			{
				return new DeviceInfo[size];
			}
		};
		
	DeviceInfo(String x)
	{
		Log.d(TAG, "create Current Time");
		this.ipv4=getIPAddress(true);
		this.ipv6=getIPAddress(false);
		this.mac=DeviceInfoPluginRuntime.getMacAddress();
		this.osVersion = System.getProperty("os.version");
		this.apiLevel = android.os.Build.VERSION.SDK;
		this.deviceType = android.os.Build.DEVICE;
		this.product = android.os.Build.PRODUCT;
		this.brand = android.os.Build.BRAND;
		this.manufacturer = android.os.Build.MANUFACTURER;
		this.serial = android.os.Build.SERIAL;
		this.board = android.os.Build.BOARD;
	}
	
	public DeviceInfo(Parcel in) 
	{
		ipv4=in.readString();
		ipv6=in.readString();
		mac=in.readString();
		osVersion=in.readString();
		apiLevel=in.readString();
		deviceType=in.readString();
		product=in.readString();
		brand=in.readString();
		manufacturer=in.readString();
		serial=in.readString();
		board=in.readString();
	}

	@Override
	public String toString() 
	{
		return this.getClass().getSimpleName();
	}
	
	@Override
	public int describeContents() 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) 
	{
		out.writeString(ipv4);
		out.writeString(ipv6);
		out.writeString(mac);
		out.writeString(osVersion);
		out.writeString(apiLevel);
		out.writeString(deviceType);
		out.writeString(product);
		out.writeString(brand);
		out.writeString(manufacturer);
		out.writeString(serial);
		out.writeString(board);
	}

	@Override
	public String getContextType() 
	{
		return "org.ambientdynamix.contextplugins.context.info.device.information";
	}

	@Override
	public String getImplementingClassname() 
	{
		return this.getClass().getName();
	}

	@Override
	public String getStringRepresentation(String format) 
	{
		String result="";
		if (format.equalsIgnoreCase("text/plain"))
		{
			return ipv4;
		}
		else if (format.equalsIgnoreCase("XML"))
		{
			return "<device>" +
				   "    <ip4>"+ipv4+"</ip4>" +
				   "    <ip6>"+ipv6+"</ip6>" +
				   "    <mac>"+mac+"</mac>" +
				   "    <osVersion>"+osVersion+"</osVersion>" +
				   "    <apiLevel>"+apiLevel+"</apiLevel>" +
				   "    <deviceType>"+deviceType+"</deviceType>" +
				   "    <product>"+product+"</product>" +
				   "    <brand>"+brand+"</brand>" +
				   "    <manufacturer>"+manufacturer+"</manufacturer>" +
				   "    <serial>"+serial+"</serial>" +
				   "    <board>"+board+"</board>" +
				   "</device>";
		}
		else if (format.equalsIgnoreCase("JSON"))
		{
			return "ipv4: "+ipv4;
		}
		else
			return null;
	}

	@Override
	public Set<String> getStringRepresentationFormats() 
	{
		Set<String> formats = new HashSet<String>();
		formats.add("text/plain");
		formats.add("XML");
		formats.add("JSON");
		return formats;
	}
	
    /**
     * Comma separated List of IP adresses of available network interfaces
     * @param useIPv4
     * @return
     */
    public static String getIPAddress(boolean useIPv4) 
    {

        String addresses = "";
        try 
        {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) 
            {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) 
                {
                    if (!addr.isLoopbackAddress()) 
                    {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) 
                        {
                            if (isIPv4)
                                addresses += sAddr + ", ";
                        } 
                        else 
                        {
                            if (!isIPv4) 
                            {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                if(delim<0) addresses += sAddr + ", ";
                                else addresses += sAddr.substring(0, delim) + ", ";
                            }
                        }
                    }
                }
            }
        } 
        catch (Exception ex) 
        { 
        	
        } // for now eat exceptions
        if(addresses == null || addresses.length() <= 3)
        { 
        	return "";
        }
        return addresses.subSequence(0, addresses.length()-2).toString();
    }

}