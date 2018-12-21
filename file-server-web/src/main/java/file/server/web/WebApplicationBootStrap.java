package file.server.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import wx.configuration.ApplicationBootStrap;


@SpringBootApplication
@EnableTransactionManagement // 事物注解
@ComponentScan(basePackages = {
								"file.server.web.controller",
								"file.server.web.inspect"
							   })
public class WebApplicationBootStrap extends ApplicationBootStrap {
	public static void main(String[] args) {
		new WebApplicationBootStrap().run(args);
	}
}