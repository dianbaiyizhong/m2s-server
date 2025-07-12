package com.nntk.m2s;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;

public class MysqlCodegen {


    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir") + "/" + "/src/main/java";
        FastAutoGenerator.create("jdbc:mysql://localhost/m2s_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8&useTimezone=true&allowPublicKeyRetrieval=true", "root", "root")
                .globalConfig(builder -> {
                    builder.author("nntk") // 设置作者
                            .outputDir(projectPath);
                })
                .packageConfig(builder -> {
                    builder.parent("com.nntk.m2s.mp") // 设置父包名
                            .moduleName("generate")
                    ;
                })
                .strategyConfig(builder -> {
                    builder
                            .mapperBuilder().enableFileOverride()
                            .entityBuilder().enableFileOverride()
                            .serviceBuilder().disableService().disableServiceImpl()
                            .controllerBuilder().disable()
                    ;
                }).execute();
    }
}
