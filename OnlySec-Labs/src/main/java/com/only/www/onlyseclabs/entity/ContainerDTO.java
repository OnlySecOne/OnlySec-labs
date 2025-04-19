package com.only.www.onlyseclabs.entity;

import lombok.Data;
import java.util.Map;

@Data
public class ContainerDTO {
    private String imageName;
    private String containerName;
    private Map<String, String> ports;
    private String dockerfilePath;
}