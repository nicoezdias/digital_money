package com.msvc.eureka.server.sprgbt.config;

import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.MyDataCenterInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EurekaClientInstanceConfig {
    @Value("${server.port}")
    private Integer serverPort;

    @Value("${server.ip-address}")
    private String serverIpAddress;



    @Bean
    public EurekaInstanceConfigBean eurekaInstanceConfig(InetUtils inetUtils) {
        EurekaInstanceConfigBean b = new EurekaInstanceConfigBean(inetUtils);
        MyDataCenterInfo info = new MyDataCenterInfo(DataCenterInfo.Name.MyOwn);
        b.setIpAddress(serverIpAddress);
        b.setPreferIpAddress(true);
        b.setNonSecurePort(serverPort);
        InetUtils.HostInfo hostInfo = inetUtils.findFirstNonLoopbackHostInfo();
        b.setHostname(hostInfo.getHostname());
        b.setDataCenterInfo(info);
        return b;
    }

}
