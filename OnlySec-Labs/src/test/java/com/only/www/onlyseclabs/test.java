package com.only.www.onlyseclabs;

import com.github.dockerjava.api.model.Image;
import com.only.www.onlyseclabs.entity.User;
import com.only.www.onlyseclabs.service.UserService;
import com.only.www.onlyseclabs.service.impl.UserServiceImpl;
import com.only.www.onlyseclabs.utils.DockerUtils;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test {
    @Test
    public void t1() throws IOException, ClassNotFoundException {
        DockerUtils dockerUtils = new DockerUtils("tcp://172.16.122.189:2375");
        dockerUtils = dockerUtils;

        // 测试列出所有镜像
        System.out.println("=== 列出所有镜像 ===");
        dockerUtils.listImages().forEach(image -> {
            System.out.println("Image ID: " + image.getId());
            System.out.println("Tags: " + image.getRepoTags()[0]);
        });

        // 测试列出所有容器
        System.out.println("\n=== 列出所有容器 ===");
        dockerUtils.listContainers(true).forEach(container -> {
            System.out.println("Container ID: " + container.getId());
            System.out.println("Image: " + container.getImage());
            System.out.println("Status: " + container.getStatus());
        });

    }


    }
