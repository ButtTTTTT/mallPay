package top.lhit.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("top.lhit.mall.module.mapper")
public class Generator {
    public static void main(String[] args) {
        SpringApplication.run(Generator.class, args);
    }
}
